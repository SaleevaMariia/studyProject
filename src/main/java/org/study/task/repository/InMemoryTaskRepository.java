package org.study.task.repository;

import org.springframework.stereotype.Repository;
import org.study.task.domain.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryTaskRepository implements TaskRepository{

    private final Map<Long, Task> tasksById = new HashMap<>();
    private long nextId = 1;

    @Override
    public Task save(Task task) {
        Long taskId = task.getId();
        if (taskId == null){
            taskId = nextId++;
            task.setId(taskId);
        }
        tasksById.put(taskId, task);
        return task;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasksById.get(id));
    }

    @Override
    public List<Task> findAll() {
        return tasksById.values().stream().toList();
    }
}
