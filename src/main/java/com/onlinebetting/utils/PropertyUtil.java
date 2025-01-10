package com.onlinebetting.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class PropertyUtil {

    private static final Properties prop = new Properties();

    static {
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("application.properties");
            prop.load(Files.newInputStream(Paths.get(url.toURI())));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getByKey(String key) {
        return (String) prop.get(key);
    }
}
