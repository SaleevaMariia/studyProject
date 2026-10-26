package org.study.task.service;

import org.springframework.stereotype.Service;
import org.study.task.api.CreateTaskRequest;
import org.study.task.api.TaskResponse;
import org.study.task.api.TaskStatus;
import org.study.task.domain.Task;
import org.study.task.exception.InvalidTaskStatusTransitionException;
import org.study.task.exception.TaskNotFoundException;
import org.study.task.repository.TaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.study.task.api.TaskStatus.CLOSED;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task(
                request.reporterEmail(),
                request.assigneeEmail(),
                request.title(),
                request.description(),
                request.dueDate());

        LocalDateTime now = LocalDateTime.now();
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        task.setStatus(TaskStatus.TODO);
        return toResponse(taskRepository.save(task));
    }

    public TaskResponse getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return toResponse(task);
    }

    public List<TaskResponse> getTasks() {
        return taskRepository.findAll().stream().map(this::toResponse).toList();
    }

    //tasks == null and status == null return empty List
    // tasks != null and status == null return copy of tasks чтобы метод единообразно не менял исходные данные
    //Список задач не должен содержать null-элементы
    public List<Task> findByStatus(
            List<Task> tasks,
            TaskStatus status
    ) {
        if (tasks == null) return List.of();
        if (status == null) return List.copyOf(tasks);

        return tasks.stream()
                .filter(t -> status.equals(t.getStatus()))
                .toList();

    }

    //null id не участвует в результате
    public List<String> getDistinctTaskIds(List<Task> tasks) {
        if (tasks == null) return List.of();
        return tasks.stream()
                .map(Task::getId)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .distinct()
                .toList();
    }

    //Вариант B: null помещается в конец - Менее критичные задачи
    public List<Task> sortByDueDate(List<Task> tasks) {
        if (tasks == null) {
            return List.of();
        }

        return tasks.stream()
                .sorted(Comparator.comparing(
                        Task::getDueDate,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    public Map<TaskStatus, List<Task>> groupByStatus(
            List<Task> tasks
    ) {
        if (tasks == null) {
            return Map.of();
        }

        return tasks.stream()
                .collect(Collectors.groupingBy(
                        Task::getStatus
                ));
    }

    public Map<String, Long> countByAssignee(
            List<Task> tasks
    ) {
        if (tasks == null) {
            return Map.of();
        }
        return tasks.stream()
                .collect(
                        Collectors.groupingBy(
                                Task::getAssigneeEmail,
                                Collectors.counting()
                        ));
    }

    public Map<TaskStatus, List<String>> titlesByStatus(
            List<Task> tasks
    ) {
        if (tasks == null) {
            return Map.of();
        }
        return tasks.stream()
                .collect(
                        Collectors.groupingBy(
                                Task::getStatus,
                                Collectors.mapping(Task::getTitle, toList())
                        )
                );
    }

    public Map<Long, Task> mapById(
            List<Task> tasks
    ) {
        if (tasks == null) {
            return Map.of();
        }
        return tasks.stream()
                .collect(
                        Collectors.toMap(
                                Task::getId,
                                task -> task
                        )
                );
    }

 /*   Требования:

    учитывать только просроченные задачи;
    задача с dueDate, равной today, не считается просроченной;
    вернуть первую подходящую задачу в порядке исходного списка;
    если подходящей задачи нет — вернуть Optional.empty();
    определить и зафиксировать поведение для tasks == null.(возвращаем Optional.Empty)*/

    public Optional<Task> findFirstOverdueTask(
            List<Task> tasks,
            LocalDate today
    ) {
        if (tasks == null) {
            return Optional.empty();
        }
        return tasks.stream()
                .filter(task -> task.getDueDate().isBefore(today))
                .findFirst();
    }


    /*    выбрать задачи указанного исполнителя;
        проверить, что у каждой выбранной задачи заполнен closedAt.
       список пустой; - true
    у исполнителя нет задач; - true
    assigneeEmail == null; - true
    у задачи closedAt == null.- false*/
    public boolean allTasksOfAssigneeClosed(
            List<Task> tasks,
            String assigneeEmail
    ) {
        if (tasks == null) return true;
        if (assigneeEmail == null) return true;

        return tasks
                .stream()
                .filter(task -> null == task.getClosedAt())
                .noneMatch(task -> assigneeEmail.equals(task.getAssigneeEmail()));
    }

/*    найти задачу по ID;
    не бросать исключение, если задача не найдена;
    вернуть Optional<TaskResponse>;
    преобразование Task → TaskResponse выполнить внутри pipeline.
    tasks == null; - Optional.Empty
    id == null; - Optional.Empty
    пустого списка.- Optional.Empty*/

    public Optional<TaskResponse> findTaskResponseById(
            List<Task> tasks,
            Long id
    ){
        if(tasks == null || id == null || tasks.isEmpty()) return Optional.empty();

        return tasks.stream()
                .filter(task -> id.equals(task.getId()))
                .findFirst()
                .map(this::toResponse);
    }


/*    выбрать только задачи со статусом TODO;
    получить их названия;
    сгруппировать названия по статусу;
    вернуть результат через Stream API;
    при tasks == null вернуть пустую Map.
    не менять исходный список;
    не использовать внешний изменяемый список;
    использовать groupingBy;
    использовать mapping.*/
    public Map<TaskStatus, List<String>> openTaskTitlesByStatus(
            List<Task> tasks
    ){
        if(tasks == null) return Map.of();

        return tasks.stream()
                .filter(task -> TaskStatus.TODO.equals(task.getStatus()))
                .collect(groupingBy(
                        Task::getStatus,
                        Collectors.mapping(
                                Task::getTitle,
                                toList()
                        )
                ));
    }


    public TaskResponse changeStatus(Long id, TaskStatus newStatus){
        if(newStatus== null){
            throw new IllegalArgumentException("Status should not be null");
        }

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if(CLOSED.equals(task.getStatus())){
            throw new InvalidTaskStatusTransitionException(task.getStatus(), newStatus);
        }
        task.setStatus(newStatus);
        task.setUpdatedAt(LocalDateTime.now());
        return toResponse(task);
    }

    public List<Task> sortByTitle(List<Task> tasks){
        if(tasks == null){
            return List.of();
        }
        return tasks.stream().sorted(Comparator.comparing(Task::getTitle, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    }

    public List<Task> sortByDueDateThenTitle(List<Task> tasks){
        if(tasks == null){
            return List.of();
        }
        return tasks.stream().sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getTitle, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    }

    public List<Task> sortByStatusThenDueDate(List<Task> tasks){
        if(tasks == null){
            return List.of();
        }
        return tasks.stream().sorted(Comparator.comparing(Task::getStatus, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))).toList();
    }



    public TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getReporterEmail(),
                task.getAssigneeEmail(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getClosedAt(),
                task.getDueDate()
        );
    }
}
