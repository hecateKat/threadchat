package threadchat.client.message;

import threadchat.client.Writer;
import threadchat.client.WriterDispatcher;

public class MessageWriter extends Writer implements WriterDispatcher {

    private final String username;

    private final String roomName;

    private final String content;

    public MessageWriter(MessageWriterType messageWriterType, String username, String roomName, String content) {
        super(messageWriterType);
        this.username = username;
        this.roomName = roomName;
        this.content = content;
    }

    @Override
    public String
    toString() {
        return "{" + roomName + "}:<" + username + ">: " + content;
    }

}