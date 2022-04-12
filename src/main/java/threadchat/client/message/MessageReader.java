package threadchat.client.message;

import lombok.Getter;
import lombok.Setter;
import threadchat.client.Reader;
import threadchat.client.ReaderDispatcher;

@Getter
@Setter
public class MessageReader extends Reader implements ReaderDispatcher {

    private String content;

    private String roomName;

    public MessageReader(String username, MessageReaderType messageReaderType, String content, String roomName) {
        super(username, messageReaderType);
        this.content = content;
        this.roomName = roomName;
    }

    @Override
    public String
    toString() {
        return "{" + roomName + "}:<" + super.getUsername() + ">: " + content;
    }
}