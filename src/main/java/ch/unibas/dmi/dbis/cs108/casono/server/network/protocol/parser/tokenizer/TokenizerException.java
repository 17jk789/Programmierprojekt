package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer;

/** Exception thrown during tokenization. */
public class TokenizerException extends RuntimeException {
    private final int line;
    private final int column;

    public TokenizerException(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
