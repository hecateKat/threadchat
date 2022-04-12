package threadchat.server.rooms;

import lombok.Getter;
import threadchat.client.Reader;

@Getter
public class InvitePrivateChatRoomReader extends Reader {

    private final String roomName;

    public InvitePrivateChatRoomReader(String username, MessageReaderType messageReaderType, String roomName) {
        super(username, messageReaderType);
        this.roomName = roomName;
    }
}