package org.study.task.lambda;

import org.junit.jupiter.api.Test;
import org.study.task.api.TaskResponse;
import org.study.task.api.TaskStatus;
import org.study.task.api.TaskValidator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskValidatorTest {

    TaskValidator hasTitle = task -> task.title() != null && !task.title().isBlank();

    @Test
    void validTask(){
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

        assertTrue(hasTitle.isValid(task));
    }

    @Test
    void notValidTask(){
        TaskResponse task = new TaskResponse(
                100L,
                "test@mail.ru",
                "test@mail.ru",
                null,
                "description",
                TaskStatus.TODO,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                null
        );

        assertFalse(hasTitle.isValid(task));
    }
}
