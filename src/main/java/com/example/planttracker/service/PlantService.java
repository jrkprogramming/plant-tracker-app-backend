package com.example.planttracker.service;

import com.example.planttracker.model.Plant;
import com.example.planttracker.repository.PlantRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlantService {

    private final PlantRepository plantRepository;

    public PlantService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    // 🌱 Get plants only for this user
    public List<Plant> getPlantsByOwner(String ownerUsername) {
        List<Plant> plants = plantRepository.findByOwnerUsername(ownerUsername);
        // persist updated flag
        plants.forEach(plantRepository::save);
        return plants;
    }

    // 🌿 Add a plant for a specific user
    public Plant addPlant(Plant plant) {
        if (plant.getOwnerUsername() == null || plant.getOwnerUsername().isEmpty()) {
            throw new RuntimeException("Plant must have an ownerUsername");
        }


        return plantRepository.save(plant);
    }

    // 🌻 Update a plant (make sure the user owns it)
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
        existingPlant.setSoilType(updatedPlant.getSoilType());
        existingPlant.setFertilizer(updatedPlant.getFertilizer());
        existingPlant.setSunExposure(updatedPlant.getSunExposure());
        existingPlant.setIdealTemperature(updatedPlant.getIdealTemperature());
        existingPlant.setNotes(updatedPlant.getNotes());

        return plantRepository.save(existingPlant);
    }

    // 🌾 Delete a plant (only if owner)
    public void deletePlant(String id, String ownerUsername) {
        Plant existingPlant = plantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plant not found"));

        if (!existingPlant.getOwnerUsername().equals(ownerUsername)) {
            throw new RuntimeException("You can only delete your own plants");
        }

        plantRepository.delete(existingPlant);
    }
}