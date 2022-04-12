package threadchat.server.rooms;

import lombok.Getter;
import lombok.Setter;
import threadchat.client.message.MessageReader;
import threadchat.client.Reader;

import java.util.List;
@Getter
@Setter
public class PrivateChatRoomReader extends Reader {

    private boolean isPrivate = true;

    private List<String> users;

    private String roomName;

    public PrivateChatRoomReader(String username, String roomName, MessageReader.MessageReaderType messageReaderType, List<String> users) {
        super(username, messageReaderType);
        this.users = users;
        this.roomName = roomName;
    }

}