package client.screens;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import client.ClientConnection;
import Models.Message;



public class AvtSelectionScreen {

    private Stage pantalla;
    private String avatar = null;
    private String username;

    public AvtSelectionScreen(Stage stage, String username) {
        this.pantalla = stage;
        this.username = username;
        //descomentar cuando ponga para asignar user

    }

    public void show(){

        VBox main = new VBox(20);
        main.setAlignment(Pos.CENTER);

        Label titulo = new Label("Seleccione un avatar");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(20);
        gridPane.setVgap(20);
        gridPane.setAlignment(Pos.CENTER);

        String [] avatares = {
            "Captain Firewall",
            "Byte Ninja",
            "Malware Muncher",
                "Crypto Llama"
        };

        for (int i = 0; i < avatares.length; i++){
            Button botonA = new Button(avatares[i]);

            //Nota para mis compañeros: e -> es una FUNCION LAMBDA. e es el evento, flechita dice "haga tal".
            botonA.setOnAction(e -> {

                avatar = botonA.getText();
                System.out.println("Avatar seleccionado:" + avatar);



            });

            gridPane.add(botonA, i%2, i/2);

        }

        Button Confirm = new Button("Confirmar");

        Confirm.setOnAction(e ->{
            if (avatar != null){
                System.out.println(avatar + " ha entrado al juego");

                try{
                    ClientConnection cone = new ClientConnection("localhost", 12345);

                    Message message = new Message();
                    message.type = "SET_AVATAR";
                    message.username = username;
                    message.avatar = avatar;

                    Message answer = cone.send(message);

                    if (answer.success){
                        System.out.println("Se ha guardado el avatar.");

                        //PASAR A PANTALLA DEL JUEGO EN SÍ.

                    }else{
                        System.out.println("Error al guardar avatar.");
                    }
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            else{
                System.out.println("Selecciona un avatar primero.");

            }
        });

        main.getChildren().addAll(titulo, gridPane, Confirm);

        Scene scene = new Scene(main, 600, 400);
        pantalla.setScene(scene);
        pantalla.show();

    }
}
