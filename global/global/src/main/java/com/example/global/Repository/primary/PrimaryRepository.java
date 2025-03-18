package com.example.global.Repository.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.global.DAO.PrimaryDAO.PrimaryDAO;
import com.example.global.entity.primary.Primary;
import java.time.LocalDateTime;
import java.util.List;

public interface PrimaryRepository extends JpaRepository<Primary, LocalDateTime> {
//     @Query("SELECT DISTINCT d FROM Primary d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
// List<Primary> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
// @Query("SELECT d FROM Primary d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC, d.tagIndex ASC")
@Query("SELECT new com.example.global.DAO.PrimaryDAO.PrimaryDAO(" +
       "f.dateAndTime, f.millitm, f.tagTable.tagIndex, f.tagTable.tagName, f.val, f.status, f.marker, 'LTPanel1') " +
       "FROM Primary f " +  
       "WHERE f.dateAndTime BETWEEN :startDate AND :endDate " +
       "ORDER BY f.dateAndTime ASC, f.tagTable.tagIndex ASC")

List<PrimaryDAO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

}
