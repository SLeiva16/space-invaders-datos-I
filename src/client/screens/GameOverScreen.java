package client.screens;

import client.ClientConnection;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class GameOverScreen {

    private final Stage stage;
    private final String username;
    private final int finalScore;
    private final int blockedAttacks;
    private final ClientConnection conexion;

    public GameOverScreen(Stage stage, String username,
                          int finalScore, int blockedAttacks,
                          ClientConnection conexion) {
        this.stage = stage;
        this.username = username;
        this.finalScore = finalScore;
        this.blockedAttacks = blockedAttacks;
        this.conexion = conexion;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: black;");

        Label titulo = new Label("GAME OVER");
        titulo.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
        titulo.setTextFill(Color.web("#e63737"));

        Label jugador = new Label("Jugador: " + username);
        jugador.setFont(Font.font("Verdana", 16));
        jugador.setTextFill(Color.GRAY);

        Label scoreLbl = new Label("Score final: " + finalScore);
        scoreLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        scoreLbl.setTextFill(Color.web("#07fa89"));

        Label blockedLbl = new Label("Ataques bloqueados: " + blockedAttacks);
        blockedLbl.setFont(Font.font("Verdana", 15));
        blockedLbl.setTextFill(Color.web("#58a6ff"));

        Button btnReintentar = new Button("JUGAR DE NUEVO");
        btnReintentar.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        btnReintentar.setPrefWidth(240);
        btnReintentar.setStyle(
                "-fx-background-color: #07fa89; -fx-text-fill: black; -fx-background-radius: 4;"
        );
        btnReintentar.setOnAction(e -> {
            // Volver al login para reconectarse
            LoginScreen login = new LoginScreen(stage);
            login.show();
        });

        root.getChildren().addAll(titulo, jugador, scoreLbl, blockedLbl, btnReintentar);
        stage.setScene(new Scene(root, 480, 400));
    }
}