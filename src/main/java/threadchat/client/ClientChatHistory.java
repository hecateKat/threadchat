package threadchat.client;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.server.rooms.ChatRoom;
import threadchat.shared.Configuration;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Log
@Data
public class ClientChatHistory {

    private Map<String, ChatRoom> chatRoomMap;
    Path path = Paths.get(String.valueOf(Configuration.chatContentFilePath));


    public ClientChatHistory() {
        readHistory();
    }

    public Boolean checkIfExists(String channelName) {
        return chatRoomMap.containsKey(channelName);
    }

    @SneakyThrows
    private void readHistory() {
        log.info("Checking history file");
        System.out.println(Configuration.chatContentFilePath.length());
        try {
            if (Configuration.chatContentFilePath.length() <= 0) {
                this.setChatRoomMap(new HashMap<>());
                log.info("History is empty");
            } else {
                this.setChatRoomMap(readHistoryFromFile(path));
                log.info("History exists");
            }
        } catch (Exception e) {
            log.warning("No file");
        }
    }

    @SneakyThrows
    private <T, K> Map<T, K> readHistoryFromFile(Path path) {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        Map<T, K> history;
        locked(lock);
        log.info("Getting history from file.");
        FileInputStream fileInputStream = new FileInputStream(String.valueOf(path));
        ObjectInputStream reader = new ObjectInputStream(fileInputStream);
        Object object = reader.readObject();
        reader.close();
        history = (HashMap<T, K>) object;
        unlocked(lock);
        return history;
    }

    private void unlocked(ReadWriteLock lock) {
        lock.readLock().unlock();
    }

    private void locked(ReadWriteLock lock) {
        lock.readLock().lock();
    }
}