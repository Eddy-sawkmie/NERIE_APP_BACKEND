package com.nic.nerie.t_assignmenttest.repository;

import com.nic.nerie.t_assignmenttest.model.T_Assignmenttest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface T_AssignmenttestRepository extends JpaRepository<T_Assignmenttest, String> {
    @Query(value = "SELECT * FROM t_assignmenttest a WHERE a.usercode = :usercode", nativeQuery = true)
    List<T_Assignmenttest> getAssignmentListByUsercode(String usercode);

    @Query(value = "SELECT * FROM t_assignmenttest WHERE assignmenttestid = :fid", nativeQuery = true)
    Optional<T_Assignmenttest> getAssignmentDetailsById(@Param("fid") String fid);

    @Query(value = "SELECT MAX(CAST(assignmenttestid AS INTEGER)) FROM t_assignmenttest",
            nativeQuery = true)
    Integer findMaxAssignmentTestId();
}
