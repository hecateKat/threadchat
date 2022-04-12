package threadchat.server.files;

import lombok.Getter;
import threadchat.client.Reader;

@Getter
public class SendFileReader extends Reader {

    private final byte[] byteFile;

    private final String fileName;

    private final String roomName;

    public SendFileReader(String username, String roomName, MessageReaderType messageReaderType, String fileName, byte[] byteFile) {
        super(username, messageReaderType);
        this.roomName = roomName;
        this.byteFile = byteFile;
        this.fileName = fileName;
    }
}