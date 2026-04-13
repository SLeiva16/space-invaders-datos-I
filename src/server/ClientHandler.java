package server;

import Models.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

/*
POR FAVOR LEER ESTAS NOTAS DE COSAS QUE NO HEMOS VISTO EN CLASE PERO SE USARON AQUÍ.

IOException.
-Input or Output Exception. Si hay un error en el input (datos puestos) o output (datos sacados).
-throws IOException es como diciendo "Si este método falla en algo de I u O, pasar el error a otro lado"
-throws es para evitar que el error se maneje ahí, donde nos puede interrumpir más.

BufferedReader.
-Lector de texto.
-Para leer mensajes que envía el cliente.

InputStream.
-Flujo de datos de entrada.
-Cliente -> Servidor.
-"Lo que el cliente le envía al servidor".

Try.
-Por si el código es muy delicado y puede fallar.
-Lo usamos aquí porque estamos toqueteando cosas con respecto a un servidor.

Catch.
-Está relacionado con IOException.
-Si algo falla, entra en el catch.

Switch y case.
-Son para ejecutar un código dependiendo del valor.
-default no es obligatorio pero es RECOMENDADO. Maneja casos inesperados.

While.
-"Mientras el cliente me siga mandando datos, yo (servidor) sigo funcionando."
*/

public class ClientHandler implements Runnable {

    private Socket socket;
    private DatabaseManager db;
    private Gson gson;
    private int finalScore = 0;

    private ClientHandler opponent;
    private PrintWriter out;      //  atributo de clase, NO redeclarar en run()
    private BufferedReader in;    //  también atributo para usarlo en ambos métodos

    private boolean gameOver = false;
    private String username;

    public ClientHandler(Socket socket, DatabaseManager db) {
        this.socket = socket;
        this.db     = db;
        this.gson   = new Gson();
    }

    @Override
    public void run() {
        try {
            // ← asignar al atributo, no crear variable local
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String json;
            while ((json = in.readLine()) != null) {

                Message message = gson.fromJson(json, Message.class);
                Message answer  = new Message();

                switch (message.type) {

                    case "LOGIN":
                        boolean loginOk = db.authenticate(message.username, message.password);
                        if (loginOk) this.username = message.username;
                        answer.type    = "LOGIN_ANSWER";
                        answer.success = loginOk;
                        break;

                    case "REGISTER":
                        System.out.println("Intentando registrar: " + message.username);
                        boolean regiOk = db.register(message.username, message.password);
                        System.out.println("Resultado de registro: " + regiOk);
                        answer.type    = "REGISTER_ANSWER";
                        answer.success = regiOk;
                        break;

                    case "SET_AVATAR":
                        System.out.println("Asignando avatar a " + message.username);
                        db.updateAvatar(message.username, message.avatar);
                        answer.type    = "SET_AVATAR_ANSWER";
                        answer.success = true;
                        break;

                    case "FIND_MATCH":
                        this.username = message.username;
                        System.out.println("[Match] " + message.username + " busca partida.");

                        // Responder WAITING
                        Message waiting = new Message();
                        waiting.type    = "WAITING";
                        waiting.success = true;
                        out.println(gson.toJson(waiting));

                        // Agregar a la cola
                        Match.addPlayer(this);

                        // Mantener thread vivo para mensajes del juego
                        String gameJson;
                        while ((gameJson = in.readLine()) != null) {
                            Message gameMsg = gson.fromJson(gameJson, Message.class);
                            handleGameMessage(gameMsg);
                        }
                        return; // salir del while principal al desconectarse

                    default:
                        answer.type    = "ERROR";
                        answer.success = false;
                        break;
                }

                out.println(gson.toJson(answer));
            }

        } catch (Exception e) {
            System.out.println("[Server] Cliente desconectado: " + username);
        }
    }

    // --- Maneja mensajes durante la partida --------

    private void handleGameMessage(Message message) {
        switch (message.type) {

            case "GAME_STATE":
                if (opponent != null) {
                    Message opState          = new Message();
                    opState.type             = "OPPONENT_STATE";
                    opState.opponentUsername = message.username;
                    opState.opponentLives    = message.lives;
                    opState.opponentScore    = message.score;
                    opState.opponentGameOver = message.isGameOver;
                    opponent.sendMessage(opState);
                }
                break;

            case "GAME_OVER":
                this.gameOver    = true;
                this.finalScore  = message.score;

                System.out.println("[Server] " + username + " terminó. Score: " + finalScore);

                // Avisar al oponente que este jugador murió
                if (opponent != null) {
                    Message opOver          = new Message();
                    opOver.type             = "OPPONENT_STATE";
                    opOver.opponentUsername = username;
                    opOver.opponentScore    = finalScore;
                    opOver.opponentGameOver = true;
                    opponent.sendMessage(opOver);
                }

                // Guardar resultado
                db.saveSessionResult(username, finalScore, 0, 0, 0);

                // Si ambos terminaron, mandar SESSION_END UNA sola vez
                if (opponent != null && opponent.isGameOver()) {
                    System.out.println("[Server] Ambos terminaron → SESSION_END");
                    sendSessionEnd();
                }
                break;

            default:
                System.out.println("[Server] Mensaje desconocido en partida: "
                        + message.type);
                break;
        }
    }

    // -- Métodos públicos ----------

    public synchronized void sendMessage(Message message) {
        if (out != null) {
            out.println(gson.toJson(message));
        }
    }

    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
    }

    public String getUsername() { return username; }
    public boolean isGameOver() { return gameOver; }

    // --- Privados ----------

    private void sendSessionEnd() {
        String winner = this.finalScore >= opponent.finalScore
                ? this.username
                : opponent.getUsername();

        Message end       = new Message();
        end.type          = "SESSION_END";
        end.username      = winner;
        end.score         = this.finalScore;
        end.opponentScore = opponent.finalScore;

        this.sendMessage(end);
        opponent.sendMessage(end);

        System.out.println("[Match] Sesión terminada. Ganador: " + winner);
    }
}