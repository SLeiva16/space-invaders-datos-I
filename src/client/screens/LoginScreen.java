package client.screens;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import client.ClientConnection;

public class LoginScreen {

    private static final int PORT = 12345 ;
    private final Stage stage;


    private TextField ipField;
    private TextField usernameField;
    private PasswordField passwordField;
    private Label statusLabel;

    public LoginScreen(Stage stage) {this.stage = stage;}

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(45));
        root.setStyle("-fx-background-color: black");


        Label title = new Label("Space Invader");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        title.setTextFill(Color.GREEN);

        Label subtitle = new Label("Conectado al Server");
        subtitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        subtitle.setTextFill(Color.GRAY);

        ipField        = styledField("Server IP");
        usernameField  = styledField("Usuario");
        passwordField  = new PasswordField();
        styleControl(passwordField, "Contraseña");

        Button loginBtn = styledButton("Iniciar Sesion", "#00ff88" );
        Button registraseBtn = styledButton("Registrarse", "#00ff88" );

        loginBtn.setOnAction(e ->{
            try{
                String ip = ipField.getText().trim();
                String username = usernameField.getText().trim();
                String password = passwordField.getText().trim();

                if (ip.isEmpty() || username.isEmpty() || password.isEmpty()){
                    statusLabel.setText("Completar todos los campos es necesario.");
                    statusLabel.setTextFill(Color.RED);
                    return;

                }

                ClientConnection client = new ClientConnection(ip, PORT);
                boolean success = client.login(username, password);

                if (success){
                    statusLabel.setText("Login completado");
                    statusLabel.setTextFill(Color.GREEN);

                    new AvtSelectionScreen(stage, username).show();

                }else{
                    statusLabel.setText("Falla al hacer login, reintentar.");
                    statusLabel.setTextFill(Color.RED);

                }


            }catch(Exception ex){
                statusLabel.setText("Error de conexión con el servidor");
                statusLabel.setTextFill(Color.RED);
                ex.printStackTrace();

            }
        });

        registraseBtn.setOnAction(XD ->{
            new RegisterScreen(stage, ipField.getText()).show();
        });



        //Mensajes de estado(Exito o errores)
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Verdana", FontWeight.NORMAL, 12));
        statusLabel.setTextFill(Color.GREEN);

        root.getChildren().addAll(
                title, subtitle,
                ipField, usernameField, passwordField,
                loginBtn, registraseBtn,
                statusLabel);

        Scene scene = new Scene(root, 500,500);
        stage.setScene(scene);
        stage.show();

    }





    /*
          =====Style=====
    */
    //Entrada de texto de una sola linea
    private TextField styledField(String prompt) {
        TextField f = new TextField();
        styleControl(f, prompt);
        return f;
    }

    private void styleControl(Control c, String prompt) {
        c.setStyle(
                "-fx-background-color: #161b22;" +      //fondo
                        "-fx-text-fill: #c9d1d9;" +     //texto
                        "-fx-border-color: #30363d;" +  //borde
                        "-fx-border-radius: 4;" +       //bordes redondos
                        "-fx-background-radius: 4;" +
                        "-fx-font-family: Verdana;" +
                        "-fx-font-size: 13;"
        );
        c.setPrefWidth(300);
        if (c instanceof TextField) ((TextField) c).setPromptText(prompt);
        if (c instanceof PasswordField) ((PasswordField) c).setPromptText(prompt);
    }

    private Button styledButton(String text, String color) {
        Button b = new Button(text);
        b.setPrefWidth(300);
        b.setFont(Font.font("Monospace", FontWeight.BOLD, 13));
        b.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: #0d1117;" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
        );
        return b;
    }



}
