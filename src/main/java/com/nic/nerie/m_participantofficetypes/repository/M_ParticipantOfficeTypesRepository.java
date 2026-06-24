package com.nic.nerie.m_participantofficetypes.repository;

import com.nic.nerie.m_participantofficetypes.model.M_ParticipantOfficeTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface M_ParticipantOfficeTypesRepository extends JpaRepository<M_ParticipantOfficeTypes, String> {
    @Query(value = "SELECT * FROM M_ParticipantOfficeTypes ORDER BY participantofficetypecode", nativeQuery = true)
    List<M_ParticipantOfficeTypes> findAllOrderedByCode();
}
