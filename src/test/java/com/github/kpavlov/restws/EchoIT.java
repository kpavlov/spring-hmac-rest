package com.github.kpavlov.restws;

import com.github.kpavlov.restws.client.ApiClient;
import com.github.kpavlov.restws.server.model.Foo;
import com.github.kpavlov.restws.server.model.FooResponseWrapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EchoIT {

    ApiClient client;


    @Before
    public void beforeTest() {
        client = new ApiClient("localhost", 8080);
        client.setCredentials("user", "secret");
        client.init();
    }

    @Test
    public void testEcho() {
        Foo request = new Foo("hoho");
        final FooResponseWrapper response = client.echo(request, FooResponseWrapper.class);
        assertNotNull(response);
        assertEquals(request, response.getData());
    }
}
