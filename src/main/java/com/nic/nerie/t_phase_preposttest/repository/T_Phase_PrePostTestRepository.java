package com.nic.nerie.t_phase_preposttest.repository;

import com.nic.nerie.t_phase_preposttest.model.T_Phase_PrePostTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface T_Phase_PrePostTestRepository extends JpaRepository<T_Phase_PrePostTest, String> {
    @Query(value = "SELECT MAX(CAST(testid AS INTEGER)) FROM T_Phase_PrePostTest", nativeQuery = true)
    Integer findMaxTestId();

    @Query(value = "SELECT COUNT(*) FROM T_Phase_PrePostTest WHERE phaseid = :phaseId", nativeQuery = true)
    Integer countByPhaseId(@Param("phaseId") String phaseId);

    @Query(value = "SELECT * FROM T_Phase_PrePostTest WHERE phaseid = :phid", nativeQuery = true)
    T_Phase_PrePostTest findPhasePrePostTestByPhaseId(@Param("phid") String phid);

    @Query(value = "SELECT * FROM T_Phase_PrePostTest WHERE testid = :testid", nativeQuery = true)
    T_Phase_PrePostTest findPhasePrePostTestById(@Param("testid") String testid);

}
