package com.example.planttracker.model;

import java.time.Instant;

public class PlantComment {

    private String username;
    private String comment;
    private Instant timestamp; // ✅ This must exist

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; } // ✅ This must exist
}