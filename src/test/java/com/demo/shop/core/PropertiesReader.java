package com.demo.shop.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private static Properties properties;

    static {
        properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties");
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

}
