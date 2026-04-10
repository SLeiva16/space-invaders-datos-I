package game.ui;

import javax.swing.*;      // Para la interfaz gráfica
import java.awt.*;         // Para el diseño de los paneles


// -------------------------------
// Clase GameWindow
    // Ventana principal del juego.
    // Se encarga de contener y gestionar los diferentes paneles (login, selección de avatar, mapa, juego, scoreboard, pantalla final).
    // Utiliza un CardLayout para cambiar entre pantallas según el flujo del juego.
    // Es el punto de entrada visual y coordina la interacción entre la interfaz gráfica y la lógica del servidor.
// -------------------------------

public class GameWindow extends JFrame      // Hereda de JFrame para crear la ventana principal del juego
{

    private CardLayout cardLayout;          // Layout para gestionar múltiples paneles en la misma ventana
    private JPanel mainPanel;               // Panel principal que contendrá los diferentes paneles del juego (login, avatar, mapa, etc.)

    public GameWindow()                     // Constructor de clase 
    {
        // Configuración básica de la ventana
        setTitle("Space Invaders");             // Título de la ventana
        setSize(800, 600);                      // Tamaño 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // Cerrar la aplicación al cerrar la ventana
        setLocationRelativeTo(null);            // Centrar en pantalla

        // Inicializar el CardLayout y el panel contenedor
        cardLayout = new CardLayout();          // Crear un nuevo CardLayout
        mainPanel = new JPanel(cardLayout);     // Crear un nuevo JPanel con el CardLayout para gestionar los paneles

        // Crear e agregar los paneles al CardLayout
        LoginPanel loginPanel = new LoginPanel(this);       // Crear una instancia del panel de login
        mainPanel.add(loginPanel, "Login");             // Agregar el panel de login al panel principal con el nombre "Login"

        AvatarPanel avatarPanel = new AvatarPanel(this);    // Crear una instancia del panel de selección de avatar
        mainPanel.add(avatarPanel, "Avatar");           // Agregar el panel de selección de avatar al panel principal con el nombre "Avatar"

        MapPanel mapPanel = new MapPanel(this);     // Crear una instancia del panel de selección de mapa con referencia a GameWindow
        mainPanel.add(mapPanel, "Map");             // Agregar el panel de mapa al panel principal con el nombre "Map"


        // Mostrar el panel de login al iniciar
        showPanel("Login");                  // Mostrarlo al iniciar 
        add(mainPanel);                      // Agregar el panel principal al JFrame
    }

    // Método para mostrar un panel específico
    public void showPanel(String name)      // Recibe el nombre del panel que se desea mostrar
    {
        cardLayout.show(mainPanel, name);   
    }
}