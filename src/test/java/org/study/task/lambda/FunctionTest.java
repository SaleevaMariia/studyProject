package org.study.task.lambda;

import org.junit.jupiter.api.Test;
import org.study.task.api.TaskResponse;
import org.study.task.api.TaskStatus;

import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunctionTest {
    Function<String, Integer> length = s -> s.length();
    Function<TaskResponse, Long> toId = task -> task.id();

    @Test
    void testLength() {
        assertEquals(4, length.apply("test"));
    }

    @Test
    void testToId() {

        TaskResponse task = new TaskResponse(
                100L,
                "test@mail.ru",
                "test@mail.ru",
                "test",
                "description",
                TaskStatus.TODO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );

        assertEquals(100L, toId.apply(task));
    }
}
