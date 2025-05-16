package io.github.wj9806.minimq.broker;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.config.GlobalPropertiesLoader;
import io.github.wj9806.minimq.broker.config.TopicInfoLoader;
import io.github.wj9806.minimq.broker.core.CommitLogAppender;
import io.github.wj9806.minimq.broker.model.TopicModel;

import java.io.IOException;
import java.util.Collection;

public class Bootstrap {

    private static CommitLogAppender commitLogAppender;

    public static void main(String[] args) throws IOException {
        init();

        commitLogAppender.appendMsg("test_topic", "hello world".getBytes());
        System.out.println(commitLogAppender.readMsg("test_topic"));
    }

    private static void init() throws IOException {
        GlobalPropertiesLoader.LOADER.loadProperties();
        TopicInfoLoader.LOADER.loadProperties();

        commitLogAppender = new CommitLogAppender();
        Collection<TopicModel> topicModelList = CommonCache.getTopicModelMap().values();
        for (TopicModel topicModel : topicModelList) {
            String topic = topicModel.getTopic();
            commitLogAppender.prepareMMap(topic);
        }

    }

}
