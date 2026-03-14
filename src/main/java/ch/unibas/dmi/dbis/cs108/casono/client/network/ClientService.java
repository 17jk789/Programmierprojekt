package ch.unibas.dmi.dbis.cs108.casono.client.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import java.io.IOException;
import java.net.UnknownHostException;

import ch.unibas.dmi.dbis.cs108.casono.client.chat.Message;

/**
 * Responsible for the transferring of the data from the Client to the Server and the other way around
 */

public class ClientService {

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;

    private String ip;
    private int port;

    private ExecutorService executor;

    public static ArrayList<String> response;

    /**
     * Creates a new ClientSession with a Socket, a Reader and Writer of the Input- and the Outputstream and a pool of threads to send requests and receive responses.
     * @param ip : ip-adress of the server
     * @param port : port of the server
     */

    public ClientService(String ip, int port) {

        this.ip = ip;
        this.port = port;

        try {
            socket = new Socket(this.ip, this.port);
            System.out.println("Connected");
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }
        catch (UnknownHostException u) {
            System.out.println(u);
            return;
        }
        catch (IOException i) {
            System.out.println(i);
            return;
        }

        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Sends a Request to get all the messages, the other clients sent, and that the client is not currently aware of.
     */

    public List<String> getMessage() {
        return processMessage("GET_MESSAGE");
    }

    public List<String> sendMessage(Message message) { return processMessage(message.toRequest()); }

    private List<String> processMessage(String message) {
        List<String> response = new ArrayList<>();
        sendRequest(() -> {
            try {
                output.write(message+"\n");
                output.flush();
                String line;
                while ((line = input.readLine()) != null) {
                    if (line.toLowerCase().startsWith(".")) {
                        break;
                    }
                    response.add(line);
                }
            } catch (Exception e) {
                throw getRuntimeException(e);
            }
        });
        return response;
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
            input.close();
            output.close();
            socket.close();
        } catch (IOException j) {
            System.out.println(j);
        }

    }
}
