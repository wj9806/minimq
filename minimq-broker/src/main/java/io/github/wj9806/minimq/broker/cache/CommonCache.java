package io.github.wj9806.minimq.broker.cache;

import io.github.wj9806.minimq.broker.config.GlobalProperties;
import io.github.wj9806.minimq.broker.core.data.QueueOffset;
import io.github.wj9806.minimq.broker.core.data.Topic;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonCache {

    private static GlobalProperties globalProperties;

    private static List<Topic> topicList;

    private static QueueOffset queueOffset = new QueueOffset();

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static void setTopicList(List<Topic> topicList) {
        CommonCache.topicList = topicList;
    }

    public static List<Topic> getTopicList() {
        return topicList;
    }

    public static Map<String, Topic> getTopicMap() {
        return topicList.stream().collect(Collectors.toMap(Topic::getTopic, Function.identity()));
    }

    public static QueueOffset getQueueOffset() {
        return queueOffset;
    }

    public static void setQueueOffset(QueueOffset queueOffset) {
        CommonCache.queueOffset = queueOffset;
    }
}
