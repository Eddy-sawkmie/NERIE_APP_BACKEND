package com.nic.nerie.t_feedbacksday.service;

import com.nic.nerie.t_feedbacksday.model.T_Feedbacksday;
import com.nic.nerie.t_feedbacksday.repository.T_FeedbacksdayRepository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class T_FeedbacksdayService {

    private final T_FeedbacksdayRepository feedbackdayRepository;

    @Autowired
    public T_FeedbacksdayService(T_FeedbacksdayRepository feedbackdayRepository) {
        this.feedbackdayRepository = feedbackdayRepository;
    }

    @Transactional
    public T_Feedbacksday saveOrUpdate(T_Feedbacksday fbd) {
        if (fbd == null) {
            return null;
        }
        try {
            if (!StringUtils.hasText(fbd.getFeedbackdayid())) {
                Optional<Integer> maxIdOpt = feedbackdayRepository.findMaxFeedbackdayIdAsInteger();
                int nextId = maxIdOpt.map(maxId -> maxId + 1).orElse(1);
                fbd.setFeedbackdayid(String.valueOf(nextId));
            }
            return feedbackdayRepository.save(fbd);
        } catch (Exception e) {
            System.err.println("Error saving T_Feedbacksday: " + e.getMessage());
            return null;
        }
    }

    public Optional<T_Feedbacksday> findExisting(String timetableCode, String userCode) {
        return feedbackdayRepository.findByProgramtimetablecode_ProgramtimetablecodeAndUsercode_Usercode(timetableCode, userCode);
    }

    @Transactional(readOnly = true)
    public List<T_Feedbacksday> getDayFeedbacksByProgramTimeTableCode(@NotNull @NotBlank String programtimetablecode) {
        try {
            return feedbackdayRepository.getDayFeedbacksByProgramTimeTableCode(programtimetablecode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving feedbacks by program timetable code: " + programtimetablecode, ex);
        }
    }
}
