package com.github.kpavlov.restws.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AbstractResponseWrapper<T> {

    @Valid
    T data;

    @Valid
    List<Error> errors;

    @Valid
    JsonApi jsonapi;

    public AbstractResponseWrapper() {
    }

    public AbstractResponseWrapper(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setError(List<Error> errors) {
        this.errors = errors;
    }

    public JsonApi getJsonapi() {
        return jsonapi;
    }

    public void setJsonapi(JsonApi jsonapi) {
        this.jsonapi = jsonapi;
    }

    public boolean addError(Error error) {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return errors.add(error);
    }

    public static class JsonApi {
        public static final JsonApi INSTANCE = new JsonApi();

        String version = "1.0";
    }
}
