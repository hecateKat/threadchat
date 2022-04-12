package threadchat.client;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
public class WriterLock {
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
}