package com.example.planttracker.repository;

import com.example.planttracker.model.Plant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PlantRepository extends MongoRepository<Plant, String> {

    // Get plants owned by a specific user
    List<Plant> findByOwnerUsername(String ownerUsername);

    // NEW — Get all publicly visible plants
    List<Plant> findByIsPublicTrue();
}