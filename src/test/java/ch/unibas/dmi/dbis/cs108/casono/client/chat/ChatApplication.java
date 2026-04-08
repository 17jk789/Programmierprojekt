package ch.unibas.dmi.dbis.cs108.casono.client.chat;

import ch.unibas.dmi.dbis.cs108.casono.client.network.ClientService;
import ch.unibas.dmi.dbis.cs108.casono.client.network.CoreClient;
import ch.unibas.dmi.dbis.cs108.casono.client.ui.chatui.ChatBoxController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatApplication extends Application {

    private static final int SCENE_WIDTH = 1200;
    private static final int SCENE_HEIGHT = 800;
    String ip = "localhost";
    String username = "mathis";
    int port = 5000;

    public ChatApplication() {}

    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ui-structure/components/chatui/chatbox.fxml"));
        ClientService clientService = new ClientService(ip, port);
        CoreClient coreClient = new CoreClient(clientService);
        // TODO login UI
        coreClient.login(username);
        ChatController chatController = new ChatController(username, clientService);
        ChatBoxController chatBoxController = new ChatBoxController(username, chatController);
        fxmlLoader.setController(chatBoxController);
        /*fxmlLoader.setControllerFactory(type -> {
                if (type == ChatBoxController.class) {
                    return chatController.getChatBoxController();
                } else {
                    try {
                        return type.getConstructor().newInstance();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
        });*/

        Scene scene = new Scene(fxmlLoader.load(), SCENE_WIDTH, SCENE_HEIGHT);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }

}
