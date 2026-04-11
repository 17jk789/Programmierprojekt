package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The ClientService class is responsible for managing the connection to the
 * server,
 * sending commands, and receiving responses. It uses a TcpTransport to
 * communicate
 * with the server and an ExecutorService to handle asynchronous requests.
 */
public class ClientService {

    private final TcpTransport clienttcptransport;
    private final Socket socket;

    private final ExecutorService executor;
    private final boolean offlineMode;

    public static ArrayList<String> response;
    private final AtomicInteger idGenerator;

    /**
     * Constructs a ClientService with the given server IP and port. It establishes
     * a socket connection to the server and initializes the TcpTransport and
     * ExecutorService for communication.
     *
     * @param ip   The IP address of the server to connect to.
     * @param port The port number of the server to connect to.
     */
    public ClientService(String ip, int port) {

        this.idGenerator = new AtomicInteger(0);

        this.offlineMode = false;
        try {
            socket = new Socket(ip, port);
            clienttcptransport = new TcpTransport(socket);
        } catch (IOException i) {
            throw new RuntimeException(i);
        }

        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Constructs a ClientService in offline mode. No network connection will be
     * attempted and calls to processCommand will throw a RuntimeException.
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

    /**
     * Returns true if this ClientService is running in offline mode (no network).
     */
    public boolean isOffline() {
        return offlineMode;
    }

    /**
     * Sends a command to the server and waits for the response. The command is
     * sent using the TcpTransport, and the response is read in a loop until a
     * valid response is received. The method handles "+OK" and "-ERROR" responses
     * from the server and returns the actual response content.
     *
     * @param message The command message to be sent to the server.
     * @return The response from the server as a string.
     */
    protected String processCommand(String message) {
        if (offlineMode) {
            throw new RuntimeException("ClientService is offline: cannot process command");
        }
        AtomicReference<String> response = new AtomicReference<>();
        sendRequest(() -> {
            try {
                writeToTransport(message);
                String responseLine = null;
                do {
                    responseLine = clienttcptransport.read().payload();
                    System.out.println("Raw message '" + responseLine + "'");
                    if ("+OK".equals(responseLine)) {
                        return;
                    } else if (("-ERROR").equals(responseLine)) {
                        throw new RuntimeException(responseLine);
                    }
                    response.set(responseLine);
                } while (true);
            } catch (Exception e) {
                throw getRuntimeException(e);
            }
        });
        return response.get();
    }

    /**
     * Helper method to send a request to the server using the ExecutorService. It
     * submits the request as a Runnable task and waits for its completion. If
     * the task is interrupted or encounters an execution exception, it throws a
     * RuntimeException with the appropriate cause.
     *
     * @param request The Runnable task representing the request to be sent to the
     *                server.
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
     * Helper method to extract the cause of an exception and return it as a
     * RuntimeException. If the cause is null, it returns the original exception as
     * a RuntimeException. If the cause is already a RuntimeException, it returns
     * it directly. Otherwise, it wraps the cause in a new RuntimeException and
     * returns it.
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
     * Closes the socket connection to the server and shuts down the
     * ExecutorService.
     * It also closes the TcpTransport used for communication. If any IOException
     * occurs during this process, it prints the exception to the console.
     */
    public void closeSocket() {
        try {
            executor.shutdown();
            if (clienttcptransport != null) {
                clienttcptransport.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException j) {
            System.out.println(j);
        }

    }

    /**
     * Helper method to write a command string to the TcpTransport. It generates a
     * unique ID for the command using the idGenerator and sends a RawPacket
     * containing the ID and the command string to the server. If an IOException
     * occurs during this process, it throws a RuntimeException with the cause.
     *
     * @param s The command string to be sent to the server.
     * @throws IOException If an I/O error occurs while writing to the transport.
     */
    private void writeToTransport(String s) throws IOException {
        int id = this.idGenerator.incrementAndGet();
        this.clienttcptransport.write(new RawPacket(id, s));
    }

}
