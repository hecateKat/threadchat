package threadchat.server.files;

import lombok.SneakyThrows;
import threadchat.client.ClientChatHistory;
import threadchat.shared.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HookFile implements Runnable {

    private final ClientChatHistory clientChatHistory;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public HookFile(ClientChatHistory clientChatHistory) {
        this.clientChatHistory = clientChatHistory;
    }

    @Override
    public void run() {
        save(Configuration.chatContentFilePath, clientChatHistory.getChatRoomMap());
    }

    @SneakyThrows
    public <T extends Map> void save(File filePath, T map) {
        try {
            lock.readLock().lock();
            ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(filePath));
            writerOperating(map, writer);
        } finally {
            lock.readLock().unlock();
        }
    }

    private <T extends Map> void writerOperating(T map, ObjectOutputStream writer) throws IOException {
        writer.writeObject(map);
        writer.flush();
        writer.close();
    }
}