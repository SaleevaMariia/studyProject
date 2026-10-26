package org.study.task.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    void test(){
        Person p1 = new Person(1L, 30);
        Person p2 = new Person(2L, 30);

        Set<Person> set = new TreeSet<>();
        set.add(p1);
        set.add(p2);

        assertEquals(2, set.size());
        assertFalse(p1.equals(p2));
        assertTrue(p1.compareTo(p2) < 0);
    }

}