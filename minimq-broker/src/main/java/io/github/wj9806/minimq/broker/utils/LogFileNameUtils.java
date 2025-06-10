package io.github.wj9806.minimq.broker.utils;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.constants.BrokerConstants;

public class LogFileNameUtils {

    public static String buildFirstCommitLogName() {
        return "00000000";
    }

    public static String incrFileName(String oldFileName) {
        if (oldFileName.length() != 8)
            throw new IllegalArgumentException("fileName must has 8 chars");

        long fileIndex = Long.parseLong(oldFileName);
        fileIndex++;

        return String.format("%08d", fileIndex);
    }

    public static String buildCommitLogPath(String topic, String newFileName) {
        return CommonCache.getGlobalProperties().getMqHome() +
                BrokerConstants.BASE_STORE_PATH + topic + "/" + newFileName;
    }

    public static String buildQueuePath(String topic, Integer queueId, String newFileName) {
        return CommonCache.getGlobalProperties().getMqHome() +
                BrokerConstants.BASE_QUEUE_PATH + topic + "/" + queueId + "/" + newFileName;
    }
}
