package com.nic.nerie.t_participantfeedbacks.repository;

import com.nic.nerie.t_participantfeedbacks.model.T_ParticipantFeedbacks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface T_ParticipantFeedbacksRepository extends JpaRepository<T_ParticipantFeedbacks,String> {
    @Query(value = "SELECT MAX(CAST(pfeedbackno AS integer)) FROM nerie.t_participantfeedbacks", nativeQuery = true)
    Integer findMaxPfeedbackNo();

    @Query(value = """
        SELECT * FROM nerie.t_participantfeedbacks 
        WHERE phaseid = :phaseid 
        AND usercode = :usercode""", nativeQuery = true)
    T_ParticipantFeedbacks getByPhaseIdAndUserCode(@Param("phaseid") String phaseid,
                                                   @Param("usercode") String usercode);

    @Query(value = "SELECT * FROM t_participantfeedbacks WHERE phaseid = :phaseid", nativeQuery = true)
    List<T_ParticipantFeedbacks> findFeedbacksByPhaseId(String phaseid);
}
