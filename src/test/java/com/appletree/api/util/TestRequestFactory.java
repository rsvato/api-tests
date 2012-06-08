package com.appletree.api.util;

public class TestRequestFactory {
    public static TestRequest createPersonExistsRequest() {
        return createTestRequest("person.exists");
    }

    public static TestRequest createRelationshipRequest() {
        return createTestRequest("relationship.add");
    }

    public static TestRequest createImageUploadRequest() {
        return createTestRequest("photo.upload");
    }

    public static TestRequest createAddUserRequest() {
        return createTestRequest("person.add");
    }

    public static TestRequest createUpdateUserRequest() {
        return createTestRequest("person.update");
    }

    public static TestRequest createTestRequest(String key) {
        TestRequestHttpClientImpl result = new TestRequestHttpClientImpl();
        result.selectURL(key);
        return result;
    }

    public static TestRequest createGetPersonRequest() {
        return createTestRequest("rest.get.person");
    }
}
