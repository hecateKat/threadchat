package threadchat.server.rooms;

import lombok.Getter;
import threadchat.client.Reader;
import threadchat.client.ReaderDispatcher;

@Getter
public class RoomReader extends Reader implements ReaderDispatcher {

    public RoomReader(String username, MessageReaderType messageReaderType) {
        super(username, messageReaderType);
    }
}