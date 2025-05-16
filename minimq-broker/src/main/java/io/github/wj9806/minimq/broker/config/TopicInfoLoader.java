package io.github.wj9806.minimq.broker.config;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.constants.BrokerConstants;
import io.github.wj9806.minimq.broker.model.TopicModel;
import io.github.wj9806.minimq.broker.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TopicInfoLoader {

    public static final TopicInfoLoader LOADER = new TopicInfoLoader();

    public void loadProperties() {
        String home = CommonCache.getGlobalProperties().getMqHome();
        if (StringUtils.isBlank(home))
            throw new NullPointerException(BrokerConstants.MINI_MQ_HOME + " is blank");

        String topicJsonPath = home + "/broker/config/topic.json";

        try {
            String json = FileUtils.readFileToString(new File(topicJsonPath));
            List<TopicModel> modelList = JsonUtils.parse(json, new TypeReference<List<TopicModel>>() {
            });
            CommonCache.setTopicModelMap(modelList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
