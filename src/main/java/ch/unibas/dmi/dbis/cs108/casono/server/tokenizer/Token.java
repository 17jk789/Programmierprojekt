package ch.unibas.dmi.dbis.cs108.casono.server.tokenizer;

/** Represents a token in the tokenizer. */
public record Token(TokenType type, String value, int line, int column) {}
