package com.nic.nerie.m_shortterm_phases.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.nic.nerie.m_shortterm_phases.model.M_ShortTerm_Phases;

public interface M_ShortTerm_PhasesRepository extends JpaRepository<M_ShortTerm_Phases, String> {
    @Query("FROM M_ShortTerm_Phases ORDER BY sphaseid, sphasename")
    List<M_ShortTerm_Phases> findAllOrderBySphaseidAscSphasenameAsc();

    @Query(value = "SELECT * FROM m_shortterm_phases ORDER BY sphaseid", nativeQuery = true)
    List<Object[]> getMasterPhases();
}
