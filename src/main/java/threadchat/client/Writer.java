package threadchat.client;

import lombok.Getter;

import java.io.Serializable;

@Getter
public abstract class Writer implements Serializable, WriterDispatcher {
    public enum MessageWriterType {
        NEW_CHANNEL,
        JOIN_CHANNEL,
        GET_HISTORY,
        SEND_FILE,
        JOIN_GENERAL,
        MSG}

    private final MessageWriterType messageWriterType;

    protected Writer(MessageWriterType messageWriterType) {
        this.messageWriterType = messageWriterType;
    }

}