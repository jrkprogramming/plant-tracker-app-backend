package com.example.planttracker.controller;

import com.example.planttracker.model.Plant;
import com.example.planttracker.model.PlantLog;
import com.example.planttracker.model.PlantComment;
import com.example.planttracker.service.PlantService;
import com.example.planttracker.service.S3Service;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "http://localhost:3000")
public class PlantController {

    private final PlantService plantService;
    private final S3Service s3Service;
    private final String bucket;

    public PlantController(PlantService plantService, S3Service s3Service, @Value("${AWS_BUCKET}") String bucket) {
        this.plantService = plantService;
        this.s3Service = s3Service;
        this.bucket = bucket;
    }

    // ------------------------------------------
    // 🌱 PLANT CRUD
    // ------------------------------------------

    @GetMapping
    public List<Plant> getPlants(@RequestParam String username) {
        return plantService.getPlantsByOwner(username);
    }

    @GetMapping("/public-plants")
    public List<Plant> getPublicPlants() {
        return plantService.getPublicPlants();
    }

    @GetMapping("/{id}")
    public Plant getPlantById(
            @PathVariable String id,
            @RequestParam(required = false) String username
    ) {
        Plant plant = plantService.getPlantById(id);

        boolean noUser = username == null ||
                username.isBlank() ||
                username.equals("null") ||
                username.equals("undefined");

        boolean isOwner = !noUser && username.equals(plant.getOwnerUsername());

        if (isOwner || plant.isPublic()) {
            return plant;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "This plant is private."
        );
    }

    @PostMapping
    public Plant addPlant(@RequestBody Plant plant) {
        return plantService.addPlant(plant);
    }

    @PutMapping("/{id}")
    public Plant updatePlant(
            @PathVariable String id,
            @RequestBody Plant plant,
            @RequestParam String username
    ) {
        return plantService.updatePlant(id, plant, username);
    }

    @DeleteMapping("/{id}")
    public void deletePlant(
            @PathVariable String id,
            @RequestParam String username
    ) {
        plantService.deletePlant(id, username);
    }

    // ------------------------------------------
    // 📸 LOGS + COMMENTS
    // ------------------------------------------

    @PostMapping("/{id}/logs")
    public Plant addLog(
            @PathVariable String id,
            @RequestBody PlantLog log,
            @RequestParam String username
    ) {
        return plantService.addLogToPlant(id, log, username);
    }

    @PostMapping("/{id}/logs/{logIndex}/comments")
    public Plant addComment(
            @PathVariable String id,
            @PathVariable int logIndex,
            @RequestBody PlantComment comment,
            @RequestParam String username
    ) {
        return plantService.addCommentToLog(id, logIndex, comment, username);
    }

    @DeleteMapping("/{id}/logs/{logIndex}")
    public Plant deleteLog(
            @PathVariable String id,
            @PathVariable int logIndex,
            @RequestParam String username
    ) {
        return plantService.deleteLog(id, logIndex, username);
    }

    // ------------------------------------------
    // 🖼️ S3 UPLOAD ENDPOINTS
    // ------------------------------------------

    // Upload a main plant image
    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadPlantImage(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (this.bucket == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("AWS_BUCKET environment variable is missing.");
            }

            String key = "plants/" + id + "/" + System.currentTimeMillis() + "-" + file.getOriginalFilename();

            String url = s3Service.uploadFile(this.bucket, key, file);
            return ResponseEntity.ok(url);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Upload failed: " + e.getMessage());
        }
    }

    // Upload a log photo
    @PostMapping("/{id}/logs/upload")
    public ResponseEntity<?> uploadLogPhoto(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file,
            @RequestParam String username
    ) {
        try {
            Plant plant = plantService.getPlantById(id);

            if (!plant.getOwnerUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You can only upload photos for your own plants.");
            }

            if (this.bucket == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("AWS_BUCKET environment variable is missing.");
            }

            String key = "plants/" + id + "/logs/" +
                    System.currentTimeMillis() + "-" + file.getOriginalFilename();

            String url = s3Service.uploadFile(this.bucket, key, file);
            return ResponseEntity.ok(url);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
