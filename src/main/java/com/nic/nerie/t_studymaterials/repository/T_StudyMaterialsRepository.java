package com.nic.nerie.t_studymaterials.repository;

import com.nic.nerie.t_studymaterials.model.T_StudyMaterials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface T_StudyMaterialsRepository extends JpaRepository<T_StudyMaterials, String> {
    @Query("SELECT t FROM T_StudyMaterials t WHERE (t.subjectcode.subjectcode = :subjectcode OR :subjectcode = 'All') " +
            "ORDER BY t.uploaddate DESC")
    List<T_StudyMaterials> findStudyMaterialsBySubjectcode(@Param("subjectcode") String subjectcode);

    @Query("SELECT t FROM T_StudyMaterials t WHERE t.facultyid.facultyid = :facultyid ORDER BY t.uploaddate DESC")
    List<T_StudyMaterials> findByFacultyIdOrderByUploadDateDesc(@Param("facultyid") String facultyid);

    @Query(value = "SELECT * FROM T_StudyMaterials WHERE studymaterialid = :sid", nativeQuery = true)
    T_StudyMaterials findByStudyMaterialId(@Param("sid") String sid);

    @Query(value = "SELECT MAX(CAST(studymaterialid AS integer)) FROM nerie.t_studymaterials", nativeQuery = true)
    Integer getMaxStudyMaterialId();

    @Query(value = "SELECT title, studymaterialid, uploaddate, subjectcode FROM nerie.t_studymaterials " +
            "WHERE (subjectcode = :subjectcode OR :subjectcode = 'All') " +
            "AND facultyid = :facultyid " +
            "ORDER BY uploaddate DESC", nativeQuery = true)
    List<Object[]> findStudyMaterialsBySubjectAndFaculty(@Param("subjectcode") String subjectcode,
                                                         @Param("facultyid") String facultyid);
}
