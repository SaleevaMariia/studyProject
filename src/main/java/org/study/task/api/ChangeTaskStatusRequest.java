package org.study.task.api;

import jakarta.validation.constraints.NotNull;

public record ChangeTaskStatusRequest(
        @NotNull(message = "Статус должен быть заполнен")
        TaskStatus status
) {
}
