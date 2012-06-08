package com.appletree.api.person;

import com.appletree.api.util.TestRequestFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApiUpdateTest {

    private static final String PIP_ID = "1035";

    @BeforeClass
    public static void init() {
        TestRequestFactory.createAddUserRequest().withFixtureFromBundle("keys")
                .withFixtureFromBundle("update_init", "pip_c")
                .get().assertResponseOk()
                .assertXpathExists("//person/@id");
        TestRequestFactory.createAddUserRequest().withFixtureFromBundle("keys")
                .withFixtureFromBundle("update_init", "estella_c")
                .get().assertResponseOk()
                .assertXpathExists("//person/@id");
    }

    @Test
    public void testAddName() {
        TestRequestFactory.createUpdateUserRequest().withPathParameter(PIP_ID)
                .withFixtureFromBundle("update", "pip_name")
                .withFixtureFromBundle("keys")
                .withFixtureFromBundle("update", "pip")
                .get()
                .assertResponseOk()
                .assertXpathExists("//person/@id");
    }

    @Test
    public void testWrongEmail() {
        TestRequestFactory.createUpdateUserRequest().withPathParameter(PIP_ID)
                .withFixtureFromBundle("keys")
                .withFixtureFromBundle("update", "pip")
                .replaceParameter("email", "scott@gig.io")
                .get()
                .assertResponseOk()
                .assertXpathExists("//person/@id");
    }

    @Test
    public void testAddLocation() {
        TestRequestFactory.createUpdateUserRequest().withPathParameter(PIP_ID)
                .withFixtureFromBundle("keys")
                .withFixtureFromBundle("update", "pip")
                .withFixtureFromBundle("update", "location")
                .get()
                .assertResponseOk()
                .assertXpathExists("//person/@id");
    }
}
