package client;

import client.screens.LoginScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class GameClient extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Space Invaders Game");
        primaryStage.setResizable(false);

        //Crea la pantalla login pasandole la ventana principal
        LoginScreen loginScreen = new LoginScreen(primaryStage);
        loginScreen.show();

    }
    public static void main(String[] args) { launch(args); }
}
