package org.purushotham.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class ConfigParser {
    private Properties prop = null;
    public ConfigParser() {
        try (FileInputStream fis = new FileInputStream(System.getProperty("user.dir")+"/src/main/resources/config.properties")) {
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e1) {
            throw new IllegalStateException();
        }
    }

    public String getString(String key) {
        return prop.getProperty(key);
    }

    public boolean getBoolean(String key) {
        String value = prop.getProperty(key);
        if (value != null) {
            return Boolean.parseBoolean(value);
        } else {
            return false;
        }
    }

    public List<String> getList(String key) {
        String value = prop.getProperty("key");
        if (value != null) {
            return Arrays.asList(value.split("\\s*,\\s*"));
        } else {
            return Collections.emptyList();
        }
    }
}
