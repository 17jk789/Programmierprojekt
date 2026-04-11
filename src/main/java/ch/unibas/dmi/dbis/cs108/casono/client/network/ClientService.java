package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
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

    public static ArrayList<String> response;
    private final AtomicInteger idGenerator;
    private Logger logger;

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

    static Pattern responseRex =
            Pattern.compile(
                    "(?<key>\\w+)=(('(?<string>([^']|\\')+)')|(?<primVal>[+-]?[\\d\\w:]+))");

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
        List<String> response = new ArrayList<>();
        sendRequest(
                () -> {
                    try {
                        writeToTransport(message);
                        String responseText = null;

                        responseText = clienttcptransport.read().payload();
                        logger.info("Raw message '" + responseText + "'");
                        Boolean success = null;
                        int count = 0;
                        for (String line : responseText.split("\n")) {
                            if (success == null) {
                                if ("+OK".equals(line)) {
                                    success = true;
                                    continue;

                                } else if (("-ERROR").equals(responseText)) {
                                    success = false;
                                }
                                continue;
                            } else if ("END".equals(line)) {
                                break;
                            }
                            line = line.replaceFirst("^\t", "");
                            response.add(line);
                        }
                        if (success != null && success) {
                            return;
                        } else {
                            throw new RuntimeException("Error in " + message + ": " + response);
                        }
                    } catch (Exception e) {
                        throw getRuntimeException(e);
                    }
                });
        return response;
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
        Throwable reason = e.getCause();
        RuntimeException re;
        if (reason == null) {
            reason = e;
        } else if (reason instanceof RuntimeException rte) {
            re = rte;
        }
        re = new RuntimeException(reason);
        return re;
    }

    /**
     * Closes the socket connection to the server and shuts down the ExecutorService. It also closes
     * the TcpTransport used for communication. If any IOException occurs during this process, it
     * prints the exception to the console.
     */
    public void closeSocket() {
        try {
            executor.shutdown();
            clienttcptransport.close();
            socket.close();
            logger.info("Socket closed");
        } catch (IOException j) {
            logger.debug(j);
        }
    }

    /**
     * Method to write with the tcp transport to the server
     *
     * @param s - Message to be sent
     * @throws IOException
     */
    private void writeToTransport(String s) throws IOException {
        int id = this.idGenerator.incrementAndGet();
        this.clienttcptransport.write(new RawPacket(id, s));
    }

    public void ping() {
        processCommand("PING");
    }
}
