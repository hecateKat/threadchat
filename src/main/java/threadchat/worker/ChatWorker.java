package threadchat.worker;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.ClientChatHistory;
import threadchat.client.message.MessageReaderDispatcher;
import threadchat.client.ReaderDispatcher;
import threadchat.server.rooms.ChatRoom;
import threadchat.server.ChatServer;

import java.io.*;
import java.net.Socket;
import java.util.Map;

@Log
@Getter
@Setter
public class ChatWorker implements Runnable {

    private final Socket socket;

    private ObjectInputStream objectInputStream;

    private ObjectOutputStream objectOutputStream;

    private MessageReaderDispatcher messageReaderDispatcher;

    private ClientChatHistory chatHistory;

    private String username;

    public ChatWorker(Socket socket, ClientChatHistory chatHistory) {
        this.chatHistory = chatHistory;
        this.socket = socket;
        try {
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
            this.messageReaderDispatcher = new MessageReaderDispatcher(this, chatHistory);
        } catch (IOException e) {
            e.printStackTrace();
            closeStreams(objectInputStream, objectOutputStream);
            stoppingSocket(socket);
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                ReaderDispatcher message = ((ReaderDispatcher) objectInputStream.readObject());
                messageReaderDispatcher.getCommand(message);
            } catch (IOException | ClassNotFoundException e) {
                removeClient(chatHistory.getChatRoomMap(), this);
                closeStreams(objectInputStream, objectOutputStream);
                stoppingSocket(socket);
                break;
            }
        }
    }


    @SneakyThrows
    public static <E extends InputStream, T extends OutputStream> void closeStreams(E inputStream, T outputStream) {
        if (outputStream != null) {
            outputStream.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
    }

    public void removeClient(Map<String, ChatRoom> rooms, ChatWorker chatWorker) {
        for (ChatRoom room : rooms.values()) {
            room.getLoggedUsers().remove(chatWorker);
        }
    }

    private void stoppingSocket(Socket socket) {
        ChatServer.stop(socket);
    }
}