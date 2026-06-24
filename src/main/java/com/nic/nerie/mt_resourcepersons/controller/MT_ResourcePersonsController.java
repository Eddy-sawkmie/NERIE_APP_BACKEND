package com.nic.nerie.mt_resourcepersons.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_designations.model.M_Designations;
import com.nic.nerie.m_designations.service.M_DesignationsService;
import com.nic.nerie.m_financialyear.service.M_FinancialYearService;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_programs.model.M_Programs;
import com.nic.nerie.m_qualificationcategories.model.M_QualificationCategories;
import com.nic.nerie.m_qualificationcategories.service.M_QualificationCategoriesService;
import com.nic.nerie.m_qualifications.model.M_Qualifications;
import com.nic.nerie.m_qualifications.service.M_QualificationsService;
import com.nic.nerie.m_qualificationsubjects.model.M_QualificationSubjects;
import com.nic.nerie.m_qualificationsubjects.service.M_QualificationSubjectsService;
import com.nic.nerie.mt_resourcepersons.dto.ResourcePersonsDTO;
import com.nic.nerie.mt_resourcepersons.model.MT_ResourcePersons;
import com.nic.nerie.mt_resourcepersons.service.MT_ResourcePersonsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.EmailValidator;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/resource-persons")
public class MT_ResourcePersonsController {
    private final MT_ResourcePersonsService mtResourcePersonsService;
    private final MT_UserloginService mtUserloginService;
    private final M_DesignationsService mDesignationsService;
    private final M_QualificationsService qualificationsService;
    private final M_QualificationCategoriesService mQualificationCategoriesService;
    private final M_QualificationSubjectsService mQualificationSubjectsService;
    private final M_FinancialYearService mFinancialYearService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public MT_ResourcePersonsController(MT_ResourcePersonsService mtResourcePersonsService,
                                        MT_UserloginService mtUserloginService,
                                        M_QualificationsService qualificationsService,
                                        M_QualificationCategoriesService mQualificationCategoriesService,
                                        M_QualificationSubjectsService mQualificationSubjectsService,
                                        M_DesignationsService mDesignationsService,
                                        M_FinancialYearService mFinancialYearService,
                                        M_ProcessesService mProcessesService,
                                        AudittrailService audittrailService) {
        this.mtResourcePersonsService = mtResourcePersonsService;
        this.mtUserloginService = mtUserloginService;
        this.qualificationsService = qualificationsService;
        this.mQualificationCategoriesService = mQualificationCategoriesService;
        this.mQualificationSubjectsService = mQualificationSubjectsService;
        this.mDesignationsService = mDesignationsService;
        this.mFinancialYearService = mFinancialYearService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Public endpoint
     */
    @PostMapping("/is-email-available")
    public ResponseEntity<String> checkEmailAvailability(
            @RequestParam("rpemailid") String email,
            @RequestParam(value = "rpslno", required = false) String rpslno) {

        if (email == null || email.trim().isEmpty())
            return ResponseEntity.badRequest().body("rpemailid parameter is missing");

        if (!EmailValidator.isEmailValid(email))
            return ResponseEntity.badRequest().body("Invalid email format");

        boolean isAvailable = mtResourcePersonsService.checkEmailAvailability(email, rpslno);

        return isAvailable ? ResponseEntity.ok("0") : ResponseEntity.ok("1");
    }

    /*
     * Secured endpoint
     * endpoint exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * 'Add Resource Person' process (processcode = 8) 
     */
    @GetMapping()
    public String getResourcePersonsPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "getResourcePersonsPage, " + request.getMethod()), "page");
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
        return "pages/resource-persons";
    }

    @GetMapping("/data")
    @ResponseBody
    public Page<ResourcePersonsDTO> getParticipants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        return mtResourcePersonsService.getResourcePersons(page, size, search);
    }

    /*
     * Secured endpoint
     * endpoint exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * 'Add Resource Person' process (processcode = 8) 
     */
    @GetMapping("/create")
    public String renderResourcePersonsCreatePage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Add Resource Person, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();
        String officeCode = user.getMoffices().getOfficecode();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 8)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Add Resource Person, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (user.getRole().getRoleCode().toUpperCase()) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }
        model.addAttribute("currentuserofficecode", officeCode);
        model.addAttribute("mtresourceList", mtResourcePersonsService.getAllResourcePersons());
        model.addAttribute("designationlist", mDesignationsService.getDesignationList("N"));
        model.addAttribute("qualificationcategorylist", mQualificationCategoriesService.getAllOrderedByQualificationcategoryname());

        return "pages/create-resource-person";
    }

    /*
     * Secured endpoint
     * endpoint exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Add Resource Person' process (processcode = 8) 
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<String> saveResourcePerson(
            @RequestParam(value = "rpslno", required = false) String rpslno,
            @RequestParam("rpname") String rpname,
            @RequestParam("rpemailid") String rpemailid,
            @RequestParam(value = "qualificationsubjectcode", required = false) String qualificationsubjectcode,
            @RequestParam(value = "rpspecialization", required = false) String rpspecialization,
            @RequestParam("designationcodess") String designationcodess,
            @RequestParam("rpinstitutename") String rpinstitutename,
            @RequestParam("rpofficeaddress") String rpofficeaddress,
            @RequestParam("rpresidentialaddress") String rpresidentialaddress,
            @RequestParam(value = "rpofficephone", required = false) String rpofficephone,
            @RequestParam(value = "rpresidencephone", required = false) String rpresidencephone,
            @RequestParam("rpmobileno") String rpmobileno,
            @RequestParam(value = "rpfax", required = false) String rpfax,
            @RequestParam("rpqualificationcategory") String rpqualificationcategory,
            @RequestParam("rpqualification") String rpqualification,
            @RequestParam(value = "qcinput", required = false) String qcinput,
            @RequestParam(value = "dinput", required = false) String dinput,
            HttpServletRequest request
    ) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = user.getRole().getRoleCode().toUpperCase();
        
        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 8)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating necessary fields
        if (rpemailid == null || rpemailid.trim().length() == 0)
            return ResponseEntity.ok("3");

        if (rpemailid.trim().length() > 50)
            return ResponseEntity.ok("4");

        if (!mtResourcePersonsService.checkEmailAvailability(rpemailid, rpslno))
            return ResponseEntity.ok("1"); // Returns 1 (Email already exists)


        if (rpinstitutename == null || rpinstitutename.trim().length() == 0)
            return ResponseEntity.ok("7");

        if (rpinstitutename.trim().length() > 100)
            return ResponseEntity.ok("8");

        if (rpmobileno == null || rpmobileno.trim().length() == 0)
            return ResponseEntity.ok("9");
        
        if (rpmobileno.trim().length() != 10)
            return ResponseEntity.ok("10");

        if (rpname == null || rpname.trim().length() == 0)
            return ResponseEntity.ok("11");

        if (rpname.trim().length() > 50)
            return ResponseEntity.ok("12");
        
        if (rpofficeaddress == null || rpofficeaddress.trim().length() == 0)
            return ResponseEntity.ok("17");

        if (rpofficeaddress.trim().length() > 100)
            return ResponseEntity.ok("18");

        if (rpresidentialaddress == null || rpresidentialaddress.trim().length() == 0)
            return ResponseEntity.ok("19");

        if (rpresidentialaddress.trim().length() > 100)
            return ResponseEntity.ok("20");

        if (rpqualificationcategory == null || rpqualificationcategory.isBlank() || rpqualification == null || 
            rpqualification.isBlank() || designationcodess == null || designationcodess.isBlank())
            return ResponseEntity.badRequest().build();
        // end of validation

        // intializing resource person instance
        MT_ResourcePersons newResourcePerson = new MT_ResourcePersons();
        newResourcePerson.setRpslno(rpslno != null ? rpslno.trim() : "");
        newResourcePerson.setRpname(rpname.trim());
        newResourcePerson.setRpemailid(rpemailid.trim());
        newResourcePerson.setMtuserlogin(user);
        newResourcePerson.setRpspecialization(rpspecialization != null ? rpspecialization.trim() : "");
        newResourcePerson.setRpinstitutename(rpinstitutename.trim());
        newResourcePerson.setRpofficeaddress(rpofficeaddress.trim());
        newResourcePerson.setRpresidentialaddress(rpresidentialaddress.trim());
        newResourcePerson.setRpofficephone(rpofficephone != null ? rpofficephone.trim() : "");
        newResourcePerson.setRpresidencephone(rpresidencephone != null ? rpresidencephone.trim() : "");
        newResourcePerson.setRpmobileno(rpmobileno.trim());
        newResourcePerson.setRpfax(rpfax != null ? rpfax.trim() : "");
        newResourcePerson.setRpresidencephone(rpresidencephone != null ? rpresidencephone.trim() : "");

        // setting moffices
        M_Offices newResourcePersonOffice = new M_Offices();
        newResourcePersonOffice.setOfficecode(user.getMoffices().getOfficecode());
        newResourcePerson.setMoffices(newResourcePersonOffice);

        // setting mqualificationsubjects
        if (qualificationsubjectcode != null && !qualificationsubjectcode.isBlank()) {
            Optional<M_QualificationSubjects> qualificationSubjects = mQualificationSubjectsService.findById(qualificationsubjectcode);
            if (qualificationSubjects.isEmpty())
                return ResponseEntity.badRequest().build();
            newResourcePerson.setMqualificationsubjects(qualificationSubjects.get());
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        // setting qualificationcode
        if (rpqualification.toLowerCase().equals("others")) {
            if (qcinput != null && !qcinput.isBlank()) {
                if (qualificationsService.checkQualificationExistByQualificationnameAndQualificationcategorycode(qcinput, rpqualificationcategory)) {
                    M_Qualifications newResourcePersonQualification = qualificationsService.findByQualificationnameAndQualificationcategorycode(qcinput, rpqualificationcategory);
                    
                    if (newResourcePersonQualification == null)
                        return ResponseEntity.badRequest().build();

                    newResourcePerson.setQualificationcode(newResourcePersonQualification);
                } else {
                    M_Qualifications newQualification = new M_Qualifications();
                    // assuming qualificationcategory already exists
                    M_QualificationCategories existingQualificationCategory = mQualificationCategoriesService.findByQualificationcategorycode(rpqualificationcategory);

                    if (existingQualificationCategory == null)
                        return ResponseEntity.badRequest().build();

                    newQualification.setMqualificationcategories(existingQualificationCategory);
                    newQualification.setQualificationname(qcinput);
                    newQualification.setQualificationcode("");

                    M_Qualifications savedQualification = null;
                    try {
                        if ((savedQualification = qualificationsService.saveQualificationDetails(newQualification)) != null) {
                            persistenceLogger.info("M_Qualifications with qualificationcode {} saved successfully by userid {}", savedQualification.getQualificationcode(), user.getUserid());
                            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualifications with qualificationcode " + savedQualification.getQualificationcode() + " saved successfully");
                            newResourcePerson.setQualificationcode(savedQualification);
                        } else
                            throw new PersistenceException();
                    } catch (Exception ex) {
                        persistenceLogger.error("M_Qualifications save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
                        audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_qualifications with qualificationcode save failed");

                        return ResponseEntity.ok("-1");
                    }                 }
            } else 
                return ResponseEntity.ok("21");
        } else {
            M_QualificationCategories newResourcePersonQualificationCategories = mQualificationCategoriesService.findByQualificationcategorycode(rpqualificationcategory);
            Optional<M_Qualifications> newResourcePersonQualifications = qualificationsService.findById(rpqualification);
            if (newResourcePersonQualifications.isEmpty() || newResourcePersonQualificationCategories == null)
                return ResponseEntity.badRequest().build();
            newResourcePersonQualifications.get().setMqualificationcategories(newResourcePersonQualificationCategories);
            newResourcePerson.setQualificationcode(newResourcePersonQualifications.get());
        }

        if (designationcodess.toLowerCase().equals("others")) {
            if (dinput != null && !dinput.isBlank()) {
                if (mDesignationsService.checkDesignationExistByDesignationname(dinput)) {
                    M_Designations newResourcePersonDesignation = mDesignationsService.findByDesignationname(dinput);
                    if (newResourcePersonDesignation == null)
                        return ResponseEntity.badRequest().build();    
                    newResourcePerson.setDesignationcode(newResourcePersonDesignation);
                } else {
                    M_Designations newDesignations = new M_Designations();
                    newDesignations.setDesignationname(dinput);
                    newDesignations.setIsparticipantdesignation("N");
                    M_Designations savedDesignations = null;
                    try {
                        if ((savedDesignations = mDesignationsService.saveDesignation(newDesignations)) != null) {
                            persistenceLogger.info("M_Designations with designationcode {} saved successfully by userid {}", savedDesignations.getDesignationcode(), user.getUserid());
                            audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_designations with designationcode " + savedDesignations.getDesignationcode() + " saved successfully");
                            newResourcePerson.setDesignationcode(savedDesignations);
                        } else
                            throw new PersistenceException();
                    } catch (Exception ex) {
                        persistenceLogger.error("M_Designations save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
                        audittrailService.logAuditTrail(auditMap, user.getUserid(), "m_designations save failed");

                        return ResponseEntity.badRequest().build();
                    }                 
                }    
            } else 
                return ResponseEntity.ok("23");
        } else {
            M_Designations newResourcePersonDesignations = mDesignationsService.getDesignation(designationcodess);
            if (newResourcePersonDesignations == null)
                return ResponseEntity.badRequest().build();
            newResourcePerson.setDesignationcode(newResourcePersonDesignations);
        }

        try {
            if ((newResourcePerson = mtResourcePersonsService.saveResourcePersons(newResourcePerson)) != null) {
                persistenceLogger.info("MT_ResourcePersons with rpslno {} saved successfully by userid {}", newResourcePerson.getRpslno(), user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_resourcepersons with rpslno " + newResourcePerson.getRpslno() + " saved successfully");

                return ResponseEntity.ok("2");
            } else
                throw new PersistenceException();
        } catch (RuntimeException ex) {
            persistenceLogger.error("MT_ResourcePersons save failed.\nMessage: {}\nUserid: {}", ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_resourcepersons save failed");
        }

        return ResponseEntity.ok("-1");
    }

     /*
     * Secured endpoint
     * endpoint exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * 'Map Resource Person' process (processcode = 9)
     */
    @GetMapping("/map")
    public String renderResourcePersonMapPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Map Resource Person, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 9)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Map Resource Person, " + request.getMethod(), user.getUserid()), "page");
        }

        switch (userRole) {
            case "A":
                model.addAttribute("layoutPath", "layouts/local-admin-layout");
                break;
            case "U":
                model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
                break;
        }

        model.addAttribute("fyList", mFinancialYearService.getFyByUsercode(user.getUsercode()));
        
        MT_ResourcePersons resourcePersons = new MT_ResourcePersons();
        model.addAttribute("mtresourceperson", resourcePersons);
        
        return "pages/map-resource-person";
    }

     /*
     * Secured endpoint
     * endpoint exclusive to role A (Local-admin) & U (Co-ordinator faculty)
     * Endpoint tied with 'Map Resource Person' process (processcode = 9)
     */
    @PostMapping("/map/save")
    public ResponseEntity<String> saveResourcePersonCourseMap(@RequestParam("programcode") String programcode, 
        @RequestParam("phaseid") String phaseid, @RequestParam("resourceperson") List<String> resourcePersons, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (RuntimeException ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        } 
        String userRole = user.getRole().getRoleCode().toUpperCase();
        
        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 9)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        // validating required parameters
        if (programcode == null || programcode.isBlank() || phaseid == null || phaseid.isBlank() || resourcePersons == null)
            return ResponseEntity.badRequest().build();

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            // saveResourcePersonsCourseMap() doesn't utilize the M_Programs() instance
            if (mtResourcePersonsService.saveResourcePersonsCourseMap(new M_Programs(), phaseid, resourcePersons)) {
                persistenceLogger.info("MT_ResourcePersons mapped successfully with M_Phases with phaseid {} by userid {}", programcode, user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_resourcepersons mapped successfully with m_phases with phaseid " + programcode);

                return ResponseEntity.ok("2");
            } else
                throw new PersistenceException();
        } catch (RuntimeException ex) {
            persistenceLogger.error("MT_ResourcePersons mapping failed with M_Phases of phaseid {}.\nMessage: {}\nUserid: {}", programcode, ex.getMessage(), user.getUserid(), ex);
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "mt_resourcepersons mapping failed with m_phases of phaseid " + programcode);
        }

        return ResponseEntity.ok("1");
    }

    /*
     * Public endpoint
     */
    @PostMapping("/list/phase")
    public ResponseEntity<?> getAllResourcePersonsWithPhase(@RequestParam("phaseid") String phaseid) {
        // validating phaseid
        if (phaseid == null || phaseid.isBlank())
            return ResponseEntity.badRequest().body("phaseid parameter is missing");
        if (phaseid.trim().length() > 6)
            return ResponseEntity.badRequest().body("Invalid phaseid");

        return ResponseEntity.ok(mtResourcePersonsService.getAllResourcePersonsWithPhase(phaseid));
    }

    /*
     * Public endpoint
     */
    @PostMapping("/list/course-phase")
    public ResponseEntity<?> getResourcePersonsByPhaseid(@RequestParam("phaseid") String phaseid) {
        // validaint phaseid
        if (phaseid == null || phaseid.isBlank())
            return ResponseEntity.badRequest().body("phaseid parameter is missing");
        if (phaseid.trim().length() > 6)
            return ResponseEntity.badRequest().body("Invalid phaseid");

        return ResponseEntity.ok(mtResourcePersonsService.getResourcePersonsByPhaseid(phaseid));
    }
}
