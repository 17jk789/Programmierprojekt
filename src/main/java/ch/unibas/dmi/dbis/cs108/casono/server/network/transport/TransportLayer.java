package ch.unibas.dmi.dbis.cs108.casono.server.network.transport;

import java.io.IOException;

/** Interface for transport layer implementations. */
public interface TransportLayer {
    /**
     * Reads data from the transport layer.
     *
     * @return the read data as a string
     * @throws IOException if an I/O error occurs
     */
    String read() throws IOException;

    /**
     * Writes data to the transport layer.
     *
     * @param data the data to write
     * @throws IOException if an I/O error occurs
     */
    void write(String data) throws IOException;

    /**
     * Closes the transport layer.
     *
     * @throws IOException if an I/O error occurs
     */
    void close() throws IOException;
}
