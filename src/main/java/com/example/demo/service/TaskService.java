package com.example.demo.service;

import com.example.demo.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {
    private final List<Task> tasks = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong nextId = new AtomicLong(1);

    public TaskService() {
        tasks.add(new Task(nextId.getAndIncrement(), "Plan deployment", "Prepare application for Tomcat deployment.", false));
        tasks.add(new Task(nextId.getAndIncrement(), "Review UI", "Validate responsive design and theme.", false));
        tasks.add(new Task(nextId.getAndIncrement(), "Run tests", "Execute unit tests before deploy.", false));
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public Task addTask(Task task) {
        task.setId(nextId.getAndIncrement());
        task.setCompleted(false);
        tasks.add(task);
        return task;
    }

    public Optional<Task> updateCompletion(long taskId, boolean completed) {
        return tasks.stream()
                .filter(task -> task.getId() == taskId)
                .findFirst()
                .map(task -> {
                    task.setCompleted(completed);
                    return task;
                });
    }
}
