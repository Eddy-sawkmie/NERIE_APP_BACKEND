package com.nic.nerie.t_feedbacksday.repository;

import com.nic.nerie.t_feedbacksday.model.T_Feedbacksday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface T_FeedbacksdayRepository extends JpaRepository<T_Feedbacksday, String> {

    // Query to check if feedback already exists for a user and timetable entry
    Optional<T_Feedbacksday> findByProgramtimetablecode_ProgramtimetablecodeAndUsercode_Usercode(String timetableCode, String userCode);

    // Query to get the maximum ID as an integer for manual generation
    @Query("SELECT MAX(CAST(tfd.feedbackdayid as integer)) FROM T_Feedbacksday tfd")
    Optional<Integer> findMaxFeedbackdayIdAsInteger();

    @Query(value = "SELECT * FROM t_feedbacksday WHERE programtimetablecode = :programtimetablecode", nativeQuery = true)
    List<T_Feedbacksday> getDayFeedbacksByProgramTimeTableCode(String programtimetablecode);

}