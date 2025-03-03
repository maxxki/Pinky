package com.maxxki.task_manager_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdDate;
    private boolean completed;

    @ManyToOne // Änderung 1: Beziehung zu PinkyUser
    @JoinColumn(name = "user_id")
    private PinkyUser user; // Änderung 2: Feldtyp zu PinkyUser

    public Task() {
    }

    public Task(String title, String description, PinkyUser user) { // Änderung 3: Konstruktor Parameter zu PinkyUser
        this.title = title;
        this.description = description;
        this.createdDate = LocalDateTime.now();
        this.completed = false;
        this.user = user;
    }

    // Getter und Setter Methoden (unverändert, hier zur Vollständigkeit)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public PinkyUser getUser() { // Getter Typ ist jetzt PinkyUser
        return user;
    }

    public void setUser(PinkyUser user) { // Setter Typ ist jetzt PinkyUser
        this.user = user;
    }
}