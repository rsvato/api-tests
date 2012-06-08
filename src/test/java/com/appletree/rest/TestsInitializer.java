package com.appletree.rest;

import com.jayway.restassured.RestAssured;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.jayway.restassured.RestAssured.preemptive;

public class TestsInitializer {

    public TestsInitializer() {
        String s = System.getProperty("environment");
        Properties properties = loadProperties(s);
        String host = properties.getProperty("api.host", "localhost");
        Integer port = Integer.parseInt(properties.getProperty("api.port", "8080"));
        String prefix = properties.getProperty("api.root", "");
        RestAssured.baseURI = String.format("http://%s", host);
        RestAssured.port = port;
        if (StringUtils.isNotBlank(prefix))
            RestAssured.basePath = prefix;
        RestAssured.authentication = preemptive().basic(properties.getProperty("api.username"),
                properties.getProperty("api.password"));
    }

     protected Properties loadProperties(String filename) {
        Properties properties = new Properties();
        if (filename == null)
            return properties;
        InputStream is = getClass().getResourceAsStream("/" + filename + ".properties");
        try {
            properties.load(is);
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }
}
