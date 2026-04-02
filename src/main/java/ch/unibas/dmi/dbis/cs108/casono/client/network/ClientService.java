package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Responsible for the transferring of the data from the Client to the Server and the other way
 * around
 */
public class ClientService {

    private final TcpTransport clienttcptransport;
    private final Socket socket;

    private final ExecutorService executor;

    public static ArrayList<String> response;
    private final AtomicInteger idGenerator;
    private final Logger logger;

    /**
     * Creates a new ClientSession with a Socket, a Reader and Writer of the Input- and the
     * Outputstream and a pool of threads to send requests and receive responses.
     *
     * @param ip : ip-adress of the server
     * @param port : port of the server
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
     * Sends a ping to the server Returns nothing, the method processMessage() already handles the
     * cases "+OK" or "-ERROR"
     */
    public void ping() {
        processMessage("PING");
    }

    /**
     * Sends a login request to the server to create a new user on the server
     *
     * @param username
     * @return - a new username, if the same username is already used by someone else
     */
    public String login(String username) {
        String msg = "LOGIN USERNAME=" + username;
        return processMessage(msg);
    }

    /**
     * Send a Request to get the number of Messages currently in the Queue for the client. Then
     * proceeds, if needed, to get the messages by sending
     */
    public List<Message> getMessages() {
        String countStr = processMessage("GET_MESSAGE_COUNT");
        int count = Integer.parseInt(countStr);
        logger.info("Got " + count + " messages");
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String message = processMessage("GET_NEXT_MESSAGE");
            if (message != null) {
                Message message1 = Message.toMessage(message);
                messages.add(message1);
            }
        }
        return messages;
    }

    /**
     * Sends a Request to the Server containing all relevant information of the message the client
     * wrote.
     *
     * @param message
     */
    public void sendMessage(Message message) {
        String request = "SEND_MESSAGE " + message.toArgsString();
        processMessage(request);
    }

    /**
     * Sends the Requests to the server and waits for the response If the response is "+OK" it
     * proceeds normal If the response "-ERROR" it throws a runtime exception
     *
     * @param message
     * @return - The response as a string, if it has to be returned (+OK will not be returned)
     */
    private String processMessage(String message) {
        AtomicReference<String> response = new AtomicReference<>();
        sendRequest(
                () -> {
                    try {
                        writeToTransport(message);
                        String responseLine = null;
                        do {
                            responseLine = clienttcptransport.read().payload();
                            logger.info("Raw message '" + responseLine + "'");
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
     * @param request
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
     * Returns a Runtime Exceptions thrown by the sendRequest method
     *
     * @param e - an Exception
     * @return - a Runtime Exception
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

    /** Closes the Socket and shuts down the Threadpool associated with that Socket-Connection. */
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
}
