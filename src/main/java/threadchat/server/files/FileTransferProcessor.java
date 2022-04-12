package threadchat.server.files;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;

public class FileTransferProcessor {

    @SneakyThrows
    public byte[] transferToByte(File path) {
        return Files.readAllBytes(path.toPath());
    }
}