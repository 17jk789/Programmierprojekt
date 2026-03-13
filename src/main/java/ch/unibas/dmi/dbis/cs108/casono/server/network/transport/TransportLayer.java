package ch.unibas.dmi.dbis.cs108.casono.server.network.transport;

import java.io.IOException;

public interface TransportLayer {
    String read() throws IOException;
    void write(String data) throws IOException;
    void close() throws IOException;
}
