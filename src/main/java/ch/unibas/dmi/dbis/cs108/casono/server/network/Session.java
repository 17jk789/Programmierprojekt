package ch.unibas.dmi.dbis.cs108.casono.server.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Session implements Runnable {
    private SessionId id;
    private Thread thread;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Logger logger;
    
    public Session(Socket socket) throws IOException {
        this.id = new SessionId();
        this.thread = new Thread(this, "session-" + this.id.value());
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());

        this.logger = LogManager.getLogger(Session.class.toString() + id.value());
        this.logger.info("Created new session");
    }

    public SessionId getId() {
        return this.id;
    }

    public void start() {
        thread.start();
    }

    public String read() throws IOException {
        int length = in.readInt();
        byte[] payload = new byte[length];
        in.readFully(payload);
        return new String(payload, StandardCharsets.UTF_8);
    }

    public void close() throws IOException {
        socket.close();
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.println("Recieved: " + read());
            } catch (EOFException e) {
                logger.info("Client disconnected");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}