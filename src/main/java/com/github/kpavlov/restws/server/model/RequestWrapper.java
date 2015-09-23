package com.github.kpavlov.restws.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.Valid;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequestWrapper<T> {

    @JsonIgnore
    String raw;

    @Valid
    T data;

    public RequestWrapper() {
    }

    public RequestWrapper(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRaw() {
        return raw;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"data\": ").append(data);
        sb.append('}');
        return sb.toString();
    }
}
