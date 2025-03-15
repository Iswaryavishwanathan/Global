package com.example.global.Repository.second;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.global.entity.second.Secondary;

import java.time.LocalDateTime;
import java.util.List;

public interface SecondaryRepository extends JpaRepository<Secondary, LocalDateTime> {

    @Query("SELECT DISTINCT d FROM Secondary d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
    List<Secondary> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
