package threadchat.client;

import lombok.Data;
import threadchat.client.message.MessageReader;

import java.io.Serializable;

@Data
public abstract class Reader implements Serializable, ReaderDispatcher {
    public enum MessageReaderType {
        START,
        NEW_CHANNEL,
        JOIN_CHANNEL, GET_HISTORY,
        SEND_FILE,
        JOIN_GENERAL,
        MSG}

    private String username;

    private MessageReaderType messageReaderType;

    public Reader(String username, MessageReader.MessageReaderType messageReaderType) {
        this.username = username;
        this.messageReaderType = messageReaderType;
    }
}