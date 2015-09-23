package com.github.kpavlov.restws.server;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kpavlov.restws.server.model.AbstractResponseWrapper;
import com.github.kpavlov.restws.server.model.Foo;
import com.github.kpavlov.restws.server.model.FooRequestWrapper;
import com.github.kpavlov.restws.server.model.FooResponseWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringRestHmacApplication.class)
@WebAppConfiguration
public class JsonMappingTest {

    @Autowired
    ObjectMapper objectMapper;

    private <T> T parseJson(String resource, Class<? extends T> target) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(resource);
        return objectMapper.readValue(inputStream, target);
    }

    private <T> T parseJson(String resource, TypeReference<?> typeRef) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(resource);
        return objectMapper.readValue(inputStream, typeRef);
    }

    @Test
    public void testPayoutResponseSuccess() throws IOException {
        final Foo foo = new Foo("aa");
        AbstractResponseWrapper<Foo> pw = new FooResponseWrapper();
        pw.setData(foo);
//        objectMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
//        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        final String str = objectMapper.writeValueAsString(new FooRequestWrapper(foo));
//        final String str = objectMapper.writeValueAsString(foo);
        System.out.println("str = " + str);

        final Foo foo2 = objectMapper.readValue(str, FooRequestWrapper.class).getData();
        assertEquals(foo.getName(), foo2.getName());
        assertEquals(str, foo2.getName());
//		final AbstractResponseWrapper<OrderStatusResource> result = parseJson("/api/v2/payout-response-success.json", new TypeReference<AbstractResponseWrapper<OrderStatusResource>>() {
//		});
    }


    @Test
    public void contextLoads() {
    }

}
