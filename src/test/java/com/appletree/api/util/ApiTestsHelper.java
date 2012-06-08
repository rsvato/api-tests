package com.appletree.api.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;

public class ApiTestsHelper {
    public static HttpResponse executeRequest(URI uri) throws IOException {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet getCommand = new HttpGet(uri);
        return httpClient.execute(getCommand);
    }

    public static void assertResponseBody(String expected, HttpResponse response) throws IOException, SAXException, ParserConfigurationException {
        assertEquals(expected, extractContent(response));
    }

    public static String extractContent(HttpResponse response) throws SAXException, IOException, ParserConfigurationException {
        return getDomDocument(response).getFirstChild().getTextContent();
    }

    public static Document getDomDocument(HttpResponse response) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().parse(response.getEntity().getContent());
    }

    public static void assertResponseIsOk(HttpResponse response) {
        assertResponseStatusCode(response, 200);
    }

    public static void assertResponseStatusCode(HttpResponse response, int statusCode) {
        assertEquals(statusCode, response.getStatusLine().getStatusCode());
    }

    public static void assertContentType(HttpResponse response, String contentType) {
        assertEquals(contentType, response.getEntity().getContentType().getValue());
    }

}
