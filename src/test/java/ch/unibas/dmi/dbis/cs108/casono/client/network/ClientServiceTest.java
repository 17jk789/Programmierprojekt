package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.command.parsing.RequestParameter;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ClientServiceTest {


    @Test
    public void parsingResponse() {
        ArrayList<RequestParameter> params = new ArrayList<>();

        params.add(new RequestParameter("KEY", "VALUE"));
        params.add(new RequestParameter("STANDARD_TEXT", "Lorem ipsum dolor sit amet, consectetur adipiscing elit"));
        params.add(new RequestParameter("TEXT_BASED_EMOJI_WITHOUT_QUOTATION_MARKS", ";-)"));
        params.add(new RequestParameter("TEXT_WITH_QUOTATION_MARKS", "SOMEBODY SAYS: 'HALLO WELT'"));
        params.add(new RequestParameter("NUMBERS", "123456789"));
        params.add(new RequestParameter("SOME_SPECIAL_CHARACTERS", "°^!§$%&/()=[]}?*+~'#`"));
        params.add(new RequestParameter("WRONG_CHARACTERS", "# * ~ +"));
        params.add(new RequestParameter("WRONG_TEXT", "HELLO WORLD"));

        ArrayList<String> paramsAsResponseLines =  new ArrayList<>();

        for (RequestParameter parameter : params) {
            if (!parameter.key().equals("WRONG_TEXT") && !parameter.key().equals("WRONG_CHARACTERS") && parameter.value().contains(" ")) {
                String newValue = "'" + parameter.value() + "'";
                paramsAsResponseLines.add(String.format("%s=%s", parameter.key(), newValue));
            } else {
                paramsAsResponseLines.add(String.format("%s=%s", parameter.key(), parameter.value()));
            }
        }

        assertEquals(8, paramsAsResponseLines.size());


        List<RequestParameter> msgRes = new ArrayList<>(ClientService.convertToRequestParameters(paramsAsResponseLines));

        assertEquals(6, msgRes.size());

        for (int i = 0; i < 6; i++) {
            assertEquals(params.get(i), msgRes.get(i));
        }

        assert(!msgRes.contains(params.get(6)));
        assert(!msgRes.contains(params.get(7)));
    }
}
