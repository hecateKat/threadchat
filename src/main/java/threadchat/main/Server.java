package threadchat.main;

import lombok.SneakyThrows;
import threadchat.server.ChatServer;
import threadchat.shared.Configuration;

import java.net.ServerSocket;

public class Server {

    @SneakyThrows
    public static void main(String[] args) {
        ServerSocket serverSocket = new ServerSocket(Configuration.serverPort);
        ChatServer chatServer = new ChatServer(serverSocket);
        chatServer.start();
    }
}