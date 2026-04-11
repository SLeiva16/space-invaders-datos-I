package game.ui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JButton;

// -------------------------------
// Clase MapPanel
    // Pantalla de selección de mapa.
    // Ofrece dos escenarios distintos para jugar.
    // El usuario selecciona uno y se guarda como configuración inicial.
    // Es la tercera pantalla en el flujo, después de elegir avatar.
// -------------------------------


public class MapPanel extends JPanel
{
    private GameWindow gameWindow;   // Referencia a la ventana principal

    public MapPanel(GameWindow gameWindow)           // Constructor 
    {
        this.gameWindow = gameWindow;   

        setLayout(new GridLayout(1, 3, 10, 10));     // Configura un GridLayout con 1 fila, 3 columnas

        JButton map1Button = new JButton("Mapa 1");  // Botón para seleccionar el primer mapa
        JButton map2Button = new JButton("Mapa 2");  // Botón para seleccionar el segundo mapa
        JButton map3Button = new JButton("Mapa 3");  // Botón para seleccionar el tercer mapa

        add(map1Button);   // Agrega el botón del primer mapa al panel
        add(map2Button);   // Agrega el botón del segundo mapa al panel
        add(map3Button);   // Agrega el botón del tercer mapa al panel

        // Acciones de los botones
        map1Button.addActionListener(e -> 
            {
                // Selección de mapa 1
                gameWindow.showPanel("Game");   // Cambiar al panel de juego
            });

        map2Button.addActionListener(e -> 
            {
                // Selección de mapa 2
                gameWindow.showPanel("Game");
            });

        map3Button.addActionListener(e -> 
            {
                // Selección de mapa 3
                gameWindow.showPanel("Game");
            });
    }
}

