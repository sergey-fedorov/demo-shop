package com.demo.shop.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private static final String DEFAULT_PROPERTIES_FILE = "src/main/resources/application.properties";
    private static Properties properties;


    static {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(DEFAULT_PROPERTIES_FILE);
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
