package com.nic.nerie.t_participantanswerkey_preposttest.repository;

import com.nic.nerie.t_participantanswerkey_preposttest.model.T_ParticipantAnswerKey_PrePostTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface T_ParticipantAnswerKey_PrePostTestRepository extends JpaRepository<T_ParticipantAnswerKey_PrePostTest, String> {
    @Query(value = "SELECT MAX(CAST(paid AS INTEGER)) FROM T_ParticipantAnswerKey_PrePostTest", nativeQuery = true)
    Integer findMaxPaid();

    @Query(value = "SELECT * FROM T_ParticipantAnswerKey_PrePostTest " +
            "WHERE participantusercode = :usercode " +
            "AND testid = :testid " +
            "AND testtype = :testtype", nativeQuery = true)
    List<T_ParticipantAnswerKey_PrePostTest> findAllParticipantPrePostTestAnswers(@Param("usercode") String usercode,
                                                                                  @Param("testid") String testid,
                                                                                  @Param("testtype") String testtype);
}
