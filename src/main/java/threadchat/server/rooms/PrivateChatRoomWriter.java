package threadchat.server.rooms;

import lombok.Getter;
import lombok.Setter;
import threadchat.client.Writer;

import java.util.List;

@Getter
@Setter
public class PrivateChatRoomWriter extends Writer {

    private List<String> users;

    private boolean isLocked;

    public PrivateChatRoomWriter(MessageWriterType messageWriterType, Boolean isLocked, List<String> users) {
        super(messageWriterType);
        this.users = users;
        this.isLocked = isLocked;
    }

    public PrivateChatRoomWriter(MessageWriterType messageWriterType, Boolean isLocked) {
        super(messageWriterType);
        this.isLocked = isLocked;
    }
}