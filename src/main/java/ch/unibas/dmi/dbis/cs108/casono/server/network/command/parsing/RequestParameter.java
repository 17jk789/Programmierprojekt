package ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing;

/** Used in the PrimitiveRequest class to store the key of a parameter with its respective value */
public record RequestParameter(String key, String value) {}
