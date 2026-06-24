package com.nic.nerie.t_feedbacks.repository;

import com.nic.nerie.t_feedbacks.model.T_Feedbacks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface T_FeedbacksRepository extends JpaRepository<T_Feedbacks, String> { // Assuming feedbackslno is String ID

    @Query("SELECT MAX(CAST(tf.feedbackslno as integer)) FROM T_Feedbacks tf")
    Optional<Integer> findMaxFeedbackSlnoAsInteger();

    @Query(nativeQuery = true, value = "SELECT feedbackslno, usercode, feedback, entrydate, phaseid " +
            "FROM nerie.t_feedbacks WHERE phaseid = :phaseid AND usercode = :usercode")
    List<Object[]> findParticipantFeedbackAsObjectArray(@Param("phaseid") String phaseid, @Param("usercode") String usercode);

    @Query(nativeQuery = true, value = """
            SELECT DISTINCT pt.subject, pa.programtimetablecode,
                CASE WHEN EXISTS (
                    SELECT 1 FROM nerie.t_feedbacksday fd
                    WHERE fd.programtimetablecode = pa.programtimetablecode
                    AND fd.usercode = pa.pusercode
                ) THEN 'Y' ELSE 'N' END as feedback_given,
                (SELECT fd.feedback FROM nerie.t_feedbacksday fd
                 WHERE fd.programtimetablecode = pa.programtimetablecode AND fd.usercode = pa.pusercode LIMIT 1) as existing_feedback
            FROM nerie.t_participantattendance pa
            JOIN nerie.t_programtimetable pt ON pa.programtimetablecode = pt.programtimetablecode
            WHERE date(pa.entrydate) = date(:entrydate)
            AND pa.pusercode = :pusercode
            AND pa.phaseid = :phaseid
            AND pt.subject IS NOT NULL AND pt.subject != 'BREAK'
            ORDER BY pt.subject
            """)
    List<Object[]> findSubjectsAttendedOnDate(@Param("entrydate") Date entrydate,
                                              @Param("phaseid") String phaseId,
                                              @Param("pusercode") String pusercode);
}