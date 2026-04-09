package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ChatTest {

    @Test
    public void chatTest() {
        List<String> list = new ArrayList<String>();
        list.add("COUNT=19199");
        list.add("TEXT='test \\' test'");
        list.add("TEXT='%/§§%&&/%=%$/%))==/?``*\\'\\'**\\'\\'§?'");
        list.add("TEXT='Hello World'");
        list.add("NUMBER=-56887387394898392849");

        List<RequestParameter> newList = ClientService.convertToRequestParameters(list);
        RequestParameter par0 = newList.get(0);
        assertEquals("COUNT", par0.key());
        assertEquals("19199", par0.value());

        RequestParameter par1 = newList.get(1);
        assertEquals("TEXT", par1.key());
        assertEquals("test ' test", par1.value());

        RequestParameter par2 = newList.get(2);
        assertEquals("TEXT", par2.key());
        assertEquals("%/§§%&&/%=%$/%))==/?``*''**''§?", par2.value());

        RequestParameter par4 = newList.get(4);
        assertEquals("NUMBER", par4.key());
        assertEquals("-56887387394898392849", par4.value());
    }
}
