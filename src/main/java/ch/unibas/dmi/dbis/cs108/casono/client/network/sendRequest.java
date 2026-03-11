package ch.unibas.dmi.dbis.cs108.casono.client.network;

import java.io.BufferedReader;
import java.util.concurrent.Callable;

import java.util.ArrayList;

import java.io.*;

public class sendRequest implements Callable<ArrayList<String>> {

    final private String request;
    final private BufferedReader input;
    final private BufferedWriter output;

    public sendRequest(String request, BufferedReader input, BufferedWriter output) {
        this.request = request;
        this.input = input;
        this.output = output;
    }

    @Override
    public ArrayList<String> call() throws Exception {
        try {
            output.write(request+"\n");
            output.flush();
            System.out.println("Writing following request: " + request);
        } catch (Exception e) {
            System.out.println(e);
        }

        ArrayList<String> response = new ArrayList<>();

        try {
            String line;
            while ((line = input.readLine()) != null) {
                if (line.toLowerCase().startsWith("ok")) {
                    break;
                }
                response.add(line);
            }

            System.out.println("Response from server: " + response.get(0));
        }
        catch (Exception e) {System.out.println(e);}

        return response;
    }
}
