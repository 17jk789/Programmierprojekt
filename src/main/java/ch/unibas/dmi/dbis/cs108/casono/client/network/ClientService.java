package ch.unibas.dmi.dbis.cs108.casono.client.network;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;


/**
 * Responsible for the transferring of the data from the Client to the Server and the other way around
 */

public class ClientService {

    private final TcpTransport clienttcptransport;
    private final Socket socket;

    private final String ip;
    private final int port;

    private final ExecutorService executor;

    public static ArrayList<String> response;
    private final AtomicInteger idGenerator;

    /**
     * Creates a new ClientSession with a Socket, a Reader and Writer of the Input- and the Outputstream and a pool of threads to send requests and receive responses.
     * @param ip : ip-adress of the server
     * @param port : port of the server
     */

    public ClientService(String ip, int port) {

        this.ip = ip;
        this.port = port;
        this.idGenerator = new AtomicInteger(0);

        try {
            socket = new Socket(this.ip, this.port);
            System.out.println("Connected");
            clienttcptransport = new TcpTransport(socket);
        }
        catch (IOException i) {
            throw new RuntimeException(i);
        }

        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Sends a Request to get all the messages, the other clients sent, and that the client is not currently aware of.
     */

    public List<Message> getMessages() {
        String countStr = processMessage("GET_MESSAGE_COUNT");
        int count = Integer.parseInt(countStr);
        System.out.println("Got " + count + " messages");
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String  message = processMessage("GET_NEXT_MESSAGE");
            if (message != null) {
                Message message1 = Message.toMessage(message);
                messages.add(message1);
            }
        }
        return messages;
    }

    public String sendMessage(Message message) {
        String request = "SEND_MESSAGE " + message.toArgsString();
        return processMessage(request);
    }

    private String processMessage(String message) {
        AtomicReference<String> response = new AtomicReference<>();
        sendRequest(() -> {
            try {
                writeToTransport(message);
                String responseLine=null;
                do {
                    responseLine = clienttcptransport.read().payload();
                    System.out.println("Raw message '" + responseLine + "'");
                    if ("+OK".equals(responseLine)) {
                        return;
                    } else if ("-FAIL".equals(responseLine)) {
                        throw new RuntimeException(responseLine);
                    }
                    response.set(responseLine);
                } while(true);
            } catch (Exception e) {
                throw getRuntimeException(e);
            }
        });
        return response.get();
    }

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
     * Closes the Socket and shuts down the Threadpool associated with that Socket-Connection.
     */

    public void closeSocket() {
        try {
            executor.shutdown();
            clienttcptransport.close();
            socket.close();
        } catch (IOException j) {
            System.out.println(j);
        }

    }

    private void writeToTransport(String s) throws IOException {
        int id = this.idGenerator.incrementAndGet();
        this.clienttcptransport.write(new RawPacket(id, s));
    }

}
