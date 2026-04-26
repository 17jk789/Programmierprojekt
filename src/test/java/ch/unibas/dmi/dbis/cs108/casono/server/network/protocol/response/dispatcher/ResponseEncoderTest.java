package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.dispatcher;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.request.RequestContext;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.PrimitiveResponse;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.Response;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBlock;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseBody;
import ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder.ResponseParameter;
import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class ResponseEncoderTest {
    private class TestResponse extends Response {
        public TestResponse(RequestContext context, ResponseBody body) {
            super(context, body);
        }

        @Override
        public String prefix() {
            return "+OK";
        }
    }

    @Test
    void testEncodeParameterSimple() {
        ResponseBody body = ResponseBody.builder().param("param", "value").build();

        RequestContext ctx = new RequestContext(new SessionId(), 42);
        Response response = new TestResponse(ctx, body);

        PrimitiveResponse pr = ResponseEncoder.encode(response);

        String expected = "+OK\n\tparam=value\nEND";
        assertEquals(expected, pr.payload());
    }

    @Test
    void testEncodeParameterMasking() {
        ResponseBody body = ResponseBody.builder().param("msg", "It's a me, malario").build();

        RequestContext ctx = new RequestContext(new SessionId(), 42);
        Response response = new TestResponse(ctx, body);

        PrimitiveResponse pr = ResponseEncoder.encode(response);
        String expected = "+OK\n\tmsg='It\\'s a me, malario'\nEND";
        assertEquals(expected, pr.payload());
    }

    @Test
    void testEncodeNestedBlock() {
        ResponseBody body =
                ResponseBody.builder().block("TAG", b -> b.param("param", "key")).build();

        RequestContext ctx = new RequestContext(new SessionId(UUID.randomUUID()), 42);
        Response response = new TestResponse(ctx, body);

        PrimitiveResponse pr = ResponseEncoder.encode(response);

        String expected = "+OK\n\tTAG\n\t\tparam=key\n\tEND\nEND";
        assertEquals(expected, pr.payload());
    }

    @Test
    void testPrivateEncodeParameter()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        var param = new ResponseParameter("p", "v");
        StringBuilder sb = new StringBuilder();

        Method m =
                ResponseEncoder.class.getDeclaredMethod(
                        "encodeParameter", ResponseParameter.class, StringBuilder.class, int.class);
        m.setAccessible(true);
        m.invoke(null, param, sb, 1);

        assertEquals("\tp=v", sb.toString());
    }

    @Test
    void testWordNoMask()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method m = ResponseEncoder.class.getDeclaredMethod("maskIfNeeded", String.class);
        m.setAccessible(true);

        String noMask = (String) m.invoke(null, "abc");
        assertEquals("abc", noMask);
    }

    @Test
    void testMaskString()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method m = ResponseEncoder.class.getDeclaredMethod("maskIfNeeded", String.class);
        m.setAccessible(true);

        String withSpace = (String) m.invoke(null, "a b");
        assertEquals("'a b'", withSpace);
    }

    @Test
    void testEscapeSingleQuote()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method m = ResponseEncoder.class.getDeclaredMethod("maskIfNeeded", String.class);
        m.setAccessible(true);

        String withSpace = (String) m.invoke(null, "a b");
        assertEquals("'a b'", withSpace);
    }

    @Test
    void testPrivateEncodeBlock()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        var child = new ResponseParameter("a", "b");
        var block = new ResponseBlock("TAG", List.of(child));
        StringBuilder sb = new StringBuilder();

        Method m =
                ResponseEncoder.class.getDeclaredMethod(
                        "encodeBlock", ResponseBlock.class, StringBuilder.class, int.class);
        m.setAccessible(true);
        m.invoke(null, block, sb, 1);

        String expected = "\tTAG\n\t\ta=b\n\tEND";
        assertEquals(expected, sb.toString());
    }
}
