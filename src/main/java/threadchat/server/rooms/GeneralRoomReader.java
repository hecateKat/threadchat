package threadchat.server.rooms;

import lombok.Getter;
import threadchat.client.Reader;

@Getter
public class GeneralRoomReader extends Reader {

    private String roomName;

    public GeneralRoomReader(String username, MessageReaderType messageReaderType, String roomName) {
        super(username, messageReaderType);
        this.roomName = roomName;
    }
}