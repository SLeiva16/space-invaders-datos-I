package server;

import Models.Message;
import structures.QueueList;

// Maneja la cola de espera y empareja jugadores.
// Cuando hay 2 jugadores, les asigna oponente y envia CONFIG a ambos.

public class Match {

    private static final QueueList<ClientHandler> waitingPlayers = new QueueList<>();

    private Match() {}

    public static synchronized void addPlayer(ClientHandler player) {
        System.out.println("[MatchManager] En espera: " + player.getUsername()
                + " | Cola: " + (waitingPlayers.size() + 1));

        waitingPlayers.enqueue(player);

        if (waitingPlayers.size() >= 2) {
            ClientHandler p1 = waitingPlayers.dequeue();
            ClientHandler p2 = waitingPlayers.dequeue();

            System.out.println("[MatchManager] Partida: "
                    + p1.getUsername() + " vs " + p2.getUsername());

            // ← correr en thread separado para no bloquear los ClientHandlers
            new Thread(() -> {
                p1.setOpponent(p2);
                p2.setOpponent(p1);

                Message matchFound = new Message();
                matchFound.type = "MATCH_FOUND";
                matchFound.opponentUsername = p2.getUsername();
                p1.sendMessage(matchFound);
                System.out.println("[MatchManager] MATCH_FOUND enviado a: " + p1.getUsername());

                matchFound.opponentUsername = p1.getUsername();
                p2.sendMessage(matchFound);
                System.out.println("[MatchManager] MATCH_FOUND enviado a: " + p2.getUsername());

                Message config = buildConfig();
                p1.sendMessage(config);
                p2.sendMessage(config);
                System.out.println("[MatchManager] CONFIG enviado a ambos");
            }).start();
        }
    }

    // Configuración base del juego , se puede ajustar aquí
    private static Message buildConfig() {
        Message config = new Message();
        config.type                    = "CONFIG";
        config.initialHp               = 100;
        config.baseSpawnRate           = 1.0;
        config.baseAttackSpeed         = 1.2;
        config.scorePerKill            = 10;
        config.difficultyStepScore     = 100;
        config.spawnMultiplierPerLevel = 1.15;
        config.speedAddPerLevel        = 0.3;
        config.damageDDoS              = 10;
        config.damageMalware           = 15;
        config.damageCredential        = 20;
        return config;
    }
}