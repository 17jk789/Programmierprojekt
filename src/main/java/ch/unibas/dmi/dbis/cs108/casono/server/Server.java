package ch.unibas.dmi.dbis.cs108.casono.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket ss = null;
    private BufferedReader input = null;
    private BufferedWriter output = null;

    public Server(int port) {
        try {
            ss = new ServerSocket(port);
            System.out.println("Server started");
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void simpleListenLoop() {
        System.out.println("Waiting for connection...");
        Socket s = null;
        try {
        while (true) {
                s = ss.accept();
            System.out.println("Accepted connection");

            input = new BufferedReader(new InputStreamReader(s.getInputStream()));

            output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            String line = null;
            while((line = input.readLine())!=null) {
                System.out.println("Server got "+ line);
                // do something with this line
                output.write("OK"+"\n");
                output.flush();
                if(Thread.currentThread().isInterrupted()) {
                    return;
                }
            }
            if(Thread.currentThread().isInterrupted()) {
                return;
            }
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                s.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

}