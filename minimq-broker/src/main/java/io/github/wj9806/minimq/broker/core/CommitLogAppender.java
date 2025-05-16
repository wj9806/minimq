package io.github.wj9806.minimq.broker.core;

import io.github.wj9806.minimq.broker.model.MessageModel;

import java.io.IOException;

public class CommitLogAppender {

    private final MMapFileModelManager mMapFileModelManager = new MMapFileModelManager();

    public void prepareMMap(String topicName) throws IOException {
        MMapFileModel mMapFileModel = new MMapFileModel();
        mMapFileModel.loadFileInMMap(topicName, 0, 1024 * 1024);
        mMapFileModelManager.put(topicName, mMapFileModel);
    }

    public void appendMsg(String topic, byte[] content) {
        MMapFileModel mmap = mMapFileModelManager.get(topic);
        if (mmap == null) throw new NullPointerException(topic + " is not found");

        MessageModel messageModel = new MessageModel();
        messageModel.setContent(content);
        messageModel.setSize(content.length);
        mmap.write(messageModel);
    }

    public String readMsg(String topic) {
        MMapFileModel mmap = mMapFileModelManager.get(topic);
        return new String(mmap.read(0, 10));
    }

}
