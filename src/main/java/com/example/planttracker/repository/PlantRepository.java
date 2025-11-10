package com.example.planttracker.repository;

import com.example.planttracker.model.Plant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PlantRepository extends MongoRepository<Plant, String> {
    List<Plant> findByOwnerUsername(String ownerUsername);
}