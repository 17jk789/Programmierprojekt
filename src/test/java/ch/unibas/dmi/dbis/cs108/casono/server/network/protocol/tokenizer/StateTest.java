package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.tokenizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StateTest {
    @Test
    void testConstructor() {
        String input = "hello";
        State state = new State(input);
        assertEquals(input, state.input);
        assertEquals(0, state.pos);
        assertEquals(1, state.line);
        assertEquals(1, state.column);
        assertTrue(state.tokens.isEmpty());
    }

    @Test
    void testCurrent() {
        String input = "abc";
        State state = new State(input);
        assertEquals('a', state.current());
        state.advance();
        assertEquals('b', state.current());
    }

    @Test
    void testPeek() {
        String input = "abc";
        State state = new State(input);
        assertEquals('b', state.peek());
        state.advance();
        assertEquals('c', state.peek());
        state.advance();
        assertEquals('\0', state.peek()); // End of input
    }

    @Test
    void testAdvance() {
        String input = "abc";
        State state = new State(input);
        assertEquals(0, state.pos);
        assertEquals(1, state.column);
        state.advance();
        assertEquals(1, state.pos);
        assertEquals(2, state.column);
    }

    @Test
    void testIsEof() {
        String input = "a";
        State state = new State(input);
        assertFalse(state.isEof());
        state.advance();
        assertTrue(state.isEof());
    }
}
