package com.appletree.api.person;

import com.appletree.api.util.TestRequestFactory;
import org.junit.Test;

public class ApiAddTest {

    @Test
    public void testAddFbUsers() {
        TestRequestFactory.createAddUserRequest().withFixtureFromBundle("keys")
                .withFixtureFromBundle("add", "firstuser")
                .get().assertResponseOk()
                .assertXpathExists("//person/@id");
        TestRequestFactory.createAddUserRequest().withFixtureFromBundle("keys")
                .withFixtureFromBundle("add", "firstuser")
                .get().assertStatusCode(409);
        TestRequestFactory.createAddUserRequest().withFixtureFromBundle("keys")
                .withFixtureFromBundle("add", "seconduser")
                .get().assertResponseOk()
                .assertXpathExists("//person/@id");
    }
}
