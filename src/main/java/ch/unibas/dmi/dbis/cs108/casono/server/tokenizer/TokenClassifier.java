package ch.unibas.dmi.dbis.cs108.casono.server.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class TokenClassifier {
    public static List<Token> classify(List<RawToken> rawTokens) {
        validateFirstToken(rawTokens);

        List<Token> tokens = new ArrayList<>();

        for (int i = 0; i < rawTokens.size(); i++) {
            RawToken raw = rawTokens.get(i);

            switch (raw.type()) {
                case SEPARATOR -> {
                    validateSeparator(rawTokens, i);
                    tokens.add(
                            new Token(TokenType.SEPARATOR, raw.value(), raw.line(), raw.column()));
                }
                case WORD -> {
                    TokenType type = resolveWordType(rawTokens, i);
                    tokens.add(new Token(type, raw.value(), raw.line(), raw.column()));
                }
                case STRING -> {
                    validateString(rawTokens, i);
                    tokens.add(new Token(TokenType.VALUE, raw.value(), raw.line(), raw.column()));
                }

                case NEWLINE -> {
                    throw new TokenizerException(
                            "Unexpected newline in request", raw.line(), raw.column());
                }

                case EOF -> {
                    tokens.add(new Token(TokenType.EOF, raw.value(), raw.line(), raw.column()));
                }
            }
        }

        return tokens;
    }

    private static void validateFirstToken(List<RawToken> rawTokens) {
        if (rawTokens.isEmpty() || rawTokens.get(0).type() != RawTokenType.WORD) {
            throw new TokenizerException("Expected COMMAND as first token", 1, 1);
        }
    }

    private static void validateSeparator(List<RawToken> rawTokens, int index) {
        boolean missingKey = index == 0 || rawTokens.get(index - 1).type() != RawTokenType.WORD;

        boolean missingValue =
                index + 1 >= rawTokens.size()
                        || (rawTokens.get(index + 1).type() != RawTokenType.WORD
                                && rawTokens.get(index + 1).type() != RawTokenType.STRING);

        boolean nextWordIsKey =
                !missingValue
                        && rawTokens.get(index + 1).type() == RawTokenType.WORD
                        && index + 2 < rawTokens.size()
                        && rawTokens.get(index + 2).type() == RawTokenType.SEPARATOR;

        RawToken separator = rawTokens.get(index);

        if (missingKey) {
            throw new TokenizerException(
                    "Expected KEY before '='", separator.line(), separator.column());
        }

        if (missingValue || nextWordIsKey) {
            throw new TokenizerException(
                    "Expected VALUE after '='", separator.line(), separator.column());
        }
    }

    private static void validateString(List<RawToken> rawTokens, int index) {
        boolean afterSeparator =
                index > 0 && rawTokens.get(index - 1).type() == RawTokenType.SEPARATOR;

        if (!afterSeparator) {
            RawToken token = rawTokens.get(index);
            throw new TokenizerException("Unexpected string literal", token.line(), token.column());
        }
    }

    private static TokenType resolveWordType(List<RawToken> rawTokens, int index) {
        boolean isFirst = index == 0;
        boolean afterNewline = index > 0 && rawTokens.get(index - 1).type() == RawTokenType.NEWLINE;
        boolean afterSeparator =
                index > 0 && rawTokens.get(index - 1).type() == RawTokenType.SEPARATOR;

        if (isFirst || afterNewline) {
            return TokenType.COMMAND;
        }

        if (afterSeparator) {
            return TokenType.VALUE;
        }

        return TokenType.KEY;
    }
}
