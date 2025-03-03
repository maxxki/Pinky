package com.maxxki.task_manager_backend.service;

import com.maxxki.task_manager_backend.model.Task;
import com.maxxki.task_manager_backend.model.PinkyUser; // Wichtig: Import PinkyUser, nicht User!
import com.maxxki.task_manager_backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(Task task, PinkyUser user) { // PinkyUser verwenden
        task.setUser(user);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(PinkyUser user) { // PinkyUser verwenden
        return taskRepository.findByUser_Id(user.getId());
    }

    public Task getTaskById(Long id, PinkyUser user) { // PinkyUser verwenden
        // Hier Logik zum Finden einer Task nach ID und User einfügen (optional)
        return taskRepository.findById(id).orElse(null); // Einfache Implementierung für den Moment
    }

    public Task updateTask(Long id, Task updatedTask, PinkyUser user) { // PinkyUser verwenden
        // Hier Logik zum Aktualisieren einer Task einfügen (optional)
        return null; // Einfache Implementierung für den Moment
    }

    public boolean deleteTask(Long id, PinkyUser user) { // PinkyUser verwenden
        // Hier Logik zum Löschen einer Task einfügen (optional)
        return false; // Einfache Implementierung für den Moment
    }
}