package com.example.planttracker.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PlantLog {
    private String photoUrl;
    private String note;
    private Instant timestamp;
    private List<PlantComment> comments = new ArrayList<>();

    // Getters and setters
    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public List<PlantComment> getComments() {
        return comments;
    }

    public void setComments(List<PlantComment> comments) {
        this.comments = comments;
    }
}