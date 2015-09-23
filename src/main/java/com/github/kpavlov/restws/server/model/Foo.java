package com.github.kpavlov.restws.server.model;

import javax.validation.constraints.Size;

public class Foo {

    @Size(min = 2, max = 5)
    private String name;

    Foo() {
    }

    public Foo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Foo foo = (Foo) o;

        return !(name != null ? !name.equals(foo.name) : foo.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
