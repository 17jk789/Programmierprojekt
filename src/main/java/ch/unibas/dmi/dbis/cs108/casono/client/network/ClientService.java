package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The ClientService class is responsible for managing the connection to the server, sending
 * commands, and receiving responses. It uses a TcpTransport to communicate with the server and an
 * ExecutorService to handle asynchronous requests.
 */
public class ClientService {

    private final TcpTransport clienttcptransport;
    private final Socket socket;

    private final ExecutorService executor;
    private final boolean offlineMode;

    private final AtomicInteger idGenerator;
    private Logger logger;

    private final Map<Integer, ArrayBlockingQueue<ParsedResponse>> pendingResponses =
            new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<Consumer<List<String>>> eventListeners =
            new CopyOnWriteArrayList<>();
    private Thread readerThread = null;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private static final int READER_JOIN_TIMEOUT_MS = 500;

    /**
     * Constructs a ClientService with the given server IP and port. It establishes a socket
     * connection to the server and initializes the TcpTransport and ExecutorService for
     * communication.
     *
     * @param ip The IP address of the server to connect to.
     * @param port The port number of the server to connect to.
     */
    public ClientService(String ip, int port) {
        this.idGenerator = new AtomicInteger(0);

        this.logger = LogManager.getLogger(ClientService.class);

        this.offlineMode = false;

        try {
            socket = new Socket(ip, port);
            clienttcptransport = new TcpTransport(socket);
            logger.info("Connected to server at " + ip);
        } catch (IOException i) {
            throw new RuntimeException(i);
        }

        executor = Executors.newSingleThreadExecutor();

        startReaderThread();
    }

    private void startReaderThread() {
        running.set(true);
        readerThread =
                new Thread(
                        () -> {
                            while (running.get()) {
                                try {
                                    RawPacket rp = clienttcptransport.read();
                                    processRawPacket(rp);
                                } catch (IOException e) {
                                    if (running.get()) {
                                        logger.warn("IO error on transport reader", e);
                                    }
                                    break;
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                        },
                        "casono-client-reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    private void processRawPacket(RawPacket rp) throws InterruptedException, IOException {
        int rid = rp.requestId();
        String responseText = rp.payload();
        logger.info("Raw message '{}'", responseText);

        boolean hasStatus = false;
        boolean success = false;

        List<String> lines = new ArrayList<>();

        int depth = 0;
        boolean inPlayer = false;

        for (String rawLine : responseText.split("\\n")) {
            String line = rawLine;
            String trimmed = line.trim();

            if (!hasStatus) {
                if ("+OK".equals(trimmed)) {
                    success = true;
                    hasStatus = true;
                    continue;
                }
                if (trimmed.startsWith("-ERR") || trimmed.startsWith("-ERROR")) {
                    success = false;
                    hasStatus = true;
                    continue;
                }
                continue;
            }

            if (trimmed.isEmpty()) continue;

            line = line.replaceFirst("^\\s+", "");
            trimmed = line.trim();

            if ("PLAYER".equals(trimmed)) {
                inPlayer = true;
                lines.add("PLAYER");
                continue;
            }

            if (isContainerStart(trimmed)) {
                depth++;
                lines.add(trimmed);
                continue;
            }

            if ("END".equals(trimmed)) {
                if (inPlayer) {
                    inPlayer = false;
                    lines.add("END");
                    continue;
                }

                if (depth > 0) {
                    depth--;
                    lines.add("END");
                    continue;
                }

                break;
            }

            lines.add(line);
        }

        if (!hasStatus) {
            if (responseText.contains("+OK")) {
                success = true;
                hasStatus = true;
            } else if (responseText.contains("-ERR") || responseText.contains("-ERROR")) {
                success = false;
                hasStatus = true;
            } else {
                success = true;
                hasStatus = true;
            }
        }

        logger.debug("Parsed response lines (rid={}, success={}, depthEnd={}, inPlayerEnd={}): {}",
                rid, success, depth, inPlayer, lines);

        if (rid == 0) {
            for (Consumer<List<String>> l : eventListeners) {
                try {
                    l.accept(List.copyOf(lines));
                } catch (Exception e) {
                    logger.warn("Event listener threw", e);
                }
            }
        } else {
            ArrayBlockingQueue<ParsedResponse> q = pendingResponses.get(rid);
            if (q != null) {
                q.put(new ParsedResponse(success, lines));
            } else {
                logger.warn("No pending response queue for id {}", rid);
            }
        }
    }

    private boolean isContainerStart(String token) {
        return token.equals("LOBBIES")
                || token.equals("LOBBY")
                || token.equals("PLAYERS")
                || token.equals("CARDS");
        // NOTE: PLAYER deliberately excluded
    }

    /**
     * Constructs a ClientService in offline mode. No network connection will be attempted and calls
     * to processCommand will throw a RuntimeException.
     *
     * @param offline true to create an offline (no-network) client service
     */
    public ClientService(boolean offline) {
        this.idGenerator = new AtomicInteger(0);
        this.offlineMode = offline;
        this.socket = null;
        this.clienttcptransport = null;
        this.executor = Executors.newSingleThreadExecutor();
    }

    /** Returns true if this ClientService is running in offline mode (no network). */
    public boolean isOffline() {
        return offlineMode;
    }

    // Allow primitive values to contain hyphens (UUIDs) in addition to
    // digits/words/colons
    static Pattern responseRex =
            Pattern.compile(
                    "(?<key>\\w+)=(('(?<string>([^']|\\')+)')|(?<primVal>[+-]?[-\\d\\w:]+))");

    /**
     * Removes escape characters from a string, specifically converting escaped single quotes (\')
     * back to regular single quotes (').
     *
     * @param input The escaped string to process.
     * @return The unescaped string.
     */
    private static String unescape(String input) {
        return input.replaceAll("\\\\'", "'");
    }

    /**
     * Converts a list of raw string parameters into a list of {@link RequestParameter} objects. It
     * uses a regex matcher to distinguish between quoted strings (which are unescaped) and
     * primitive values.
     *
     * @param input A list of raw strings to be parsed.
     * @return A list of parsed {@link RequestParameter} objects.
     * @throws RuntimeException if a parameter does not match the expected format.
     */
    public static List<RequestParameter> convertToRequestParameters(List<String> input) {
        return input.stream()
                .map((String parString) -> responseRex.matcher(parString))
                .filter(Matcher::matches)
                .map(
                        (m) -> {
                            if (!(m.group("string") == null)) {
                                return new RequestParameter(
                                        m.group("key"), unescape(m.group("string")));
                            } else if (!(m.group("primVal") == null)) {
                                return new RequestParameter(m.group("key"), m.group("primVal"));
                            } else {
                                throw new RuntimeException();
                            }
                        })
                .toList();
    }

    private static record ParsedResponse(boolean success, List<String> lines) {}

    /**
     * Register an event listener that receives unsolicited event payload lines (no status prefix).
     */
    public void addEventListener(Consumer<List<String>> listener) {
        eventListeners.add(listener);
    }

    public void removeEventListener(Consumer<List<String>> listener) {
        eventListeners.remove(listener);
    }

    /**
     * Sends a command to the server and processes the multi-line response. It handles the protocol
     * handshake (expecting +OK), strips leading tabs from response lines, and collects them until
     * the "END" marker is reached.
     *
     * @param message The raw command string to be sent to the transport layer.
     * @return A list of response lines received from the server (excluding protocol markers).
     * @throws RuntimeException if the server responds with an error or if a communication failure
     *     occurs.
     */
    protected List<String> processCommand(String message) {
        if (offlineMode) {
            throw new RuntimeException("ClientService is offline");
        }

        int reqId = idGenerator.incrementAndGet();
        ArrayBlockingQueue<ParsedResponse> q = new ArrayBlockingQueue<>(1);
        pendingResponses.put(reqId, q);

        Future<?> writeFuture =
                executor.submit(
                        () -> {
                            try {
                                clienttcptransport.write(new RawPacket(reqId, message));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });

        try {
            writeFuture.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pendingResponses.remove(reqId);
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            pendingResponses.remove(reqId);
            throw getRuntimeException(e);
        }

        ParsedResponse pr;
        try {
            pr = q.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            pendingResponses.remove(reqId);
            throw new RuntimeException(e);
        } finally {
            pendingResponses.remove(reqId);
        }

        if (pr.success) {
            return pr.lines;
        }

        throw new RuntimeException("Error in " + message + ": " + pr.lines);
    }

    /**
     * Helper method to send a request to the server using the ExecutorService. It submits the
     * request as a Runnable task and waits for its completion. If the task is interrupted or
     * encounters an execution exception, it throws a RuntimeException with the appropriate cause.
     *
     * @param request The Runnable task representing the request to be sent to the server.
     */
    private void sendRequest(Runnable request) {
        Future<?> future = executor.submit(request);
        try {
            future.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw getRuntimeException(e);
        }
    }

    /**
     * Helper method to extract the cause of an exception and return it as a RuntimeException. If
     * the cause is null, it returns the original exception as a RuntimeException. If the cause is
     * already a RuntimeException, it returns it directly. Otherwise, it wraps the cause in a new
     * RuntimeException and returns it.
     *
     * @param e The exception from which to extract the cause.
     * @return A RuntimeException representing the cause of the original exception.
     */
    private static RuntimeException getRuntimeException(Exception e) {
        Throwable cause = e.getCause();
        if (cause == null) {
            return e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
        if (cause instanceof RuntimeException) {
            return (RuntimeException) cause;
        }
        return new RuntimeException(cause);
    }

    /**
     * Closes the socket connection to the server and shuts down the ExecutorService. It also closes
     * the TcpTransport used for communication. If any IOException occurs during this process, it
     * prints the exception to the console.
     */
    public void closeSocket() {
        try {
            running.set(false);
            if (readerThread != null) {
                readerThread.interrupt();
                try {
                    readerThread.join(READER_JOIN_TIMEOUT_MS);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }

            executor.shutdownNow();
            clienttcptransport.close();
            socket.close();
            logger.info("Socket closed");
        } catch (IOException j) {
            logger.debug(j);
        }
    }

    // removed unused helper writeToTransport; write is done via processCommand()
    // which
    // manages request ids and response matching

    public void ping() {
        processCommand("PING");
    }
}
