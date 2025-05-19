package io.github.wj9806.minimq.broker.utils;

public interface PutMessageLock {

    void lock();

    void unlock();

}
