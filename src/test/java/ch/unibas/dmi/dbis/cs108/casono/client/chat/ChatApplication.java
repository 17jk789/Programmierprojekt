package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.CoreClient;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatBoxController;
import java.io.IOException;
import java.util.List;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApplication extends Application {

    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 800;

    public ChatApplication() {}

    public void start(Stage stage) throws IOException {
        List<String> params = getParameters().getRaw();
        String ip = params.get(0);
        int port = Integer.parseInt(params.get(1));
        String username = params.get(2);
        FXMLLoader fxmlLoader =
                new FXMLLoader(
                        getClass().getResource("/ui-structure/components/chatui/chatbox.fxml"));
        ClientService clientService = new ClientService(ip, port);
        CoreClient coreClient = new CoreClient(clientService);
        coreClient.login(username);
        ChatController chatController = new ChatController(username, clientService);
        ChatBoxController chatBoxController = chatController.getChatBoxController();
        fxmlLoader.setController(chatBoxController);
        Parent node = fxmlLoader.load();
        Scene scene = new Scene(node, SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }
}
