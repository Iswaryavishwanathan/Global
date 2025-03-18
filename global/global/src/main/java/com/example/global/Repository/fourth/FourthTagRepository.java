package com.example.global.Repository.fourth;

import com.example.global.entity.TagTable;
import com.example.global.entity.fourth.Fourth;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FourthTagRepository extends JpaRepository<TagTable, Integer> {
   @Query("SELECT f FROM Fourth f JOIN f.tagTable t WHERE t.tagIndex = :tagIndex")
List<Fourth> findByTagIndex(@Param("tagIndex") Integer tagIndex);

}
