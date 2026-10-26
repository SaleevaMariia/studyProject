package org.study.task.api;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateTaskRequest(
        @NotBlank(message = "Reporter email must not be blank")
        @Email(message = "Некорректный формат email для поля Reporter")
        String reporterEmail,
        @Email(message = "Некорректный формат email для поля Assignee")
        String assigneeEmail,
        @NotBlank(message = "Title must not be blank")
        @Size(min = 2, max = 255, message = "Название должно быть от 2 до 255 символов")
        String title,
        @Size(max = 5000, message = "Описание должно быть до 5000 символов")
        String description,
        @FutureOrPresent(message = "Срок для задачи должен быть больше или равен текущей дате")
        LocalDate dueDate
) {
}
