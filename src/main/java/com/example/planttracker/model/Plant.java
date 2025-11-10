package com.example.planttracker.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "plants")
public class Plant {
    @Id
    private String id;
    private String name;
    private String species;
    private LocalDate lastWateredDate;
    private int wateringFrequencyDays;
    private String ownerUsername;
    private String soilType;
    private String fertilizer;
    private String sunExposure;
    private String idealTemperature;
    private String notes;


    public Plant() {}

    public Plant(String name, String species, LocalDate lastWateredDate, int wateringFrequencyDays, String ownerUsername) {
        this.name = name;
        this.species = species;
        this.lastWateredDate = lastWateredDate;
        this.wateringFrequencyDays = wateringFrequencyDays;
        this.ownerUsername = ownerUsername;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public LocalDate getLastWateredDate() { return lastWateredDate; }
    public void setLastWateredDate(LocalDate lastWateredDate) { this.lastWateredDate = lastWateredDate; }

    public int getWateringFrequencyDays() { return wateringFrequencyDays; }
    public void setWateringFrequencyDays(int wateringFrequencyDays) { this.wateringFrequencyDays = wateringFrequencyDays; }

    public String getOwnerUsername() { return ownerUsername; }
    public void setOwnerUsername(String ownerUsername) { this.ownerUsername = ownerUsername; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getFertilizer() { return fertilizer; }
    public void setFertilizer(String fertilizer) { this.fertilizer = fertilizer; }

    public String getSunExposure() { return sunExposure; }
    public void setSunExposure(String sunExposure) { this.sunExposure = sunExposure; }

    public String getIdealTemperature() { return idealTemperature; }
    public void setIdealTemperature(String idealTemperature) { this.idealTemperature = idealTemperature; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}