package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TokenizerTest {
    @Test
    void testEofAlwaysPresent() {
        List<RawToken> tokens = Tokenizer.tokenize("");

        assertEquals(1, tokens.size());
        assertEquals(RawTokenType.EOF, tokens.get(0).type());
    }

    @Test
    void testSimpleCommand() {
        List<RawToken> tokens = Tokenizer.tokenize("PING");
        assertEquals(2, tokens.size());
        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("PING", tokens.get(0).value());
        assertEquals(RawTokenType.EOF, tokens.get(1).type());
    }

    @Test
    void testSimpleCommandWithUnderscore() {
        List<RawToken> tokens = Tokenizer.tokenize("JOIN_GAME");
        assertEquals(2, tokens.size());
        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("JOIN_GAME", tokens.get(0).value());
        assertEquals(RawTokenType.EOF, tokens.get(1).type());
    }

    @Test
    void testCommandWithOneParameter() {
        List<RawToken> tokens = Tokenizer.tokenize("GET VALUE=42");
        assertEquals(5, tokens.size());

        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("GET", tokens.get(0).value());

        // Parameter
        assertEquals(RawTokenType.WORD, tokens.get(1).type());
        assertEquals("VALUE", tokens.get(1).value());

        assertEquals(RawTokenType.SEPARATOR, tokens.get(2).type());
        assertEquals("=", tokens.get(2).value());

        assertEquals(RawTokenType.WORD, tokens.get(3).type());
        assertEquals("42", tokens.get(3).value());

        assertEquals(RawTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testCommandWithOneParameterWhitespacesBetweenSeperator() {
        List<RawToken> tokens = Tokenizer.tokenize("GET VALUE = 42");
        assertEquals(5, tokens.size());

        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("GET", tokens.get(0).value());

        // Parameter
        assertEquals(RawTokenType.WORD, tokens.get(1).type());
        assertEquals("VALUE", tokens.get(1).value());

        assertEquals(RawTokenType.SEPARATOR, tokens.get(2).type());
        assertEquals("=", tokens.get(2).value());

        assertEquals(RawTokenType.WORD, tokens.get(3).type());
        assertEquals("42", tokens.get(3).value());

        assertEquals(RawTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testCommandWithOneParameterAndNewline() {
        List<RawToken> tokens = Tokenizer.tokenize("GET\nVALUE=42");
        assertEquals(6, tokens.size());

        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("GET", tokens.get(0).value());

        // Parameter
        assertEquals(RawTokenType.WORD, tokens.get(2).type());
        assertEquals("VALUE", tokens.get(2).value());

        assertEquals(RawTokenType.SEPARATOR, tokens.get(3).type());
        assertEquals("=", tokens.get(3).value());

        assertEquals(RawTokenType.WORD, tokens.get(4).type());
        assertEquals("42", tokens.get(4).value());

        assertEquals(RawTokenType.EOF, tokens.get(5).type());
    }

    @Test
    void testCommandWithStringParameter() {
        List<RawToken> tokens = Tokenizer.tokenize("GREET MSG='Hello World'");
        assertEquals(5, tokens.size());

        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("GREET", tokens.get(0).value());

        // First parameter
        assertEquals(RawTokenType.WORD, tokens.get(1).type());
        assertEquals("MSG", tokens.get(1).value());

        assertEquals(RawTokenType.SEPARATOR, tokens.get(2).type());
        assertEquals("=", tokens.get(2).value());

        assertEquals(RawTokenType.STRING, tokens.get(3).type());
        assertEquals("Hello World", tokens.get(3).value());

        assertEquals(RawTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testCommandWithMultilineStringParameter() {
        List<RawToken> tokens = Tokenizer.tokenize("GREET MSG='Hello\nWorld'");
        assertEquals(5, tokens.size());

        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("GREET", tokens.get(0).value());

        // First parameter
        assertEquals(RawTokenType.WORD, tokens.get(1).type());
        assertEquals("MSG", tokens.get(1).value());

        assertEquals(RawTokenType.SEPARATOR, tokens.get(2).type());
        assertEquals("=", tokens.get(2).value());

        assertEquals(RawTokenType.STRING, tokens.get(3).type());
        assertEquals("Hello\nWorld", tokens.get(3).value());

        assertEquals(RawTokenType.EOF, tokens.get(4).type());
    }

    @Test
    void testWhitespace() {
        List<RawToken> tokens = Tokenizer.tokenize("  \t  PING");
        assertEquals(2, tokens.size());
        assertEquals(RawTokenType.WORD, tokens.get(0).type());
        assertEquals("PING", tokens.get(0).value());
    }

    @Test
    void testStringWithEscapedQuote() {
        List<RawToken> tokens = Tokenizer.tokenize("WONDERFUL_GREETING MSG='it\\'s a wonderful day'");
        assertEquals(5, tokens.size());
        assertEquals(RawTokenType.STRING, tokens.get(3).type());
        assertEquals("it's a wonderful day", tokens.get(3).value());
    }

    @Test
    void testStringWithWronglyEscapedQuote() {
        TokenizerException ex = assertThrows(TokenizerException.class, () -> Tokenizer.tokenize("WONDERFUL_GREETING MSG='it\'s a wonderful day'"));
        assertEquals("Unterminated string literal", ex.getMessage());
    }

    @Test
    void testStringWithEscapedBackslash() {
        List<RawToken> tokens = Tokenizer.tokenize("EXECUTE_COMMAND CMD='\\whoami'");
        assertEquals(5, tokens.size());
        assertEquals(RawTokenType.STRING, tokens.get(3).type());
        assertEquals("\\whoami", tokens.get(3).value());
    }

    @Test
    void testColumnTracking() {
        List<RawToken> tokens = Tokenizer.tokenize("GET VAL=1");
        assertEquals(5, tokens.size());
        assertEquals(1, tokens.get(0).column());
        assertEquals(5, tokens.get(1).column());
        assertEquals(8, tokens.get(2).column());
        assertEquals(9, tokens.get(3).column());
    }

    @Test
    void testUnterminatedStringThrows() {
        TokenizerException ex = assertThrows(TokenizerException.class, () -> Tokenizer.tokenize("GREETING = 'unclosed"));
        assertEquals(1, ex.getLine());
        assertEquals(12, ex.getColumn());
        assertTrue(ex.getMessage().contains("Unterminated string literal"));
    }

    @Test
    void testUnexpectedCharacterThrows() {
        TokenizerException ex = assertThrows(TokenizerException.class, () -> Tokenizer.tokenize("CMD @ KEY=VALUE"));
        assertTrue(ex.getMessage().contains("Unexpected character '@'"));
        assertEquals(1, ex.getLine());
    }

    @Test
    void testEmptyStringValue() {
        List<RawToken> tokens = Tokenizer.tokenize("cmd = ''");
        assertEquals(4, tokens.size());
        assertEquals(RawTokenType.STRING, tokens.get(2).type());
        assertEquals("", tokens.get(2).value());
    }
}
