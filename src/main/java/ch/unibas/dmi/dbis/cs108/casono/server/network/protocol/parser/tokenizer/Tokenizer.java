package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer;

import java.util.List;

/**
 * A static utility class for tokenizing input strings into a sequence of tokens.
 *
 * <p>Strings can be enclosed in single quotes and may contain escaped single quotes using a
 * backslash.
 */
public class Tokenizer {
    /**
     * Tokenizes the given input string into a list of tokens.
     *
     * @param input the string to tokenize
     * @return a list of tokens representing the input
     * @throws TokenizerException if the input contains unexpected characters, unterminated strings,
     *     or other syntax errors
     */
    public static List<RawToken> tokenize(String input) {
        State state = new State(input);

        while (!state.isEof()) {
            char c = state.current();

            if (c == ' ' || c == '\t') {
                state.advance();

            } else if (c == '\n') {
                readNewline(state);

            } else if (c == '=') {
                readSeparator(state);

            } else if (c == '\'') {
                readString(state);

            } else if (Character.isLetterOrDigit(c) || c == '_') {
                readWord(state);

            } else {
                throw new TokenizerException(
                        "Unexpected character '" + c + "'", state.line, state.column);
            }
        }

        state.tokens.add(new RawToken(RawTokenType.EOF, null, state.line, state.column));
        return state.tokens;
    }

    /**
     * Reads a newline character and creates a NEWLINE token.
     *
     * <p>Updates the line counter and resets the column to 1.
     *
     * @param state the current parsing state
     */
    private static void readNewline(State state) {
        state.tokens.add(new RawToken(RawTokenType.NEWLINE, "", state.line, state.column));
        state.advance();
        state.line++;
        state.column = 1;
    }

    /**
     * Reads a separator character ('=') and creates a SEPARATOR token.
     *
     * @param state the current parsing state
     */
    private static void readSeparator(State state) {
        state.tokens.add(new RawToken(RawTokenType.SEPARATOR, "=", state.line, state.column));
        state.advance();
    }

    /**
     * Reads a word (sequence of alphanumeric characters and underscores) and creates an appropriate
     * token (COMMAND, KEY, or VALUE) based on the parsing context.
     *
     * <p>The token type is determined by the {@link #resolveWordType(State)} method.
     *
     * @param state the current parsing state
     */
    private static void readWord(State state) {
        int startColumn = state.column;
        StringBuilder sb = new StringBuilder();

        while (!state.isEof()
                && (Character.isLetterOrDigit(state.current()) || state.current() == '_')) {
            sb.append(state.current());
            state.advance();
        }

        state.tokens.add(new RawToken(RawTokenType.WORD, sb.toString(), state.line, startColumn));
    }

    /**
     * Reads a string literal enclosed in single quotes and creates a VALUE token.
     *
     * <p>Supports escaped single quotes using backslash notation (\'). Newlines within strings are
     * properly tracked for line counting.
     *
     * @param state the current parsing state
     * @throws TokenizerException if the string literal is not terminated before end of input
     */
    private static void readString(State state) {
        int startColumn = state.column;
        state.advance();
        StringBuilder sb = new StringBuilder();

        while (true) {
            if (state.isEof()) {
                throw new TokenizerException(
                        "Unterminated string literal", state.line, startColumn);
            }

            char c = state.current();

            if (c == '\\' && state.peek() == '\'') {
                sb.append('\'');
                state.advance();
                state.advance();

            } else if (c == '\'') {
                state.advance();
                break;

            } else {
                if (c == '\n') {
                    state.line++;
                    state.column = 1;
                }
                sb.append(c);
                state.advance();
            }
        }

        state.tokens.add(new RawToken(RawTokenType.STRING, sb.toString(), state.line, startColumn));
    }
}
