package com.github.kpavlov.restws.server.model;

public class FooRequestWrapper extends RequestWrapper<Foo> {

    FooRequestWrapper() {
    }

    public FooRequestWrapper(Foo data) {
        super(data);
    }
}
