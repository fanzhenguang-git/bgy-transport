/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.*;

/**
 * @author: tangyixiang
 * @date: 2019/11/6
 **/
@Slf4j
public class YamlUtil {

    public static Properties yamlConvertProperties() {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> applicationYamlPropertySource = null;// null indicated common properties for all profiles.

        try {
            applicationYamlPropertySource = loader.load("properties", new ClassPathResource("application.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties properties = new Properties();
        applicationYamlPropertySource.forEach(propertySource -> {
            Map source = ((MapPropertySource) propertySource).getSource();
            properties.putAll(source);
        });
        return properties;
    }

    public static List<Object> matchKeyGetValue(Properties properties, String keyPattern) {
        List<Object> list = new ArrayList<>();
        Set<Object> keySet = properties.keySet();
        keySet.forEach(object -> {
            String key = (String) object;
            if (key.contains(keyPattern)) {
                list.add(properties.get(key));
            }
        });
        return list;
    }
}
