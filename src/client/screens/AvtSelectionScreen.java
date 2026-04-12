package client.screens;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import client.ClientConnection;
import Models.Message;
import javafx.geometry.Insets;

import java.awt.*;


public class AvtSelectionScreen {

    private Stage pantalla;
    private String avatar = null;
    private String username;
    private ClientConnection conexion;

    public AvtSelectionScreen(Stage stage, String username, ClientConnection conexion) {
        this.pantalla = stage;
        this.username = username;
        this.conexion = conexion;
        //descomentar cuando ponga para asignar user

    }

    public void show(){

        VBox main = new VBox(20);
        main.setAlignment(Pos.CENTER);
        main.setPadding(new Insets(30));
        main.setStyle("-fx-background-color: black;");

        Label titulo = new Label("Seleccione un avatar");
        titulo.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        titulo.setTextFill(Color.GREEN);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.CENTER);

        String [] avatares = {
            "Captain Firewall",
            "Byte Ninja",
            "Malware Muncher",
            "Crypto Llama",
            "Packet Pirate",
            "Null Pointer Paladin"
        };

        Button[] botones = new Button[avatares.length];

        for (int i = 0; i < avatares.length; i++){
            final int idx = i;
            Button botonA = new Button(avatares[i]);
            botonA.setPrefSize(160, 50);
            botonA.setFont(Font.font("Verdana", 12));
            botonA.setStyle(inactiveStyle());

            //Nota para mis compañeros: e -> es una FUNCION LAMBDA. e es el evento, flechita dice "haga tal".
            botonA.setOnAction(e -> {

                avatar = avatares[idx];
                // Resetear todos
                for (Button b : botones) b.setStyle(inactiveStyle());
                // Destacar el seleccionado
                botonA.setStyle(activeStyle());


            });

            botones[i] = botonA;
            gridPane.add(botonA, i%3, i/3);
        }

        Label errorLabel = new Label("");
        errorLabel.setFont(Font.font("Verdana", 12));
        errorLabel.setTextFill(Color.RED);

        Button Confirm = new Button("Confirmar");
        Confirm.setPrefWidth(240);
        Confirm.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        Confirm.setStyle("-fx-background-color: #07fa89; -fx-text-fill: black; -fx-background-radius: 4;");

        Confirm.setOnAction(e ->{
            if (avatar == null) {
                errorLabel.setText("Selecciona un avatar primero.");
                return;
            }
            try{

                Message message = new Message();
                message.type = "SET_AVATAR";
                message.username = username;
                message.avatar = avatar;

                Message answer = conexion.send(message);

                if (answer.success){
                     System.out.println("Se ha guardado el avatar." + avatar);

                 //PASAR A PANTALLA DEL JUEGO EN SÍ.
                     MapSelectscreen mapScreen = new MapSelectscreen(pantalla, username, avatar, conexion);
                     mapScreen.show();
                }else{
                    System.out.println("Error al guardar avatar.");
                }
            }catch(Exception ex){
                errorLabel.setText("Error de conexion.");
                ex.printStackTrace();
            }

        });

        main.getChildren().addAll(titulo, gridPane, Confirm);

        Scene scene = new Scene(main, 600, 400);
        pantalla.setScene(scene);
        pantalla.show();

    }
    private String inactiveStyle() {
        return "-fx-background-color: #161b22; -fx-text-fill: #c9d1d9;" +
                "-fx-border-color: #30363d; -fx-border-radius: 4; -fx-background-radius: 4;" +
                "-fx-cursor: hand;";
    }

    private String activeStyle() {
        return "-fx-background-color: #0d2818; -fx-text-fill: #07fa89;" +
                "-fx-border-color: #07fa89; -fx-border-width: 2;" +
                "-fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand;";
    }
}
