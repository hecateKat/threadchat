package threadchat.client;

public interface ReaderDispatcher {

    Reader.MessageReaderType getMessageReaderType();

    String getUsername();
}