package org.study.task.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.study.task.api.CreateTaskRequest;
import org.study.task.api.TaskResponse;
import org.study.task.api.TaskStatus;
import org.study.task.domain.Task;
import org.study.task.exception.InvalidTaskStatusTransitionException;
import org.study.task.exception.TaskNotFoundException;
import org.study.task.repository.InMemoryTaskRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp(){
        taskService = new TaskService(new InMemoryTaskRepository());
    }


    @Test
    void createTaskShouldReturnTaskWithId() {
        CreateTaskRequest request = createTaskRequest("title1");
        TaskResponse response = taskService.createTask(request);
        assertNotNull(response.id());
    }

    @Test
    void createTaskShouldSetDefaultStatusTodo() {
        CreateTaskRequest request = createTaskRequest("title2");
        TaskResponse response = taskService.createTask(request);
        assertEquals(TaskStatus.TODO, response.status());
    }

    @Test
    void createTaskShouldSetCreatedAtAndUpdatedAt() {
        CreateTaskRequest request = createTaskRequest("title3");
        TaskResponse response = taskService.createTask(request);
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());
        assertEquals(response.createdAt(), response.updatedAt());
    }

    @Test
    void getTaskShouldReturnExistingTask() {
        CreateTaskRequest request = createTaskRequest("title4");
        TaskResponse response = taskService.createTask(request);
        TaskResponse responseGetTask = taskService.getTask(response.id());
        assertEquals(response.id(), responseGetTask.id());
        assertEquals(response.title(), responseGetTask.title());
    }

    @Test
    void getTaskShouldThrowTaskNotFoundException() {
        assertThrows(
                TaskNotFoundException.class,
                () -> taskService.getTask(Long.MAX_VALUE)
        );
    }

    @Test
    void getTasksShouldReturnAllTasks() {
        CreateTaskRequest request = createTaskRequest("title5");
        TaskResponse response = taskService.createTask(request);
        List<TaskResponse> responseGetTasks = taskService.getTasks();
        assertTrue(responseGetTasks.stream().map(TaskResponse::id).anyMatch(id -> Objects.equals(id, response.id())));
    }

    @Test
    void testFilter(){
        List<Task> tasks = new ArrayList<>();
        tasks.add(createTask(TaskStatus.TODO));
        tasks.add(createTask(TaskStatus.CLOSED));

        assertEquals(0, taskService.findByStatus(tasks, TaskStatus.CANCELED).size());
        assertEquals(1, taskService.findByStatus(tasks, TaskStatus.TODO).size());
        assertEquals(2, taskService.findByStatus(tasks, null).size());
        assertEquals(0, taskService.findByStatus(null, TaskStatus.TODO).size());
    }

    @Test
    void testDeleteDistinctSomeEquals(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setId(1L);
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setId(2L);
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setId(1L);
        tasks.add(task3);

        List<String> idsExpected = new ArrayList<>();
        idsExpected.add("1");
        idsExpected.add("2");

        assertEquals(idsExpected, taskService.getDistinctTaskIds(tasks));
    }

    @Test
    void testDeleteDistinctAllDifferent(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setId(1L);
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setId(2L);
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setId(3L);
        tasks.add(task3);

        List<String> idsExpected = new ArrayList<>();
        idsExpected.add("1");
        idsExpected.add("2");
        idsExpected.add("3");

        assertEquals(idsExpected, taskService.getDistinctTaskIds(tasks));
    }

    @Test
    void testDeleteDistinctNull(){
        assertEquals(0, taskService.getDistinctTaskIds(null).size());
    }

    @Test
    void testDueDateRandom(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setDueDate(LocalDate.of(2026, 1, 2));
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setDueDate(LocalDate.of(2026, 1, 5));
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setDueDate(LocalDate.of(2026, 1, 1));
        tasks.add(task3);

        List<Task> tasksExpected = new ArrayList<>();
        tasksExpected.add(task3);
        tasksExpected.add(task1);
        tasksExpected.add(task2);

        assertEquals(tasksExpected, taskService.sortByDueDate(tasks));
    }

    @Test
    void testDueDateAlreadySorted(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setDueDate(LocalDate.of(2026, 1, 1));
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setDueDate(LocalDate.of(2026, 1, 2));
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setDueDate(LocalDate.of(2026, 1, 5));
        tasks.add(task3);

        List<Task> tasksExpected = new ArrayList<>();
        tasksExpected.add(task1);
        tasksExpected.add(task2);
        tasksExpected.add(task3);

        assertEquals(tasksExpected, taskService.sortByDueDate(tasks));
    }

    @Test
    void testDueDateOneTask(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setDueDate(LocalDate.of(2026, 1, 1));
        tasks.add(task1);

        List<Task> tasksExpected = new ArrayList<>();
        tasksExpected.add(task1);

        assertEquals(tasksExpected, taskService.sortByDueDate(tasks));
    }

    @Test
    void testDueDateEmptyList(){
        List<Task> tasks = new ArrayList<>();
        List<Task> tasksExpected = new ArrayList<>();

        assertEquals(tasksExpected, taskService.sortByDueDate(tasks));
    }

    @Test
    void testDueDateTwoEqual(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setDueDate(LocalDate.of(2026, 1, 1));
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setDueDate(LocalDate.of(2026, 1, 1));
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setDueDate(LocalDate.of(2026, 1, 5));
        tasks.add(task3);

        List<Task> tasksExpected = new ArrayList<>();
        tasksExpected.add(task1);
        tasksExpected.add(task2);
        tasksExpected.add(task3);

        assertEquals(tasksExpected, taskService.sortByDueDate(tasks));
    }


    @Test
    void testGroupByPositive(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        tasks.add(task3);

        Map<TaskStatus, List<Task>> tasksExpectedMap = new HashMap<>();
        List<Task> tasksExpected1 = new ArrayList<>();
        tasksExpected1.add(task1);
        tasksExpected1.add(task2);

        List<Task> tasksExpected2 = new ArrayList<>();
        tasksExpected2.add(task3);

        tasksExpectedMap.put(TaskStatus.TODO, tasksExpected1);
        tasksExpectedMap.put(TaskStatus.CANCELED, tasksExpected2);

        assertEquals(tasksExpectedMap, taskService.groupByStatus(tasks));
    }

    @Test
    void testGroupByNegative(){
        Map<TaskStatus, List<Task>> tasksExpectedMap = new HashMap<>();
        assertEquals(tasksExpectedMap, taskService.groupByStatus(null));
    }

    @Test
    void testCountPositive(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setAssigneeEmail("test1@mail.ru");
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setAssigneeEmail("test2@mail.ru");
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setAssigneeEmail("test1@mail.ru");
        tasks.add(task3);

        Map<String, Long> tasksExpectedMap = new HashMap<>();

        tasksExpectedMap.put("test1@mail.ru", 2L);
        tasksExpectedMap.put("test2@mail.ru", 1L);

        assertEquals(tasksExpectedMap, taskService.countByAssignee(tasks));
    }

    @Test
    void testCountNegative(){
        Map<String, Long> tasksExpectedMap = new HashMap<>();
        assertEquals(tasksExpectedMap, taskService.countByAssignee(null));
    }

    @Test
    void testTitlePositive(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setTitle("test1");
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setTitle("test2");
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setTitle("test1");
        tasks.add(task3);

        Map<TaskStatus, List<String>> tasksExpectedMap = new HashMap<>();

        tasksExpectedMap.put(TaskStatus.TODO, Arrays.asList("test1", "test2"));
        tasksExpectedMap.put(TaskStatus.CANCELED, Arrays.asList("test1"));

        assertEquals(tasksExpectedMap, taskService.titlesByStatus(tasks));
    }


    @Test
    void testOverduePositive(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setDueDate(LocalDate.now());
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setDueDate(LocalDate.now().minusDays(1));
        task2.setTitle("new");
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setDueDate(LocalDate.now().plusDays(1));
        tasks.add(task3);

        assertEquals(Optional.of(task2), taskService.findFirstOverdueTask(tasks, LocalDate.now()));
    }

    @Test
    void testOverdueNegative(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setDueDate(LocalDate.now());
        tasks.add(task1);
        assertTrue(taskService.findFirstOverdueTask(tasks, LocalDate.now()).isEmpty());
    }

    @Test
    void testClosedAtFalse(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setClosedAt(null);
        task1.setAssigneeEmail("test1@mail.ru");
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setClosedAt(LocalDate.now().atStartOfDay());
        task2.setAssigneeEmail("test1@mail.ru");
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setClosedAt(null);
        task3.setAssigneeEmail("test2@mail.ru");
        tasks.add(task3);

        assertFalse(taskService.allTasksOfAssigneeClosed(tasks, "test1@mail.ru"));
    }

    @Test
    void testClosedAtTrue(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setClosedAt(LocalDateTime.now());
        task1.setAssigneeEmail("test1@mail.ru");
        tasks.add(task1);

        assertTrue(taskService.allTasksOfAssigneeClosed(tasks, "test4@mail.ru"));
    }

    @Test
    void testTaskResponsePositive(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setId(4L);
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setId(5L);
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setId(3L);
        tasks.add(task3);

        TaskResponse expected = taskService.toResponse(task2);
        assertEquals(Optional.of(expected), taskService.findTaskResponseById(tasks, 5L));
    }

    @Test
    void testTaskResponseNegative(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setId(4L);
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setId(5L);
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setId(3L);
        tasks.add(task3);

        assertEquals(Optional.empty(), taskService.findTaskResponseById(tasks, 10L));
    }

    @Test
    void testMapToIdPositive(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setId(4L);
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setId(5L);
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setId(3L);
        tasks.add(task3);

        Map<Long, Task> tasksExpectedMap = new HashMap<>();

        tasksExpectedMap.put(4L, task1);
        tasksExpectedMap.put(5L, task2);
        tasksExpectedMap.put(3L, task3);


        assertEquals(tasksExpectedMap, taskService.mapById(tasks));
    }

    @Test
    void testMapToIdNegative(){
        Map<Long, Task> tasksExpectedMap = new HashMap<>();
        assertEquals(tasksExpectedMap, taskService.mapById(null));
    }

    @Test
    void testPositive(){
        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(TaskStatus.TODO);
        task1.setTitle("123");
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setTitle("456");
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.CANCELED);
        task3.setTitle("789");
        tasks.add(task3);

        Map<TaskStatus, List<String>> tasksExpectedMap = new HashMap<>();

        tasksExpectedMap.put(TaskStatus.TODO, List.of("123", "456"));

        assertEquals(tasksExpectedMap, taskService.openTaskTitlesByStatus(tasks));
    }

    @Test
    void testNegative(){
        assertEquals(Map.of(), taskService.openTaskTitlesByStatus(null));
    }

    @Test
    void testChangeStatusPositive(){
        CreateTaskRequest request = createTaskRequest("title");
        TaskResponse response = taskService.createTask(request);
        TaskResponse result = taskService.changeStatus(response.id(), TaskStatus.CANCELED);

        assertEquals(TaskStatus.CANCELED, result.status());
    }

    @Test
    void testChangeStatusNegative(){
        CreateTaskRequest request = createTaskRequest("title");
        TaskResponse response = taskService.createTask(request);
        TaskResponse result = taskService.changeStatus(response.id(), TaskStatus.CLOSED);
        assertThrows(
                InvalidTaskStatusTransitionException.class,
                () -> taskService.changeStatus(result.id(),TaskStatus.CANCELED)
        );
    }

    @Test
    void testSortByTitle(){
        List<Task> tasks = new ArrayList<>();
        Task task0 = createTask(TaskStatus.TODO);
        task0.setTitle(null);
        tasks.add(task0);
        Task task1 = createTask(TaskStatus.TODO);
        task1.setTitle("bbb");
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setTitle("ccc");
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setTitle("aaa");
        tasks.add(task3);

        List<Task> tasksExpected = List.of(task3, task1, task2, task0);

        assertEquals(tasksExpected, taskService.sortByTitle(tasks));
    }

    @Test
    void testSortByDueDateTitle(){
        List<Task> tasks = new ArrayList<>();
        Task task0 = createTask(TaskStatus.TODO);
        task0.setTitle(null);
        task0.setDueDate(LocalDate.of(2026, 7, 1));
        tasks.add(task0);
        Task task1 = createTask(TaskStatus.TODO);
        task1.setTitle("bbb");
        task1.setDueDate(LocalDate.of(2026, 7, 3));
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.TODO);
        task2.setDueDate(LocalDate.of(2026, 7, 1));
        task2.setTitle("ccc");
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setTitle("aaa");
        task3.setDueDate(LocalDate.of(2026, 7, 1));
        tasks.add(task3);

        List<Task> tasksExpected = List.of(task3, task2, task0, task1);

        assertEquals(tasksExpected, taskService.sortByDueDateThenTitle(tasks));
    }

    @Test
    void testSortByDueDateTitleNegative(){
        assertEquals(List.of(), taskService.sortByDueDateThenTitle(null));
    }

    @Test
    void testSortByTitleNegative(){
        assertEquals(List.of(), taskService.sortByTitle(null));
    }

    @Test
    void testSortByStatusThenDueDateNegative(){
        assertEquals(List.of(), taskService.sortByStatusThenDueDate(null));
    }

    @Test
    void testSortByStatusDueDate(){
        List<Task> tasks = new ArrayList<>();
        Task task0 = createTask(TaskStatus.CLOSED);
        task0.setDueDate(LocalDate.of(2026, 7, 1));
        tasks.add(task0);
        Task task1 = createTask(TaskStatus.TODO);
        task1.setDueDate(LocalDate.of(2026, 7, 3));
        tasks.add(task1);
        Task task2 = createTask(TaskStatus.CANCELED);
        task2.setDueDate(LocalDate.of(2026, 7, 1));
        tasks.add(task2);
        Task task3 = createTask(TaskStatus.TODO);
        task3.setDueDate(LocalDate.of(2026, 7, 1));
        tasks.add(task3);
        Task task4 = createTask(null);
        task4.setDueDate(LocalDate.of(2026, 7, 1));
        tasks.add(task4);

        List<Task> tasksExpected = List.of(task3, task1, task2, task0, task4);

        assertEquals(tasksExpected, taskService.sortByStatusThenDueDate(tasks));
    }

    private Task createTask(TaskStatus status) {
        Task task = new Task(
                "reporter@test.com",
                "assignee@test.com",
                "title",
                "description",
                LocalDate.now().plusDays(1)
        );
        task.setStatus(status);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        return task;
    }

    private CreateTaskRequest createTaskRequest(String title) {
        return new CreateTaskRequest(
                "reporter@test.com",
                "assignee@test.com",
                title,
                "description",
                LocalDate.now().plusDays(1)
        );
    }
}