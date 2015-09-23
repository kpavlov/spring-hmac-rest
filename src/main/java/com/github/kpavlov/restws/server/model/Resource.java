package com.github.kpavlov.restws.server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;

public class Resource<T> {

    String id;

    @NotBlank
    String type;

    @Valid
    @JsonProperty("attributes")
    T data;
}
