package org.study.task.lambda;

import org.junit.jupiter.api.Test;
import org.study.task.api.TaskStatus;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupplierTest {

    Supplier<String> defaultTitle = () -> "Untitled";
    Supplier<TaskStatus> defaultStatus = () -> TaskStatus.TODO;

    @Test
    void testDefaultTitle(){
        assertEquals("Untitled", defaultTitle.get());
    }

    @Test
    void testDefaultStatus(){
        assertEquals(TaskStatus.TODO, defaultStatus.get());
    }
}
