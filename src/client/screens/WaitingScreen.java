package client.screens;

import Models.Message;
import client.ClientConnection;
import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class WaitingScreen {

    private final Stage stage;
    private final String username;
    private final String avatar;
    private final String map;
    private final ClientConnection conexion;
    private static final Gson gson = new Gson();

    public WaitingScreen(Stage stage, String username, String avatar,
                         String map, ClientConnection conexion) {
        this.stage    = stage;
        this.username = username;
        this.avatar   = avatar;
        this.map      = map;
        this.conexion = conexion;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: black;");

        Label title = new Label("BUSCANDO OPONENTE...");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        title.setTextFill(Color.GREEN);

        Label info = new Label("Usuario: " + username + "   |   Avatar: " + avatar);
        info.setFont(Font.font("Verdana", 13));
        info.setTextFill(Color.GRAY);

        Label mapLabel = new Label("Mapa: " + map);
        mapLabel.setFont(Font.font("Verdana", 12));
        mapLabel.setTextFill(Color.web("#58a6ff"));

        Label waiting = new Label("Esperando segundo jugador...");
        waiting.setFont(Font.font("Verdana", 14));
        waiting.setTextFill(Color.web("#c9d1d9"));

        root.getChildren().addAll(title, info, mapLabel, waiting);
        stage.setScene(new Scene(root, 500, 320));

        // Mandar FIND_MATCH al servidor
        new Thread(() -> {
            try {
                Message msg = new Message();
                msg.type     = "FIND_MATCH";
                msg.username = username;
                conexion.sendOnly(msg); // solo manda, no espera respuesta aquí

                // Escuchar hasta recibir MATCH_FOUND + CONFIG
                Message matchFound = null;
                Message config     = null;

                while (matchFound == null || config == null) {
                    Message incoming = conexion.readMessage();
                    if (incoming == null) break;

                    if ("MATCH_FOUND".equals(incoming.type)) {
                        matchFound = incoming;
                        Platform.runLater(() ->
                                waiting.setText("Oponente encontrado! Cargando..."));
                    }
                    if ("CONFIG".equals(incoming.type)) {
                        config = incoming;
                    }
                }

                if (matchFound != null && config != null) {
                    final Message finalConfig     = config;
                    final Message finalMatchFound = matchFound;
                    Platform.runLater(() -> {
                        GameScreen game = new GameScreen(
                                stage, username, avatar, map,
                                conexion, finalMatchFound.opponentUsername, finalConfig
                        );
                        game.show();
                    });
                }

            } catch (Exception e) {
                Platform.runLater(() ->
                        waiting.setText("Error de conexion: " + e.getMessage()));
            }
        }).start();
    }
}
