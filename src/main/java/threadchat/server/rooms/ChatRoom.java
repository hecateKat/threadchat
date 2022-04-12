package threadchat.server.rooms;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import threadchat.client.message.MessageReader;
import threadchat.client.Writer;
import threadchat.worker.ChatWorker;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@Log
public class ChatRoom implements Serializable {

    private final String chatRoomName;

    private List<String> users;

    private List<ChatWorker> loggedUsers;

    private List<MessageReader> messageHistory;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final boolean isPrivate;

    public ChatRoom(String chatRoomName) {
        this.chatRoomName = chatRoomName;
        this.users = new ArrayList<>();
        this.loggedUsers = new ArrayList<>();
        this.messageHistory = new ArrayList<>();
        this.isPrivate = false;
    }

    public ChatRoom(String chatRoomName, boolean isPrivate, List<String> users) {
        this.chatRoomName = chatRoomName;
        this.users = users;
        this.loggedUsers = new ArrayList<>();
        this.messageHistory = new ArrayList<>();
        this.isPrivate = isPrivate;
    }

    @SneakyThrows
    public void addUserToChatRoom(ChatWorker chatWorker) {
        writerLocked();
        try {
            if (isPermittedToJoin(chatWorker.getUsername())) {
                loggedUsers.add(chatWorker);
                if (!isPrivate) {
                    users.add(chatWorker.getUsername());
                }
                log.info("User " + chatWorker.getUsername() + " has entered " + chatRoomName);
            } else throw new Exception("Joining not successful");
        } finally {
            writerUnlocked();
        }
    }

    public Boolean isPermittedToJoin(String userName) {
        if (!this.isPrivate) {
            return true;
        } else {
            for (String user : this.getUsers()) {
                if (user.equals(userName)) return true;
            }
        }
        return false;
    }

    @SneakyThrows
    public void broadcast(ChatWorker chatWorker, Writer writer) {
        readerLocked();
        try {
            for (ChatWorker worker : loggedUsers) {
                System.out.println(worker.getUsername());
                System.out.println(loggedUsers.toString());
                System.out.println(worker);
                if (!worker.getUsername().equals(chatWorker.getUsername())) {
                    getObjectOutputStream(worker).writeObject(writer);
                    getObjectOutputStream(worker).flush();
                }
            }
        } finally {
            readerUnlocked();
        }
    }

    @SneakyThrows
    private void readObject(ObjectInputStream in) {
        in.defaultReadObject();
        loggedUsers = new ArrayList<>();
    }

    public synchronized void save(MessageReader message) {
        messageHistory.add(message);}

    @SneakyThrows
    public synchronized List<MessageReader> readHistory(String username) {
        if (isPossibleToGetRoomHistory(username)) {
            return messageHistory;
        } else {
            throw new Exception("Permission denied");
        }
    }

    @SneakyThrows
    public Boolean isPossibleToGetRoomHistory(String userName){
        return this.getUsers().contains(userName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoom room)) return false;
        return chatRoomName.equals(room.getChatRoomName());
    }

    @Override
    public int hashCode() {
        return chatRoomName.hashCode();
    }

    private ObjectOutputStream getObjectOutputStream(ChatWorker worker) {
        return worker.getObjectOutputStream();
    }

    private void writerLocked() {
        lock.writeLock().lock();
    }

    private void writerUnlocked() {
        lock.writeLock().unlock();
    }

    private void readerLocked() {
        lock.readLock().lock();
    }

    private void readerUnlocked() {
        lock.readLock().unlock();
    }
}