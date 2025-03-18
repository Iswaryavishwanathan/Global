package com.example.global.Repository.third;


import com.example.global.DAO.ThirdDAO.ThirdDAO;
import com.example.global.entity.third.Third;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface ThirdRepository extends JpaRepository<Third, LocalDateTime> {

    // @Query("SELECT DISTINCT d FROM Third d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
    // List<Third> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    // @Query("SELECT d FROM Third d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC, d.tagIndex ASC")
    @Query("SELECT new com.example.global.DAO.ThirdDAO.ThirdDAO(" +
    "f.dateAndTime, f.millitm, f.tagTable.tagIndex, f.tagTable.tagName, f.val, f.status, f.marker, 'LTPanel3') " +
    "FROM Third f " +  
    "WHERE f.dateAndTime BETWEEN :startDate AND :endDate " +
    "ORDER BY f.dateAndTime ASC, f.tagTable.tagIndex ASC")
List<ThirdDAO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
