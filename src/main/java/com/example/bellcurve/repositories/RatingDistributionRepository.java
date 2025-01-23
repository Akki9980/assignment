package com.example.bellcurve.repositories;

import com.example.bellcurve.entities.RatingDistribution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingDistributionRepository extends JpaRepository<RatingDistribution, String> {
}
