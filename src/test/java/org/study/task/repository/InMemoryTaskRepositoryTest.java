package org.study.task.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.study.task.api.TaskStatus;
import org.study.task.domain.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskRepositoryTest {

    private InMemoryTaskRepository repository;

    @BeforeEach
    void setUp(){
        repository = new InMemoryTaskRepository();
    }

    @Test
    void saveNewTaskShouldAssignId() {
        Task task = createTask("title1");
        Task createdTask = repository.save(task);
        assertNotNull(createdTask.getId());

    }

    @Test
    void findByIdShouldReturnSavedTask() {
        Task task = createTask("title2");
        Task createdTask = repository.save(task);
        Task getTask = repository.findById(createdTask.getId()).orElseThrow();
        assertEquals(createdTask.getId(), getTask.getId());
    }

    @Test
    void findByIdShouldReturnEmptyWhenTaskDoesNotExist() {
        Optional<Task> getTask = repository.findById(Long.MAX_VALUE);
        assertTrue(getTask.isEmpty());
    }

    @Test
    void findAllShouldReturnSavedTasks() {
        Task task = createTask("title3");
        Task createdTask = repository.save(task);
        List<Task> getTasks = repository.findAll();
        assertTrue(getTasks.stream().map(Task::getId).anyMatch(id -> Objects.equals(id, createdTask.getId())));
    }

    @Test
    void saveExistingTaskShouldUpdateTask() {
        Task task = createTask("title4");
        Task createdTask = repository.save(task);
        createdTask.setTitle("title5");
        Task updatedTask = repository.save(createdTask);
        Task getTask = repository.findById(updatedTask.getId()).orElseThrow();
        assertEquals("title5", getTask.getTitle());
    }

    private Task createTask(String title) {
        Task task = new Task(
                "reporter@test.com",
                "assignee@test.com",
                title,
                "description",
                LocalDate.now().plusDays(1)
        );
        task.setStatus(TaskStatus.TODO);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }
}