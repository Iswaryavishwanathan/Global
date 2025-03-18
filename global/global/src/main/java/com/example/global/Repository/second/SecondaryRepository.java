package com.example.global.Repository.second;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.global.DAO.SecondaryDAO.SecondaryDAO;
import com.example.global.entity.second.Secondary;

import java.time.LocalDateTime;
import java.util.List;

public interface SecondaryRepository extends JpaRepository<Secondary, LocalDateTime> {

    // @Query("SELECT DISTINCT d FROM Secondary d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
    // List<Secondary> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    // @Query("SELECT d FROM Secondary d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC, d.tagIndex ASC")
    @Query("SELECT new com.example.global.DAO.SecondaryDAO.SecondaryDAO(" +
    "f.dateAndTime, f.millitm, f.tagTable.tagIndex, f.tagTable.tagName, f.val, f.status, f.marker, 'LTPanel2') " +
    "FROM Secondary f " +  
    "WHERE f.dateAndTime BETWEEN :startDate AND :endDate " +
    "ORDER BY f.dateAndTime ASC, f.tagTable.tagIndex ASC")

List<SecondaryDAO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

}
