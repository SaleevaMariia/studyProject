package org.study.task.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateTaskRequest(
        @Size(min = 2, max = 255, message = "Название должно быть от 2 до 255 символов")
        String title,
        @Size(max = 5000, message = "Описание должно быть до 5000 символов")
        String description,
        @Email(message = "Некорректный формат email для поля Assignee")
        String assigneeEmail,
        @FutureOrPresent(message = "Срок для задачи должен быть больше или равен текущей дате")
        LocalDate dueDate

) {
}
