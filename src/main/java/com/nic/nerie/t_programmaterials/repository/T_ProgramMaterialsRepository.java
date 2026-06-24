package com.nic.nerie.t_programmaterials.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nic.nerie.t_programmaterials.model.T_ProgramMaterials;

public interface T_ProgramMaterialsRepository extends JpaRepository<T_ProgramMaterials, String> {
    @Query(nativeQuery = true, value = "SELECT programmaterialid, materialdesc, uploaddate " +
            "FROM nerie.t_programmaterials WHERE phaseid = :phaseid")
    List<Object[]> findMaterialsByPhaseId(@Param("phaseid") String phaseid);

    @Query("SELECT MAX(CAST(pm.programmaterialid as int)) FROM T_ProgramMaterials pm")
    Integer findLastUsedProgrammaterialid();

    @Query(value = "SELECT * FROM nerie.t_programmaterials WHERE programmaterialid = :pmid", nativeQuery = true)
    T_ProgramMaterials findCourseMaterial(@Param("pmid") String pmid);

}
