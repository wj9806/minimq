package io.github.wj9806.minimq.broker.core;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QueueMMapFileManager {

    private static final Map<String, List<QueueMMapFile>> QUEUE_FILES = new ConcurrentHashMap<>();

    public void put(String topic, List<QueueMMapFile> queueMMapFiles) {
        QUEUE_FILES.put(topic, queueMMapFiles);
    }

    public List<QueueMMapFile> get(String topic) {
        return QUEUE_FILES.get(topic);
    }
}
