package org.jundeng.sprc.common.config;

import static org.jundeng.srpc.common.config.ConfigManager.getConfigManager;

import org.jundeng.srpc.common.config.ConfigManager;
import org.junit.Test;

public class ConfigLoaderTest {

    @Test
    public void testPropertiesConfig() {
        ConfigManager configManager = getConfigManager();
        TestConfig1 testConfig1 = configManager.loadConfig(TestConfig1.class);
        System.out.println(testConfig1);
    }

    @Test
    public void testAnnotationConfig() {
        ConfigManager configManager = getConfigManager();
        TestConfig2 testConfig2 = configManager.loadConfig(TestConfig2.class);
        System.out.println(testConfig2);
    }
}
