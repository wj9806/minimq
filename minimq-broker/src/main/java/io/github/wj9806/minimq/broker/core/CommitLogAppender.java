package io.github.wj9806.minimq.broker.core;

import io.github.wj9806.minimq.broker.core.data.Message;

import java.io.IOException;

import static io.github.wj9806.minimq.broker.constants.BrokerConstants.COMMIT_LOG_DEFAULT_SIZE;

public class CommitLogAppender {

    private final MMapFileManager mMapFileManager = new MMapFileManager();

    public void prepareMMap(String topicName) throws IOException {
        MMapFile mMapFile = new MMapFile();
        mMapFile.loadFileInMMap(topicName, 0, COMMIT_LOG_DEFAULT_SIZE);
        mMapFileManager.put(topicName, mMapFile);
    }

    public void appendMsg(String topic, byte[] content) throws IOException {
        MMapFile mmap = mMapFileManager.get(topic);
        if (mmap == null) throw new NullPointerException(topic + " is not found");

        Message message = new Message();
        message.setContent(content);
        mmap.write(message);
    }

    public String readMsg(String topic) {
        MMapFile mmap = mMapFileManager.get(topic);
        return new String(mmap.read(0, 1000));
    }

}
