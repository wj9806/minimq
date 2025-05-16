package io.github.wj9806.minimq.broker.config;

import io.github.wj9806.minimq.broker.cache.CommonCache;
import io.github.wj9806.minimq.broker.constants.BrokerConstants;
import org.apache.commons.lang3.StringUtils;

public class GlobalPropertiesLoader {

    public static final GlobalPropertiesLoader LOADER = new GlobalPropertiesLoader();

    public void loadProperties() {
        GlobalProperties globalProperties = new GlobalProperties();
        String home = System.getenv(BrokerConstants.MINI_MQ_HOME);
        if (StringUtils.isBlank(home))
            throw new NullPointerException(BrokerConstants.MINI_MQ_HOME + " is blank");
        globalProperties.setMqHome(home);

        CommonCache.setGlobalProperties(globalProperties);
    }

}
