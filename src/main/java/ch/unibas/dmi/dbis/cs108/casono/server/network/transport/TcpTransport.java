package ch.unibas.dmi.dbis.cs108.casono.server.network.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TcpTransport implements TransportLayer {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public TcpTransport(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public String read() throws IOException {
        int length = in.readInt();
        byte[] payload = new byte[length];
        in.readFully(payload);
        return new String(payload, StandardCharsets.UTF_8);
    }

    public void write(String payload) throws IOException {
        byte[] rawPayload = payload.getBytes(StandardCharsets.UTF_8);
        out.writeInt(rawPayload.length);
        out.write(rawPayload);
        out.flush();    
    }

    public void close() throws IOException {
        socket.close();
    }
}
