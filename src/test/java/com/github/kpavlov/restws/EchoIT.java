package com.github.kpavlov.restws;

import com.github.kpavlov.restws.client.ApiClient;
import com.github.kpavlov.restws.server.model.Foo;
import com.github.kpavlov.restws.server.model.FooResponseWrapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EchoIT {

    ApiClient client;


    @Before
    public void beforeTest() {
        client = new ApiClient();
        client.setCredentials("user", "secret");
        client.init();
    }

    @Test
    public void testEcho() {
        Foo request = new Foo("hoho");
        final FooResponseWrapper responseWrapper = client.echo(request, FooResponseWrapper.class);
        assertEquals(request, responseWrapper.getData());
    }
}
