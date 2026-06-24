package com.nic.nerie.t_participants.controller;

import static com.nic.nerie.utils.Patterns.isSpcFound;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.captcha.service.CaptchaService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_categories.model.M_Categories;
import com.nic.nerie.m_categories.service.M_CategoriesService;
import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_designations.service.M_DesignationsService;
import com.nic.nerie.m_districts.model.M_Districts;
import com.nic.nerie.m_districts.service.M_DistrictsService;
import com.nic.nerie.m_financialyear.service.M_FinancialYearService;
import com.nic.nerie.m_minorities.model.M_Minorities;
import com.nic.nerie.m_minorities.service.M_MinoritiesService;
import com.nic.nerie.m_participantofficetypes.model.M_ParticipantOfficeTypes;
import com.nic.nerie.m_participantofficetypes.service.M_ParticipantOfficeTypesService;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_phases.service.M_PhasesService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_qualifications.model.M_Qualifications;
import com.nic.nerie.m_qualifications.service.M_QualificationsService;
import com.nic.nerie.m_qualificationsubjects.model.M_QualificationSubjects;
import com.nic.nerie.m_qualificationsubjects.service.M_QualificationSubjectsService;
import com.nic.nerie.m_states.model.M_States;
import com.nic.nerie.m_states.service.M_StatesService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.mt_userloginrole.model.MT_UserloginRole;
import com.nic.nerie.mt_userloginrole.service.MT_UserloginRoleService;
import com.nic.nerie.t_applications.model.T_Applications;
import com.nic.nerie.t_applications.service.TApplicationsService;
import com.nic.nerie.t_participants.dto.ParticipantRecordDTO;
import com.nic.nerie.t_participants.model.T_Participants;
import com.nic.nerie.t_participants.service.T_ParticipantsService;
import com.nic.nerie.utils.EmailValidator;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.RandomPasswordGenerator;
import com.nic.nerie.utils.UtilCommon;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;


@Controller
@Validated
public class T_ParticipantsController {

    private final MT_UserloginService mtUserloginService;
    private final T_ParticipantsService tParticipantsService;
    private final M_QualificationsService mQualificationsService;
    private final M_DesignationsService mDesignationsService;
    private final M_StatesService mStatesService;
    private final M_DistrictsService mDistrictsService;
    private final M_CategoriesService mCategoriesService;
    private final M_MinoritiesService mMinoritiesService;
    private final M_QualificationSubjectsService mQualificationSubjectsService;
    private final M_ParticipantOfficeTypesService mParticipantOfficeTypesService;
    private final AudittrailService audittrailService;
    private final M_FinancialYearService mFinancialYearService;
    private final TApplicationsService tApplicationsService;
    private final M_PhasesService mPhasesService;
    private final CaptchaService captchaService;
    private final MT_UserloginRoleService mtUserloginRoleService;
    private final M_ProcessesService mProcessesService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_ParticipantsController(
        MT_UserloginService mtUserloginService, 
        T_ParticipantsService tParticipantsService, 
        M_QualificationsService mQualificationsService, 
        M_DesignationsService mDesignationsService, 
        M_StatesService mStatesService, 
        M_DistrictsService mDistrictsService, 
        M_CategoriesService mCategoriesService, 
        M_MinoritiesService mMinoritiesService, 
        M_QualificationSubjectsService mQualificationSubjectsService, 
        M_ParticipantOfficeTypesService mParticipantOfficeTypesService, 
        AudittrailService audittrailService, 
        CaptchaService captchaService, 
        M_FinancialYearService mFinancialYearService, 
        M_PhasesService mPhasesService,
        TApplicationsService tApplicationsService,
        MT_UserloginRoleService mtUserloginRoleService,
        M_ProcessesService mProcessesService
    ) {
        this.mtUserloginService = mtUserloginService;
        this.tParticipantsService = tParticipantsService;
        this.mQualificationsService = mQualificationsService;
        this.mDesignationsService = mDesignationsService;
        this.mStatesService = mStatesService;
        this.mDistrictsService = mDistrictsService;
        this.mCategoriesService = mCategoriesService;
        this.mMinoritiesService = mMinoritiesService;
        this.mQualificationSubjectsService = mQualificationSubjectsService;
        this.mParticipantOfficeTypesService = mParticipantOfficeTypesService;
        this.mFinancialYearService = mFinancialYearService;
        this.tApplicationsService = tApplicationsService;
        this.audittrailService = audittrailService;
        this.mPhasesService = mPhasesService;
        this.captchaService = captchaService;
        this.mtUserloginRoleService = mtUserloginRoleService;
        this.mProcessesService = mProcessesService;
    }

    /*
     * Public endpoint
     */
    /*Participant Registration Section*/
    @GetMapping("/participants/register")
    public String renderParticipantsRegistrationPage(Model model) {
        model.addAttribute("currentPage", "participant");
        model.addAttribute("captchaPrincipal", captchaService.generateNewCaptcha());
        model.addAttribute("mtUserLogin", new MT_Userlogin());
        
        return "pages/landing/participant-registration";
    }

    /*
     * Public endpoint
     */
    @PostMapping("/participants/register")
    public ResponseEntity<String> registerParticipant(@RequestBody(required = false) MT_Userlogin mtUserLogin,
                                                      HttpServletRequest request) {

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);

        if (mtUserLogin == null) return ResponseEntity.ok("1"); // Email missing

        String email = mtUserLogin.getEmailid() != null ? mtUserLogin.getEmailid().trim() : "";
        String password = mtUserLogin.getUserpassword() != null ? mtUserLogin.getUserpassword().trim() : "";
        String mobile = mtUserLogin.getUsermobile() != null ? mtUserLogin.getUsermobile().trim() : "";
        String username = mtUserLogin.getUsername() != null ? mtUserLogin.getUsername().trim() : "";

        if (email.isEmpty()) return ResponseEntity.ok("1"); // Email Id cannot be empty
        if (email.length() > 50) return ResponseEntity.ok("3"); // Email id should be 1-50 characters long
        if (password.isEmpty()) return ResponseEntity.ok("5"); // Email id Already exist
        if (password.length() > 512) return ResponseEntity.ok("6"); // Optional validation
        if (mobile.isEmpty()) return ResponseEntity.ok("7"); //Mobile No. cannot be empty
        if (!mobile.matches("\\d{10}")) return ResponseEntity.ok("8"); // Mobile No. should be 10 digits long
        if (username.isEmpty()) return ResponseEntity.ok("1"); // Fallback: treat as email empty

        try {
            String usercode = mtUserloginService.registerUserloginParticipant(mtUserLogin);

            switch (usercode) {
                case "1": return ResponseEntity.ok("1"); // Duplicate/invalid
                case "4": return ResponseEntity.ok("4"); // Email exists
                default:
                    boolean saved = tParticipantsService.saveParticipantDetails(usercode, mtUserLogin);
                    logAuditTrail(auditMap, email, saved ? "Registration Success" : "Participant Save Failed");
                    return ResponseEntity.ok(saved ? "2" : "1"); // "2" success, fallback "1"
            }

        } catch (Exception ex) {
            logAuditTrail(auditMap, email, "Exception: " + ex.getMessage());
            return ResponseEntity.ok("1");
        }
    }

    @GetMapping("/participants/check-existing-email")
    @ResponseBody
    public String checkEmailExists(@NotBlank @RequestParam(name = "email") String email,
                                   @RequestParam(value = "userid", required = false) String userid) {
        // Trim the email first
        String trimmedEmail = email != null ? email.trim() : "";

        if (!EmailValidator.isEmailValid(trimmedEmail)) {
            return "2"; // "2" for invalid email format
        }

        boolean exists = mtUserloginService.checkUserExists(trimmedEmail, userid);

        return exists ? "1" : ""; // "1" means email exists, empty string means available
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive for role "P" (Participant).
     * Profile > Update Profile
     */
    /*Updated Participant Profile Methods*/
    @GetMapping("/participant/edit-profile")
    public String showEditProfilePage(
        @ModelAttribute("tparticipant") T_Participants tparticipantFromModelAttribute,
        Model model, 
        HttpServletRequest request
    ) {
        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();

            if ("P".equals(user.getUserrole())) {
                T_Participants participantProfile = tParticipantsService.getSpecificParticipant(user.getUsercode());

                if (participantProfile == null) {
                    // This is a new participant profile being created for the first time
                    participantProfile = tparticipantFromModelAttribute;
                    participantProfile.setUsercode(user.getUsercode());

                    if (participantProfile.getMqualifications() == null) participantProfile.setMqualifications(new M_Qualifications());
                    if (participantProfile.getMqualificationsubjects() == null) participantProfile.setMqualificationsubjects(new M_QualificationSubjects());
                    if (participantProfile.getMdesignations() == null) participantProfile.setMdesignations(new M_Designations());
                    if (participantProfile.getMstatesparticipant() == null) participantProfile.setMstatesparticipant(new M_States());
                    if (participantProfile.getMparticipantofficetypes() == null) participantProfile.setMparticipantofficetypes(new M_ParticipantOfficeTypes());
                    if (participantProfile.getMstates() == null) participantProfile.setMstates(new M_States());
                    if (participantProfile.getMdistricts() == null) participantProfile.setMdistricts(new M_Districts());
                    if (participantProfile.getMminorities() == null) participantProfile.setMminorities(new M_Minorities());
                    if (participantProfile.getMcategories() == null) participantProfile.setMcategories(new M_Categories());
                }

                model.addAttribute("tparticipant", participantProfile);
                model.addAttribute("currentUserDetails", user);

                // For pre-populating dropdowns when editing
                if (participantProfile.getMstates() != null && participantProfile.getMstates().getStatecode() != null) {
                    model.addAttribute("districtlist", mDistrictsService.getStateDistrict(participantProfile.getMstates().getStatecode()));
                } else {
                    model.addAttribute("districtlist", java.util.Collections.emptyList());
                }

                if (participantProfile.getMqualifications() != null && participantProfile.getMqualifications().getQualificationcode() != null) {
                    model.addAttribute("mqualificationsubjectlist", mQualificationSubjectsService.getQualificationSubject(participantProfile.getMqualifications().getQualificationcode()));
                } else {
                    model.addAttribute("mqualificationsubjectlist", java.util.Collections.emptyList());
                }
            } else 
                throw new AuthorizationDeniedException(user.getUserid());

            model.addAttribute("mqualificationlist", mQualificationsService.getQualificationList());
            model.addAttribute("mdesignationlist", mDesignationsService.getDesignationList("Y"));
            model.addAttribute("statelist", mStatesService.getAllStates());
            model.addAttribute("participantofficetypelist", mParticipantOfficeTypesService.getAllParticipantOfficeType());
            model.addAttribute("mcategorylist", mCategoriesService.getAllCategories());
            model.addAttribute("mminoritylist", mMinoritiesService.getAllMinorities());

        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
        } catch (AuthorizationDeniedException ex) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), ex.getMessage()), "page");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return "pages/t_participants/edit-participant-profile";
    }

    @PostMapping("/participant/update-profile")
    @ResponseBody
    public String handleUpdateProfile(@ModelAttribute("tparticipant") T_Participants tparticipant,
                                      @RequestParam(value = "emailid", required = false) String emailid,
                                      @RequestParam(value = "username", required = false) String username,
                                      @RequestParam(value = "usermobile", required = false) String usermobile,
                                      @RequestParam(value = "otherdisability", required = false) String otherdisability, 
                                      @RequestParam(value = "disabilitytype", required = false) List<String> disabilitytype, 
                                      String designationcode, String dinput,
                                      Model model,
                                      RedirectAttributes redirectAttributes,
                                      HttpServletRequest request) {

        // Initialize audit trail
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        String currentUsername = "";
        String response = "";

        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication();

            if (user.getUserrole().equals("P")) {
                currentUsername = user.getUserid();

                if (emailid.trim().isEmpty()) {
                    response = "1";
                }
                if (emailid.trim().length() < 0 || emailid.trim().length() > 50) {
                    response = "3";
                }
                if (mtUserloginService.checkParticipantEmailProfileUpdate(emailid.trim(), user.getUsercode())) {
                    response = "4";
                }

                //T_Participants tp = tParticipantsService.getSpecificParticipant(tparticipant.getUsercode());

                if (disabilitytype != null && !disabilitytype.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (String type : disabilitytype) {
                        if ("Other".equalsIgnoreCase(type)) {
                            if (otherdisability != null && !otherdisability.trim().isEmpty()) {
                                sb.append("Other (")
                                .append(otherdisability.trim())
                                .append(")");
                            }
                        } else {
                            sb.append(type);
                        }
                        sb.append(", ");
                    }
                    // remove last comma and space
                    sb.setLength(sb.length() - 2);
                    tparticipant.setDifferentlyableddetails(sb.toString());
                }

                // MT_Userlogin mtuserlogin = new MT_Userlogin();
                // mtuserlogin.setUsercode(tp.getMtuserlogin().getUsercode());
                tparticipant.setMtuserlogin(user);

                String qualsubcode = tparticipant.getMqualificationsubjects().getQualificationsubjectcode();
                if (qualsubcode.isEmpty()) {
                    tparticipant.setMqualificationsubjects(null);
                }

                if ("others".equals(designationcode)) {
                    if (dinput != null || !"".equals(dinput) || dinput.length() > 0) {
                        M_Designations des = new M_Designations();
                        des.setDesignationname(dinput);
                        des.setIsparticipantdesignation("Y");
                        designationcode = mDesignationsService.saveDesignationDetails2(des);
                        M_Designations des2 = new M_Designations();
                        des2.setDesignationcode(designationcode);
                        user.setMdesignations(des2);
                        tparticipant.setMdesignations(des2);
                    }
                } else {
                    M_Designations des2 = mDesignationsService.getDesignation(designationcode);
                    tparticipant.setMdesignations(des2);
                    user.setMdesignations(des2);
                }

                if (mtUserloginService.updateParticipantProfile(emailid, username, usermobile, designationcode, tparticipant)) {
                    response = "2";
                    logAuditTrail(auditMap, currentUsername, "Update Profile Success for Participant " + tparticipant.getUsercode());
                } else {
                    response = "";
                    logAuditTrail(auditMap, currentUsername, "Update Profile Failed (DB update returned false) for Participant " + tparticipant.getUsercode());
                }

                return response;
            } else
                throw new AuthorizationDeniedException(user.getUserid());
        } catch (AuthenticationCredentialsNotFoundException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "page");
        } catch (AuthorizationDeniedException ex) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), ex.getMessage()), "page");
        } catch (Exception e) {
            throw new RuntimeException(e);
            //logAuditTrail(auditMap, currentUsername, "Update Profile Failed (Exception: " + e.getMessage() + ")"); 
        }
    }

    @PostMapping("/participant/check-existing-email")
    @ResponseBody
    public String checkUserExistOnUpdate(@RequestParam(value = "userid", required = true) String userid) {
        String response = "";

        try {
            MT_Userlogin user = mtUserloginService.getUserloginFromAuthentication(
                    SecurityContextHolder.getContext().getAuthentication());

            if (user == null) {
                return "redirect:/login?msg=unauthenticated";
            } else if ("P".equals(user.getUserrole())) {

                // Check for invalid input
                if (userid == null || userid.trim().length() > 50 || isSpcFound(userid.trim())) {
                    return "";
                }

                if (mtUserloginService.checkParticipantEmailProfileUpdate(userid.trim(), user.getUsercode())) {
                    response = "1"; // Email already exists
                } else {
                    response = "";  // Email available
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "pages/error/404";
        }

        return response;
    }

    @PostMapping("/participant/list")
    public ResponseEntity<List<Object[]>> getProgramParticipants(@RequestParam("phaseid") String phaseid) {
        return ResponseEntity.ok(tParticipantsService.getProgramParticipants(phaseid));
    }

    @PostMapping("/participant/get-participant")
    public ResponseEntity<List<Object[]>> getProgramParticipant(@RequestParam("userid") String userid) {
        if (userid == null || userid.isBlank() || userid.trim().length() > 50)
            return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(tParticipantsService.checkAndGetParticipantDetails(userid));
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive for role "A" (Local Admin) and "U" (Coordinator-Faculty).
     * 'Manage Participants' process (processcode = 6)
     */ 
    @GetMapping("/participant/manage")
    public String renderAddParticipantsPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Participants, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 6)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Manage Participants, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (userRole) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("fylist", mFinancialYearService.getfy());
        model.addAttribute("mstatelist", mStatesService.getAllStates());

        return "pages/t_participants/add-participant";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive for role "A" (Local Admin) and "U" (Coordinator-Faculty).
     * Endpoint tied with 'Manage Participants' process (processcode = 6)
     */ 
    @PostMapping("/participant/create")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String[]> addParticipant(
        @RequestParam(value = "usercode", required = false) String usercode,
        @RequestParam("phaseid") String phaseid,
        @RequestParam("username") String username,
        @RequestParam("emailid") String emailid,
        @RequestParam("usermobile") String usermobile,
        @RequestParam("statecode") String statecode,
        HttpServletRequest request
    ) {
        String[] response = {"", ""};
        MT_Userlogin currentUser;
        try {
            currentUser = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = currentUser.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(currentUser.getUsercode(), 6)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), currentUser.getUserid()), "json");
        }

        // validating required fields
        if (
            phaseid == null || phaseid.isBlank() ||
            username == null || username.isBlank() ||
            emailid == null || emailid.isBlank() ||
            usermobile == null || usermobile.isBlank() ||
            statecode == null || statecode.isBlank()
        ) {
            response[1] = "Required fields are missing";
            return ResponseEntity.badRequest().body(response); 
        }

        // validating phaseid
        if (phaseid.trim().length() > 6) {
            response[1] = "Invalid phaseid";
            return ResponseEntity.badRequest().body(response);
        }
        if (!mPhasesService.existsById(phaseid)) {
            response[1] = "Phase does not exist";
            return ResponseEntity.badRequest().body(response);  
        }

        // validating emailid
        if (!EmailValidator.isEmailValid(emailid)) {
            response[1] = "Invalid emailid";
            return ResponseEntity.badRequest().body(response);
        }

        // validating usermobile
        if (usermobile.trim().length() != 10 || !usermobile.matches("\\d{10}")) {
            response[1] = "Invalid usermobile";
            return ResponseEntity.badRequest().body(response);
        }

        MT_UserloginRole participantRole = mtUserloginRoleService.findByRoleCode("P");
        if (participantRole == null) {
            throw new RuntimeException("Role 'P' not found");
        }
        MT_Userlogin newUser = new MT_Userlogin();  // MT_Userlogin instance representing the Participant
        newUser.setUsercode(usercode != null ? usercode.trim() : "");
        newUser.setUsername(username.trim());
        newUser.setUsermobile(usermobile.trim());
        newUser.setEmailid(emailid.trim());
        newUser.setIsfaculty("0");
        
        // setting password
        String password = "";
        // for creating
        if (newUser.getUsercode().isBlank()) {
            password = RandomPasswordGenerator.generateRandomPassword();
            response[1] = "Please note the user id and password:\n User Id:" + newUser.getEmailid() + "\n Password:" + password;
            password = mtUserloginService.getBcryptPassword(password); 
            newUser.setIsmodified("Y"); // why? I don't know
            newUser.setUseBcrypt(true); // 80% of the bug is forgetting to add this line
        } 
        // for updating
        else {
            password = mtUserloginService.findByUsercode(newUser.getUsercode()).getUserpassword();
            newUser.setUseBcrypt(mtUserloginService.getIsUseBcryptByUsercode(newUser.getUsercode()));
            newUser.setIsmodified("N"); // makes sense? nope.
        }

        newUser.setUserpassword(password);
        newUser.setUserid(newUser.getEmailid());
        newUser.setEnabled((short) 1);
        newUser.setUserdescription("Participants");

        // setting role
        newUser.setUserrole("P");
        MT_UserloginRole newUserRole = mtUserloginRoleService.findByRoleCode("P");
        if (newUserRole == null)
            return ResponseEntity.internalServerError().build();
        newUser.setRole(newUserRole);

        // saving
        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        MT_Userlogin savedUser = null;
        try {
            if ((savedUser = mtUserloginService.save(newUser)) != null) {
                persistenceLogger.info("MT_Userlogin with usercode {} saved successfully by userid {}", savedUser.getUsercode(), currentUser.getUserid());
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "mt_userlogin with usercode " + savedUser.getUsercode() + " saved successfully");

                T_Participants newParticipant = new T_Participants();
                newParticipant.setUsercode(savedUser.getUsercode());    
                M_States newParticipantState = new M_States();
                newParticipantState.setStatecode(statecode);
                newParticipant.setMstatesparticipant(newParticipantState);
                newParticipant.setMtuserlogin(currentUser);
            
                if (tParticipantsService.insertOrUpdateParticipantsByCC(newParticipant)) {
                    persistenceLogger.info("T_Participants record inserted successfully by userid {}", currentUser.getUserid());
                    audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_participants record inserted successfully");
                    response[0] = "2";
                    
                    if (tApplicationsService.checkParticipantApplicationExists(savedUser.getUsercode(), phaseid)) 
                        response[0] = "1";
                    else {
                        T_Applications newApplications = new T_Applications();
                        newUser.setUsercode(savedUser.getUsercode());
                        newApplications.setMtuserlogin(newUser);
                        newApplications.setMtuserloginapplied(currentUser);
                        M_Phases newApplicationsPhases = new M_Phases();
                        newApplicationsPhases.setPhaseid(phaseid);
                        newApplications.setPhaseid(newApplicationsPhases);
                        newApplications.setStatus("P");
                        newApplications.setEmailsent("N");
                        String newApplicationsCode = String.format("%05d", Short.valueOf(phaseid)) + "P" + String.format("%06d", Integer.valueOf(savedUser.getUsercode()));
                        newApplications.setApplicationcode(newApplicationsCode);
                        
                        if ((newApplications = tApplicationsService.saveApplications(newApplications)) == null) {
                            persistenceLogger.error("T_Applications save failed");
                            audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_applications save failed");
                            response[0] = "";
                            throw new Exception("T_Applications save failed");  // for rollback
                        } else {
                            persistenceLogger.info("T_Applications with applicationcode {} saved successfully by userid {}", newApplications.getApplicationcode(), currentUser.getUserid());
                            audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_applications with applicationcode " + newApplications.getApplicationcode() + " saved successfully");
                        }
                    }
                } else {
                    persistenceLogger.error("T_Participants record insert failed");
                    audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_participants record insert failed");
                    throw new Exception("T_Participants record insert failed");  // for rollback
                }
            } else {
                persistenceLogger.error("MT_Userlogin save failed");
                audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "mt_userlogin save failed");
                throw new Exception("MT_Userlogin save failed");  // for rollback
            }
        } catch (Exception ex) {
            persistenceLogger.error("T_Participants Add Failed.\nMessage: {}\nUserid: {}", ex.getMessage(), ex, currentUser.getUserid());
            audittrailService.logAuditTrail(auditMap, currentUser.getUserid(), "t_participants add failed");
        }

        return ResponseEntity.ok(response);
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive for role "A" (Local Admin) and "U" (Coordinator-Faculty).
     * Endpoint tied with 'Manage Participants' process (processcode = 6)
     */ 
    @PostMapping("/participant/remove")
    public ResponseEntity<String> removeParticipantByApplicationcode(@RequestParam("applicationcode") String applicationcode, HttpServletRequest request) {
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
            mProcessesService.isProcessGranted(user.getUsercode(), 6)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating applicationcode
        if (applicationcode == null || applicationcode.isBlank() || applicationcode.trim().length() > 12)
            return ResponseEntity.badRequest().body("Invalid application code");

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            tApplicationsService.deleteApplicationByApplicationcode(applicationcode);
            persistenceLogger.info("T_Applications with applicationcode {} removed successfully by userid {}", applicationcode, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_applications with applicationcode " + applicationcode + " removed successfully");

            return ResponseEntity.ok("1");
        } catch (Exception ex) {
            persistenceLogger.error("T_Applications remove failed by userid {}", user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_applications remove failed");
        }

        return ResponseEntity.ok("");
    }

    @GetMapping("/participants")
    public String participantsPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Manage Participants, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();
         switch (userRole) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }
        return "pages/t_participants/participants";
    }

    @GetMapping("/participants/data")
    @ResponseBody
    public Page<ParticipantRecordDTO> getParticipants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        return tParticipantsService.getParticipants(page, size, search);
    }

    private void logAuditTrail(HashMap<String, String> auditMap,String userId, String actionTaken) {
        if (auditMap != null) {
            auditMap.put("userid", userId);
            auditMap.put("actiontaken", actionTaken);
            audittrailService.saveAuditTrail(auditMap);
        }
    }
}
