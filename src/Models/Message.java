package Models;

//Nota para los compañeros: clase message para la comunicación con los json.

public class Message {
    public String type;
    public String username;
    public String password;
    public boolean success;
    public String avatar;

    // ── Estado del juego (GAME_STATE / GAME_OVER) ─────────────────────────────
    public int lives;          // Vidas actuales del jugador
    public int score;          // Puntaje actual del jugador
    public boolean isGameOver; // true cuando el jugador perdió todas las vidas

    // ── Estado del oponente (retransmitido por el servidor) ───────────────────
    public String opponentUsername; // Nombre del oponente
    public int opponentLives;       // Vidas actuales del oponente
    public int opponentScore;       // Puntaje actual del oponente
    public boolean opponentGameOver;// true cuando el oponente perdió

    // ── Configuración inicial (CONFIG, enviada por servidor al iniciar partida) ─
    public int initialHp;                  // Vida inicial (ej: 100)
    public double baseSpawnRate;           // Frecuencia base de ataques (ataques/seg)
    public double baseAttackSpeed;         // Velocidad base de los ataques
    public int scorePerKill;               // Puntos por bloquear correctamente
    public int difficultyStepScore;        // Cada cuántos puntos sube el nivel
    public double spawnMultiplierPerLevel; // Factor de spawn por nivel
    public double speedAddPerLevel;        // Incremento de velocidad por nivel
    public int damageDDoS;                 // Daño del ataque DDoS
    public int damageMalware;              // Daño del ataque Malware
    public int damageCredential;           // Daño del ataque Credential

}
