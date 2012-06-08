package com.appletree.api.relationship;

import com.appletree.api.util.TestRequest;
import com.appletree.api.util.TestRequestFactory;
import org.junit.Test;

public class AddTest {
    @Test
    public void testAddFather() {
        assertRequestSuccess("father");
    }

    @Test
    public void testAddSonRelationship()  {
        assertRequestSuccess("son");
    }

    @Test
    public void testAddExistentRelationship() {
        assertRequestSuccess("existing");
        assertRequestResult("existing", "Relationship already exists or cannot be added");
    }

    @Test
    public void testAddInexistentRelationship() {
        assertRequestResult("wrongcode", "Relationship code or people ids are invalid");
    }

    @Test
    public void testAddFatherRelationshipForInexistentPeople() {
        assertRequestResult("wrongpeople", "Relationship code or people ids are invalid");
    }

    @Test
    public void testAddCousinRelationships() {
        assertRequestSuccess("cousin1");
        assertRequestSuccess("cousin2");
    }

    private TestRequest prepareRequest(String prefix) {
        return TestRequestFactory.createRelationshipRequest().
                withFixtureFromBundle("keys").
                withFixtureFromBundle("relationships", prefix);
    }

    private void assertRequestSuccess(String prefix) {
        assertRequestResult(prefix, "Relationship set successfully");
    }

    private void assertRequestResult(String prefix, String errorText){
        prepareRequest(prefix).get().assertResponseOk().
                assertXpathExists(String.format("//Result[text() = '%s']", errorText)).
                close();
    }

}
