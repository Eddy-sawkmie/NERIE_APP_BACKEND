package com.nic.nerie.t_preposttestquestions.repository;

import com.nic.nerie.t_preposttestquestions.model.T_PrePostTestQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface T_PrePostTestQuestionsRepository extends JpaRepository<T_PrePostTestQuestions, String> {
    @Query(value = "SELECT * FROM T_PrePostTestQuestions", nativeQuery = true)
    List<T_PrePostTestQuestions> getAllPrePostTestQuestions();

    @Query(value = "SELECT MAX(CAST(questionid AS INTEGER)) FROM T_PrePostTestQuestions", nativeQuery = true)
    Integer findMaxQuestionId();

    @Query(value = "SELECT * FROM T_PrePostTestQuestions WHERE questionid = :qid", nativeQuery = true)
    T_PrePostTestQuestions findPrePostTestQuestionById(@Param("qid") String questionId);
}
