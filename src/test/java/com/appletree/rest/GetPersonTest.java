package com.appletree.rest;

import groovyx.net.http.ContentType;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class GetPersonTest extends TestsInitializer{

    private static final String PROFILE_ID = "Scott_Allen_Mueller_4";

    @Test
    public void testGetPerson() {
        given()
                .auth().none().
        expect()
                .contentType(ContentType.JSON)
                .body("profileId", is(PROFILE_ID)).
        when()
                .get("/rest/people/{profileId}", PROFILE_ID);
    }

    @Test
    public void testGetPersonFacts() {
        given()
                .auth().none().
        expect()
                .contentType(ContentType.JSON).
        when()
                .get("/rest/people/{profileId}/facts", PROFILE_ID);
    }

}
