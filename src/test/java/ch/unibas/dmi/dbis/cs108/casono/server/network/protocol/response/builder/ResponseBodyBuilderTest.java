package ch.unibas.dmi.dbis.cs108.casono.server.network.protocol.response.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ResponseBodyBuilderTest {
    @Test
    void testSingleParameter() {
        ResponseBody body = ResponseBody.builder().param("param1", "value1").build();

        assertEquals(1, body.nodes().size());
        ResponseParameter parameter = (ResponseParameter) body.nodes().get(0);
        assertEquals("param1", parameter.key());
        assertEquals("value1", parameter.value());
    }

    @Test
    void testSingleBlock() {
        ResponseBody body =
                ResponseBody.builder()
                        .block("block1", block -> block.param("param1", "value1"))
                        .build();

        assertEquals(1, body.nodes().size());

        ResponseBlock block = (ResponseBlock) body.nodes().get(0);
        assertEquals(1, block.children().size());
        assertEquals("block1", block.tag());

        ResponseParameter parameter = (ResponseParameter) block.children().get(0);
        assertEquals("param1", parameter.key());
        assertEquals("value1", parameter.value());
    }

    @Test
    void testMultipleParameter() {
        ResponseBody body =
                ResponseBody.builder().param("param1", "value1").param("param2", "value2").build();

        assertEquals(2, body.nodes().size());
        ResponseParameter parameter;

        parameter = (ResponseParameter) body.nodes().get(0);
        assertEquals("param1", parameter.key());
        assertEquals("value1", parameter.value());

        parameter = (ResponseParameter) body.nodes().get(1);
        assertEquals("param2", parameter.key());
        assertEquals("value2", parameter.value());
    }

    @Test
    void testMultipleBlocks() {
        ResponseBody body =
                ResponseBody.builder()
                        .block("block1", block -> block.param("param1", "value1"))
                        .block(
                                "block2",
                                block -> {
                                    block.param("param2", "value2");
                                    block.param("param3", "value3");
                                })
                        .build();

        assertEquals(2, body.nodes().size());
        ResponseBlock block;
        ResponseParameter parameter;

        block = (ResponseBlock) body.nodes().get(0);
        assertEquals("block1", block.tag());
        assertEquals(1, block.children().size());
        parameter = (ResponseParameter) block.children().get(0);
        assertEquals("param1", parameter.key());
        assertEquals("value1", parameter.value());

        block = (ResponseBlock) body.nodes().get(1);
        assertEquals("block2", block.tag());
        assertEquals(2, block.children().size());
        parameter = (ResponseParameter) block.children().get(0);
        assertEquals("param2", parameter.key());
        assertEquals("value2", parameter.value());
    }
}
