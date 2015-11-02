package com.github.kpavlov.restws;

import com.github.kpavlov.restws.client.ApiClient;
import com.github.kpavlov.restws.server.SpringRestHmacApplication;
import com.github.kpavlov.restws.server.model.Foo;
import com.github.kpavlov.restws.server.model.FooResponseWrapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringRestHmacApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ApiClientIntegrationTest {

    private ApiClient client;

    @Value("${local.server.port}")
    private int port;

    @Before
    public void beforeTest() {
        client = new ApiClient("localhost", port);
        client.setCredentials("user", "secret");
        client.init();
    }

    @Test
    public void testEcho() {
        Foo request = new Foo(randomAlphanumeric(5));
        final FooResponseWrapper response = client.echo(request, FooResponseWrapper.class);
        assertNotNull(response);
        assertEquals(request, response.getData());
    }
}
