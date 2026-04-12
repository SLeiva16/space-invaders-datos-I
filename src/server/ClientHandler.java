package server;
import Models.Message;
import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;

/*
POR FAVOR LEER ESTAS NOTAS DE COSAS QUE NO HEMOS VISTO EN CLASE PERO SE USARON AQUÍ.

IOException.
-Input or Output Exception. Si hay un error en el input (datos puestos) o output (datos sacados).
-throws IOException es como diciendo "Si este métdo falla en algo de I u O, pasar el error a otro lado"
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
-Lo usamos aquí porque estamos toqueteando cosas con respecto a un servidor. Para salvarnos de cualquier error mortal.

Catch.
-Está relacionado con IOException.
-Si algo falla, entra en el catch.

Switch y case.
-Son para ejecutar un código dependiendo del valor.
-Si es "LOGIN", ejecuta el primero; si es "REGISTER", el segundo...
-default no es obligatorio pero es RECOMENDADO. Maneja casos inesperados.

While.
-Solo para aclarar, usamos while en este sentido:
    "Mientras el cliente me siga mandando datos, yo (servidor) sigo funcionando.".

 */

public class ClientHandler implements Runnable {

    private Socket socket;
    private DatabaseManager db;
    private Gson gson;

    // Referencia al oponente -- la asigna MatchManager cuando hay pareja
    private ClientHandler opponent;
    private PrintWriter out;

    // Para saber si este jugador ya termino
    private boolean gameOver = false;
    private String username;

    public ClientHandler(Socket socket, DatabaseManager db){
        this.socket = socket;
        this.db = db;
        this.gson = new Gson();
    }


    @Override
    public void run(){
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            String json;

            while ((json = in.readLine()) != null){

                Message message = gson.fromJson(json, Message.class);
                Message answer = new Message();

                switch (message.type) {

                    case "LOGIN":
                        boolean loginok = db.authenticate(message.username, message.password);
                        answer.type = "LOGIN_ANSWER";
                        answer.success = loginok;
                        break;

                    case "REGISTER":
                        System.out.println("Intentando registrar: " + message.username);
                        boolean regiok = db.register(message.username, message.password);
                        System.out.println("Resultado de registro: " + regiok);
                        answer.type = "REGISTER_ANSWER";
                        answer.success = regiok;
                        break;

                    case "SET_AVATAR":
                        System.out.println("Asignando avatar a " + message.username);
                        db.updateAvatar(message.username, message.avatar);
                        answer.type = "SET_AVATAR_ANSWER";
                        answer.success = true;
                        break;

                    // ----  buscar partida ------
                    case "FIND_MATCH":
                        System.out.println("[Match] " + message.username + " busca partida.");
                        this.username = message.username;
                        answer.type = "WAITING";
                        answer.success = true;
                        out.println(gson.toJson(answer));
                        // Agregar a la cola — MatchManager notifica cuando hay pareja
                        Match.addPlayer(this);
                        continue; // no mandar respuesta doble

                        // ---- estado del juego en tiempo real ------
                    case "GAME_STATE":
                        // Retransmitir al oponente si existe
                        if (opponent != null) {
                            Message opState = new Message();
                            opState.type = "OPPONENT_STATE";
                            opState.opponentUsername = message.username;
                            opState.opponentLives = message.lives;
                            opState.opponentScore = message.score;
                            opState.opponentGameOver = message.isGameOver;
                            opponent.sendMessage(opState);
                        }
                        continue; // no necesita respuesta directa

                        // ---- jugador termino ---
                    case "GAME_OVER":
                        this.gameOver = true;
                        System.out.println("[Match] " + message.username + " termino. Score: " + message.score);

                        // Guardar estadisticas
                        db.saveSessionResult(message.username, message.score, 0, 0, 0);

                        // Notificar al oponente
                        if (opponent != null) {
                            Message opOver = new Message();
                            opOver.type = "OPPONENT_STATE";
                            opOver.opponentUsername = message.username;
                            opOver.opponentScore = message.score;
                            opOver.opponentGameOver = true;
                            opponent.sendMessage(opOver);

                            // Si el oponente también terminó les manda resumen a ambos
                            if (opponent.gameOver) {
                                sendSessionEnd(message.score, opponent);
                            }
                        }
                        continue;

                    default:
                        answer.type = "ERROR";
                        answer.success = false;
                        break;

                }

                out.println(gson.toJson(answer));

            }

        }catch(Exception e){
            System.out.println("[Server] Cliente desconectado");
        }
    }

// ----- Metodos publicos usados por MatchManager y otros handlers -----

    // Enviar un mensaje a este cliente
    public synchronized void sendMessage(Message message) {
        if (out != null) {
            out.println(gson.toJson(message));
        }
    }

    // Asignar oponente (llamado por MatchManager)
    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
    }

    public String getUsername() {
        return username;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    // -- Privados ------

    // Mandar resumen final a ambos jugadores
    private void sendSessionEnd(int myScore, ClientHandler opponent) {
        int opScore = 0; // el oponente ya guardó su score antes

        String winner = myScore >= opScore ? username : opponent.getUsername();

        Message end = new Message();
        end.type = "SESSION_END";
        end.username = winner;       // ganador
        end.score = myScore;
        end.opponentScore = opScore;

        this.sendMessage(end);
        opponent.sendMessage(end);

        System.out.println("[Match] Sesión terminada. Ganador: " + winner);
    }
}
