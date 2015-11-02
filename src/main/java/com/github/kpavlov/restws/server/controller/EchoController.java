package com.github.kpavlov.restws.server.controller;

import com.github.kpavlov.restws.server.model.*;
import com.github.kpavlov.restws.server.model.Error;
import org.slf4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("unused")
@RestController
public class EchoController {

    private final Logger logger = getLogger(EchoController.class);

    @RequestMapping(value = "/api/echo",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE,
                    "application/vnd.api+json"},
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    AbstractResponseWrapper<Foo> echo(@RequestBody @Valid FooRequestWrapper request,
                                      BindingResult bindingResult,
                                      HttpServletResponse httpServletResponse) {

        final AbstractResponseWrapper<Foo> response = new FooResponseWrapper();

        if (bindingResult.hasErrors()) {
            logger.debug("bindingResult.errors = {}", bindingResult.getFieldErrors());

            bindingResult.getFieldErrors().forEach(e -> {
                final Error error = new Error();
                error.addPointerSource(e.getField());
                error.setTitle("Invalid Attribute");
                error.setDetail(e.getDefaultMessage());
                response.addError(error);
            });

            httpServletResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return response;
        }

        logger.trace("request = {}", request);

        response.setData(request.getData());

        return response;
    }


}
