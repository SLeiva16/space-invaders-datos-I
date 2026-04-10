package game.ui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

// -------------------------------
// Clase GamePanel
    // Área principal del juego.
    // Aquí se dibujan los personajes, ataques y defensas.
    // Captura eventos de teclado: flechas para movimiento, teclas Q/W/E para defensa.
    // Actualiza el estado del jugador (HP, score, nivel) en tiempo real.
    // Es la pantalla central donde ocurre la partida.
// -------------------------------


// -----------------------------------------------
// GamePanel.java
// -----------------------------------------------
// Panel principal del juego.
// Dibuja la nave, enemigos y proyectiles usando Java 2D.
// Controla el movimiento con teclas y actualiza la pantalla.
// -----------------------------------------------

public class GamePanel extends JPanel implements ActionListener 
{

    private GameWindow gameWindow;     // Referencia a la ventana principal
    private Timer timer;               // Temporizador para animaciones
    private int playerX = 400;         // Posición inicial de la nave (X)
    private int playerY = 550;         // Posición inicial de la nave (Y)
    private int bulletY = -1;          // Posición del proyectil (-1 = no activo)
    private int enemyX = 200;          // Posición inicial del enemigo
    private int enemyY = 100;

    public GamePanel(GameWindow gameWindow) // Constructor con referencia a GameWindow
    {       
        this.gameWindow = gameWindow;

        setBackground(Color.BLACK);    // Fondo del panel
        setFocusable(true);            // Permitir capturar teclas

        // Capturar teclas para mover la nave y disparar
        addKeyListener(new KeyAdapter() // Escuchar eventos de teclado
        {       
            @Override                           // Método que se ejecuta cuando se presiona una tecla
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) // Flecha izquierda
                {          
                    playerX -= 10;     // Mover nave a la izquierda
                } 
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT) // Flecha derecha
                {  
                    playerX += 10;     // Mover nave a la derecha
                } 
                else if (e.getKeyCode() == KeyEvent.VK_SPACE) // Barra espaciadora
                {  
                    if (bulletY == -1) // Si no hay proyectil activo
                    {                           
                        bulletY = playerY;   // Activar proyectil
                    }
                }

                repaint(); // Redibujar cada vez que se mueve o dispara
            }

        });

        // Temporizador para animar proyectiles y enemigos
        timer = new Timer(30, this);   // Cada 30 ms se actualiza
        timer.start();
    }

    @Override       // Método para dibujar el juego
    protected void paintComponent(Graphics g) 
    {
        super.paintComponent(g);        // Limpiar el panel antes de dibujar

        // Dibujar el "mapa" como un rectángulo verde
        g.setColor(Color.GREEN);
        g.fillRect(50, 50, 700, 500);

        // Dibujar la nave como un triángulo blanco
        g.setColor(Color.WHITE);
        int[] xPoints = {playerX, playerX - 20, playerX + 20};
        int[] yPoints = {playerY, playerY + 30, playerY + 30};
        g.fillPolygon(xPoints, yPoints, 3);

        // Dibujar el enemigo como un círculo rojo
        g.setColor(Color.RED);
        g.fillOval(enemyX, enemyY, 40, 40);

        // Dibujar el proyectil si está activo
        if (bulletY != -1) 
        {
            g.setColor(Color.YELLOW);
            g.fillRect(playerX - 2, bulletY, 4, 10);
        }
    }




    @Override       // Método que se ejecuta cada vez que el temporizador se activa (cada 30 ms)
    public void actionPerformed(ActionEvent e) 
    {
        // Animar proyectil
        if (bulletY != -1) 
        {
            bulletY -= 10;   // Subir proyectil
            if (bulletY < 0) {
                bulletY = -1;   // Desactivar proyectil al salir de pantalla
            }
        }

        // Animar enemigo (movimiento simple)
        enemyX += 2;
        if (enemyX > 700 || enemyX < 50) 
        {
            enemyX = 200;   // Reiniciar posición
        }

        repaint(); // Redibujar cada frame
    }
}
