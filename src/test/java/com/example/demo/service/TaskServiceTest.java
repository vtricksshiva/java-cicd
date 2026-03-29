package com.example.demo.service;

import com.example.demo.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TaskServiceTest {
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService();
    }

    @Test
    void getAllTasks_returnsInitialTasks() {
        List<Task> tasks = taskService.getAllTasks();

        assertThat(tasks).isNotEmpty();
        assertThat(tasks).hasSize(3);
        assertThat(tasks.get(0).getTitle()).contains("Plan deployment");
    }

    @Test
    void addTask_assignsIdAndAddsTask() {
        Task task = new Task();
        task.setTitle("Test task");
        task.setDescription("Validate addTask method.");

        Task created = taskService.addTask(task);

        assertThat(created.getId()).isNotNull();
        assertThat(created.isCompleted()).isFalse();
        assertThat(taskService.getAllTasks()).contains(created);
    }

    @Test
    void updateCompletion_updatesTaskStatus() {
        Task newTask = taskService.addTask(new Task(null, "Finish tests", "Mark task complete.", false));

        taskService.updateCompletion(newTask.getId(), true);

        assertThat(taskService.getAllTasks())
                .filteredOn(task -> task.getId().equals(newTask.getId()))
                .extracting(Task::isCompleted)
                .containsExactly(true);
    }
}
