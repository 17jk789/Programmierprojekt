package ch.unibas.dmi.dbis.cs108.casono.server.network.response;

import ch.unibas.dmi.dbis.cs108.casono.server.network.sessions.SessionId;

public class ErrorResponse extends Response {
    public ErrorResponse(SessionId sessionId, int requestId, String errorCode, String errorMessage) {
        super(sessionId, requestId, ResponseBody.builder()
            .param("CODE", errorCode)
            .param("MSG", errorMessage)
            .build()
        );
    }

    @Override
    public String prefix() {
        return "-ERR";
    }
}
