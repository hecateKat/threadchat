package threadchat.client.message;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.ClientChatHistory;
import threadchat.client.ReaderDispatcher;
import threadchat.client.Writer;
import threadchat.server.files.SendFileReader;
import threadchat.server.files.SendFileWriter;
import threadchat.server.rooms.*;
import threadchat.worker.ChatWorker;

import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log
public class MessageReaderDispatcher {

    private final ChatWorker chatWorker;

    private final ClientChatHistory chatHistory;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public MessageReaderDispatcher(ChatWorker chatWorker, ClientChatHistory chatHistory) {
        this.chatWorker = chatWorker;
        this.chatHistory = chatHistory;
    }

    public void getCommand(ReaderDispatcher messageReaderType) {
        System.out.println(messageReaderType.toString());
        switch (messageReaderType.getMessageReaderType()) {
            case START -> startChatting((RoomReader) messageReaderType);
            case NEW_CHANNEL -> createNewChannel((PrivateChatRoomReader) messageReaderType);
            case JOIN_GENERAL -> getToGeneral((GeneralRoomReader) messageReaderType);
            case JOIN_CHANNEL -> joinPrivateChannel((InvitePrivateChatRoomReader) messageReaderType);
            case GET_HISTORY -> getHistory((RoomHistoryReader) messageReaderType);
            case SEND_FILE -> sendFile((SendFileReader) messageReaderType);
            case MSG -> saveMsg((MessageReader) messageReaderType);
        }
    }

    private void startChatting(RoomReader room){
        chatWorker.setUsername(room.getUsername());
    }

    private void saveMsg(MessageReader messageReaderType) {
        MessageWriter msg = new MessageWriter(Writer.MessageWriterType.MSG,
                messageReaderType.getUsername(),
                messageReaderType.getRoomName(),
                messageReaderType.getContent());
        getChatRoom(messageReaderType.getRoomName()).save(messageReaderType);
        getChatRoom(messageReaderType.getRoomName()).broadcast(chatWorker, msg);

    }

    public void createNewChannel(PrivateChatRoomReader room) {
        log.info("Private room");
        if (chatHistory.checkIfExists(room.getUsername())) {
            broadcast(chatWorker, new PrivateChatRoomWriter(Writer.MessageWriterType.NEW_CHANNEL, false));
        } else {
            ChatRoom chatRoom = new ChatRoom(room.getRoomName(), room.isPrivate(), room.getUsers());
            chatRoom.getLoggedUsers().add(chatWorker);
            getChatRoomMap().put(chatRoom.getChatRoomName(), chatRoom);
            broadcast(chatWorker, new PrivateChatRoomWriter(Writer.MessageWriterType.NEW_CHANNEL, true, room.getUsers()));
        }
    }

    @SneakyThrows
    public void getToGeneral(GeneralRoomReader room) {
        log.info("General room");
        System.out.println(getChatRoomMap().toString());

        if (getChatRoomMap().containsKey(room.getRoomName())) {
                getChatRoom(room.getRoomName()).addUserToChatRoom(chatWorker);
                GeneralRoomWriter writer = new GeneralRoomWriter(Writer.MessageWriterType.JOIN_GENERAL, room.getRoomName());
                broadcast(chatWorker, writer);
                getChatRoom(room.getRoomName()).broadcast(chatWorker,
                        new MessageWriter(Writer.MessageWriterType.JOIN_GENERAL,
                                "General",
                                room.getUsername(),
                                "joined"));
        } else {
            getLock().lock();
            try {
                ChatRoom chatRoom = new ChatRoom(room.getRoomName());
                chatRoom.getUsers().add(room.getUsername());
                chatRoom.getLoggedUsers().add(chatWorker);
                getChatRoomMap().put(room.getRoomName(), chatRoom);
                broadcast(chatWorker, new GeneralRoomWriter(Writer.MessageWriterType.JOIN_GENERAL, room.getRoomName()));
            } finally {
                getLock().unlock();
            }
        }

    }

    @SneakyThrows
    public void joinPrivateChannel(InvitePrivateChatRoomReader room) {
        log.info("Welcome to private room" + room.getRoomName());
        if (getChatRoomMap().containsKey(room.getRoomName())) {
            getChatRoom(room.getRoomName()).addUserToChatRoom(chatWorker);
            broadcast(chatWorker, new InvitePrivateChatRoomWriter(Writer.MessageWriterType.JOIN_CHANNEL, true, room.getRoomName()));
            getChatRoom(room.getRoomName())
                    .broadcast(chatWorker, new MessageWriter(Writer.MessageWriterType.JOIN_CHANNEL,
                            "Private",
                            room.getUsername(),
                            "joined"));
        } else {
            broadcast(chatWorker, new InvitePrivateChatRoomWriter(Writer.MessageWriterType.JOIN_CHANNEL, false, room.getRoomName()));
        }
    }


    @SneakyThrows
    public void getHistory(RoomHistoryReader roomHistoryReader) {
        log.info("Room history");
        List<MessageReader> history;
        history = getChatRoom(roomHistoryReader.getRoomName()).readHistory(roomHistoryReader.getUsername());
        RoomHistoryWriter writer = new RoomHistoryWriter(Writer.MessageWriterType.GET_HISTORY, history);
        broadcast(chatWorker, writer);
    }

    protected void sendFile(SendFileReader reader) {
        SendFileWriter sendFileWriter = new SendFileWriter(Writer.MessageWriterType.SEND_FILE, reader.getUsername(), reader.getFileName(), reader.getByteFile());
        getChatRoom(reader.getRoomName()).broadcast(chatWorker, sendFileWriter);
        log.info("File send");
    }

    @SneakyThrows
    public static void broadcast(ChatWorker chatWorker, Writer writer) {
        getObjectOutputStream(chatWorker).writeObject(writer);
        getObjectOutputStream(chatWorker).flush();
        getObjectOutputStream(chatWorker).reset();
    }

    private static ObjectOutputStream getObjectOutputStream(ChatWorker chatWorker) {
        return chatWorker.getObjectOutputStream();
    }

    private ChatRoom getChatRoom(String messageReaderType) {
        return getChatRoomMap().get(messageReaderType);
    }

    private Map<String, ChatRoom> getChatRoomMap() {
        return chatHistory.getChatRoomMap();
    }

    private Lock getLock() {
        return lock.writeLock();
    }

}