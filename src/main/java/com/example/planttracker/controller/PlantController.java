package com.example.planttracker.controller;

import com.example.planttracker.model.Plant;
import com.example.planttracker.model.PlantLog;
import com.example.planttracker.model.PlantComment;
import com.example.planttracker.service.PlantService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

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

    // Get public/community plants
    @GetMapping("/public-plants")
    public List<Plant> getPublicPlants() {
        return plantService.getPublicPlants();
    }

    // ⭐ FIXED: allow public plants OR owner access
    @GetMapping("/{id}")
    public Plant getPlantById(
            @PathVariable String id,
            @RequestParam(required = false) String username
    ) {
        Plant plant = plantService.getPlantById(id);

        // Normalize invalid username values
        boolean noUser = (
                username == null ||
                        username.equals("undefined") ||
                        username.equals("null") ||
                        username.isBlank()
        );

        boolean isOwner = (!noUser && username.equals(plant.getOwnerUsername()));

        // Owner can access their plant
        if (isOwner) {
            return plant;
        }

        // Public plant → accessible by anybody
        if (plant.isPublic()) {
            return plant;
        }

        // Otherwise private → block
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "This plant is private."
        );
    }

    // Add plant
    @PostMapping
    public Plant addPlant(@RequestBody Plant plant) {
        return plantService.addPlant(plant);
    }

    // Update plant (only owner)
    @PutMapping("/{id}")
    public Plant updatePlant(
            @PathVariable String id,
            @RequestBody Plant plant,
            @RequestParam String username
    ) {
        return plantService.updatePlant(id, plant, username);
    }

    // Delete plant (only owner)
    @DeleteMapping("/{id}")
    public void deletePlant(@PathVariable String id, @RequestParam String username) {
        plantService.deletePlant(id, username);
    }

    // Add a log
    @PostMapping("/{id}/logs")
    public Plant addLog(
            @PathVariable String id,
            @RequestBody PlantLog log,
            @RequestParam String username
    ) {
        return plantService.addLogToPlant(id, log, username);
    }

    // Add comment to log
    @PostMapping("/{id}/logs/{logIndex}/comments")
    public Plant addComment(
            @PathVariable String id,
            @PathVariable int logIndex,
            @RequestBody PlantComment comment,
            @RequestParam String username
    ) {
        return plantService.addCommentToLog(id, logIndex, comment, username);
    }

    // Delete a log
    @DeleteMapping("/{id}/logs/{logIndex}")
    public Plant deleteLog(
            @PathVariable String id,
            @PathVariable int logIndex,
            @RequestParam String username
    ) {
        return plantService.deleteLog(id, logIndex, username);
    }
}