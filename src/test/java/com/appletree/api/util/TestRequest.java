package com.appletree.api.util;

public interface TestRequest {
    TestRequest addParameter(String parameterName, String parameterValue);
    TestRequest replaceParameter(String parameterName, String parameterValue);
    TestRequest withFixtureFromBundle(String bundleName);
    TestRequest withFixtureFromBundle(String bundleName, String prefix);
    TestRequest withPathParameter(String parameterValue);
    TestRequest get();
    TestRequest post();
    TestRequest delete();
    TestRequest put();
    TestRequest assertResponseOk();
    TestRequest assertStatusCode(int expected);
    TestRequest assertXpathExists(String s);
    TestRequest assertXpathNotExists(String s);
    TestRequest close();
    TestRequest assertResponseBody(String expected);
    <T> T consumeJson(Class<T> klass);
    TestRequest assertContentType(String contentType);
    TestRequest assertJson();
    TestRequest assertXml();
}
