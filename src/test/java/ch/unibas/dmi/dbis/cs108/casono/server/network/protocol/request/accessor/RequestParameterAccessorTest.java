package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.accessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.List;
import org.junit.jupiter.api.Test;

public class RequestParameterAccessorTest {
    @Test
    void testRequiredParameterPresent() {
        List<RequestParameter> parameters = List.of(new RequestParameter("ARG1", "VAL1"));
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertEquals("VAL1", accessor.require("ARG1"));
    }

    @Test
    void testMissingRequiredParameterThrows() {
        List<RequestParameter> parameters = List.of();
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        MissingParameterException ex =
                assertThrows(MissingParameterException.class, () -> accessor.require("ARG1"));
        assertEquals("Required parameter with key 'ARG1' is missing.", ex.getMessage());
        assertEquals("ARG1", ex.getParameterKey());
    }

    @Test
    void testRequiredParameterSuccessfulParsing() {
        List<RequestParameter> parameters = List.of(new RequestParameter("ARG1", "42"));
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertEquals(42, (int) accessor.require("ARG1", Integer::valueOf));
    }

    @Test
    void testRequiredParameterInvalidParsingThrows() {
        List<RequestParameter> parameters =
                List.of(new RequestParameter("ARG1", "The answer is: 42"));
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        ParameterParseException ex =
                assertThrows(
                        ParameterParseException.class,
                        () -> accessor.require("ARG1", Integer::valueOf));
        assertEquals("Error while parsing 'ARG1' with specified parser", ex.getMessage());
        assertEquals("ARG1", ex.getParameterKey());
    }

    @Test
    void testOptionalParameterPresent() {
        List<RequestParameter> parameters = List.of(new RequestParameter("ARG1", "VAL1"));
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertEquals("VAL1", accessor.optional("ARG1", "DEFAULT"));
    }

    @Test
    void testOptionalParameterMissing() {
        List<RequestParameter> parameters = List.of();
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertEquals("DEFAULT", accessor.optional("ARG1", "DEFAULT"));
    }

    @Test
    void testOptionalParameterSuccessfulParsing() {
        List<RequestParameter> parameters = List.of(new RequestParameter("ARG1", "42"));
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertEquals(42, (int) accessor.optional("ARG1", 7411, Integer::valueOf));
    }

    @Test
    void testMissingOptionalParameterSuccessfulParsing() {
        List<RequestParameter> parameters = List.of();
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertEquals(7411, (int) accessor.optional("ARG1", 7411, Integer::valueOf));
    }

    @Test
    void testOptionalParameterInvalidParsingThrows() {
        List<RequestParameter> parameters =
                List.of(new RequestParameter("ARG1", "The answer is: 42"));
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        ParameterParseException ex =
                assertThrows(
                        ParameterParseException.class,
                        () -> accessor.optional("ARG1", 7411, Integer::valueOf));
        assertEquals("Error while parsing 'ARG1' with specified parser", ex.getMessage());
        assertEquals("ARG1", ex.getParameterKey());
    }

    @Test
    void testContainsExistingKey() {
        List<RequestParameter> parameters = List.of(new RequestParameter("ARG1", "VAL1"));
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertTrue(accessor.containsKey("ARG1"));
    }

    @Test
    void testContainsMissingKey() {
        List<RequestParameter> parameters = List.of();
        RequestParameterAccessor accessor = new RequestParameterAccessor(parameters);

        assertFalse(accessor.containsKey("ARG1"));
    }
}
