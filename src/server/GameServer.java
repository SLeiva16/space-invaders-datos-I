package server;

import javax.xml.crypto.Data;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    private static final int PORT = 12345;

    public static void main(String[] args) {

        DatabaseManager db = new DatabaseManager();

        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Space Invaders Game");
            System.out.println("El server inicio en el puerto: " + PORT);

            while (true) {
                //El servidor se queda bloqueado hasta que un cliente se conecte
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] Se conecto: " +
                        clientSocket.getInetAddress().getHostName());

                ClientHandler handler = new ClientHandler(clientSocket, db);
                new Thread(handler).start();

            }
        } catch (Exception e) {
            System.out.println("[Server] Error: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
