package threadchat.server.files;

import lombok.Getter;
import threadchat.client.Writer;

@Getter
public class SendFileWriter extends Writer {

    private final byte[] byteFile;

    private final String fileName;

    private final String username;

    public SendFileWriter(MessageWriterType messageWriterType, String userName, String fileName, byte[] byteFile) {
        super(messageWriterType);
        this.byteFile = byteFile;
        this.fileName = fileName;
        this.username = userName;
    }
}