package org.study.task.lambda;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EffectivelyFinalTest {

    @Test
    void testCompile() {
        String prefix = "Task: ";
        // prefix = "New: "; //Лямбда может захватывать локальные переменные только если они final/effectively final.
        // Если переменная меняется после присваивания, компилятор не позволит использовать её внутри lambda.
        Function<String, String> addPrefix = title -> prefix + title;
        assertEquals("Task: test", addPrefix.apply("test"));
    }
}
