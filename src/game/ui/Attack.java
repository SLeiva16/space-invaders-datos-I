package game.ui;

import java.awt.Graphics;
import java.awt.Color;

public class Attack // Clase para ataque enemigo 
{
    private String type;   // "DDoS", "Malware", "Credential"
    private int x, y;      // Posición en el mapa
    private int speed;     // Velocidad de avance

    public Attack(String type, int x, int y, int speed) // Constructor 
    {
        this.type = type;       // Tipo de ataque
        this.x = x;             
        this.y = y;
        this.speed = speed;     // Velocidad de avance (mayor = más rápido)
    }

    public void move()  // Método para mover el ataque hacia abajo
    {
        y += speed; // Avanza hacia abajo
    }

    public void draw(Graphics g) //dibujar el ataque en pantalla
    {
        switch (type)   // Diferentes formas y colores según el tipo de ataque
        {
            case "DDoS":                    // Ataque DDoS representado por un círculo naranja
                g.setColor(Color.ORANGE);   
                g.fillOval(x, y, 20, 20);   
                break; 
            case "Malware":     // Ataque Malware representado por un rectángulo rosa
                g.setColor(Color.PINK);
                g.fillRect(x, y, 20, 20);
                break;
            case "Credential":      // Ataque Credential representado por un triángulo cyan
                g.setColor(Color.CYAN);
                g.fillPolygon(new int[]{x, x+10, x+20}, new int[]{y, y+20, y}, 3);
                break;
        }
    }

    public String getType() { return type; }
    public int getY() { return y; }
    public int getX() { return x; }
}
