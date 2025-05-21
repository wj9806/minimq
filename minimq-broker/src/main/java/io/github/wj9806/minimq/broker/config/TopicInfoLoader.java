package io.github.wj9806.minimq.broker.config;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.constants.BrokerConstants;
import io.github.wj9806.minimq.broker.core.data.Topic;
import io.github.wj9806.minimq.broker.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.github.wj9806.minimq.broker.constants.BrokerConstants.DEFAULT_REFRESH_MQ_TOPIC_INTERVAL;

public class TopicInfoLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicInfoLoader.class);

    public static final TopicInfoLoader LOADER = new TopicInfoLoader();

    private String filePath;

    public void loadProperties() {
        String home = CommonCache.getGlobalProperties().getMqHome();
        if (StringUtils.isBlank(home))
            throw new NullPointerException(BrokerConstants.MINI_MQ_HOME + " is blank");

        String topicJsonPath = home + "/broker/config/topic.json";
        filePath = topicJsonPath;
        try {
            String json = FileUtils.readFileToString(new File(topicJsonPath));
            List<Topic> topicList = JsonUtils.parse(json, new TypeReference<List<Topic>>() {
            });
            CommonCache.setTopicList(topicList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void startRefreshTopicInfoTask() {
        CommonThreadPoolConfig.REFRESH_MQ_TOPIC_EXECUTOR.execute(() -> {
            do {
                try {
                    TimeUnit.SECONDS.sleep(DEFAULT_REFRESH_MQ_TOPIC_INTERVAL);
                    List<Topic> topicList = CommonCache.getTopicList();
                    FileUtils.writeStringToFile(new File(filePath), JsonUtils.toJson(topicList));
                    LOGGER.info("refresh topic info success");
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
        });
    }

}
