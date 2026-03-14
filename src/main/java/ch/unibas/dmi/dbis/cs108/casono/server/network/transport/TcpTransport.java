package ch.unibas.dmi.dbis.cs108.casono.server.network.transport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Implements TCP-based transport layer for network communication. */
public class TcpTransport implements TransportLayer {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    /**
     * Creates a new TcpTransport with the given socket.
     *
     * @param socket the socket to use for communication
     * @throws IOException if an I/O error occurs
     */
    public TcpTransport(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Reads a string from the socket.
     *
     * @return the read string
     * @throws IOException if an I/O error occurs
     */
    public String read() throws IOException {
        int length = in.readInt();
        byte[] payload = new byte[length];
        in.readFully(payload);
        return new String(payload, StandardCharsets.UTF_8);
    }

    /**
     * Writes a string to the socket.
     *
     * @param payload the string to write
     * @throws IOException if an I/O error occurs
     */
    public void write(String payload) throws IOException {
        byte[] rawPayload = payload.getBytes(StandardCharsets.UTF_8);
        out.writeInt(rawPayload.length);
        out.write(rawPayload);
        out.flush();    
    }

    /**
     * Closes the socket.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        socket.close();
    }
}
