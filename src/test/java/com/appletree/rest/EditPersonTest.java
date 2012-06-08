package com.appletree.rest;

import com.jayway.restassured.path.json.JsonPath;
import groovyx.net.http.ContentType;
import org.junit.Test;

import java.util.Map;
import java.util.Random;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class EditPersonTest extends TestsInitializer {

    private static final String PROFILE_ID = "Scott_Allen_Mueller_4";

    @Test
    public void testEditInvalidPerson() {
        String profileId = "Not_Found_1024";
        expect()
                .contentType(ContentType.JSON)
                .statusCode(404)
                .body("error", is("Person " + profileId + " has not been found")).
        when()
                .post("/rest/people/{profileId}", profileId);
    }

    @Test
    public void testSetEmailFromOtherPerson() {
        String email = "demi@yahoo.com";
        given()
                .param("email", email).
        expect()
                .statusCode(409)
                .contentType(ContentType.JSON)
                .body("error", is("Email " + email + " is registered for other person")).
        when()
                .post("/rest/people/{profileId}", PROFILE_ID);
    }

    @Test
    public void testSetInvalidEmail() {
        String email = "address_host";
        given()
                .param("email", email).
        expect()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error", is("Invalid email address")).
        when()
                .post("/rest/people/{profileId}", PROFILE_ID);
    }

    @Test
    public void testAddStory() {
        int i = new Random().nextInt();
        String story = "Once upon a time " + i;
        given()
                .formParam("story", story).
        expect()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("profileId", is(PROFILE_ID)).
        when()
                .post("/rest/people/{profileId}", PROFILE_ID);
        //check it
        String s = get("/rest/people/{profileId}/stories", PROFILE_ID).asString();
        Map<Object, String> result = JsonPath.from(s).get();
        assertThat(result, hasValue(story));
    }

    @Test
    public void testSetLifeEvent() {
        Random random = new Random();
        int month = random.nextInt(11) + 1;
        int year = random.nextInt(2011);
        given()
                .formParam("birthEvent.year", year)
                .formParam("birthEvent.month", month).
        expect()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .body("profileId", is(PROFILE_ID)).
        when()
                .post("/rest/people/{profileId}", PROFILE_ID);
        //check it
        expect()
                .contentType(ContentType.JSON).statusCode(200)
                .body("birthEvent.month", is(month), "birthEvent.year", is(year)).
        when()
                .get("/rest/people/{profileId}/facts", PROFILE_ID);
    }

    @Test
    public void testCommitInvalidImage() {
        long invalidId = -100;
        given()
                .formParam("uuid", invalidId).
        expect()
                .contentType(ContentType.JSON)
                .statusCode(404)
                .body("error", is("Specified image not found")).
        when()
                .post("/rest/people/{profileId}", PROFILE_ID);

    }
}
