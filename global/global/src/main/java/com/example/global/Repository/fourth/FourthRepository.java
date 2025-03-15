package com.example.global.Repository.fourth;

import com.example.global.entity.fourth.Fourth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface FourthRepository extends JpaRepository<Fourth, LocalDateTime> {

    @Query("SELECT DISTINCT d FROM Fourth d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
    List<Fourth> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
