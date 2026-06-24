package com.nic.nerie.t_researchpaper.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.nic.nerie.t_researchpaper.model.T_ResearchPaper;
import com.nic.nerie.t_researchpaper.repository.T_ResearchPaperRepository;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Service
@Validated
public class T_ResearchPaperService {

    private final T_ResearchPaperRepository tResearchPaperRepository;

    public T_ResearchPaperService(T_ResearchPaperRepository tResearchPaperRepository) {
        this.tResearchPaperRepository = tResearchPaperRepository;
    }

    @Transactional
    public boolean saveResearchPaper(T_ResearchPaper paper) {
        try {
            if (paper.getResearchpaperid() == null) {
                Integer maxId = tResearchPaperRepository.findMaxResearchPaperId();
                int newId = (maxId == null) ? 1 : maxId + 1;
                paper.setResearchpaperid(String.valueOf(newId));
            }
            tResearchPaperRepository.save(paper);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteResearchPaper(@NotNull @NotBlank String researchpaperid) {
        try {
            if (!tResearchPaperRepository.existsById(researchpaperid)) {
                return false;
            }
            tResearchPaperRepository.deleteById(researchpaperid);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean updateResearchPaper(
            @NotNull @NotBlank String researchpaperid,
            String title,
            String journal,
            Integer year,
            String category,
            String publisher,
            String authors,
            String link) {

        try {
            Optional<T_ResearchPaper> optional = tResearchPaperRepository.findById(researchpaperid);
            if (optional.isEmpty()) {
                return false;
            }
            T_ResearchPaper paper = optional.get();
            paper.setTitle(title);
            paper.setJournal(journal);
            paper.setYear(year);
            paper.setCategory(category);
            paper.setPublisher(publisher);
            paper.setAuthors(authors);
            paper.setLink(link);
            tResearchPaperRepository.save(paper);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
