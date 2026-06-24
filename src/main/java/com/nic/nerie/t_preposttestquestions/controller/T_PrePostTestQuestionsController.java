package com.nic.nerie.t_preposttestquestions.controller;

import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_financialyear.service.M_FinancialYearService;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.m_preposttestqcategory.model.M_PrePostTestQCategory;
import com.nic.nerie.m_preposttestqcategory.service.M_PrePostTestQCategoryService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_notifications.model.T_Notifications;
import com.nic.nerie.t_notifications.service.T_NotificationsService;
import com.nic.nerie.t_phase_preposttest.model.T_Phase_PrePostTest;
import com.nic.nerie.t_phase_preposttest.service.T_Phase_PrePostTestService;
import com.nic.nerie.t_preposttestquestions.model.T_PrePostTestQuestions;
import com.nic.nerie.t_preposttestquestions.service.T_PrePostTestQuestionsService;
import com.nic.nerie.utils.ExceptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/pre-post-test/questions")
public class T_PrePostTestQuestionsController {
    private final T_PrePostTestQuestionsService tPrePostTestQuestionsService;
    private final M_PrePostTestQCategoryService mPrePostTestQCategoryService;

    @Autowired
    public T_PrePostTestQuestionsController(T_PrePostTestQuestionsService tPrePostTestQuestionsService, M_PrePostTestQCategoryService mPrePostTestQCategoryService) {
        this.tPrePostTestQuestionsService = tPrePostTestQuestionsService;
        this.mPrePostTestQCategoryService = mPrePostTestQCategoryService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to A (Local-admin) & U (Coordinator-Faculty)
     */
    @GetMapping("/count")
    @ResponseBody
    public int getAvailableQuestionsCount() {
        List<T_PrePostTestQuestions> qList = tPrePostTestQuestionsService.getAllPrePostTestQuestions();
        return qList.size();
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to A (Local-admin) & U (Coordinator-Faculty)
     */
    @PostMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> saveNewPrePostTestQuestion(@ModelAttribute T_PrePostTestQuestions question,
                                                             @RequestParam(required=false) List<Integer> correctans,
                                                             @RequestParam String questiontype,
                                                             @RequestParam(required=false) String newCategory){
        if (correctans == null || correctans.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select at least one correct option.");
        }

        question.setQuestiontype(questiontype);

        if(StringUtils.hasText(newCategory) && (!mPrePostTestQCategoryService.checkPrePostTestQCategoryExists(newCategory.toUpperCase()))){
            M_PrePostTestQCategory qc = new M_PrePostTestQCategory();
            qc.setPptqcategoryname(newCategory);
            mPrePostTestQCategoryService.saveNewPrePostTestQCategory(qc);
            question.setQuestiontype(newCategory.toUpperCase());
        }

        for (int option : correctans) {
            switch (option) {
                case 1 -> question.setOp1correct("1");
                case 2 -> question.setOp2correct("1");
                case 3 -> question.setOp3correct("1");
                case 4 -> question.setOp4correct("1");
                case 5 -> question.setOp5correct("1");
                case 6 -> question.setOp6correct("1");
            }
        }

        if(!StringUtils.hasText(question.getOption5()))
            question.setOption5(null);

        if(!StringUtils.hasText(question.getOption6()))
            question.setOption6(null);

        question.setQopcode(String.valueOf(correctans.size()));
        try{
            boolean result = tPrePostTestQuestionsService.saveNewPrePostTestQuestion(question);
            if (result) {
                return ResponseEntity.ok("Question saved successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to save the question.");
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while saving the question.");
        }
    }
}
