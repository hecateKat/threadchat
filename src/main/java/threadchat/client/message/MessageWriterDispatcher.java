package threadchat.client.message;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.ChatClient;
import threadchat.client.WriterDispatcher;
import threadchat.server.files.SendFileWriter;
import threadchat.server.rooms.InvitePrivateChatRoomWriter;
import threadchat.server.rooms.PrivateChatRoomWriter;
import threadchat.server.rooms.RoomHistoryWriter;
import threadchat.shared.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

@Log
public class MessageWriterDispatcher {

    ChatClient client;

    public MessageWriterDispatcher(ChatClient client) {
        this.client = client;
    }

    public void getCommand(WriterDispatcher messageWriterType) {
        switch (messageWriterType.getMessageWriterType()) {
            case NEW_CHANNEL -> createNewChannel((PrivateChatRoomWriter) messageWriterType);
            case JOIN_GENERAL -> getToGeneral();
            case JOIN_CHANNEL -> joinPrivateChannel((InvitePrivateChatRoomWriter) messageWriterType);
            case GET_HISTORY -> getHistory((RoomHistoryWriter) messageWriterType);
            case SEND_FILE -> sendFile((SendFileWriter) messageWriterType);
            case MSG -> sendMsg((MessageWriter) messageWriterType);
        }
    }

    private void sendMsg(MessageWriter messageWriterType) {
        System.out.println(messageWriterType);
    }

    @SneakyThrows
    private void sendFile(SendFileWriter messageWriterType) {
        String pathFile = Configuration.fileStorePath + messageWriterType.getFileName();
        System.out.println("path: " + pathFile);
        File file = new File(pathFile);
        OutputStream os = new FileOutputStream(file);
        os.write((messageWriterType).getByteFile());
        os.close();
    }

    private void getHistory(RoomHistoryWriter messageWriterType) {
        locked();
        List<MessageReader> roomHistory = messageWriterType.getHistory();
        try {
            for (MessageReader message : roomHistory) {
                log.info(message.toString());
            }
            getConditionSignal();
        } finally {
            unlocked();
        }
    }

    private void joinPrivateChannel(InvitePrivateChatRoomWriter messageWriterType) {
        locked();
        try {
            client.setNotLockedToRoom(messageWriterType.isLocked());
            if (!client.isNotLockedToRoom()) {
                log.info("Access denied");
            }
            getConditionSignal();
        } finally {
            unlocked();
        }
    }


    private void getToGeneral() {
        locked();
        try {
            log.info("join to general");
            getConditionSignal();
        } finally {
            unlocked();
        }
    }

    private void createNewChannel(PrivateChatRoomWriter messageWriterType) {
        locked();
        try {
            client.setNotLockedToRoom(messageWriterType.isLocked());
            if (client.isNotLockedToRoom()) {
                System.out.println("Room created.");
            } else {
                System.out.println("Room exists");
            }
            getConditionSignal();
        } finally {
            unlocked();
        }
    }

    private void unlocked() {
        client.getLock().getLock().unlock();
    }

    private void getConditionSignal() {
        client.getLock().getCondition().signal();
    }

    private void locked() {
        client.getLock().getLock().lock();
    }
}