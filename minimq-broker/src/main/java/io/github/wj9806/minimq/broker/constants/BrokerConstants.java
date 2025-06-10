package io.github.wj9806.minimq.broker.constants;

public class BrokerConstants {

    public static final String MINI_MQ_HOME = "mini.mq.home";

    public static final String BASE_STORE_PATH = "/broker/commitlog/";

    public static final String BASE_QUEUE_PATH = "/broker/consumequeue/";

    //1MB
    public static final Integer COMMIT_LOG_DEFAULT_SIZE = 1024 * 1024;

    //10S
    public static final Integer DEFAULT_REFRESH_MQ_TOPIC_INTERVAL = 3;

    public static final Integer DEFAULT_REFRESH_QUEUE_OFFSET_INTERVAL = 1;
}
