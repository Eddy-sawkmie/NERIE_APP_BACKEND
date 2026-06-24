package com.nic.nerie.t_participants.repository;

import com.nic.nerie.t_participants.dto.ParticipantRecordDTO;
import com.nic.nerie.t_participants.model.T_Participants;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface T_ParticipantsRepository extends JpaRepository<T_Participants, String> {
        @Query("SELECT p FROM T_Participants p WHERE p.usercode = :usercode")
        Optional<T_Participants> findByUsercode(@Param("usercode") String usercode);

        @Query(value = """
                        SELECT u.usercode AS c0, u.username AS c1, u.userid AS c2, u.usermobile AS c3,
                               a.applicationcode AS c4, a.status AS c5, s.statecode AS c6, s.statename AS c7,
                               a.phaseid AS c8
                        FROM nerie.mt_userlogin u
                        JOIN nerie.t_participants p ON u.usercode = p.usercode
                        JOIN nerie.m_states s ON p.participantofficestatecode = s.statecode
                        JOIN nerie.t_applications a ON u.usercode = a.usercode
                        WHERE a.phaseid = :phaseid
                        """, nativeQuery = true)
        List<Object[]> getProgramParticipants(@Param("phaseid") String phaseid);

        @Modifying
        @Query(value = """
                        INSERT INTO nerie.t_participants
                        (usercode, participantofficestatecode, usercodewhoregistered)
                        VALUES (:usercode, :statecode, :registeredBy)
                        """, nativeQuery = true)
        void insertParticipant(@Param("usercode") String usercode, @Param("statecode") String statecode,
                        @Param("registeredBy") String registeredBy);

        @Modifying
        @Query(value = """
                        UPDATE nerie.t_participants
                        SET participantofficestatecode = :statecode,
                            usercodewhoregistered = :registeredBy
                        WHERE usercode = :usercode
                        """, nativeQuery = true)
        void updateParticipant(@Param("usercode") String usercode, @Param("statecode") String statecode,
                        @Param("registeredBy") String registeredBy);

        @Query(value = """
                        SELECT u.usercode, u.username, u.userid, u.usermobile, p.participantofficestatecode
                        FROM nerie.mt_userlogin u
                        JOIN nerie.t_participants p ON u.usercode = p.usercode
                        WHERE UPPER(u.userid) = UPPER(:userid)
                        """, nativeQuery = true)
        List<Object[]> findParticipantDetailsByUserid(@Param("userid") String userid);

        //--------------------------------------------------------------------------------------------------------
        
        //⚠️ Native queries cannot automatically map to a Java record DTO unless you use one of these:
        // Interface-based projection (recommended for native queries) ✅
        // @SqlResultSetMapping (rarely used)
        // Manual mapping

        @Query(value = """
            SELECT 
                p.usercode AS usercode,
                u.username AS username,
                CASE 
                    WHEN p.gender = 'M' THEN 'Male'
                    WHEN p.gender = 'F' THEN 'Female'
                    ELSE 'Others'
                END AS gender,
                c.categoryname AS category,
                s.statename AS state
            FROM t_participants p
            LEFT JOIN mt_userlogin u ON u.usercode = p.usercode
            LEFT JOIN m_categories c ON c.categorycode = p.categorycode
            LEFT JOIN m_states s ON s.statecode = p.statecode
            """,
            countQuery = """
            SELECT COUNT(*) FROM t_participants
            """,
            nativeQuery = true)
        Page<ParticipantRecordDTO> getParticipants(Pageable pageable);
        
        @Query(value = """
            SELECT 
                p.usercode AS usercode,
                u.username AS username,
                CASE 
                    WHEN p.gender = 'M' THEN 'Male'
                    WHEN p.gender = 'F' THEN 'Female'
                    ELSE 'Others'
                END AS gender,
                c.categoryname AS category,
                s.statename AS state
            FROM t_participants p
            LEFT JOIN mt_userlogin u ON u.usercode = p.usercode
            LEFT JOIN m_categories c ON c.categorycode = p.categorycode
            LEFT JOIN m_states s ON s.statecode = p.statecode
            WHERE 
                LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.usercode) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.categoryname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(s.statename) LIKE LOWER(CONCAT('%', :search, '%'))
            """,
            countQuery = """
            SELECT COUNT(*)
            FROM t_participants p
            LEFT JOIN mt_userlogin u ON u.usercode = p.usercode
            LEFT JOIN m_categories c ON c.categorycode = p.categorycode
            LEFT JOIN m_states s ON s.statecode = p.statecode
            WHERE 
                LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(p.usercode) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(c.categoryname) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(s.statename) LIKE LOWER(CONCAT('%', :search, '%'))
            """,
            nativeQuery = true)
    Page<ParticipantRecordDTO> searchParticipants(String search, Pageable pageable);
        //--------------------------------------------------------------------------------------------------------
}