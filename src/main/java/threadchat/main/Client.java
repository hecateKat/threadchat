package threadchat.main;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.Chat;
import threadchat.client.ChatClient;
import threadchat.client.Reader;
import threadchat.server.files.FileTransferProcessor;
import threadchat.server.rooms.*;
import threadchat.shared.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

@Log
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(Configuration.serverHost, Configuration.serverPort);
        String username = typeNickname();
        ChatClient chatClient = new ChatClient(socket, username);
        chatClient.start();
        chatClient.send(new RoomReader(username, Reader.MessageReaderType.START));
        log.info("Welcome to threadchat");
        log.info("To join public room type !general");
        log.info("To create private chat type !private");
        log.info("To join private chat type !joinp");
        log.info("To download history type !history");
        choseMode(chatClient);

    }

    @SneakyThrows
    public static String typeNickname() {
        log.info("Enter username: ");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        return bufferedReader.readLine();
    }

    @SneakyThrows
    private static void choseMode(ChatClient client) {
        FileTransferProcessor fileTransferProcessor = new FileTransferProcessor();
        while (true) {
            String mode;
            System.out.print("Choose mode: ");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            mode = bufferedReader.readLine();
            switch (mode) {
                case "!general" -> {
                    String roomName = "general";
                    locks(client);
                    try {
                        client.send(new GeneralRoomReader(client.getUsername(), Reader.MessageReaderType.JOIN_GENERAL, roomName));
                        awaits(client);
                    } finally {
                        unlock(client);
                    }
                    Chat chat = new Chat(fileTransferProcessor, client, roomName);
                    chat.start();
                }
                case "!private" -> {
                    log.info("Type room name: ");
                    String newPrivateChannelName = bufferedReader.readLine();
                    log.info("Chose your partners! When finished typed !done.");
                    ArrayList<String> users = new ArrayList<>();
                    users.add(client.getUsername());
                    while (true) {
                        String allowed = bufferedReader.readLine();
                        if (allowed.equalsIgnoreCase("!done")) {
                            break;
                        }
                        users.add(allowed);
                    }
                    locks(client);
                    try {
                        client.send(new PrivateChatRoomReader(client.getUsername(), newPrivateChannelName, Reader.MessageReaderType.NEW_CHANNEL, users));
                        awaits(client);
                    } finally {
                        unlock(client);
                    }
                    if(notLocked(client)){
                        Chat chat = new Chat(fileTransferProcessor, client, newPrivateChannelName);
                        chat.start();
                    }
                }
                case "!joinp" -> {
                    System.out.println("Type room name you want to join: ");
                    String privateChannelName = bufferedReader.readLine();
                    locks(client);
                    try {
                        client.send(new InvitePrivateChatRoomReader(client.getUsername(), Reader.MessageReaderType.JOIN_CHANNEL, privateChannelName));
                        awaits(client);
                    } finally {
                        unlock(client);
                    }
                    if(notLocked(client)){
                        Chat chat = new Chat(fileTransferProcessor, client, privateChannelName);
                        chat.start();
                    }
                }
                case "!history" -> {
                    System.out.println("Type room name you want to download history: ");
                    String historicChannelName = bufferedReader.readLine();
                    locks(client);
                    try {
                        client.send(new RoomHistoryReader(client.getUsername(), historicChannelName, Reader.MessageReaderType.GET_HISTORY));
                        awaits(client);
                    } finally {
                        unlock(client);
                    }
                }
            }
        }
    }

    private static boolean notLocked(ChatClient client) {
        return client.isNotLockedToRoom();
    }

    private static void locks(ChatClient client) {
        client.getLock().getLock().lock();
    }

    private static void awaits(ChatClient client) throws InterruptedException {
        client.getLock().getCondition().await();
    }

    private static void unlock(ChatClient client) {
        client.getLock().getLock().unlock();
    }
}