package com.nic.nerie.t_internalevaluationmarks.repository;

import com.nic.nerie.t_internalevaluationmarks.model.T_InternalEvaluationMarks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface T_InternalEvaluationMarksRepository extends JpaRepository<T_InternalEvaluationMarks, String> {
    @Query(value = "SELECT COALESCE(MAX(CAST(internalevaluationid AS INTEGER)), 0) FROM T_InternalEvaluationMarks", nativeQuery = true)
    Integer findMaxInternalEvaluationId();

    @Query(value = "SELECT t.testname, t.fullmark, ie.marks, t.passmark " +
            "FROM nerie.t_internalevaluationmarks ie " +
            "JOIN nerie.mt_test t ON ie.testid = t.testid " +
            "WHERE ie.studentid = :studentid AND ie.subjectcode = :subjectcode " +
            "ORDER BY t.testname", nativeQuery = true)
    List<Object[]> findMarksByStudentAndSubject(@Param("studentid") String studentid, @Param("subjectcode") String subjectcode);
}
