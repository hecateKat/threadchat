package threadchat.server.rooms;

import lombok.Getter;
import threadchat.client.message.MessageReader;
import threadchat.client.Writer;

import java.util.List;

@Getter
public class RoomHistoryWriter extends Writer {

    private final List<MessageReader> history;

    public RoomHistoryWriter(MessageWriterType messageWriterType, List<MessageReader> history) {
        super(messageWriterType);
        this.history = history;
    }
}