package io.github.wj9806.minimq.broker.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MMapFileManager {

    /**
     * key:主题名称
     * value:MMap对象
     */
    private static final Map<String, MMapFile> MMAPS = new ConcurrentHashMap<>();

    public void put(String topic, MMapFile mMapFile) {
        MMAPS.put(topic, mMapFile);
    }

    public MMapFile get(String topic) {
        return MMAPS.get(topic);
    }
}
