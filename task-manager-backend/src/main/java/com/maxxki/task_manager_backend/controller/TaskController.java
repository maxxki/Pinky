package com.maxxki.task_manager_backend.controller;

import com.maxxki.task_manager_backend.model.PinkyUser; // Änderung 1: Import zu PinkyUser geändert
import com.maxxki.task_manager_backend.model.Task;
import com.maxxki.task_manager_backend.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @AuthenticationPrincipal PinkyUser user) { // Parameter Typ ist PinkyUser
        Task createdTask = taskService.createTask(task, user);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@AuthenticationPrincipal PinkyUser user) { // Parameter Typ ist PinkyUser
        List<Task> tasks = taskService.getAllTasks(user);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, @AuthenticationPrincipal PinkyUser user) { // Parameter Typ ist PinkyUser
        Task task = taskService.getTaskById(id, user);
        if (task != null) {
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask, @AuthenticationPrincipal PinkyUser user) { // Parameter Typ ist PinkyUser
        Task task = taskService.updateTask(id, updatedTask, user);
        if (task != null) {
            return new ResponseEntity<>(task, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteTask(@PathVariable Long id, @AuthenticationPrincipal PinkyUser user) { // Parameter Typ ist PinkyUser
        boolean deleted = taskService.deleteTask(id, user);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}