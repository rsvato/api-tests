package com.appletree.api.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class TestRequestHttpClientImpl extends AbstractTestRequest implements TestRequest {
    private HttpResponse response;
    private ObjectMapper mapper = new ObjectMapper();

    protected TestRequestHttpClientImpl() {
        super();
    }

    @Override
    public TestRequest get() {
        return execute(HttpGet.METHOD_NAME);
    }

    private TestRequest execute(String method) {
        try {
            URI uri = URIUtils.createURI("http", host, port, url, URLEncodedUtils.format(parameters, "UTF-8"), null);
            HttpRequestBase post = createRequest(method);
            HttpClient httpClient = new DefaultHttpClient();
            response = httpClient.execute(post);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private HttpRequestBase createRequest(String method) throws URISyntaxException, IOException {
        if (HttpGet.METHOD_NAME.equals(method)) {
            URI uri = URIUtils.createURI("http", host, port, url, URLEncodedUtils.format(parameters, "UTF-8"), null);
            return new HttpGet(uri);
        } else if (HttpDelete.METHOD_NAME.equals(method)) {
            return new HttpDelete(url);
        } else if (HttpPost.METHOD_NAME.equals(method)) {
            URI uri = URIUtils.createURI("http", host, port, url, null, null);
            HttpPost post = new HttpPost(uri);
            post.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
            return post;
        } else if (HttpPut.METHOD_NAME.equals(method)) {
            return new HttpPut(URIUtils.createURI("http", host, port, url, null, null));
        } else
            throw new IllegalArgumentException("Unsupported method: " + method);
    }

    @Override
    public TestRequest post() {
        return execute(HttpPost.METHOD_NAME);
    }

    @Override
    public TestRequest delete() {
        return execute(HttpDelete.METHOD_NAME);
    }

    @Override
    public TestRequest put() {
        return execute(HttpDelete.METHOD_NAME);
    }

    @Override
    public TestRequest assertStatusCode(int expected) {
        assertNotNull(response);
        ApiTestsHelper.assertResponseStatusCode(response, expected);
        return this;
    }

    public TestRequest assertContentType(String contentType) {
        assertNotNull(response);
        ApiTestsHelper.assertContentType(response, contentType);
        return this;
    }

    public TestRequest assertJson() {
        return assertContentType("application/json;charset=UTF-8");
    }

    public TestRequest assertXml() {
        return assertContentType("application/xml");
    }

    @Override
    public TestRequest assertXpathExists(String s) {
        assertNotNull(response);
        try {
            XMLAssert.assertXpathExists(s, new InputSource(response.getEntity().getContent()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (XpathException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public TestRequest assertXpathNotExists(String s) {
        assertNotNull(response);
        try {
            XMLAssert.assertXpathNotExists(s, new InputSource(response.getEntity().getContent()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (XpathException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public TestRequest close() {
        try {
            response.getEntity().consumeContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public TestRequest assertResponseBody(String expected) {
        assertNotNull(response);
        try {
            ApiTestsHelper.assertResponseBody(expected, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public<T> T consumeJson(Class<T> klass) {
        try {
            InputStream content = response.getEntity().getContent();
            return mapper.readValue(content, klass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                response.getEntity().consumeContent();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
