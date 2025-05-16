package io.github.wj9806.minimq.broker.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MMapFileModelManager {

    /**
     * key:主题名称
     * value:MMap对象
     */
    private static final Map<String, MMapFileModel> MMAP_MODELS = new ConcurrentHashMap<>();

    public void put(String topic, MMapFileModel mMapFileModel) {
        MMAP_MODELS.put(topic, mMapFileModel);
    }

    public MMapFileModel get(String topic) {
        return MMAP_MODELS.get(topic);
    }
}
