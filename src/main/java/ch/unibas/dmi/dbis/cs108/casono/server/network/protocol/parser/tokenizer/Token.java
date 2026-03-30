package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer;

/** Represents a token in the tokenizer. */
public record Token(TokenType type, String value, int line, int column) {}
