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
    private final int myScore;
    private final int blockedAttacks;
    private final String opponentUsername;
    private final int opponentScore;
    private final String winner;
    private final ClientConnection conexion;

    public GameOverScreen(Stage stage, String username, int myScore,
                          int blockedAttacks, String opponentUsername,
                          int opponentScore, String winner,
                          ClientConnection conexion) {
        this.stage            = stage;
        this.username         = username;
        this.myScore          = myScore;
        this.blockedAttacks   = blockedAttacks;
        this.opponentUsername = opponentUsername;
        this.opponentScore    = opponentScore;
        this.winner           = winner;
        this.conexion         = conexion;
    }

    public void show() {
        boolean yoGane = username.equals(winner);

        VBox root = new VBox(18);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.setStyle("-fx-background-color: black;");

        Label resultado = new Label(yoGane ? "VICTORIA!" : "DERROTA");
        resultado.setFont(Font.font("Verdana", FontWeight.BOLD, 38));
        resultado.setTextFill(yoGane ? Color.web("#00ff88") : Color.web("#ff4444"));

        Label ganadorLabel = new Label("Ganador: " + winner);
        ganadorLabel.setFont(Font.font("Verdana", 15));
        ganadorLabel.setTextFill(Color.GRAY);

        // Mis stats
        Label myTitle = new Label("--- Tu resultado ---");
        myTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        myTitle.setTextFill(Color.web("#c9d1d9"));

        Label myScoreLbl = new Label("Score: " + myScore);
        myScoreLbl.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        myScoreLbl.setTextFill(Color.web("#00ff88"));

        Label myBlocked = new Label("Ataques bloqueados: " + blockedAttacks);
        myBlocked.setFont(Font.font("Verdana", 14));
        myBlocked.setTextFill(Color.web("#58a6ff"));

        // Stats del oponente
        Label opTitle = new Label("--- " + (opponentUsername != null ? opponentUsername : "Oponente") + " ---");
        opTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        opTitle.setTextFill(Color.web("#ff9900"));

        Label opScoreLbl = new Label("Score: " + opponentScore);
        opScoreLbl.setFont(Font.font("Verdana", 15));
        opScoreLbl.setTextFill(Color.web("#ff9900"));

        Button btnReintentar = new Button("VOLVER AL INICIO");
        btnReintentar.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        btnReintentar.setPrefWidth(240);
        btnReintentar.setStyle(
                "-fx-background-color: #00ff88; -fx-text-fill: black; -fx-background-radius: 4;"
        );
        btnReintentar.setOnAction(e -> {
            conexion.disconnect();
            LoginScreen login = new LoginScreen(stage);
            login.show();
        });

        root.getChildren().addAll(
                resultado, ganadorLabel,
                myTitle, myScoreLbl, myBlocked,
                opTitle, opScoreLbl,
                btnReintentar
        );

        stage.setScene(new Scene(root, 480, 500));
    }
}
