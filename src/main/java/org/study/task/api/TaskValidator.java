package org.study.task.api;

@FunctionalInterface
public interface TaskValidator {
    boolean isValid(TaskResponse task);
}
