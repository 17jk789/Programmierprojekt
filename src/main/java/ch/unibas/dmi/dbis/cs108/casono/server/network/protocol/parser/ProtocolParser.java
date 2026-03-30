package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.parser;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RawRequest;
import ch.unibas.dmi.dbis.cs108.casono.server.tokenizer.RawToken;
import ch.unibas.dmi.dbis.cs108.casono.server.tokenizer.Token;
import ch.unibas.dmi.dbis.cs108.casono.server.tokenizer.TokenClassifier;
import ch.unibas.dmi.dbis.cs108.casono.server.tokenizer.TokenType;
import ch.unibas.dmi.dbis.cs108.casono.server.tokenizer.Tokenizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** ProtocolParser, used to parse RawPacket into a PrimitiveRequest */
public class ProtocolParser {
    /**
     * Parses the payload of the provided RawPacket
     *
     * @param packet the RawPacket containing the recieved data
     * @return created PrimitiveRequest
     */
    public static RawRequest parse(String payload) {
        List<RawToken> rawTokens = Tokenizer.tokenize(payload);
        List<Token> tokens = TokenClassifier.classify(rawTokens);

        Iterator<Token> iterator = tokens.iterator();
        String command = readCommand(iterator);
        List<RequestParameter> parameters = readParameters(iterator);

        return new RawRequest(command, parameters);
    }

    /**
     * Reads the first token, which is ensured to be the command of the request
     *
     * @param iterator
     * @return read command
     */
    private static String readCommand(Iterator<Token> iterator) {
        Token token = iterator.next();
        if (token.type() != TokenType.COMMAND) {
            throw new ProtocolParserException("Expected COMMAND got " + token.type());
        }

        return token.value();
    }

    /**
     * Iterates over the remaining tokens, calling helperfunctions to validate token type
     *
     * @param iterator
     * @return list containing all parsed parameters
     */
    private static List<RequestParameter> readParameters(Iterator<Token> iterator) {
        List<RequestParameter> parameters = new ArrayList<>();

        try {
            while (iterator.hasNext()) {
                Token nextToken = iterator.next();

                if (nextToken.type() == TokenType.EOF) {
                    break;
                }

                String key = readKey(nextToken);
                readSeperator(iterator.next());
                String value = readValue(iterator.next());

                parameters.add(new RequestParameter(key, value));
            }
        } catch (NoSuchElementException e) {
            throw new ProtocolParserException("Ran out of tokens while reading parameter");
        }

        return parameters;
    }

    /**
     * @param token the current token
     * @return
     */
    private static String readKey(Token token) {
        if (token.type() != TokenType.KEY) {
            throw new ProtocolParserException("Expected KEY got " + token.type());
        }

        return token.value();
    }

    /**
     * @param token the current token
     */
    private static void readSeperator(Token token) {
        if (token.type() != TokenType.SEPARATOR) {
            throw new ProtocolParserException("Expected SEPARATOR got " + token.type());
        }
    }

    /**
     * @param token the current token
     * @return
     */
    private static String readValue(Token token) {
        if (token.type() != TokenType.VALUE) {
            throw new ProtocolParserException("Expected VALUE got " + token.type());
        }

        return token.value();
    }
}
