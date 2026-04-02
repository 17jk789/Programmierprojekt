package ch.unibas.dmi.dbis.cs108.casono.client.network;

public class CoreClient {
    private final ClientService clientService;

    public CoreClient(ClientService clientservice) {
        this.clientService = clientservice;
    }

    public void ping() {
        clientService.processCommand("PING");
    }

    public void login(String user) {
        clientService.processCommand("LOGIN USERNAME=" + user);
    }
}