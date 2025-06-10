package io.github.wj9806.minimq.broker;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.config.GlobalPropertiesLoader;
import io.github.wj9806.minimq.broker.config.QueueOffsetLoader;
import io.github.wj9806.minimq.broker.config.TopicInfoLoader;
import io.github.wj9806.minimq.broker.core.CommitLogAppender;
import io.github.wj9806.minimq.broker.core.QueueAppender;
import io.github.wj9806.minimq.broker.core.data.Topic;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class Bootstrap {

    private static final CommitLogAppender commitLogAppender = CommitLogAppender.COMMIT_LOG_APPENDER;

    private static final QueueAppender queueAppender = QueueAppender.QUEUE_APPENDER;

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
        QueueOffsetLoader.LOADER.loadProperties();
        QueueOffsetLoader.LOADER.startRefreshQueueOffsetTask();

        Collection<Topic> topicList = CommonCache.getTopicMap().values();
        for (Topic topic : topicList) {
            String topicName = topic.getTopic();
            commitLogAppender.prepareMMap(topicName);
            queueAppender.prepareQueue(topicName);
        }

    }

}
