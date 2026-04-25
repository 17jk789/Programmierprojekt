package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TokenClassifierTest {
    @Test
    void testClassifySimpleCommand() {
        List<RawToken> raw = Tokenizer.tokenize("PING");
        List<Token> tokens = TokenClassifier.classify(raw);

        assertEquals(2, tokens.size());
        assertEquals(TokenType.COMMAND, tokens.get(0).type());
        assertEquals("PING", tokens.get(0).value());
        assertEquals(TokenType.EOF, tokens.get(1).type());
    }

    @Test
    void testCommandWithParameter() {
        List<RawToken> raw = Tokenizer.tokenize("ANSWER VALUE=42");
        List<Token> tokens = TokenClassifier.classify(raw);

        assertEquals(5, tokens.size());
        assertEquals(TokenType.COMMAND, tokens.get(0).type());
        assertEquals(TokenType.KEY, tokens.get(1).type());
        assertEquals(TokenType.SEPARATOR, tokens.get(2).type());
        assertEquals(TokenType.VALUE, tokens.get(3).type());
        assertEquals("42", tokens.get(3).value());
        assertEquals(TokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testStringValue() {
        List<RawToken> raw = Tokenizer.tokenize("GREET MSG='Hello World'");
        List<Token> tokens = TokenClassifier.classify(raw);

        assertEquals(5, tokens.size());
        assertEquals(TokenType.COMMAND, tokens.get(0).type());
        assertEquals(TokenType.KEY, tokens.get(1).type());
        assertEquals(TokenType.SEPARATOR, tokens.get(2).type());
        assertEquals(TokenType.VALUE, tokens.get(3).type());
        assertEquals("Hello World", tokens.get(3).value());
    }

    @Test
    void testUnexpectedStringLiteralThrows() {
        List<RawToken> raw = Tokenizer.tokenize("CMD 'oops'");

        TokenizerException ex =
                assertThrows(TokenizerException.class, () -> TokenClassifier.classify(raw));
        assertTrue(ex.getMessage().contains("Unexpected string literal"));
    }

    @Test
    void testMissingValueAfterSeparatorThrows() {
        List<RawToken> raw = Tokenizer.tokenize("CMD KEY=");

        TokenizerException ex =
                assertThrows(TokenizerException.class, () -> TokenClassifier.classify(raw));
        assertEquals("Expected VALUE after '='", ex.getMessage());
    }

    @Test
    void testNextWordIsKeyThrows() {
        List<RawToken> raw = Tokenizer.tokenize("CMD KEY1=KEY2=42");

        TokenizerException ex =
                assertThrows(TokenizerException.class, () -> TokenClassifier.classify(raw));
        assertEquals("Expected VALUE after '='", ex.getMessage());
    }

    @Test
    void testEmptyRawTokensThrows() {
        List<RawToken> raw = new ArrayList<>();

        TokenizerException ex =
                assertThrows(TokenizerException.class, () -> TokenClassifier.classify(raw));
        assertEquals("Expected COMMAND as first token", ex.getMessage());
        assertEquals(1, ex.getLine());
        assertEquals(1, ex.getColumn());
    }
}
