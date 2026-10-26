package org.study.task.lambda;

import org.junit.jupiter.api.Test;
import org.study.task.api.TaskResponse;
import org.study.task.api.TaskStatus;

import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodReferenceTest {

    Function<TaskResponse, Long> toId = TaskResponse::id;

    @Test
    void test1(){
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

        assertEquals(100, toId.apply(task));
    }


}
