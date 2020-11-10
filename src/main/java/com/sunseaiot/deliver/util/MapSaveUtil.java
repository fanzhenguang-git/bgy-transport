/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@Slf4j
public class MapSaveUtil {

    private static final String FILE_PATH = "./src/main/resources/map.properties";

    public static void saveMap(Map<String, String> map) {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (getValue(entry.getKey()) == null) {
                properties.setProperty(entry.getKey(), entry.getValue());
            }
        }
        try {
            if (properties == null) {
                OutputStream os = new FileOutputStream(FILE_PATH, true);
                properties.store(os, null);
            }
        } catch (IOException e) {
            log.error("Save map in the properties file failed: {}", e.getMessage());
        }
    }

    public static String getValue(String deviceId) {
        Properties properties = new Properties();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(FILE_PATH));
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String gwid = null;
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            if (deviceId.equals(entry.getKey())) {
                gwid = (String) entry.getValue();
            }
        }
        return gwid;
    }
}
