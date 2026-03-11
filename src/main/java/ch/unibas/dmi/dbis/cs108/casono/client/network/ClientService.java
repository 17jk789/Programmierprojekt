package ch.unibas.dmi.dbis.cs108.casono.client.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
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
     * Sends a Chat Message to the Server and awaits a response, which will be passed along
     * @param message : Protocol Message of type "Message" to be sent to the server
     * @return ArrayList<String> : The Response of the Server to be interpreted later
     */

    public Future<ArrayList<String>> sendMessage(Message message) {

        String time = message.hourTime + ":" + message.minuteTime;

        String request = "";

        if (message.target == null && message.game_id == 0) {
            request = String.format("SEND_MESSAGE TYPE=GLOBAL GAME=null USER=%s TARGET=null TEXT=%s TIME=%s", message.name, message.getMessage(), time);
        }
        else if (message.target == null) {
            request = String.format("SEND_MESSAGE TYPE=LOBBY GAME=%d USER=%s TARGET=null TEXT=%s TIME=%s", message.game_id, message.name, message.getMessage(), time);
        } else {
            request = String.format("SEND_MESSAGE TYPE=WHISPER GAME=%d USER=%s TARGET=%s TEXT=%s TIME=%s", message.game_id, message.name, message.target, message.getMessage(), time);
        }

        System.out.println("Writing following request: " + request);

        sendRequest task = new sendRequest(request, input, output);

        Future<ArrayList<String>> response = executor.submit(task);

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
