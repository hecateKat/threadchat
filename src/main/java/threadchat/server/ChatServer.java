package threadchat.server;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.ClientChatHistory;
import threadchat.server.files.HookFile;
import threadchat.shared.Configuration;
import threadchat.worker.ChatWorker;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


@Log
@Data
public class ChatServer {

    private final ServerSocket serverSocket;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ClientChatHistory clientChatHistory;

    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.clientChatHistory = new ClientChatHistory();
    }

    @SneakyThrows
    public void start() {
        log.info("Server port: " + Configuration.serverPort);
        Runtime.getRuntime().addShutdownHook(new Thread(new HookFile(clientChatHistory)));
        try {
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                log.info("New Client has connected!");
                ChatWorker chatWorker = new ChatWorker(socket, clientChatHistory);
                executorService.execute(chatWorker);
            }
        } catch (IOException e) {
            log.info("Failed to start server: " + e.getMessage());
            stop(serverSocket);
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    shutdownNow();
                }
            } catch (InterruptedException exception) {
                shutdownNow();
            }
        }
    }

    @SneakyThrows
    public static <T extends Closeable> void stop(T socket){
        if (socket != null) socket.close();
    }

    private void shutdownNow() {
        executorService.shutdownNow();
    }
}