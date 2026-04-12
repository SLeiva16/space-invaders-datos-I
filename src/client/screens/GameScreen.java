package client.screens;

import client.ClientConnection;
import javafx.animation.AnimationTimer;
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
    private static final int INITIAL_LIVES = 3;

    private final Stage stage;
    private final String username;
    private final String avatar;
    private final String map;
    private final ClientConnection conexion;

    // Estado del jugador
    private double playerX = W / 2;
    private int lives = INITIAL_LIVES;
    private int score = 0;
    private int blockedAttacks = 0;
    private String activeDefense = "";
    private boolean gameOver = false;

    // Teclas presionadas
    private boolean leftDown = false;
    private boolean rightDown = false;

    // Ataques activos — usa List propia (sin ArrayList)
    private List<AttackObj> attacks = new List<>();
    private Random random = new Random();

    // HUD
    private Label livesLabel;
    private Label scoreLabel;
    private Label defenseLabel;
    private Label blockedLabel;
    private ProgressBar livesBar;

    // Clase interna para ataques
    private static class AttackObj
    {
        String type;
        double x, y;
        double speed;
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
                      String map, ClientConnection conexion) {
        this.stage = stage;
        this.username = username;
        this.avatar = avatar;
        this.map = map;
        this.conexion = conexion;
    }

    public void show() {

        // ---- HUD superior ----
        HBox hud = new HBox(20);
        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setPadding(new Insets(8, 16, 8, 16));
        hud.setStyle("-fx-background-color: #161b22;");

        livesBar = new ProgressBar(1.0);
        livesBar.setPrefWidth(100);
        livesBar.setStyle("-fx-accent: #00ff88;");

        livesLabel   = hudLabel("Vidas: " + lives);
        scoreLabel   = hudLabel("Score: 0");
        defenseLabel = hudLabel("Defensa: Ninguna");
        blockedLabel = hudLabel("Bloqueados: 0");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label mapLabel = hudLabel("Mapa: " + map);
        mapLabel.setTextFill(Color.web("#58a6ff"));

        hud.getChildren().addAll(livesBar, livesLabel, scoreLabel,
                defenseLabel, blockedLabel, spacer, mapLabel);

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

        VBox root = new VBox(0, hud, canvas, legend);
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

                if (!gameOver) {
                    update(delta);
                }
                render(gc);
            }
        }.start();
    }

    // ---- Lógica del juego -----

    private void update(double delta) {
        // Movimiento del jugador
        if (leftDown)  playerX = Math.max(20, playerX - 200 * delta);
        if (rightDown) playerX = Math.min(W - 20, playerX + 200 * delta);

        // Generar ataques aleatorios (≈1% por frame a 60fps)
        if (random.nextInt(100) < 1) {
            String[] tipos = {"DDoS", "Malware", "Credential"};
            String tipo = tipos[random.nextInt(3)];
            double x = 50 + random.nextDouble() * (W - 100);
            attacks.add(new AttackObj(tipo, x, 0, 1.2 + (score / 200.0)));
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
                    updateHUD();
                } else {
                    // Defensa incorrecta o sin defensa
                    lives--;
                    a.active = false;
                    toRemove.add(i);
                    updateHUD();
                    if (lives <= 0) triggerGameOver();
                }
            }
        }

        // Eliminar ataques procesados (de atrás para adelante)
        for (int i = toRemove.size() - 1; i >= 0; i--) {
            attacks.remove(toRemove.get(i));
        }
    }

    private void activateDefense(String type) {
        activeDefense = type;
        defenseLabel.setText("Defensa: " + type);
    }

    private void updateHUD() {
        livesLabel.setText("Vidas: " + lives);
        scoreLabel.setText("Score: " + score);
        blockedLabel.setText("Bloqueados: " + blockedAttacks);
        livesBar.setProgress((double) lives / INITIAL_LIVES);
        if (lives == 1) livesBar.setStyle("-fx-accent: #ff4444;");
        else if (lives == 2) livesBar.setStyle("-fx-accent: #ffaa00;");
    }

    private void triggerGameOver() {
        gameOver = true;
        // Transición a GameOverScreen después de 2 segundos
        new Thread(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                GameOverScreen over = new GameOverScreen(stage, username, score, blockedAttacks, conexion);
                over.show();
            });
        }).start();
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

        // Dibujar jugador (triángulo)
        gc.setFill(Color.web("#00ff88"));
        double[] xs = {playerX, playerX - 22, playerX + 22};
        double[] ys = {H - 40, H - 10, H - 10};
        gc.fillPolygon(xs, ys, 3);

        // Nombre del avatar sobre el jugador
        gc.setFill(Color.web("#c9d1d9"));
        gc.setFont(Font.font("Verdana", 10));
        gc.fillText(avatar, playerX - 30, H - 45);

        // Defensa activa visual
        if (!activeDefense.isEmpty()) {
            gc.setStroke(defenseColor(activeDefense));
            gc.setLineWidth(2);
            gc.strokeOval(playerX - 35, H - 55, 70, 50);
        }

        // Game Over overlay
        if (gameOver) {
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