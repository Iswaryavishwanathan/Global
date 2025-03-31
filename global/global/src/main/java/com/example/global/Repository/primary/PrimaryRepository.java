package com.example.global.Repository.primary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.global.DAO.CommonDAO;
import com.example.global.DAO.KWhConsumptionDAO;
import com.example.global.entity.primary.Primary;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrimaryRepository extends JpaRepository<Primary, LocalDateTime> {

@Query("SELECT new com.example.global.DAO.CommonDAO(" +
       "f.dateAndTime, f.millitm, f.tagTable.tagIndex, f.tagTable.tagName, f.val, f.status, f.marker, 'LTPanel1') " +
       "FROM Primary f " +  
       "WHERE f.dateAndTime BETWEEN :startDate AND :endDate " +
       "ORDER BY f.dateAndTime ASC, f.tagTable.tagIndex ASC")

List<CommonDAO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);


@Query("""
       SELECT new com.example.global.DAO.KWhConsumptionDAO(
           'LTPanel1', t.tagName,
           (SELECT MIN(p1.dateAndTime) FROM Primary p1 WHERE p1.tagTable.tagName = t.tagName 
                AND p1.dateAndTime BETWEEN :startOfDay AND :endOfDay),
           (SELECT MAX(p2.dateAndTime) FROM Primary p2 WHERE p2.tagTable.tagName = t.tagName 
                AND p2.dateAndTime BETWEEN :startOfDay AND :endOfDay),
           COALESCE(CAST((SELECT p3.val FROM Primary p3 WHERE p3.tagTable.tagName = t.tagName 
                AND p3.dateAndTime = (SELECT MIN(p4.dateAndTime) FROM Primary p4 
                WHERE p4.tagTable.tagName = t.tagName AND p4.dateAndTime BETWEEN :startOfDay AND :endOfDay)
           ) AS double), 0.0),
           COALESCE(CAST((SELECT p5.val FROM Primary p5 WHERE p5.tagTable.tagName = t.tagName 
                AND p5.dateAndTime = (SELECT MAX(p6.dateAndTime) FROM Primary p6 
                WHERE p6.tagTable.tagName = t.tagName AND p6.dateAndTime BETWEEN :startOfDay AND :endOfDay)
           ) AS double), 0.0)
           
       )
       FROM Primary p
       JOIN p.tagTable t
       WHERE LOWER(t.tagName) LIKE '%kwh%' OR LOWER(t.tagName) LIKE '%wh%'
       GROUP BY t.tagName
    """)
List<KWhConsumptionDAO> findConsumptionData(LocalDateTime startOfDay, LocalDateTime endOfDay);





                                            





}
