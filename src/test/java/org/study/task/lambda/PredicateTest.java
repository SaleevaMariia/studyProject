package org.study.task.lambda;

import org.junit.jupiter.api.Test;
import org.study.task.api.TaskResponse;
import org.study.task.api.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PredicateTest {

    Predicate<TaskResponse> isTodo = x -> x.status() == TaskStatus.TODO;
    Predicate<TaskResponse> isClosed = x -> x.status() == TaskStatus.CLOSED;

    @Test
    void test(){
        TaskResponse task = new TaskResponse(
                1L,
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

        assertTrue(isTodo.test(task));
        assertFalse(isClosed.test(task));
    }
}