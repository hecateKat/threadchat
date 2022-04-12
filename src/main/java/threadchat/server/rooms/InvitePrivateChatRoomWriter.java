package threadchat.server.rooms;

import lombok.Getter;
import threadchat.client.Writer;

@Getter
public class InvitePrivateChatRoomWriter extends Writer {

    private final String roomName;

    private final boolean isLocked;

    public InvitePrivateChatRoomWriter(MessageWriterType messageWriterType, Boolean isLocked, String roomName) {
        super(messageWriterType);
        this.isLocked = isLocked;
        this.roomName = roomName;
    }
}