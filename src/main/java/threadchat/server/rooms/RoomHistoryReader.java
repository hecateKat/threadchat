package threadchat.server.rooms;

import lombok.Getter;
import threadchat.client.message.MessageReader;
import threadchat.client.Reader;

public class RoomHistoryReader extends Reader {

    @Getter
    private final String roomName;

    public RoomHistoryReader(String username, String roomName, MessageReader.MessageReaderType messageReaderType) {
        super(username, messageReaderType);
        this.roomName = roomName;
    }
}