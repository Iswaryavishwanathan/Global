package com.example.global.Repository.fourth;


import com.example.global.DAO.CommonDAO;
import com.example.global.DAO.KWhConsumptionDAO;
import com.example.global.entity.fourth.Fourth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface FourthRepository extends JpaRepository<Fourth, LocalDateTime> {

//    @Query("SELECT new com.example.global.DAO.CommonDAO(" +
//        "f.dateAndTime, f.millitm, f.tagTable.tagIndex, f.tagTable.tagName, f.val, f.status, f.marker, 'VLL') " +
//        "FROM Fourth f " +  
//        "WHERE f.dateAndTime BETWEEN :startDate AND :endDate " +
//        "ORDER BY f.dateAndTime ASC, f.tagTable.tagIndex ASC")

// List<CommonDAO> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);


@Query(value = """
    SELECT
        'MFM_KWH' AS databaseName,  
        t.TagName,
        MIN(f.DateAndTime) AS startDateTime,
        MAX(f.DateAndTime) AS endDateTime,
        (SELECT TOP 1 ff.Val FROM FloatTable ff 
            WHERE ff.TagIndex = f.TagIndex AND ff.DateAndTime = MIN(f.DateAndTime)) AS startVal,
        (SELECT TOP 1 ff.Val FROM FloatTable ff 
            WHERE ff.TagIndex = f.TagIndex AND ff.DateAndTime = MAX(f.DateAndTime)) AS endVal
    FROM FloatTable f
    JOIN TagTable t ON f.TagIndex = t.TagIndex
    WHERE f.DateAndTime BETWEEN :start AND :end
      AND (LOWER(t.TagName) LIKE '%kwh%' OR LOWER(t.TagName) LIKE '%wh%')
    GROUP BY t.TagName, f.TagIndex
""", nativeQuery = true)
 List<KWhConsumptionDAO> findConsumptionData(LocalDateTime start, LocalDateTime end);

@Query("""
    SELECT new com.example.global.DAO.CommonDAO(
        f.dateAndTime, f.tagTable.tagName, f.val)
    FROM Fourth f
    WHERE f.dateAndTime BETWEEN :start AND :end
""")
List<CommonDAO> findReadings(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
