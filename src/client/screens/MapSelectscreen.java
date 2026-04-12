package client.screens;

import client.ClientConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class MapSelectscreen 
{

    private final Stage stage;
    private final String username;
    private final String avatar;
    private final ClientConnection conexion;

    public MapSelectscreen(Stage stage, String username, String avatar, ClientConnection conexion) {
        this.stage = stage;
        this.username = username;
        this.avatar = avatar;
        this.conexion = conexion;
    }

    public void show() {
        VBox root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: black;");

        Label title = new Label("SELECCIONA EL MAPA");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        title.setTextFill(Color.GREEN);

        // Mapa 1 — verde
        java.awt.Button btnMapa1 = new Button("Data Center Dojo");
        btnMapa1.setPrefSize(160, 80);
        btnMapa1.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        btnMapa1.setStyle(
                "-fx-background-color: #213147;" +
                        "-fx-text-fill: #07fa89;" +
                        "-fx-border-color: #07fa89;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;"
        );

        // Mapa 2 — azul
        Button btnMapa2 = new Button("Packet Bay Carnival");
        btnMapa2.setPrefSize(160, 80);
        btnMapa2.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        btnMapa2.setStyle(
                "-fx-background-color: #213147;" +
                        "-fx-text-fill: #58a6ff;" +
                        "-fx-border-color: #58a6ff;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;"

        btnMapa1.setOnAction(e -> goToGame("Mapa1"));   // Acción para el botón del Mapa 1
        btnMapa2.setOnAction(e -> goToGame("Mapa 2"));      // Acción para el botón del Mapa 2
        );



        HBox botones = new HBox(20);
        botones.setAlignment(Pos.CENTER);
        botones.getChildren().addAll(btnMapa1, btnMapa2);

        root.getChildren().addAll(title, botones);
        stage.setScene(new Scene(root, 480, 260));
    }

    private void goToGame(String mapa) // Método para iniciar el juego con el mapa seleccionado
    {
        GameScreen gameScreen = new GameScreen(stage, username, avatar, mapa, conexion);    // Crear una nueva instancia de GameScreen con la información del usuario, el mapa seleccionado y la conexión al servidor
        gameScreen.show();
    }

}
