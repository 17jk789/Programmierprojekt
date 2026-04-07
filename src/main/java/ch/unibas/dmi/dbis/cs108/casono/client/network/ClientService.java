package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    public static ArrayList<String> response;
    private final AtomicInteger idGenerator;
    private final Logger logger;
    /*
     * Constructs a ClientService with the given server IP and port. It establishes
     * a socket connection to the server and initializes the TcpTransport and
     * ExecutorService for communication.
     *
     * @param ip   The IP address of the server to connect to.
     * @param port The port number of the server to connect to.
     */
    public ClientService(String ip, int port) {

        this.idGenerator = new AtomicInteger(0);

        this.logger = LogManager.getLogger(ClientService.class);

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
     * Sends the Requests to the server and waits for the response If the response is "+OK" it
     * proceeds normal If the response "-ERROR" it throws a runtime exception
     *
     * @param message
     * @return - The response as a string, if it has to be returned (+OK will not be returned)
     */
    protected String processCommand(String message) {
        AtomicReference<String> response = new AtomicReference<>();
        sendRequest(
                () -> {
                    try {
                        writeToTransport(message);
                        String responseText = null;
                        do {
                            responseText = clienttcptransport.read().payload();
                            logger.info("Raw message '" + responseText + "'");
                            for(String line: responseText.split("\n")) {
                                if ("+OK".equals(line)) {
                                    return;
                                } else if (("-ERROR").equals(responseText)) {
                                    throw new RuntimeException(responseText);
                                } else {
                                    String start = response.get();
                                    if(start == null) {
                                        response.set(line);
                                    } else {
                                        response.set(start + "\n" + line);
                                    }
                                }
                            }
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
