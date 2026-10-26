package org.study.task.lambda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsumerTest {

    List<String> output;
    Consumer<String> addToOutput;

    @BeforeEach()
    void setUp() {
        output = new ArrayList<>();
        addToOutput = value -> output.add(value);
    }

    @Test
    void test() {
        addToOutput.accept("A");
        addToOutput.accept("B");
        assertEquals(List.of("A", "B"), output);
    }
}
