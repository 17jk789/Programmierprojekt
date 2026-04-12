package ch.unibas.dmi.dbis.cs108.casono.client.network;

import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.RawPacket;
import ch.unibas.dmi.dbis.cs108.casono.server.network.transport.TcpTransport;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Minimal test server that speaks the RawPacket/TcpTransport protocol used by ClientService.
 * Provides deterministic responses for CREATE_LOBBY and GET_LOBBY_STATUS so unit tests can run
 * without a real backend.
 */
public class TestServer implements AutoCloseable {
    private final ServerSocket serverSocket;
    private final ExecutorService exec = Executors.newSingleThreadExecutor();
    private final AtomicInteger nextLobbyId = new AtomicInteger(1);
    private final Map<Integer, String> lobbyStatus = new ConcurrentHashMap<>();
    private volatile boolean running = true;

    public TestServer() throws IOException {
        this.serverSocket = new ServerSocket(0);
        exec.submit(this::acceptLoop);
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    private void acceptLoop() {
        try (Socket client = serverSocket.accept()) {
            TcpTransport transport = new TcpTransport(client);
            while (running) {
                RawPacket request = transport.read();
                String payload = request.payload();
                String response = handleRequest(payload);
                // send the response payload followed by +OK
                transport.write(new RawPacket(request.requestId(), response));
                transport.write(new RawPacket(request.requestId(), "+OK"));
            }
        } catch (IOException e) {
            if (running) {
                throw new RuntimeException(e);
            }
        }
    }

    private String handleRequest(String payload) {
        if (payload == null) {
            return "";
        }
        if (payload.startsWith("CREATE_LOBBY")) {
            int id = nextLobbyId.getAndIncrement();
            lobbyStatus.put(id, "created");
            return Integer.toString(id);
        }
        if (payload.startsWith("GET_LOBBY_STATUS")) {
            // payload format: GET_LOBBY_STATUS ID=<id>
            String[] parts = payload.split("ID=");
            if (parts.length == 2) {
                try {
                    int id = Integer.parseInt(parts[1].trim());
                    return lobbyStatus.getOrDefault(id, "created");
                } catch (NumberFormatException e) {
                    return "created";
                }
            }
            return "created";
        }
        if (payload.startsWith("JOIN_LOBBY")) {
            // no-op
            return "";
        }
        return "";
    }

    @Override
    public void close() throws Exception {
        running = false;
        serverSocket.close();
        exec.shutdownNow();
    }
}
