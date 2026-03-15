package ch.unibas.dmi.dbis.cs108.casono.server.network.transport;

/**
 * Transport object, created by the transport layer to store raw data about the request
 * prior to any processing
 */
public record RawPacket(int requestId, String payload) {}
