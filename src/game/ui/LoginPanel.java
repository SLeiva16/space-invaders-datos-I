package game.ui;       

import java.awt.GridLayout;     // Importar la clase GridLayout para organizar los componentes en una cuadrícula
import javax.swing.JButton;     // Importar la clase JButton para crear botones en la interfaz gráfica
import javax.swing.JLabel;      // Importar la clase JLabel para crear etiquetas de texto en la interfaz gráfica
import javax.swing.JPanel;      // Importar la clase JPanel para crear paneles que contengan otros componentes en la interfaz gráfica
import javax.swing.JPasswordField;      // Importar la clase JPasswordField para crear un campo de texto que oculte la entrada, ideal para contraseñas
import javax.swing.JTextField;          // Importar la clase JTextField para crear un campo de texto para la entrada de datos, como el nombre de usuario

// -------------------------------
// Clase LoginPanel
    // Pantalla inicial de login y registro.
    // Permite al usuario ingresar nombre y contraseña.
    // Incluye botones para iniciar sesión y registrarse.
    // Se conecta con el servidor para validar credenciales o crear nuevos usuarios.
    // Es la primera pantalla que se muestra al abrir el juego.
// -------------------------------


public class LoginPanel extends JPanel      // Extiende JPanel para crear una interfaz gráfica personalizada
{
    private GameWindow gameWindow;   // Referencia a la ventana principal

    public LoginPanel(GameWindow gameWindow)        // Constructor con referencia
    {
        this.gameWindow = gameWindow;

        setLayout(new GridLayout(3, 2, 10, 10));    // Configura un GridLayout con 3 filas, 2 columnas y espacio entre componentes

        JLabel userLabel = new JLabel("Usuario:");          // Etiqueta para el campo de usuario
        JTextField userField = new JTextField();            // Campo de texto para ingresar el nombre de usuario

        JLabel passLabel = new JLabel("Contraseña:");       // Etiqueta para el campo de contraseña
        JPasswordField passField = new JPasswordField();    // Campo de texto para ingresar la contraseña, oculta por seguridad

        JButton loginButton = new JButton("Iniciar sesión");    // Botón para iniciar sesión
        JButton registerButton = new JButton("Registrarse");    // Botón para registrarse

        add(userLabel);         // Agrega la etiqueta de usuario al panel
        add(userField);         // Agrega el campo de usuario al panel
        add(passLabel);         // Agrega la etiqueta de contraseña al panel
        add(passField);         // Agrega el campo de contraseña al panel
        add(loginButton);       // Agrega el botón de iniciar sesión al panel
        add(registerButton);    // Agrega el botón de registrarse al panel

        // 👉 Acción del botón de login
        loginButton.addActionListener(e -> {
            // Aquí podrías validar usuario/contraseña
            gameWindow.showPanel("Avatar");   // Cambiar al panel de avatar
        });

        // 👉 Acción del botón de registro
        registerButton.addActionListener(e -> {
            // Aquí podrías manejar el registro de nuevo usuario
        });
    }
}

