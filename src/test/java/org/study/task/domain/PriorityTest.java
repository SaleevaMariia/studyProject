package org.study.task.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PriorityTest {

    @Test
    void compareTo() {
        List<Priority> priorities = new ArrayList<>();
        priorities.add(new Priority(3));
        priorities.add(new Priority(1));
        priorities.add(new Priority(2));
        List<Priority> sortedPriorities = priorities.stream().sorted().toList();
        assertEquals(List.of(1, 2, 3), sortedPriorities.stream().map(Priority::getValue).toList());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6})
    void testException(int value) {
        assertThrows(
                IllegalArgumentException.class,
                () -> {
                    new Priority(value);
                }
        );
    }
}