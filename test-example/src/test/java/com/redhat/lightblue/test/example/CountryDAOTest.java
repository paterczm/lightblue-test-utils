package com.redhat.lightblue.test.example;

import static com.redhat.lightblue.client.expression.query.ValueQuery.withValue;
import static com.redhat.lightblue.client.projection.FieldProjection.includeField;
import static com.redhat.lightblue.util.test.AbstractJsonNodeTest.loadJsonNode;

import java.io.IOException;
import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.redhat.lightblue.client.http.LightblueHttpClient;
import com.redhat.lightblue.client.request.data.DataFindRequest;
import com.redhat.lightblue.client.request.data.DataInsertRequest;
import com.redhat.lightblue.test.utils.AbstractCRUDControllerWithRest;

/**
 * Testing your code against lightblue example.
 *
 * @author mpatercz
 *
 */
public class CountryDAOTest extends AbstractCRUDControllerWithRest {

    private LightblueHttpClient client;

    @Before
    public void before() throws UnknownHostException {
        client = getLightblueClient();
        cleanupMongoCollections(Country.objectType);
    }

    public CountryDAOTest() throws Exception {
        super();
    }

    @Override
    protected JsonNode[] getMetadataJsonNodes() throws Exception {
        return new JsonNode[]{loadJsonNode("country.json")};
    }

    private Country insertPL() throws IOException {
        Country c = new Country();
        c.setName("Poland");
        c.setIso2Code("PL");
        c.setIso3Code("POL");

        DataInsertRequest request = new DataInsertRequest(Country.objectType, Country.objectVersion);

        request.create(c);
        request.returns(includeField("*"));
        return client.data(request, Country[].class)[0];
    }

    @Test
    public void testInsertCountry() throws IOException {
        Assert.assertEquals("PL", insertPL().getIso2Code());
    }

    @Test
    public void testDirectMongoCleanup() throws IOException {
        insertPL();

        cleanupMongoCollections(Country.objectType);

        DataFindRequest request = new DataFindRequest(Country.objectType, Country.objectVersion);
        request.where(withValue("objectType = country"));
        request.select(includeField("*"));
        Country[] countries = client.data(request, Country[].class);

        Assert.assertTrue(countries.length == 0);
    }

}
