package threadchat.client;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.message.MessageWriterDispatcher;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Getter
@Setter
@Log
public class ChatClient {

    private final WriterLock lock = new WriterLock();
    private final MessageWriterDispatcher messageWriterDispatcher = new MessageWriterDispatcher(this);

    private String username;
    private String roomname;
    private boolean isNotLockedToRoom;

    private Socket socket;
    private ObjectInputStream inputStreamReader;
    private ObjectOutputStream outputStreamWriter;

    @SneakyThrows
    public ChatClient(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
        this.inputStreamReader = new ObjectInputStream(socket.getInputStream());
        this.outputStreamWriter = new ObjectOutputStream(socket.getOutputStream());
    }

    @SneakyThrows
    public void send(Reader reader) {
        outputStreamWriter.writeObject(reader);
        outputStreamWriter.flush();
    }

    public void start() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    messageWriterDispatcher.getCommand((Writer) inputStreamReader.readUnshared());
                } catch (IOException | ClassNotFoundException e) {
                    close(socket, outputStreamWriter, inputStreamReader);
                }}}).start();
    }

    @SneakyThrows
    public void close(Socket socket, ObjectOutputStream outputStream, ObjectInputStream inputStream) {
        if (outputStream != null) {outputStream.close();}
        if (inputStream != null) {inputStream.close();}
        if (socket != null) {socket.close();}
    }
}