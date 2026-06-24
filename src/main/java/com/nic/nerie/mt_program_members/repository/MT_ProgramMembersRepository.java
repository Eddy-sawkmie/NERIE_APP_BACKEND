package com.nic.nerie.mt_program_members.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.nic.nerie.mt_program_members.model.MT_ProgramMembers;

public interface MT_ProgramMembersRepository extends JpaRepository<MT_ProgramMembers, Integer> {
        @Query(value = "SELECT * FROM nerie.mt_program_members " +
                        "WHERE programcode = :programcode AND phaseid = :phaseid " +
                        "ORDER BY program_memberid", nativeQuery = true)
        List<MT_ProgramMembers> getProgramMembers(@RequestParam("programcode") String programcode,
                        @RequestParam("phaseid") String phaseid);

        @Modifying
        @Query(value = "insert into nerie.mt_program_members(programcode, usercode, phaseid, isheadcoordinator, isdelegated) " +
                "values(:pcode, :ucode, :phaseid, :isheadcoordinator, :isdelegated)", nativeQuery = true)
        void createProgramMembersEntry(@Param("pcode") String pcode, @Param("ucode") String ucode, @Param("phaseid") String phaseid, 
                @Param("isheadcoordinator") String isheadcoordinator, @Param("isdelegated") String isdelegated);

        
        @Modifying
        @Query("DELETE FROM MT_ProgramMembers m WHERE m.programcode.programcode = :programcode")
        int deleteByProgramcode(@Param("programcode") String programcode);

        @Query(value = """
                SELECT ul.usercode, ul.username, pm.program_memberid
                FROM nerie.mt_program_members pm
                JOIN nerie.mt_userlogin ul ON pm.usercode = ul.usercode
                WHERE pm.programcode = :pcode AND pm.phaseid = :phaseid
                """, nativeQuery = true)
        List<Object[]> findMembersByProgramAndPhase(@Param("pcode") String pcode, @Param("phaseid") String phaseid);

        @Modifying
        @Transactional
        @Query(value = "UPDATE nerie.mt_program_members SET islocalcoordinator = '1' WHERE program_memberid = :programMemberId", nativeQuery = true)
        int setAsLocalCoordinator(@Param("programMemberId") Integer programMemberId);

        @Query(value = "SELECT * FROM nerie.mt_program_members " +
                "WHERE programcode = :programcode " +
                "AND phaseid = :phaseid " +
                "AND (isheadcoordinator='1' OR isdelegated='1') " +
                "LIMIT 1", nativeQuery = true)
        MT_ProgramMembers findHeadCoordinator(@Param("programcode") String programcode, @Param("phaseid") String phaseid);
}
