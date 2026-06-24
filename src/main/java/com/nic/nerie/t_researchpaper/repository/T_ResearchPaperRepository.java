package com.nic.nerie.t_researchpaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nic.nerie.t_researchpaper.model.T_ResearchPaper;

@Repository
public interface T_ResearchPaperRepository extends JpaRepository<T_ResearchPaper,String>{

    @Query("SELECT MAX(CAST(r.researchpaperid AS integer)) FROM T_ResearchPaper r")
    Integer findMaxResearchPaperId();

}
