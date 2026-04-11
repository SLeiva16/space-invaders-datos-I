package game.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

// -------------------------------
// Clase GamePanel
    // Área principal del juego.
    // Aquí se dibujan los personajes, ataques y defensas.
    // Captura eventos de teclado: flechas para movimiento, teclas Q/W/E para defensa.
    // Actualiza el estado del jugador (HP, score, nivel) en tiempo real.
    // Es la pantalla central donde ocurre la partida.
// -------------------------------


public class GamePanel extends JPanel implements ActionListener 
{
    private ArrayList<Attack> attacks = new ArrayList<>(); //almacenar los ataques activos en el juego
    private Random random = new Random();   // Para generar ataques aleatorios
    private GameWindow gameWindow;     // Referencia a la ventana principal
    private Timer timer;               // Temporizador para animaciones
    private int playerX = 400;         // Posición inicial de la nave (X)
    private int playerY = 550;         // Posición inicial de la nave (Y)
    private int bulletY = -1;          // Posición del proyectil (-1 = no activo)
    private int enemyX = 200;          // Posición inicial del enemigo
    private int enemyY = 100;           // Posición inicial del enemigo
    private int lives = 3;              // Vidas del jugador
    private String activeDefense = "";  // Defensa actual activada
    private int score = 0;              // Puntuación del jugador
    private int blockedAttacks = 0;     // Contador de ataques bloqueados


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
                else if (e.getKeyCode() == KeyEvent.VK_UP) // Flecha arriba
                {  
                    playerY -= 10;     // Mover nave hacia arriba
                } 
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) // Flecha abajo
                {  
                    playerY += 10;     // Mover nave hacia abajo
                }
                else if (e.getKeyCode() == KeyEvent.VK_SPACE) // Barra espaciadora
                {  
                    if (bulletY == -1) // Si no hay proyectil activo
                    {                           
                        bulletY = playerY;   // Activar proyectil
                    }
                }
                else if (e.getKeyCode() == KeyEvent.VK_Q)  // Tecla Q para activar defensa
                {
                    activeDefense = "DDoS";
                    System.out.println("Defensa activada: Firewall (bloquea DDoS)");
                } 
                else if (e.getKeyCode() == KeyEvent.VK_W)  // Tecla W para activar defensa
                {
                    activeDefense = "Malware";
                    System.out.println("Defensa activada: Antivirus (bloquea Malware)");
                } 
                else if (e.getKeyCode() == KeyEvent.VK_E)  // Tecla E para activar defensa
                {
                    activeDefense = "Credential";
                    System.out.println("Defensa activada: Crypto Shield (bloquea Credential Attack)");
                }

                repaint(); // Redibujar cada vez que se mueve, dispara o activa defensa
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
            g.setColor(Color.RED);   // Proyectil representado por una línea roja
            g.drawLine(playerX, bulletY, playerX, bulletY - 10);    
        }

        // Dibujar ataques enemigos
        for (Attack attack : attacks)
        {
            attack.draw(g);    // Dibujar cada ataque en pantalla
        }

        // HUD: mostrar vidas y defensa activa (fuera del área verde)
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));    // Fuente para el HUD
        g.drawString("Vidas: " + lives, 60, 40);        // Mostrar vidas restantes en el HUD
        g.drawString("Defensa activa: " + (activeDefense.isEmpty() ? "Ninguna" : activeDefense), 200, 40);  // Mostrar defensa activa en el HUD
        g.drawString("Puntuación: " + score, 60, 60);
        g.drawString("Ataques bloqueados: " + blockedAttacks, 200, 60); // Mostrar el número de ataques bloqueados en el HUD

        // Condición de fin de juego
        if (lives <= 0)         // Si el jugador pierde todas las vidas, mostrar mensaje de "Game Over"
        {
            g.setColor(Color.RED);      // Mensaje de "Game Over" en rojo
            g.setFont(new Font("Arial", Font.BOLD, 30));    // Fuente grande para el mensaje de fin de juego
            g.drawString("GAME OVER", 300, 300);            // Mostrar mensaje de fin de juego en el centro del panel
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) 
    {
        // Animar proyectil
        if (bulletY != -1) 
        {
            bulletY -= 10;   // Subir proyectil
            if (bulletY < 0) 
            {
                bulletY = -1;   // Desactivar proyectil al salir de pantalla
            }
        }

        // Animar enemigo (movimiento simple)
        enemyX += 2;
        if (enemyX > 700 || enemyX < 50) 
        {
            enemyX = 200;   // Reiniciar posición
        }

        // Generar ataques enemigos aleatorios
        if (random.nextInt(100) < 5) // 5% de probabilidad cada frame
        {
            String[] types = {"DDoS", "Malware", "Credential"}; // Tipos de ataques disponibles
            String type = types[random.nextInt(types.length)];
            attacks.add(new Attack(type, random.nextInt(750) + 50, 50, 2)); // Posición aleatoria en X
        }

        // Mover ataques existentes
        for (Attack attack : attacks) 
        {
            attack.move();
        }

        // Revisar colisiones de ataques
        ArrayList<Attack> toRemove = new ArrayList<>();
        for (Attack attack : attacks) 
        {
            // Si el ataque llega a la altura de la nave
            if (attack.getY() >= playerY) 
            {
                if (attack.getType().equals(activeDefense)) 
                {
                    // Defensa correcta → eliminar ataque y sumar puntos
                    toRemove.add(attack);
                    blockedAttacks++;
                    score += 10;   // Sumar puntos por ataque bloqueado
                    System.out.println("Ataque bloqueado: " + attack.getType() + " | Puntos: " + score);
                } 
                else 
                {
                    // Defensa incorrecta → perder vida
                    lives--;
                    toRemove.add(attack);
                    System.out.println("Impacto recibido. Vidas restantes: " + lives);
                }
            }
        }

        // Eliminar ataques procesados
        attacks.removeAll(toRemove);
        
        repaint(); // Redibujar cada frame
    }
    
}
