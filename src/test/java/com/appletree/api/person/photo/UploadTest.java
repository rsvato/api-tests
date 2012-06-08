package com.appletree.api.person.photo;

import com.appletree.api.util.TestRequestFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class UploadTest {

    @BeforeClass
    public static void init() {
       TestRequestFactory.createAddUserRequest().
               withFixtureFromBundle("keys").
               withFixtureFromBundle("upload", "pretest").
               get().assertResponseOk().close();
    }

    @Test
    public void testSuccessfulUploadImage() {
        TestRequestFactory.createImageUploadRequest().withFixtureFromBundle("keys").withFixtureFromBundle("upload", "success").
                get().assertResponseOk().assertResponseBody("Image uploaded successfully").
                close();
    }

    @Test
    public void testUnsuccessfuleUploadImage() {
        makeWrongRequest("error");
    }

    @Test
    public void testWrongUserUploadImage() {
        makeWrongRequest("wronguser");
    }

    private void makeWrongRequest(String prefix) {
        TestRequestFactory.createImageUploadRequest().withFixtureFromBundle("keys").withFixtureFromBundle("upload", prefix).
                get().assertStatusCode(400).
                close();
    }
}
