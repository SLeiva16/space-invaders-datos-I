package server;

import Models.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import structures.List;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.security.MessageDigest;

/*
    Guardado de cada Jugador mediante database.json
    Lee al iniciar y escribe despues de cada sesion
*/
public class DatabaseManager {

    private static final String DB_FILE = "database.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Guarda los Jugadores en memoria
    private List<Player> players;

    public DatabaseManager() {
        this.players = new List<>();
        load();   //lee el json si existe
    }




    public synchronized boolean register(String username, String password) {
        System.out.println("Database Register llamado con: " + username);

        if (findPlayer(username) != null) {
            System.out.println("Falso");
            return false;
        }
        Player p = new Player(username, hash(password));  //Crea al jugador con la contrase;a hasheada
        players.add(p);

        System.out.println("Tamaño de la lista players después de agregar " + players.size());

        System.out.println("Database Usuario agregado, guardando...");
        save();
        return true;
    }


    public synchronized boolean authenticate(String username, String password) {
        Player p = findPlayer(username);
        if (p == null) return false;
        return p.getPasswordHash().equals(hash(password)); //haseha la contra y la compara con la guardada
    }

    //Obtener usuario por nombre
    public synchronized Player getPlayer(String username) {
        return findPlayer(username);
    }


    public synchronized void updateAvatar(String username, String avatar) {
        Player p = findPlayer(username);
        if (p != null) {
            p.setAvatar(avatar);
            save();
        }
    }


    public synchronized void saveSessionResult(String username, int sessionScore,
                                               int xpNetwork, int xpMalware, int xpCrypto) {
        Player p = findPlayer(username);
        if (p != null) {
            p.addScore(sessionScore);
            p.incrementGamesPlayed();
            p.addXpNetwork(xpNetwork);
            p.addXpMalware(xpMalware);
            p.addXpCrypto(xpCrypto);
            save();
        }
    }

    // ====== Private helpers ======

    private Player findPlayer(String username) {
        System.out.println("Database buscando al usuario: " + username);

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);

            System.out.println("Comparando con " + p.getUsername());

            if (p.getUsername().equals(username)) {
                System.out.println("Database Usuario encontado");
                return p;
            }
        }

        System.out.println("Usuario no encontado.");
        return null;
    }

    private void load() {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            System.out.println("[DB] No se encontraron archivos en database.json");
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<Player[]>(){}.getType();
            Player[] loaded = GSON.fromJson(reader, Player[].class); //Gson lee el json y lo convierte en un array
            if (loaded != null) {
                for (Player p : loaded) {
                    players.add(p);
                }
            }
            System.out.println("[DB] Cargando " + players.size() + " jugadores.");
        } catch (Exception e) {
            System.err.println("[DB] Error al cargar el database.json: " + e.getMessage());
        }
    }

    private void save() {
        Player[] arr = new Player[players.size()];
        for (int i = 0; i < players.size(); i++) {
            arr[i] = players.get(i);
        }
        //convierte List en un array para el Gson(convertir objetos en cadena de texto)
        try (Writer writer = new FileWriter(DB_FILE)) {
            GSON.toJson(arr, writer); //array -> json
        } catch (IOException e) {
            System.err.println("[DB] Error saving database.json: " + e.getMessage());
        }
    }

    // SHA-256 hash para las contrase;as
    private String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            //convierte la contraseña en un array de bytes cifrados
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b)); //convierte 2 caracteres en hexadecimal
            }
            return sb.toString();
        } catch (Exception e) {
            return input;
        }
    }
}