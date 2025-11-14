package com.example.planttracker.service;

import com.example.planttracker.model.Plant;
import com.example.planttracker.model.PlantLog;
import com.example.planttracker.model.PlantComment;
import com.example.planttracker.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlantService {

    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    // Get all plants for a specific user
    public List<Plant> getPlantsByOwner(String ownerUsername) {
        return plantRepository.findByOwnerUsername(ownerUsername);
    }

    // Get public/community plants
    public List<Plant> getPublicPlants() {
        return plantRepository.findByIsPublicTrue();
    }

    // Get single plant by id
    public Plant getPlantById(String plantId) {
        return plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));
    }

    // Add plant
    public Plant addPlant(Plant plant) {
        if (plant.getOwnerUsername() == null || plant.getOwnerUsername().isEmpty()) {
            throw new RuntimeException("Plant must have an ownerUsername");
        }
        if (plant.getLogs() == null) plant.setLogs(new ArrayList<>());
        return plantRepository.save(plant);
    }

    // Update plant (owner only)
    public Plant updatePlant(String plantId, Plant updatedPlant, String ownerUsername) {
        Plant existing = getPlantById(plantId);
        if (!existing.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only update your own plants");
        }

        existing.setName(updatedPlant.getName());
        existing.setSpecies(updatedPlant.getSpecies());
        existing.setLastWateredDate(updatedPlant.getLastWateredDate());
        existing.setWateringFrequencyDays(updatedPlant.getWateringFrequencyDays());
        existing.setSoilType(updatedPlant.getSoilType());
        existing.setFertilizer(updatedPlant.getFertilizer());
        existing.setSunExposure(updatedPlant.getSunExposure());
        existing.setIdealTemperature(updatedPlant.getIdealTemperature());
        existing.setNotes(updatedPlant.getNotes());

        // preserve or update isPublic flag (allow owner to change it)
        existing.setPublic(updatedPlant.isPublic());

        if (existing.getLogs() == null) existing.setLogs(new ArrayList<>());

        return plantRepository.save(existing);
    }

    // Delete plant
    public void deletePlant(String plantId, String ownerUsername) {
        Plant existing = getPlantById(plantId);
        if (!existing.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only delete your own plants");
        }
        plantRepository.delete(existing);
    }

    // Add a log
    public Plant addLogToPlant(String plantId, PlantLog newLog, String ownerUsername) {
        Plant plant = getPlantById(plantId);
        if (!plant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only add logs to your own plants");
        }
        if (newLog.getNote() == null || newLog.getNote().trim().isEmpty()) {
            throw new RuntimeException("Log must have a note");
        }
        if (plant.getLogs() == null) plant.setLogs(new ArrayList<>());
        if (newLog.getComments() == null) newLog.setComments(new ArrayList<>());

        newLog.setTimestamp(Instant.now());
        plant.getLogs().add(newLog);
        return plantRepository.save(plant);
    }

    // Add comment
    public Plant addCommentToLog(String plantId, int logIndex, PlantComment newComment, String ownerUsername) {
        Plant plant = getPlantById(plantId);
        if (!plant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only comment on your own plants' logs");
        }
        if (logIndex < 0 || logIndex >= plant.getLogs().size()) {
            throw new RuntimeException("Invalid log index");
        }
        if (newComment.getComment() == null || newComment.getComment().trim().isEmpty()) {
            throw new RuntimeException("Comment must have text");
        }
        newComment.setTimestamp(Instant.now());
        var log = plant.getLogs().get(logIndex);
        if (log.getComments() == null) log.setComments(new ArrayList<>());
        log.getComments().add(newComment);
        return plantRepository.save(plant);
    }

    // Delete a log
    public Plant deleteLog(String plantId, int logIndex, String ownerUsername) {
        Plant plant = getPlantById(plantId);
        if (!plant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only delete your own logs");
        }
        if (logIndex < 0 || logIndex >= plant.getLogs().size()) {
            throw new RuntimeException("Invalid log index");
        }
        plant.getLogs().remove(logIndex);
        return plantRepository.save(plant);
    }
}