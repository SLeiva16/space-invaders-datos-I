package client.screens;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import client.ClientConnection;
import javafx.scene.paint.Color;


public class RegisterScreen {

    private Stage stage;
    private String ip;

    public RegisterScreen(Stage stage, String ip){
        this.stage = stage; this.ip = ip;
    }

    public void show(){

        VBox c1 = new VBox(20);
        c1.setAlignment(Pos.CENTER);

        TextField user = new TextField();
        user.setPromptText("Usuario");

        PasswordField password = new PasswordField();
        password.setPromptText("Contraseña");

        Button registerbtn = new Button("Registrarse");
        Button back = new Button("Volver");

        Label statusLabel = new Label();

        registerbtn.setOnAction(Regi ->{
            try{
                String username = user.getText().trim();
                String contrasena = password.getText().trim();

                if (username.isEmpty() || contrasena.isEmpty()){
                    statusLabel.setText("Rellenar ambos campos es necesario.");
                    statusLabel.setTextFill(Color.RED);
                    return;

                }

                ClientConnection client = new ClientConnection(ip, 12345);
                boolean success = client.register(username, contrasena);

                if (success){
                    statusLabel.setText("¡Registración completa!");
                    statusLabel.setTextFill(Color.GREEN);
                }else{
                    statusLabel.setText("Fallo al completar el registro.");
                    statusLabel.setTextFill(Color.RED);
                }
            }catch(Exception ex){
                statusLabel.setText("Error de conexión con el servidor.");
                statusLabel.setTextFill(Color.RED);
                ex.printStackTrace();
            }

        });

        back.setOnAction(b ->{
            new LoginScreen(stage).show();
        });

        c1.getChildren().addAll(user, password, registerbtn, back, statusLabel);

        stage.setScene(new Scene(c1, 500, 400));
    }
}
