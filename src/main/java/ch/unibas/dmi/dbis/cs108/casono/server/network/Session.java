package ch.unibas.dmi.dbis.cs108.casono.server.network;

public interface Session {
    /**
     * Outlines an interface all session implementations have to conform to
 */

    SessionId id();

    void send(byte[] payload);

    void close();
}