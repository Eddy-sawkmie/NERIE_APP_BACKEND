package com.nic.nerie.t_feedbackstudent.service;

import com.nic.nerie.m_categories.service.M_CategoriesService;
import com.nic.nerie.m_subjects.model.M_Subjects;
import com.nic.nerie.m_subjects.repository.M_SubjectRepository;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_faculties.repository.T_FacultiesRepository;
import com.nic.nerie.t_feedbackstudent.model.T_FeedbackStudent;
import com.nic.nerie.t_feedbackstudent.repository.T_FeedbackStudentRepository;

import com.nic.nerie.t_notifications.service.T_NotificationsService;
import com.nic.nerie.t_students.model.T_Students;
import com.nic.nerie.t_students.service.T_StudentsService;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class T_FeedbackStudentService {
    private final T_FeedbackStudentRepository tFeedbackStudentRepository;
    private final  T_StudentsService tStudentsService;
    private final M_SubjectRepository mSubjectRepository;
    private final  T_FacultiesRepository tFacultiesRepository;

    @Autowired
    public T_FeedbackStudentService(T_FeedbackStudentRepository tFeedbackStudentRepository, T_StudentsService tStudentsService, M_SubjectRepository mSubjectRepository, T_FacultiesRepository tFacultiesRepository) {
        this.tFeedbackStudentRepository = tFeedbackStudentRepository;
        this.tStudentsService = tStudentsService;
        this.mSubjectRepository = mSubjectRepository;
        this.tFacultiesRepository = tFacultiesRepository;
    }

    // @Transactional
    // public T_FeedbackStudent saveFeedbackStudent(T_FeedbackStudent tFeedbackStudent) {
    //     if (tFeedbackStudent.getFeedbackid() == null
    //             || tFeedbackStudent.getFeedbackid().isBlank()) {
    //         Integer maxId =
    //                 tFeedbackStudentRepository.findMaxFeedbackId();
    //         if (maxId == null)
    //             maxId = 0;
    //         tFeedbackStudent.setFeedbackid(
    //                 String.valueOf(maxId + 1));
    //     }
    //     return tFeedbackStudentRepository.save(tFeedbackStudent);
    // }

//    @Transactional
//    public T_FeedbackStudent saveFeedbackStudent(T_FeedbackStudent tFeedbackStudent) {
//        try {
//            return tFeedbackStudentRepository.save(tFeedbackStudent);
//        } catch (Exception ex) {
//            throw new RuntimeException("Error saving T_FeedbackStudent entity", ex);
//        }
//    }

    @Transactional
    public T_FeedbackStudent saveFeedbackStudent(T_FeedbackStudent tFeedbackStudent, MT_Userlogin user) {
        try {

            T_Students student = tStudentsService.findByUsercode(user.getUsercode());

            if (student == null) {
                throw new RuntimeException("Student record not found for user: " + user.getUserid());
            }


            M_Subjects subject = mSubjectRepository.findById(tFeedbackStudent.getSubjectcode().getSubjectcode())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));


            T_Faculties faculty = tFacultiesRepository.findById(tFeedbackStudent.getFacultyid().getFacultyid())
                    .orElseThrow(() -> new RuntimeException("Faculty not found"));

            tFeedbackStudent.setStudentid(student);
            tFeedbackStudent.setSubjectcode(subject);
            tFeedbackStudent.setFacultyid(faculty);

            return tFeedbackStudentRepository.save(tFeedbackStudent);
        } catch (Exception ex) {
            throw new RuntimeException("Error saving Feedback", ex);
        }
    }

    public List<Object[]> getSubjectsListFeed(String usercode) {
        return tFeedbackStudentRepository.getSubjectsListFeed(usercode);
    }

    public List<Object[]> getStudentsFeedbackList(String subjectcode, String usercode) {
        return tFeedbackStudentRepository.getStudentsFeedbackList(subjectcode, usercode);
    }
    public List<Object[]> getSubjectsListForStudent(String usercode) {
        return tFeedbackStudentRepository.getSubjectsListForStudent(usercode);
    }
}
