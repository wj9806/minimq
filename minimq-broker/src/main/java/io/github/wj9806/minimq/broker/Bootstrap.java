package io.github.wj9806.minimq.broker;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.config.GlobalPropertiesLoader;
import io.github.wj9806.minimq.broker.config.TopicInfoLoader;
import io.github.wj9806.minimq.broker.core.CommitLogAppender;
import io.github.wj9806.minimq.broker.model.TopicModel;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class Bootstrap {

    private static CommitLogAppender commitLogAppender;

    public static void main(String[] args) throws IOException, InterruptedException {
        init();

        for (int i = 0; i < 50000; i++) {
            commitLogAppender.appendMsg("test_topic", ("this is content "+ i).getBytes());
            TimeUnit.MILLISECONDS.sleep(1);
        }
        System.out.println(commitLogAppender.readMsg("test_topic"));
    }

    private static void init() throws IOException {
        GlobalPropertiesLoader.LOADER.loadProperties();
        TopicInfoLoader.LOADER.loadProperties();
        TopicInfoLoader.LOADER.startRefreshTopicInfoTask();

        commitLogAppender = new CommitLogAppender();
        Collection<TopicModel> topicModelList = CommonCache.getTopicModelMap().values();
        for (TopicModel topicModel : topicModelList) {
            String topic = topicModel.getTopic();
            commitLogAppender.prepareMMap(topic);
        }

    }

}
