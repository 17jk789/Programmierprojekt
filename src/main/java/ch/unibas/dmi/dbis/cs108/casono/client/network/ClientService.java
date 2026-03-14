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
     * Sends a Chat Message to the Server and awaits a response, which will be passed along
     * @param message : Protocol Message of type "Message" to be sent to the server
     * @return ArrayList<String> : The Response of the Server to be interpreted later
     */

    public ArrayList<String> sendMessage(Message message) {
        String time = message.hourTime + ":" + message.minuteTime;
        String request = "";
        if (message.target == null && message.game_id == 0) {
            request = String.format("SEND_MESSAGE TYPE=GLOBAL GAME=null USER=%s TARGET=null TIME=%s TEXT=%s", message.name, time, message.getMessage());
        }
        else if (message.target == null) {
            request = String.format("SEND_MESSAGE TYPE=LOBBY GAME=%d USER=%s TARGET=null TIME=%s TEXT=%s", message.game_id, message.name,  time, message.getMessage());
        } else {
            request = String.format("SEND_MESSAGE TYPE=WHISPER GAME=%d USER=%s TARGET=%s TIME=%s TEXT=%s", message.game_id, message.name, message.target, time, message.getMessage());
        }
        System.out.println("Writing following request: " + request);
        sendRequest task = new sendRequest(request, input, output);
        executor.submit(task);
        return this.response;
    }

    public ArrayList<String> getState(int game_id) {
        String request = String.format("GET_STATE GAME=%d", game_id);
        sendRequest task = new sendRequest(request, input, output);
        executor.submit(task);
        return this.response;

    }

    public ArrayList<String> joinGame(int game_id, String name) {
        String request = String.format("JOIN GAME=%d NAME=%s", game_id, name);
        sendRequest task = new sendRequest(request, input, output);
        executor.submit(task);
        return response;
    }

    /**
     *
     * @param type : Keyword, as one-word description of the action the player did.
     * @param game_id : ID of the Lobby that the client is currently in
     * @param value : Dependent on the action of the User (money)
     * @param name : Username of the player of that Client
     * @return The Response of the Server -> Action successfull only if response is valid
     */

    public ArrayList<String>sendAction(String type, int game_id, String action, int value, String name) {
        String request = String.format("SEND_ACTION TYPE=%s GAME=%s ACTION=%s VALUE=%d NAME=%s", type, game_id, action, value, name);
        sendRequest task = new sendRequest(request, input, output);
        executor.submit(task);
        return response;
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
