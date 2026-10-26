package org.study.task.domain;

import java.util.Objects;

public class Person implements Comparable<Person>{

    private final Long id;
    private final int age;

    public Person(Long id, int age) {
        if (id == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        this.id = id;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(Person o) {
        return Long.compare(this.id, o.id);
    }
}
