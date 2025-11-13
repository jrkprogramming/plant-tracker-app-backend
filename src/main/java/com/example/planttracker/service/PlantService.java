package com.example.planttracker.service;

import com.example.planttracker.model.Plant;
import com.example.planttracker.model.PlantLog;
import com.example.planttracker.model.PlantComment;
import com.example.planttracker.repository.PlantRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PlantService {

    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    // 🌱 Get plants only for a specific user
    public List<Plant> getPlantsByOwner(String ownerUsername) {
        return plantRepository.findByOwnerUsername(ownerUsername);
    }

    // 🌿 Add a new plant
    public Plant addPlant(Plant plant) {
        if (plant.getOwnerUsername() == null || plant.getOwnerUsername().isEmpty()) {
            throw new RuntimeException("Plant must have an ownerUsername");
        }
        return plantRepository.save(plant);
    }

    // 🌻 Update an existing plant (only owner can update)
    public Plant updatePlant(String id, Plant updatedPlant, String ownerUsername) {
        Plant existingPlant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        if (!existingPlant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only update your own plants");
        }

        existingPlant.setName(updatedPlant.getName());
        existingPlant.setSpecies(updatedPlant.getSpecies());
        existingPlant.setLastWateredDate(updatedPlant.getLastWateredDate());
        existingPlant.setWateringFrequencyDays(updatedPlant.getWateringFrequencyDays());

        // Logs and comments are managed separately
        return plantRepository.save(existingPlant);
    }

    // 🌾 Delete a plant (only owner)
    public void deletePlant(String id, String ownerUsername) {
        Plant existingPlant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        if (!existingPlant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only delete your own plants");
        }

        plantRepository.delete(existingPlant);
    }

    // 📸 Add a log (photo + note) to a plant
    public Plant addLogToPlant(String plantId, PlantLog newLog, String ownerUsername) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        if (!plant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only add logs to your own plants");
        }

        newLog.setTimestamp(Instant.now());
        plant.getLogs().add(newLog);
        return plantRepository.save(plant);
    }

    // 💬 Add a comment to a specific plant log
    public Plant addCommentToLog(String plantId, int logIndex, PlantComment newComment, String ownerUsername) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        if (!plant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only comment on your own plants' logs");
        }

        if (logIndex < 0 || logIndex >= plant.getLogs().size()) {
            throw new RuntimeException("Invalid log index");
        }

        newComment.setTimestamp(Instant.now());
        plant.getLogs().get(logIndex).getComments().add(newComment);
        return plantRepository.save(plant);
    }

    // 🗑️ Delete a log from a plant
    public Plant deleteLog(String plantId, int logIndex, String ownerUsername) {
        Plant plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

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