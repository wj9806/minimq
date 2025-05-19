package io.github.wj9806.minimq.broker.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CommonThreadPoolConfig {

    public static ThreadPoolExecutor REFRESH_MQ_TOPIC_EXECUTOR
            = new ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10), r -> new Thread(r, "refresh-mq-topic-thread"));

}
