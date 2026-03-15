package ch.unibas.dmi.dbis.cs108.casono.server.tokenizer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TokenizerExceptionTest {
    @Test
    void testConstructorAndGetters() {
        String message = "Test error";
        int line = 5;
        int column = 10;
        TokenizerException e = new TokenizerException(message, line, column);
        assertEquals(message, e.getMessage());
        assertEquals(line, e.getLine());
        assertEquals(column, e.getColumn());
    }
}
