package com.appletree.api.person.exists;

import com.appletree.api.util.TestRequestFactory;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URISyntaxException;

public class ExistsTest {
    public static final String PERSON_ROOT = "//person";
    private static final String EXISTING_PERSON_PATH = PERSON_ROOT + "/@id";

    @Test
    public void testPersonExists() throws URISyntaxException, IOException, SAXException, XpathException {
        TestRequestFactory.createPersonExistsRequest().
                withPathParameter("scott@gig.io").
                withFixtureFromBundle("keys").
                withFixtureFromBundle("exists").
                get().
                assertResponseOk().
                assertXpathExists(EXISTING_PERSON_PATH).
                close();
    }

    @Test
    public void testPersonNotExists() throws URISyntaxException, IOException, SAXException, XpathException, ParserConfigurationException {
        TestRequestFactory.createPersonExistsRequest().
                withPathParameter("Bellis_Coldwine_1").
                withFixtureFromBundle("keys").
                withFixtureFromBundle("exists").
                get().
                assertResponseOk().
                assertXpathNotExists(EXISTING_PERSON_PATH).
                close();
    }

    @Test
    public void testEmptyRequest() throws URISyntaxException, IOException, SAXException, XpathException, ParserConfigurationException {
        TestRequestFactory.createPersonExistsRequest().
                withFixtureFromBundle("keys", null).
                withFixtureFromBundle("exists", null).
                get().
                assertStatusCode(404).
                close();
    }
}
