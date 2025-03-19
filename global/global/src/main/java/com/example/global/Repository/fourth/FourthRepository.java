package com.example.global.Repository.fourth;


import com.example.global.DAO.CommonDAO;
import com.example.global.entity.fourth.Fourth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;

public interface FourthRepository extends JpaRepository<Fourth, LocalDateTime> {

    // @Query("SELECT DISTINCT d FROM Fourth d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC")
    // List<Fourth> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    //  @Query("SELECT d FROM Fourth d WHERE d.dateAndTime BETWEEN :startDate AND :endDate ORDER BY d.dateAndTime ASC, d.tagIndex ASC")
    @Query("SELECT new com.example.global.DAO.CommonDAO(" +
    "f.dateAndTime, f.millitm, f.tagTable.tagIndex, f.tagTable.tagName, f.val, f.status, f.marker, 'SevenVCB') " +
    "FROM Fourth f " +  
    "WHERE f.dateAndTime BETWEEN :startDate AND :endDate " +
    "ORDER BY f.dateAndTime ASC, f.tagTable.tagIndex ASC")
   
List<CommonDAO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

}
