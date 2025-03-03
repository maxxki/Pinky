package com.maxxki.task_manager_backend.repository;

import com.maxxki.task_manager_backend.model.PinkyUser; // Änderung 1: Import zu PinkyUser geändert
import com.maxxki.task_manager_backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> { // Keine Änderung im JpaRepository Generic selbst!

    List<Task> findByUser_Id(Long userId); // Änderung 2:  Methode verwendet implizit PinkyUser über user_Id

    // Hier könnten weitere benutzerdefinierte Repository-Methoden sein
}