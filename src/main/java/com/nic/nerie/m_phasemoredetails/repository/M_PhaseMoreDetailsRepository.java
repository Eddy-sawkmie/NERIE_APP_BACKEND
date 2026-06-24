package com.nic.nerie.m_phasemoredetails.repository;

import com.nic.nerie.m_phasemoredetails.model.M_PhaseMoreDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface M_PhaseMoreDetailsRepository extends JpaRepository<M_PhaseMoreDetails, String> {
    @Query(value = "SELECT * FROM m_phasemoredetails WHERE phaseid = :phaseid", nativeQuery = true)
    M_PhaseMoreDetails getPhaseMoreDetailsByPhaseId(String phaseid);

    @Query(value = "SELECT MAX(CAST(phasedetailsid AS INT)) FROM nerie.m_phasemoredetails", nativeQuery = true)
    Integer findMaxPhasedetailsId();
}
