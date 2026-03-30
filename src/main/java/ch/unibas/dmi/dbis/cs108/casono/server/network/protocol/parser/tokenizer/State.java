package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser.tokenizer;

import java.util.ArrayList;
import java.util.List;

/** Internal state for the tokenizer. */
class State {
    final String input;
    int pos;
    int line;
    int column;
    final List<RawToken> tokens;

    State(String input) {
        this.input = input;
        this.pos = 0;
        this.line = 1;
        this.column = 1;
        this.tokens = new ArrayList<>();
    }

    char current() {
        return input.charAt(pos);
    }

    char peek() {
        if (pos + 1 >= input.length()) {
            return '\0';
        }
        return input.charAt(pos + 1);
    }

    void advance() {
        pos++;
        column++;
    }

    boolean isEof() {
        return pos >= input.length();
    }
}
