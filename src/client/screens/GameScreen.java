package client.screens;

import Models.Message;
import client.ClientConnection;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import structures.List;

import java.util.Random;

public class GameScreen
{

    private static final double W = 750;
    private static final double H = 500;

    private final Stage stage;
    private final String username;
    private final String avatar;
    private final String map;
    private final ClientConnection conexion;
    private final String opponentUsername;
    private final Message config; // CONFIG recibido del servidor

    // Estado del jugador
    private double playerX = W / 2;
    private int hp;
    private int score = 0;
    private int blockedAttacks = 0;
    private String activeDefense = "";
    private boolean gameOver = false;
    private boolean waitingForOpponent = false; // murió pero oponente sigue

    // Estado del oponente (actualizado por el servidor)
    private int opponentHp;
    private int opponentScore = 0;
    private boolean opponentGameOver = false;

    // Dificultad dinámica
    private int level = 0;

    // Teclas presionadas
    private boolean leftDown = false;
    private boolean rightDown = false;

    // Ataques activos — usa List propia (sin ArrayList)
    private List<AttackObj> attacks = new List<>();
    private Random random = new Random();
    private double timeSinceLastSpawn = 0;


    // HUD propio
    private Label hpLabel;
    private Label scoreLabel;
    private Label defenseLabel;
    private Label levelLabel;
    private ProgressBar hpBar;

    // HUD oponente
    private Label opHpLabel;
    private Label opScoreLabel;
    private ProgressBar opHpBar;

    // Timing para envío de estado
    private long lastStateSent = 0;
    private static final long STATE_INTERVAL_NS = 500_000_000L; // 0.5s

    // Clase interna para ataques
    private static class AttackObj
    {
        String type;
        double x, y, speed;
        boolean active;

        AttackObj(String type, double x, double y, double speed) {
            this.type = type;
            this.x = x;
            this.y = y;
            this.speed = speed;
            this.active = true;
        }

        void move() { y += speed; }
        boolean offScreen() { return y > H; }
    }

    public GameScreen(Stage stage, String username, String avatar,
                      String map, ClientConnection conexion, String opponentUsername, Message config) {
        this.stage = stage;
        this.username = username;
        this.avatar = avatar;
        this.map = map;
        this.conexion = conexion;
        this.opponentUsername = opponentUsername;
        this.config = config;
    }

    public void show() {

        // --- HUD oponente (derecha) -----
        opHpBar = new ProgressBar(1.0);
        opHpBar.setPrefWidth(100);
        opHpBar.setStyle("-fx-accent: #ff4444;");

        opHpLabel    = hudLabel("HP: " + opponentHp);
        opHpLabel.setTextFill(Color.web("#ff9900"));
        opScoreLabel = hudLabel("Score: 0");
        opScoreLabel.setTextFill(Color.web("#ff9900"));

        Label opName = hudLabel(opponentUsername != null ? opponentUsername : "Oponente");
        opName.setTextFill(Color.web("#ff9900"));

        HBox myHud = new HBox(14);
        myHud.setAlignment(Pos.CENTER_LEFT);
        myHud.setPadding(new Insets(8, 16, 8, 16));
        myHud.setStyle("-fx-background-color: #161b22;");
        myHud.getChildren().addAll(hpBar, hpLabel, scoreLabel, defenseLabel, levelLabel);

        HBox opHud = new HBox(10);
        opHud.setAlignment(Pos.CENTER_RIGHT);
        opHud.setPadding(new Insets(8, 16, 8, 16));
        opHud.setStyle("-fx-background-color: #1a0d0d;");
        opHud.getChildren().addAll(opName, opHpBar, opHpLabel, opScoreLabel);

        // ---- Barra superior completa ----
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topBar = new HBox(0, myHud, spacer, opHud);
        topBar.setStyle("-fx-background-color: #161b22; -fx-border-color: #30363d; -fx-border-width: 0 0 1 0;");

        // ---- Canvas --------
        Canvas canvas = new Canvas(W, H);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // ----- Leyenda inferior --------
        HBox legend = new HBox(24);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(6));
        legend.setStyle("-fx-background-color: #161b22;");
        legend.getChildren().addAll(
                legendItem("[Q]", "Firewall → DDoS",        "#ff8800"),
                legendItem("[W]", "Antivirus → Malware",    "#ff69b4"),
                legendItem("[E]", "Crypto Shield → Cred",   "#00ffff")
        );

        VBox root = new VBox(0, topBar, canvas, legend);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, W, H + 90);

        // ---- Teclado ------
        scene.setOnKeyPressed(ev -> {
            KeyCode k = ev.getCode();
            if (k == KeyCode.LEFT)  leftDown = true;
            if (k == KeyCode.RIGHT) rightDown = true;
            if (k == KeyCode.Q) activateDefense("DDoS");
            if (k == KeyCode.W) activateDefense("Malware");
            if (k == KeyCode.E) activateDefense("Credential");
        });
        scene.setOnKeyReleased(ev -> {
            if (ev.getCode() == KeyCode.LEFT)  leftDown = false;
            if (ev.getCode() == KeyCode.RIGHT) rightDown = false;
        });

        stage.setScene(scene);
        scene.getRoot().requestFocus();

        // ---- Game loop ------
        new AnimationTimer() {
            long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) { lastTime = now; return; }
                double delta = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                if (!gameOver && !waitingForOpponent) {
                    update(delta);
                }
                render(gc);

                // Enviar estado cada 0.5s
                if (now - lastStateSent >= STATE_INTERVAL_NS) {
                    sendState(false);
                    lastStateSent = now;
                }
            }
        }.start();
    }

    // --- Escuchar mensajes del servidor ------

    private void startListening() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    Message msg = conexion.readMessage();
                    if (msg == null) break;

                    if ("OPPONENT_STATE".equals(msg.type)) {
                        Platform.runLater(() -> updateOpponentHUD(msg));
                    } else if ("SESSION_END".equals(msg.type)) {
                        Platform.runLater(() -> goToGameOver(msg));
                    }
                }
            } catch (Exception e) {
                System.out.println("[GameScreen] Desconectado del servidor.");
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void updateOpponentHUD(Message msg) {
        opponentHp      = msg.opponentLives;
        opponentScore   = msg.opponentScore;
        opponentGameOver = msg.opponentGameOver;

        opHpLabel.setText("HP: " + opponentHp);
        opScoreLabel.setText("Score: " + opponentScore);
        opHpBar.setProgress((double) opponentHp / config.initialHp);

        if (opponentHp <= config.initialHp * 0.3)
            opHpBar.setStyle("-fx-accent: #ff0000;");
    }

    private void goToGameOver(Message msg) {
        GameOverScreen over = new GameOverScreen(
                stage, username, score, blockedAttacks,
                opponentUsername, opponentScore, msg.username, conexion
        );
        over.show();
    }

    // ---- Enviar estado al servidor ----

    private void sendState(boolean isGameOver) {
        Message msg    = new Message();
        msg.type       = isGameOver ? "GAME_OVER" : "GAME_STATE";
        msg.username   = username;
        msg.lives      = hp;
        msg.score      = score;
        msg.isGameOver = isGameOver;
        conexion.sendOnly(msg);
    }

    // ---- Lógica del juego -----

    private void update(double delta) {
        // Movimiento del jugador
        if (leftDown)  playerX = Math.max(20, playerX - 200 * delta);
        if (rightDown) playerX = Math.min(W - 20, playerX + 200 * delta);

        // Dificultad dinámica
        int newLevel = score / config.difficultyStepScore;
        if (newLevel > level) {
            level = newLevel;
            Platform.runLater(() -> levelLabel.setText("Nivel: " + level));
        }

        // Spawn usando CONFIG
        double spawnRate = config.baseSpawnRate
                * Math.pow(config.spawnMultiplierPerLevel, level);
        timeSinceLastSpawn += delta;
        if (timeSinceLastSpawn >= 1.0 / spawnRate) {
            spawnAttack();
            timeSinceLastSpawn = 0;
        }


        // Mover ataques y revisar colisiones
        List<Integer> toRemove = new List<>();
        for (int i = 0; i < attacks.size(); i++) {
            AttackObj a = attacks.get(i);
            a.move();

            if (a.offScreen() || !a.active) {
                toRemove.add(i);
                continue;
            }

            // Colisión con el jugador
            if (a.y >= H - 60 && Math.abs(a.x - playerX) < 40) {
                if (a.type.equals(activeDefense)) {
                    // Defensa correcta
                    score += 10;
                    blockedAttacks++;
                    a.active = false;
                    toRemove.add(i);
                    updateMyHUD();
                } else {
                    // Daño según tipo
                    int damage = getDamage(a.type);
                    hp = Math.max(0, hp - damage);
                    a.active = false;
                    toRemove.add(i);
                    if (hp <= 0) triggerGameOver();
                }
                Platform.runLater(this::updateMyHUD);
            }
        }

        // Eliminar ataques procesados (de atrás para adelante)
        for (int i = toRemove.size() - 1; i >= 0; i--) {
            attacks.remove(toRemove.get(i));
        }
    }


    private void spawnAttack() {
        String[] tipos = {"DDoS", "Malware", "Credential"};
        String tipo = tipos[random.nextInt(3)];
        double x     = 50 + random.nextDouble() * (W - 100);
        double speed = config.baseAttackSpeed + config.speedAddPerLevel * level;
        attacks.add(new AttackObj(tipo, x, 0, speed));
    }

    private int getDamage(String type) {
        switch (type) {
            case "DDoS":       return config.damageDDoS;
            case "Malware":    return config.damageMalware;
            case "Credential": return config.damageCredential;
            default:           return 5;
        }
    }

    private void activateDefense(String type) {
        activeDefense = type;
        defenseLabel.setText("Defensa: " + type);
    }

    private void updateMyHUD() {
        hpLabel.setText("HP: " + hp);
        scoreLabel.setText("Score: " + score);
        hpBar.setProgress((double) hp / config.initialHp);
        if (hp <= config.initialHp * 0.3) hpBar.setStyle("-fx-accent: #ff4444;");
        else if (hp <= config.initialHp * 0.6) hpBar.setStyle("-fx-accent: #ffaa00;");
    }

    private void triggerGameOver() {
        gameOver = true;
        waitingForOpponent = !opponentGameOver;
        sendState(true); // avisar al servidor
    }

    // ------ Renderizado --------

    private void render(GraphicsContext gc) {
        // Fondo según mapa
        boolean carnival = map.contains("Carnival");
        gc.setFill(carnival ? Color.web("#1a0a2e") : Color.web("#0a0a1a"));
        gc.fillRect(0, 0, W, H);

        // Cuadrícula decorativa
        gc.setStroke(carnival ? Color.web("#2a1a4e") : Color.web("#0d2218"));
        gc.setLineWidth(1);
        for (int x = 0; x < W; x += 50) gc.strokeLine(x, 0, x, H);
        for (int y = 0; y < H; y += 50) gc.strokeLine(0, y, W, y);

        // Dibujar ataques
        for (int i = 0; i < attacks.size(); i++) {
            AttackObj a = attacks.get(i);
            if (!a.active) continue;
            drawAttack(gc, a);
        }




        // Jugador
        if (!gameOver) {
            gc.setFill(Color.web("#00ff88"));
            gc.fillPolygon(
                    new double[]{playerX, playerX - 22, playerX + 22},
                    new double[]{H - 40, H - 10, H - 10},
                    3
            );
            gc.setFill(Color.web("#c9d1d9"));
            gc.setFont(Font.font("Verdana", 10));
            gc.fillText(avatar, playerX - 30, H - 45);

            if (!activeDefense.isEmpty()) {
                gc.setStroke(defenseColor(activeDefense));
                gc.setLineWidth(2);
                gc.strokeOval(playerX - 35, H - 55, 70, 50);
            }
        }

        // Overlay: murió pero espera al oponente
        if (waitingForOpponent) {
            gc.setFill(Color.color(0, 0, 0, 0.75));
            gc.fillRect(0, 0, W, H);
            gc.setFill(Color.web("#ff4444"));
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 36));
            gc.fillText("GAME OVER", W / 2 - 110, H / 2 - 20);
            gc.setFill(Color.web("#ffaa00"));
            gc.setFont(Font.font("Verdana", 18));
            gc.fillText("Esperando a " + opponentUsername + "...", W / 2 - 130, H / 2 + 30);
            gc.setFill(Color.web("#c9d1d9"));
            gc.setFont(Font.font("Verdana", 14));
            gc.fillText("Tu score: " + score, W / 2 - 50, H / 2 + 65);
        }

        // Game Over overlay
        if (gameOver && !waitingForOpponent && opponentUsername == null) {
            gc.setFill(Color.color(0, 0, 0, 0.75));
            gc.fillRect(0, 0, W, H);
            gc.setFill(Color.web("#ff4444"));
            gc.setFont(Font.font("Verdana", FontWeight.BOLD, 42));
            gc.fillText("GAME OVER", W / 2 - 120, H / 2);
            gc.setFill(Color.web("#c9d1d9"));
            gc.setFont(Font.font("Verdana", 16));
            gc.fillText("Score final: " + score, W / 2 - 60, H / 2 + 40);
        }
    }

    private void drawAttack(GraphicsContext gc, AttackObj a) {
        switch (a.type) {
            case "DDoS":
                gc.setFill(Color.web("#ff8800"));
                gc.fillOval(a.x - 10, a.y - 10, 22, 22);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Verdana", FontWeight.BOLD, 8));
                gc.fillText("Q", a.x - 4, a.y + 4);
                break;
            case "Malware":
                gc.setFill(Color.web("#ff69b4"));
                gc.fillRect(a.x - 10, a.y - 10, 22, 22);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Verdana", FontWeight.BOLD, 8));
                gc.fillText("W", a.x - 5, a.y + 4);
                break;
            case "Credential":
                gc.setFill(Color.web("#00ffff"));
                gc.fillPolygon(
                        new double[]{a.x, a.x - 12, a.x + 12},
                        new double[]{a.y - 12, a.y + 10, a.y + 10},
                        3
                );
                gc.setFill(Color.BLACK);
                gc.setFont(Font.font("Verdana", FontWeight.BOLD, 8));
                gc.fillText("E", a.x - 3, a.y + 5);
                break;
        }
    }

    private Color defenseColor(String type) {
        switch (type) {
            case "DDoS":       return Color.web("#ff8800");
            case "Malware":    return Color.web("#ff69b4");
            case "Credential": return Color.web("#00ffff");
            default:           return Color.WHITE;
        }
    }

    // ---- Helpers de UI --------

    private Label hudLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        l.setTextFill(Color.web("#c9d1d9"));
        return l;
    }

    private HBox legendItem(String key, String label, String color) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);
        Label k = new Label(key);
        k.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        k.setTextFill(Color.web(color));
        Label l = new Label(label);
        l.setFont(Font.font("Verdana", 11));
        l.setTextFill(Color.GRAY);
        box.getChildren().addAll(k, l);
        return box;
    }
}