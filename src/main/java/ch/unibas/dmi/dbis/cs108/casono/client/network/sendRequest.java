package ch.unibas.dmi.dbis.cs108.casono.client.network;

import java.io.BufferedReader;
import java.util.concurrent.Callable;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;

import java.util.ArrayList;

import java.io.*;

public class sendRequest implements Runnable {

    final private String request;
    final private BufferedReader input;
    final private BufferedWriter output;

    public sendRequest(String request, BufferedReader input, BufferedWriter output) {
        this.request = request;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            output.write(request + "\r\n");
            output.flush();
            //System.out.println("Writing following request: " + request);
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            while (true) {
                String line;
                line = input.readLine();
                if (line.toLowerCase().startsWith("+ok")) {
                    break;
                }
                ClientService.response.add(line);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}