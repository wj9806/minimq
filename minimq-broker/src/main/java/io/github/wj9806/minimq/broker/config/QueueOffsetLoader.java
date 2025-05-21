package io.github.wj9806.minimq.broker.config;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.constants.BrokerConstants;
import io.github.wj9806.minimq.broker.core.data.QueueOffset;
import io.github.wj9806.minimq.broker.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.github.wj9806.minimq.broker.constants.BrokerConstants.DEFAULT_REFRESH_QUEUE_OFFSET_INTERVAL;

public class QueueOffsetLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicInfoLoader.class);

    public static final QueueOffsetLoader LOADER = new QueueOffsetLoader();

    private String filePath;

    public void loadProperties() {
        String home = CommonCache.getGlobalProperties().getMqHome();
        if (StringUtils.isBlank(home))
            throw new NullPointerException(BrokerConstants.MINI_MQ_HOME + " is blank");

        String offsetJsonPath = home + "/broker/config/queue-offset.json";
        filePath = offsetJsonPath;
        try {
            String json = FileUtils.readFileToString(new File(offsetJsonPath));
            QueueOffset queueOffset = JsonUtils.parse(json, new TypeReference<QueueOffset>() {
            });
            CommonCache.setQueueOffset(queueOffset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void startRefreshQueueOffsetTask() {
        ThreadExecutors.REFRESH_QUEUE_OFFSET_EXECUTOR.execute(() -> {
            do {
                try {
                    TimeUnit.SECONDS.sleep(DEFAULT_REFRESH_QUEUE_OFFSET_INTERVAL);
                    QueueOffset queueOffset = CommonCache.getQueueOffset();
                    FileUtils.writeStringToFile(new File(filePath), JsonUtils.toJson(queueOffset));
                    LOGGER.info("refresh queue-offset success");
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            } while (true);
        });
    }

}
