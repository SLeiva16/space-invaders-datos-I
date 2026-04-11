package game.ui;

import javax.swing.*;   
import java.awt.*;         

// -------------------------------
// Clase AvatarPanel
    // Pantalla de selección de avatar.
    // Muestra varias imágenes de personajes para que el usuario elija uno.
    // La elección se guarda y se utiliza durante la partida.
    // Es la segunda pantalla en el flujo, después del login exitoso.
// -------------------------------

public class AvatarPanel extends JPanel // Extiende JPanel para crear una interfaz gráfica personalizada
{
    private GameWindow gameWindow;   // Referencia a la ventana principal

    public AvatarPanel(GameWindow gameWindow)                     // Constructor de la clase AvatarPanel
    {
        this.gameWindow = gameWindow;       // Guarda la referencia a la ventana principal para poder cambiar de panel después de la selección
        
        setLayout(new GridLayout(2, 2, 10, 10));   // Configura un GridLayout con 2 filas, 2 columnas y espacio entre botones

        JButton avatar1Button = new JButton("Avatar 1");   // Botón para seleccionar el primer avatar
        JButton avatar2Button = new JButton("Avatar 2");   // Botón para seleccionar el segundo avatar
        JButton avatar3Button = new JButton("Avatar 3");   // Botón para seleccionar el tercer avatar
        JButton avatar4Button = new JButton("Avatar 4");   // Botón para seleccionar el cuarto avatar

        add(avatar1Button);   // Agrega el botón del primer avatar al panel
        add(avatar2Button);   // Agrega el botón del segundo avatar al panel
        add(avatar3Button);   // Agrega el botón del tercer avatar al panel
        add(avatar4Button);   // Agrega el botón del cuarto avatar al panel

        //Acciones de los botones
        avatar1Button.addActionListener(e -> 
            {
                // Aquí podrías guardar la selección del avatar
                // Luego mostrar el MapPanel
                // gameWindow.showPanel("Map");
            });

        avatar2Button.addActionListener(e -> 
            { 
                // Selección de avatar 2
                // gameWindow.showPanel("Map");
            });

        avatar3Button.addActionListener(e -> 
            {
                // Selección de avatar 3
                // gameWindow.showPanel("Map");
            });

        avatar4Button.addActionListener(e -> 
            {
                // Selección de avatar 4
                // gameWindow.showPanel("Map");
            });
    }
}
