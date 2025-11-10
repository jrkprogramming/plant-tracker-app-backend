package com.example.planttracker.controller;

import com.example.planttracker.model.Plant;
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

    // Add a plant
    @PostMapping
    public Plant addPlant(@RequestBody Plant plant) {
        return plantService.addPlant(plant);
    }

    // Update a plant
    @PutMapping("/{id}")
    public Plant updatePlant(@PathVariable String id,
                             @RequestBody Plant plant,
                             @RequestParam String username) {
        return plantService.updatePlant(id, plant, username);
    }

    // Delete a plant
    @DeleteMapping("/{id}")
    public void deletePlant(@PathVariable String id, @RequestParam String username) {
        plantService.deletePlant(id, username);
    }
}