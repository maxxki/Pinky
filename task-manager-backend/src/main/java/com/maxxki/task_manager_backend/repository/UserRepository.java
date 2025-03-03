package com.maxxki.task_manager_backend.repository;

import com.maxxki.task_manager_backend.model.PinkyUser; // Korrekter Import ist PinkyUser
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<PinkyUser, Long> { // **WICHTIG: Generic-Typ auf PinkyUser geändert!**

    // Hier könnten weitere benutzerdefinierte Repository-Methoden sein
    java.util.Optional<PinkyUser> findByUsername(String username); // Auch hier sollte es PinkyUser sein, explizit machen!

}