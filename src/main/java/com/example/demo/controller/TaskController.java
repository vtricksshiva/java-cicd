package com.example.demo.controller;

import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("projectName", "Java Frontend App");
        return "index";
    }

    @GetMapping("/api/tasks")
    @ResponseBody
    public List<Task> getTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping("/api/tasks")
    @ResponseBody
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        if (task.getTitle() == null || task.getTitle().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Task created = taskService.addTask(task);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/api/tasks/{id}")
    @ResponseBody
    public ResponseEntity<Task> toggleTask(@PathVariable("id") long id, @RequestParam("completed") boolean completed) {
        return taskService.updateCompletion(id, completed)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
