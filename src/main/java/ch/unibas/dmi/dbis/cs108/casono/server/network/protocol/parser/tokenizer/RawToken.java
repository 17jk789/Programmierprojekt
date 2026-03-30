package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer;

/** Represents a raw (unclassified) token in the tokenizer. */
public record RawToken(RawTokenType type, String value, int line, int column) {}
