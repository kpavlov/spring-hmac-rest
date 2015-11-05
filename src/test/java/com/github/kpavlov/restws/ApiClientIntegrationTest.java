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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
        client.init();
    }

    @Test
    public void testEcho() {
        client.setCredentials("user", "secret");
        Foo request = new Foo(randomAlphanumeric(5));
        final FooResponseWrapper response = client.echo(request, FooResponseWrapper.class);
        assertNotNull(response);
        assertEquals(request, response.getData());
    }

    @Test
    public void testFailWhenPasswordIsBad() {
        client.setCredentials("user", "bad password");
        Foo request = new Foo(randomAlphanumeric(5));
        try {
            client.echo(request, FooResponseWrapper.class);
            fail("Http Error 403 is expected");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.FORBIDDEN));
        }
    }

    @Test
    public void testFailWhenUserIsUnknown() {
        client.setCredentials("unknownUser", "any password");
        Foo request = new Foo(randomAlphanumeric(5));
        try {
            client.echo(request, FooResponseWrapper.class);
            fail("Http Error 403 is expected");
        } catch (HttpClientErrorException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.FORBIDDEN));
        }
    }
}
