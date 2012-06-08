package com.appletree.rest;

import com.jayway.restassured.path.json.JsonPath;
import groovyx.net.http.ContentType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ParentshipTest extends TestsInitializer {
    private static final Logger log = LoggerFactory.getLogger(ParentshipTest.class);
    private String childProfileId;

    @Before
    public void init() {
        if (childProfileId == null) {
            childProfileId = createChild();
        }
        log.debug("Child profile id is {}", childProfileId);
    }

    private String createChild() {
        String firstName = "Child" + new Random().nextInt(10000);
        String response = given().param("first", firstName).post("/rest/people").asString();
        return new JsonPath(response).getString("profileId");
    }

    @Test
    public void testCreateSingleParent() {
        assertTrue(getParents(childProfileId).isEmpty());
        createParent("Parent", true, childProfileId, false);
        List parents = getParents(childProfileId);
        assertFalse(parents.isEmpty());
        assertEquals(1, parents.size());
        Map<String, String> parent = (Map<String, String>) parents.iterator().next();
        assertThat(parent, hasEntry("first", "Parent"));
    }

    private void deleteParentRelationship(Integer parentRelationshipId) {
        expect()
                .statusCode(204)
                .when().delete("/rest/relationships/parent/{id}", parentRelationshipId);
    }

    @Test
    public void testCreateAndDeleteSingleParent() {
        assertTrue(getParents(childProfileId).isEmpty());
        createParent("Parent", true, childProfileId, false);
        List parents = getParents(childProfileId);
        assertFalse(parents.isEmpty());
        assertEquals(1, parents.size());
        Map<String, Object> parent = (Map<String, Object>) parents.iterator().next();
        assertThat(parent, hasEntry("first", (Object) "Parent"));
        Integer parentRelationshipId = (Integer) parent.get("parentOfBirthEventId");
        deleteParentRelationship(parentRelationshipId);
        assertTrue(getParents(childProfileId).isEmpty());
    }

    @Test
    public void testCreateSameSexParents() {
        assertTrue(getParents(childProfileId).isEmpty());
        createParent("Father", true, childProfileId, false);
        List parents = getParents(childProfileId);
        Map<String, String> parent = (Map<String, String>) parents.iterator().next();
        assertThat(parent, hasEntry("first", "Father"));
        String fatherProfile = parent.get("profileId");
        given()
                .param("first", "Mother")
                .param("male", String.valueOf(true)).
        expect()
                .contentType(ContentType.JSON)
                .statusCode(409)
                .body("error", is("Cannot set parent")).
        when()
                .post("/rest/people/{profile}/parents", childProfileId);
    }

    @Test
    public void testCreateExcessiveParents() {
        assertTrue(getParents(childProfileId).isEmpty());
        createParent("Father", true, childProfileId, false);
        List parents = getParents(childProfileId);
        Map<String, String> parent = (Map<String, String>) parents.iterator().next();
        assertThat(parent, hasEntry("first", "Father"));
        createParent("Mother", false, childProfileId, false);
        given()
                .param("first", "Mother")
                .param("male", String.valueOf(true)).
        expect()
                .contentType(ContentType.JSON)
                .statusCode(409)
                .body("error", is("Cannot set parent")).
        when()
                .post("/rest/people/{profile}/parents", childProfileId);
    }


    @Test
    public void testCreateIndependentParents() {
        assertTrue(getParents(childProfileId).isEmpty());
        createParent("Father", true, childProfileId, false);
        List parents = getParents(childProfileId);
        Map<String, String> parent = (Map<String, String>) parents.iterator().next();
        assertThat(parent, hasEntry("first", "Father"));
        String fatherProfile = parent.get("profileId");
        createParent("Mother", false, childProfileId, false);
        parents = getParents(childProfileId);
        assertEquals(2, parents.size());
        String motherProfile = null;
        for (Object o : parents) {
            Map<String, String> parentMap = (Map<String, String>) o;
            motherProfile = parentMap.get("profileId");
            if (! motherProfile.equals(fatherProfile)) {
                break;
            }
        }
        assertNotNull(motherProfile);
        List fatherPartners = getPartners(fatherProfile);
        assertTrue(fatherPartners.isEmpty());
        assertTrue(getPartners(motherProfile).isEmpty());
    }

    @Test
    public void testCreateMarriedParents() {
        assertTrue(getParents(childProfileId).isEmpty());
        createParent("Father", true, childProfileId, false);
        List parents = getParents(childProfileId);
        Map<String, String> parent = (Map<String, String>) parents.iterator().next();
        assertThat(parent, hasEntry("first", "Father"));
        String fatherProfile = parent.get("profileId");
        createParent("Mother", false, childProfileId, true);
        parents = getParents(childProfileId);
        assertEquals(2, parents.size());
        String motherProfile = findProfileExcept(parents, fatherProfile);
        assertNotNull(motherProfile);
        List fatherPartners = getPartners(fatherProfile);
        assertThat(fatherPartners.size(), is(1));
        assertThat((Map<String,String>) fatherPartners.iterator().next(), hasEntry("profileId", motherProfile));
        List motherPartners = getPartners(motherProfile);
        assertThat(motherPartners.size(), is(1));
        assertThat((Map<String,String>) motherPartners.iterator().next(), hasEntry("profileId", fatherProfile));
    }

    private String findProfileExcept(List parents, String fatherProfile) {
        String result = null;
        for (Object o : parents) {
            Map<String, String> parentMap = (Map<String, String>) o;
            result = parentMap.get("profileId");
            if (! result.equals(fatherProfile)) {
                break;
            }
        }
        return fatherProfile.equals(result) ? null : result;
    }

    private void createParent(String firstName, boolean male, String childProfileId, boolean married) {
        given()
                .param("first", firstName)
                .param("male", String.valueOf(male))
                .param("married", String.valueOf(married)).
        expect()
                .contentType(ContentType.JSON)
                .statusCode(201)
                .body("person.first", is(firstName)).
        when()
                .post("/rest/people/{profile}/parents", childProfileId);
    }

    private List getPartners(String profileId) {
        return expect()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .when().get("/rest/people/{profile}/partners", profileId).as(List.class);
    }

    private List getParents(String childId) {
        return expect()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .when().get("/rest/people/{profile}/parents", childId).as(List.class);
    }
}
