package io.github.wj9806.minimq.broker.core;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.core.data.Queue;
import io.github.wj9806.minimq.broker.core.data.Topic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QueueAppender {

    public static final QueueAppender QUEUE_APPENDER = new QueueAppender();

    private static final QueueMMapFileManager QUEUE_M_MAP_FILE_MANAGER = new QueueMMapFileManager();

    private QueueAppender() {
    }

    public void prepareQueue(String topicName) throws IOException {
        Topic topic = CommonCache.getTopicMap().get(topicName);
        List<Queue> queueList = topic.getQueueList();

        List<QueueMMapFile> queueMMapFiles = new ArrayList<>();
        for (Queue queue : queueList) {
            String fileName = queue.getFileName();
            int offsetLimit = queue.getOffsetLimit();
            int lastOffset = queue.getLastOffset();

            QueueMMapFile queueMMapFile = new QueueMMapFile();
            queueMMapFile.loadFileInMMap(topicName, queue.getId(), fileName, lastOffset, offsetLimit);

            queueMMapFiles.add(queueMMapFile);
        }

        QUEUE_M_MAP_FILE_MANAGER.put(topicName, queueMMapFiles);
    }
}
