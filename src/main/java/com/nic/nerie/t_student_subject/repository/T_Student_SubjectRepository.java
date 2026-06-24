package com.nic.nerie.t_student_subject.repository;

import com.nic.nerie.t_student_subject.model.T_Student_Subject;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface T_Student_SubjectRepository extends JpaRepository<T_Student_Subject, String> {
    @Modifying
    @Query(value = "UPDATE t_student_subject SET isactive = '0' WHERE usercode = :usercode", nativeQuery = true)
    int falsifyStudentSubject(@Param("usercode") String usercode);

    @Query("FROM T_Student_Subject ss where ss.usercode.usercode = :usercode AND ss.isactive = '1'")
    List<T_Student_Subject> findByUsercode(@Param("usercode") String usercode);
}
