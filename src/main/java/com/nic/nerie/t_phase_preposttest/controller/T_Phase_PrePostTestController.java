package com.nic.nerie.t_phase_preposttest.controller;

import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_financialyear.service.M_FinancialYearService;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.m_preposttestqcategory.service.M_PrePostTestQCategoryService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_notifications.model.T_Notifications;
import com.nic.nerie.t_notifications.service.T_NotificationsService;
import com.nic.nerie.t_participantanswerkey_preposttest.model.T_ParticipantAnswerKey_PrePostTest;
import com.nic.nerie.t_participantanswerkey_preposttest.service.T_ParticipantAnswerKey_PrePostTestService;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pre-post-test")
public class T_Phase_PrePostTestController {
    private final MT_UserloginService mtUserloginService;
    private final M_ProcessesService mProcessesService;
    private final M_FinancialYearService mFinancialYearService;
    private final T_PrePostTestQuestionsService tPrePostTestQuestionsService;
    private final M_PrePostTestQCategoryService mPrePostTestQCategoryService;
    private final M_PhasesService mPhasesService;
    private final T_Phase_PrePostTestService tPhasePrePostTestService;
    private final T_NotificationsService tNotificationsService;
    private final T_ParticipantAnswerKey_PrePostTestService tParticipantAnswerKeyPrePostTestService;

    @Autowired
    public T_Phase_PrePostTestController(MT_UserloginService mtUserloginService, M_ProcessesService mProcessesService, M_FinancialYearService mFinancialYearService, T_PrePostTestQuestionsService tPrePostTestQuestionsService, M_PrePostTestQCategoryService mPrePostTestQCategoryService, M_PhasesService mPhasesService, T_Phase_PrePostTestService tPhasePrePostTestService, T_NotificationsService tNotificationsService, T_ParticipantAnswerKey_PrePostTestService tParticipantAnswerKeyPrePostTestService) {
        this.mtUserloginService = mtUserloginService;
        this.mProcessesService = mProcessesService;
        this.mFinancialYearService = mFinancialYearService;
        this.tPrePostTestQuestionsService = tPrePostTestQuestionsService;
        this.mPrePostTestQCategoryService = mPrePostTestQCategoryService;
        this.mPhasesService = mPhasesService;
        this.tPhasePrePostTestService = tPhasePrePostTestService;
        this.tNotificationsService = tNotificationsService;
        this.tParticipantAnswerKeyPrePostTestService = tParticipantAnswerKeyPrePostTestService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to A (Local-admin) & U (Coordinator-Faculty)
     */
    @GetMapping("/create")
    public String renderCreatePrePostTestPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
                List.of("A", "U").contains(userRole) &&
                        mProcessesService.isProcessGranted(user.getUsercode(), 46)
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add/Edit Program, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (userRole) {
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
        }
        model.addAttribute("fylist", mFinancialYearService.getfy());
        model.addAttribute("questions", tPrePostTestQuestionsService.getAllPrePostTestQuestions());
        model.addAttribute("qcategory", mPrePostTestQCategoryService.getAllPrePostTestQuestionCategories());
        model.addAttribute("t_preposttestquestions", new T_PrePostTestQuestions());
        return "pages/create-pre-post-test";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to A (Local-admin) & U (Coordinator-Faculty)
     */
    @PostMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> savePhasePrePostTest(
            @RequestParam(value="randomizeFlag",required=false) boolean randomizeFlag,
            @RequestParam(value="randomizeCount",required=false) Integer randomizeCount,
            @RequestParam(required=false) List<String> question,
            @RequestParam String activityProgcode,
            @RequestParam String activityPhaseID,
            @RequestParam(value="testtype",required=false) String testtype,
            @RequestParam(value="testlink",required=false)String testlink,
            HttpServletRequest request) {

        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
                List.of("A", "U").contains(userRole) &&
                        mProcessesService.isProcessGranted(user.getUsercode(), 46)
        )) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add/Edit Program, " + request.getMethod(), user.getUserid()), "page");
        }

        M_Phases phase = mPhasesService.getPhaseByPhaseId(activityPhaseID);

        if (tPhasePrePostTestService.testExistsForPhase(activityPhaseID)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("A test already exists for this program phase. Cannot create a new one.");
        }

        T_Phase_PrePostTest ptest = new T_Phase_PrePostTest();
        ptest.setPhaseid(phase);
        ptest.setTesttype(testtype);

        Set<T_PrePostTestQuestions> questionSet = new HashSet<>();

        if ("LINK".equalsIgnoreCase(testtype)) {
            ptest.setTesturl(testlink);
        } else {
            List<T_PrePostTestQuestions> allQuestions = tPrePostTestQuestionsService.getAllPrePostTestQuestions();

            if (randomizeFlag) {
                if (allQuestions.size() < 10) {
                    return ResponseEntity.badRequest().body("Not enough questions to randomize. At least 10 questions are required.");
                }

                for (int i = 0; i < randomizeCount && !allQuestions.isEmpty(); i++) {
                    int randIndex = (int) (Math.random() * allQuestions.size());
                    questionSet.add(allQuestions.remove(randIndex));
                }

            } else {
                if (question != null && !question.isEmpty()) {
                    for (String qid : question) {
                        T_PrePostTestQuestions q = tPrePostTestQuestionsService.getPrePostTestQuestionById(qid);
                        questionSet.add(q);
                    }
                }
            }
        }

        ptest.setQuestions(questionSet);

        try {
            boolean saved = tPhasePrePostTestService.saveNewPhasePrePostTest(ptest);
            if (saved) {
                List<MT_Userlogin> participants = mtUserloginService.getParticipantsForPhase(phase);
                if (!participants.isEmpty()) {
                    Set<MT_Userlogin> receivers = new HashSet<>(participants);
                    T_Notifications notification = new T_Notifications();
                    notification.setNotification("Please Attempt The Test for " +
                            phase.getProgramcode().getProgramname() + ", Phase " + phase.getPhaseno() + "!!");
                    notification.setReceivertype("SPECIFIC");
                    notification.setUsercode(user);
                    notification.setOfficecode(user.getMoffices());
                    notification.setEntrydate(new java.util.Date());
                    notification.setReceivers(receivers);

                    tNotificationsService.addNotifications(notification);
                }

                return ResponseEntity.ok("Test Saved Successfully");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error Saving pre post test.", ex);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save Pre/Post Test");
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role P (Participant)
     */
    @GetMapping("/view")
    public String renderViewPrePostTestPage(@RequestParam(value = "aid", required = false) String phaseid,
                                        @RequestParam(value = "test", required = false) String type,
                                        HttpServletRequest request,
                                        Model model) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"P".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        T_Phase_PrePostTest test = tPhasePrePostTestService.getPhasePrePostTestByPhaseId(phaseid);

        Set<T_PrePostTestQuestions> questions =  test.getQuestions();

        model.addAttribute("questions", questions);
        model.addAttribute("test", test.getTestid());
        model.addAttribute("type",type.toUpperCase());
        model.addAttribute("mprogramlist", mPhasesService.getPhaseDetailsForFeedbackByPhaseId(phaseid));
        model.addAttribute("view", true);
        model.addAttribute("login", user);

        // This line sets the active menu for child page that belong to the "My Program" section.
        model.addAttribute("activeMenuItem", "/program/my-programs");

        return "pages/t_participants/pre-post-test";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role P (Participant)
     */
    @PostMapping("/answer/submit")
    @ResponseBody
    public ResponseEntity<String> submitPrePostAnswers(@RequestParam(value = "testid", required = false) String testid,
                                                       @RequestParam(value = "testtype", required = false) String testtype,
                                                       @RequestBody Map<String, List<String>> answers,
                                                       HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"P".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(
                            request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        T_Phase_PrePostTest test = tPhasePrePostTestService.getPhasePrePostTestById(testid);
        if (test == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Test not found for ID: " + testid);
        }

        try {
            for (Map.Entry<String, List<String>> entry : answers.entrySet()) {
                String questionId = entry.getKey();
                List<String> selectedOptions = entry.getValue();

                T_PrePostTestQuestions q = tPrePostTestQuestionsService.getPrePostTestQuestionById(questionId);
                if (q == null) continue;

                for (String option : selectedOptions) {
                    T_ParticipantAnswerKey_PrePostTest answer = new T_ParticipantAnswerKey_PrePostTest();
                    answer.setParticipantusercode(user);
                    answer.setTestid(test);
                    answer.setTesttype(testtype);
                    answer.setQuestionid(q);

                    // Match selected option to option index (1–6)
                    if (option.equals(q.getOption1())) answer.setSelectedoption("1");
                    else if (option.equals(q.getOption2())) answer.setSelectedoption("2");
                    else if (option.equals(q.getOption3())) answer.setSelectedoption("3");
                    else if (option.equals(q.getOption4())) answer.setSelectedoption("4");
                    else if (option.equals(q.getOption5())) answer.setSelectedoption("5");
                    else if (option.equals(q.getOption6())) answer.setSelectedoption("6");

                    tParticipantAnswerKeyPrePostTestService.saveNewParticipantAnswerKey(answer);
                }
            }

            return ResponseEntity.ok("Test Saved Successfully");
        } catch (Exception e) {
            throw new RuntimeException("Error while saving participant answers.", e);
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role P (Participant)
     */
    @GetMapping("/view-scores")
    public String renderViewPrePostTestScores(@RequestParam("aid") String phaseid,
                                              HttpServletRequest request,
                                              Model model) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }

        if (!"P".equalsIgnoreCase(user.getUserrole())) {
            throw new MyAuthorizationDeniedException(
                    ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        T_Phase_PrePostTest test = tPhasePrePostTestService.getPhasePrePostTestByPhaseId(phaseid);
        Set<T_PrePostTestQuestions> questions = test.getQuestions();

        Map<String, T_PrePostTestQuestions> questionMap = questions.stream()
                .collect(Collectors.toMap(
                        T_PrePostTestQuestions::getQuestionid, // This method returns a String
                        Function.identity(),
                        (existing, replacement) -> existing // Merge function to handle potential duplicates safely
                ));

        List<T_ParticipantAnswerKey_PrePostTest> preTestAnswers =
                tParticipantAnswerKeyPrePostTestService.getAllParticipantPrePostTestAnswers(user, test, "PRE");

        List<T_ParticipantAnswerKey_PrePostTest> postTestAnswers =
                tParticipantAnswerKeyPrePostTestService.getAllParticipantPrePostTestAnswers(user, test, "POST");

        // Calculate Pre-Test Marks
        int preMarksObtained = 0;
        for (T_ParticipantAnswerKey_PrePostTest answer : preTestAnswers) {
            // The 'answer' object contains the question object. Get its String ID.
            String questionIdFromAnswer = answer.getQuestionid().getQuestionid();
            T_PrePostTestQuestions question = questionMap.get(questionIdFromAnswer);
            if (question != null && isAnswerCorrect(question, answer.getSelectedoption())) {
                // Logic for multi-select questions 1 point per correct answer.
                preMarksObtained++;
            }
        }

        // Calculate Post-Test Marks
        int postMarksObtained = 0;
        for (T_ParticipantAnswerKey_PrePostTest answer : postTestAnswers) {
            String questionIdFromAnswer = answer.getQuestionid().getQuestionid();
            T_PrePostTestQuestions question = questionMap.get(questionIdFromAnswer);
            if (question != null && isAnswerCorrect(question, answer.getSelectedoption())) {
                postMarksObtained++;
            }
        }

        // Calculate total marks based on the total number of correct answers possible.
        int totalMarks = 0;
        for (T_PrePostTestQuestions q : questions) {
            // handles both single-choice and multiple-choice (multi-select) questions
            // by summing up every possible correct option to get the maximum score.
            if ("1".equals(q.getOp1correct())) totalMarks++;
            if ("1".equals(q.getOp2correct())) totalMarks++;
            if ("1".equals(q.getOp3correct())) totalMarks++;
            if ("1".equals(q.getOp4correct())) totalMarks++;
            if ("1".equals(q.getOp5correct())) totalMarks++;
            if ("1".equals(q.getOp6correct())) totalMarks++;
        }

        model.addAttribute("pretestanswers", preTestAnswers);
        model.addAttribute("posttestanswers", postTestAnswers);
        model.addAttribute("totalmarks", totalMarks);
        model.addAttribute("questions", questions); // Pass the original Set to the view
        model.addAttribute("test", test.getTestid());
        model.addAttribute("mprogramlist", mPhasesService.getPhaseDetailsForFeedbackByPhaseId(phaseid));
        model.addAttribute("view", true);
        model.addAttribute("login", user);

        // final calculated scores
        model.addAttribute("preMarksObtained", preMarksObtained);
        model.addAttribute("postMarksObtained", postMarksObtained);

        // This line sets the active menu for child page that belong to the "My Program" section.
        model.addAttribute("activeMenuItem", "/program/my-programs");

        return "pages/t_participants/view-pre-post-test-scores";
    }

    private boolean isAnswerCorrect(T_PrePostTestQuestions question, String selectedOption) {
        if (selectedOption == null) return false;
        switch (selectedOption) {
            case "1": return "1".equals(question.getOp1correct());
            case "2": return "1".equals(question.getOp2correct());
            case "3": return "1".equals(question.getOp3correct());
            case "4": return "1".equals(question.getOp4correct());
            case "5": return "1".equals(question.getOp5correct());
            case "6": return "1".equals(question.getOp6correct());
            default: return false;
        }
    }
}
