package threadchat.client;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.message.MessageReader;
import threadchat.server.files.FileTransferProcessor;
import threadchat.server.files.SendFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Log
public class Chat {

    private static final String START_APPLICATION = "Welcome user to the chat. Feel free to type! If you want to leave the chat type !q";

    protected  BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    protected FileTransferProcessor fileTransferProcessor;
    protected ChatClient client;
    private final String roomName;

    public Chat(FileTransferProcessor fileConverter, ChatClient client, String roomName) {
        this.fileTransferProcessor = fileConverter;
        this.client = client;
        this.roomName = roomName;
    }

    @SneakyThrows
    public void start() {
        log.info(START_APPLICATION);
        String s;
        while (true) {
            s = bufferedReader.readLine();
            if (s.equalsIgnoreCase("!q")) {
                break;
            }
            if (s.equalsIgnoreCase("!send")) {
                log.info("Chose file path");
                String path = bufferedReader.readLine();
                File file = new File(path);
                sendFile(file);
            } else {
                sendMessage(s);
            }
        }
    }

    private void sendMessage(String text) {
        MessageReader message = new MessageReader(client.getUsername(), Reader.MessageReaderType.MSG, text, roomName);
        client.send(message);
    }

    private void sendFile(File file) {
        byte[] bytes = fileTransferProcessor.transferToByte(file);
        SendFileReader request = new SendFileReader(client.getUsername(), roomName, Reader.MessageReaderType.SEND_FILE, file.getName(), bytes);
        client.send(request);
    }
}