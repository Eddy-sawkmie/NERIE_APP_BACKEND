package com.nic.nerie.t_preposttestquestions.service;

import com.nic.nerie.t_preposttestquestions.model.T_PrePostTestQuestions;
import com.nic.nerie.t_preposttestquestions.repository.T_PrePostTestQuestionsRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class T_PrePostTestQuestionsService {
    private final T_PrePostTestQuestionsRepository tPrePostTestQuestionsRepository;

    @Autowired
    public T_PrePostTestQuestionsService(T_PrePostTestQuestionsRepository tPrePostTestQuestionsRepository) {
        this.tPrePostTestQuestionsRepository = tPrePostTestQuestionsRepository;
    }

    public List<T_PrePostTestQuestions> getAllPrePostTestQuestions() {
        try {
            return tPrePostTestQuestionsRepository.getAllPrePostTestQuestions();
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Failed to fetch all pre post test questions", ex);
        }
    }

    @Transactional
    public boolean saveNewPrePostTestQuestion(@NotNull T_PrePostTestQuestions question) {
        try {
            Integer maxId = tPrePostTestQuestionsRepository.findMaxQuestionId();
            int newId = (maxId == null) ? 1 : maxId + 1;

            question.setQuestionid(String.valueOf(newId));
            tPrePostTestQuestionsRepository.save(question);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException("Error saving Pre/Post Test question", ex);
        }
    }

    public T_PrePostTestQuestions getPrePostTestQuestionById(@NotNull @NotBlank String id) {
        try {
            return tPrePostTestQuestionsRepository.findPrePostTestQuestionById(id);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching pre post test question by id: "+ id, ex);
        }
    }
}
