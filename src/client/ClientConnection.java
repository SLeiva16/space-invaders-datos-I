package client;

import com.google.gson.Gson;
import Models.Message;
//Message es para la comunicación

import java.io.*;
import java.net.Socket;


public class ClientConnection {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;

    public ClientConnection(String ip, int port) throws IOException{
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        gson = new Gson();

    }

    public boolean login(String username, String password) throws IOException{
        Message message = new Message();
        message.type = "LOGIN";
        message.username = username;
        message.password = password;

        out.println(gson.toJson(message));
        String answerJson = in.readLine();
        Message answer = gson.fromJson(answerJson, Message.class);
        return answer.success;

    }

    public boolean register(String username, String password) throws IOException{
        Message message = new Message();
        message.type = "REGISTER";
        message.username = username;
        message.password = password;

        out.println(gson.toJson(message));
        String answerJson = in.readLine();
        Message answer = gson.fromJson(answerJson, Message.class);

        return answer.success;
    }

    public Message send(Message message) throws Exception {
        String json = gson.toJson(message);
        out.println(json);

        String answer = in.readLine();
        return gson.fromJson(answer, Message.class);
    }
}
