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
     * @return the read RawPacket
     * @throws IOException if an I/O error occurs
     */
    public RawPacket read() throws IOException {
        int length = in.readInt();
        int requestId = in.readInt();
        byte[] rawPayload = new byte[length];
        in.readFully(rawPayload);

        String payload = new String(rawPayload, StandardCharsets.UTF_8);
        return new RawPacket(requestId, payload);
    }

    /**
     * Writes a string to the socket.
     *
     * @param data the RawPacket's data to write
     * @throws IOException if an I/O error occurs
     */
    public void write(RawPacket data) throws IOException {
        int requestId = data.requestId();
        byte[] rawPayload = data.payload().getBytes(StandardCharsets.UTF_8);

        out.writeInt(requestId);
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
