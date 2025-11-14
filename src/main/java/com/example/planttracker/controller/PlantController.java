package com.example.planttracker.controller;

import com.example.planttracker.model.Plant;
import com.example.planttracker.model.PlantLog;
import com.example.planttracker.model.PlantComment;
import com.example.planttracker.service.PlantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "http://localhost:3000")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    // Get all plants for a specific user
    @GetMapping
    public List<Plant> getPlants(@RequestParam String username) {
        return plantService.getPlantsByOwner(username);
    }

    // Get public/community plants (read-only)
    @GetMapping("/public-plants")
    public List<Plant> getPublicPlants() {
        // This returns plants where isPublic == true
        return plantService.getPublicPlants();
    }

    // Get plant by id (owner-only access already enforced in service or can be checked here if desired)
    @GetMapping("/{id}")
    public Plant getPlantById(@PathVariable String id, @RequestParam String username) {
        Plant plant = plantService.getPlantById(id);
        if (!plant.getOwnerUsername().equals(username)) {
            throw new RuntimeException("You can only access your own plants");
        }
        return plant;
    }

    // Add plant
    @PostMapping
    public Plant addPlant(@RequestBody Plant plant) {
        return plantService.addPlant(plant);
    }

    // Update plant
    @PutMapping("/{id}")
    public Plant updatePlant(@PathVariable String id, @RequestBody Plant plant, @RequestParam String username) {
        return plantService.updatePlant(id, plant, username);
    }

    // Delete plant
    @DeleteMapping("/{id}")
    public void deletePlant(@PathVariable String id, @RequestParam String username) {
        plantService.deletePlant(id, username);
    }

    // Add a log
    @PostMapping("/{id}/logs")
    public Plant addLog(@PathVariable String id, @RequestBody PlantLog log, @RequestParam String username) {
        return plantService.addLogToPlant(id, log, username);
    }

    // Add comment to log
    @PostMapping("/{id}/logs/{logIndex}/comments")
    public Plant addComment(@PathVariable String id, @PathVariable int logIndex, @RequestBody PlantComment comment, @RequestParam String username) {
        return plantService.addCommentToLog(id, logIndex, comment, username);
    }

    // Delete log
    @DeleteMapping("/{id}/logs/{logIndex}")
    public Plant deleteLog(@PathVariable String id, @PathVariable int logIndex, @RequestParam String username) {
        return plantService.deleteLog(id, logIndex, username);
    }
}