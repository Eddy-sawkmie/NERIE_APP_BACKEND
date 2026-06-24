package com.nic.nerie.m_activities.service;

import com.nic.nerie.m_activities.model.M_Activities;
import com.nic.nerie.m_activities.repository.M_ActivitiesRepository;

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
public class M_ActivitiesService {
    @Autowired
    private M_ActivitiesRepository mActivitiesRepository;

    public List<M_Activities> getActivitiesByPhaseId(String phaseid) {
        return mActivitiesRepository.findActivitiesByPhaseId(phaseid);
    }

    @Transactional(readOnly = false)
    public M_Activities saveActivities(@NotNull M_Activities activities) {
        try {
            if (activities.getActivityid() == null || activities.getActivityid().isBlank())
                activities.setActivityid(generateNextActivityId());
            
            return mActivitiesRepository.save(activities);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving M_Activities entity", ex);
        } 
    }

    @Transactional(readOnly = true)
    private String generateNextActivityId() {
        try {
            Integer lastUsedActivityId = mActivitiesRepository.getLastUsedActivityId();
            return lastUsedActivityId != null ? String.valueOf(lastUsedActivityId + 1) : "1";       
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error generating next activityid", ex);
        }
    }
}
