package com.appletree.api.util;

import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class AbstractTestRequest implements TestRequest {
    protected String host;
    protected Integer port;
    protected String prefix;
    protected String url;
    protected Properties apiMap;
    protected List<BasicNameValuePair> parameters;

    protected AbstractTestRequest() {
        String s = System.getProperty("environment");
        Properties properties = loadProperties(s);
        this.host = properties.getProperty("api.host", "localhost");
        this.port = Integer.parseInt(properties.getProperty("api.port", "8080"));
        this.prefix = properties.getProperty("api.root", "");
        apiMap = loadProperties("path");
        parameters = new ArrayList<BasicNameValuePair>();
    }

    protected void selectURL(String key) {
        String apiPath = apiMap.getProperty(key);
        this.url = prefix + apiPath;
    }

    protected Properties loadProperties(String filename) {
        Properties properties = new Properties();
        InputStream is = getClass().getResourceAsStream("/" + filename + ".properties");
        try {
            properties.load(is);
            is.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    @Override
    public TestRequest addParameter(String parameterName, String parameterValue) {
        parameters.add(new BasicNameValuePair(parameterName, parameterValue));
        return this;
    }

    @Override
    public TestRequest replaceParameter(String parameterName, String parameterValue) {
        Iterator<BasicNameValuePair> iterator = parameters.iterator();
        while(iterator.hasNext()) {
            BasicNameValuePair pair= iterator.next();
            if (pair.getName().equals(parameterName))
                iterator.remove();
        }
        return addParameter(parameterName, parameterValue);
    }

    @Override
    public TestRequest withFixtureFromBundle(String bundleName) {
        return withFixtureFromBundle(bundleName, null);
    }

    @Override
    public TestRequest withFixtureFromBundle(String bundleName, String prefix) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName);
        Enumeration<String> keys = bundle.getKeys();
        while(keys.hasMoreElements()) {
            String parameterName = keys.nextElement();
            if (prefix != null) {
                if(parameterName.startsWith(prefix + ".")) {
                    addParameter(stripParameter(parameterName, prefix), bundle.getString(parameterName));
                }
            } else
                addParameter(parameterName, bundle.getString(parameterName));
        }
        return this;
    }

    private String stripParameter(String parameterName, String prefix) {
        return parameterName.substring((prefix + ".").length());
    }

    @Override
    public TestRequest withPathParameter(String parameterValue) {
        url += "/" + parameterValue;
        return this;
    }

    @Override
    public TestRequest assertResponseOk() {
        assertStatusCode(200);
        return this;
    }
}
