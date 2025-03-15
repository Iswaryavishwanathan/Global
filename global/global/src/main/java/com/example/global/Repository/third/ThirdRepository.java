package com.example.global.Repository.third;

import com.example.global.entity.third.Third;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ThirdRepository extends JpaRepository<Third, LocalDateTime> {

    @Query("SELECT DISTINCT d FROM Third d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
    List<Third> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
