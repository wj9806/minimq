package io.github.wj9806.minimq.broker.cache;

import io.github.wj9806.minimq.broker.config.GlobalProperties;
import io.github.wj9806.minimq.broker.model.TopicModel;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonCache {

    private static GlobalProperties globalProperties;

    public static Map<String, TopicModel> topicModelMap;

    public static GlobalProperties getGlobalProperties() {
        return globalProperties;
    }

    public static void setGlobalProperties(GlobalProperties globalProperties) {
        CommonCache.globalProperties = globalProperties;
    }

    public static void setTopicModelMap(List<TopicModel> topicModelList) {
        CommonCache.topicModelMap = topicModelList.stream().collect(Collectors.toMap(TopicModel::getTopic, Function.identity()));
    }

    public static Map<String, TopicModel> getTopicModelMap() {
        return topicModelMap;
    }
}
