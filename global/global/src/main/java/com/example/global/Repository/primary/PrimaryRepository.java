package com.example.global.Repository.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.global.entity.primary.Primary;
import java.time.LocalDateTime;
import java.util.List;

public interface PrimaryRepository extends JpaRepository<Primary, LocalDateTime> {
    @Query("SELECT DISTINCT d FROM Primary d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
List<Primary> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

}
