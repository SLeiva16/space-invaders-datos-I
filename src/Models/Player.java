package Models;

//Datos de cada jugador que va directamente para usarlos en el database.json

public class Player {

    //Datos guardados para cada Jugador
    private String username;
    private String passwordHash;
    private String avatar;
    private int totalScore;
    private int gamesPlayed;

    //XP para la especializacion
    private int xpNetwork;   // Obtenido tras bloquear ataques DDOS
    private int xpMalware;   // Obtenido tras bloquear ataques Malware
    private int xpCrypto;    // Obtenido tras bloquear ataques Credential attacks



    public Player(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.avatar = "Captain Firewall"; //Avatar por defecto
        this.totalScore = 0;
        this.gamesPlayed = 0;
        this.xpNetwork = 0;
        this.xpMalware = 0;
        this.xpCrypto = 0;
    }

    //Comprueba los 3 XP, para definir la especializacion del Jugador
    public String getSpecialization() {
        if (xpNetwork >= xpMalware && xpNetwork >= xpCrypto) return "Network";
        if (xpMalware >= xpCrypto) return "Malware";
        return "Crypto";
    }

    // Getters and setters
    public String getUsername()              { return username; }
    public void setUsername(String u)        { this.username = u; }

    public String getPasswordHash()          { return passwordHash; }
    public void setPasswordHash(String p)    { this.passwordHash = p; }

    public String getAvatar()                { return avatar; }
    public void setAvatar(String a)          { this.avatar = a; }

    public int getTotalScore()               { return totalScore; }    //devuelve valo
    public void setTotalScore(int s)         { this.totalScore = s; }  //reemplaza valor
    public void addScore(int s)              { this.totalScore += s; } //Suma el valor

    public int getGamesPlayed()              { return gamesPlayed; }
    public void setGamesPlayed(int g)        { this.gamesPlayed = g; }
    public void incrementGamesPlayed()       { this.gamesPlayed++; }

    public int getXpNetwork()                { return xpNetwork; }
    public void setXpNetwork(int x)          { this.xpNetwork = x; }
    public void addXpNetwork(int x)          { this.xpNetwork += x; }

    public int getXpMalware()                { return xpMalware; }
    public void setXpMalware(int x)          { this.xpMalware = x; }
    public void addXpMalware(int x)          { this.xpMalware += x; }

    public int getXpCrypto()                 { return xpCrypto; }
    public void setXpCrypto(int x)           { this.xpCrypto = x; }
    public void addXpCrypto(int x)           { this.xpCrypto += x; }

    @Override
    public String toString() {
        return "Jugador {Nombre='" + username + "', Puntuacion=" + totalScore +
                ", Partidas jugadas=" + gamesPlayed + ", Especializacion=" + getSpecialization() + "}";
    }
}
