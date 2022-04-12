package threadchat.server.rooms;

import lombok.Getter;
import threadchat.client.Writer;

@Getter
public class GeneralRoomWriter extends Writer {

    private String roomName;

    public GeneralRoomWriter(MessageWriterType messageWriterType, String roomName) {
        super(messageWriterType);
        this.roomName = roomName;
    }
}