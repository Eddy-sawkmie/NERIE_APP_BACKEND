package com.nic.nerie.t_studymaterials.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.m_subjects.service.M_SubjectService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_faculties.model.T_Faculties;
import com.nic.nerie.t_faculties.service.T_FacultiesService;
import com.nic.nerie.t_studymaterials.model.T_StudyMaterials;
import com.nic.nerie.t_studymaterials.service.T_StudyMaterialsService;
import com.nic.nerie.utils.ExceptionUtil;
import com.nic.nerie.utils.UtilCommon;

import jakarta.persistence.PersistenceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/study-materials")
public class T_StudyMaterialsController {
    private final T_StudyMaterialsService tStudyMaterialsService;
    private final MT_UserloginService mtUserloginService;
    private final M_SubjectService mSubjectService;
    private final T_FacultiesService tFacultiesService;
    private final M_ProcessesService mProcessesService;
    private final AudittrailService audittrailService;
    private static final Logger logger = LoggerFactory.getLogger(T_StudyMaterialsController.class);
    private static final Logger persistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");

    @Autowired
    public T_StudyMaterialsController(
        T_StudyMaterialsService tStudyMaterialsService, 
        MT_UserloginService mtUserloginService, 
        M_SubjectService mSubjectService, 
        T_FacultiesService tFacultiesService,
        M_ProcessesService mProcessesService,
        AudittrailService audittrailService) {
        this.tStudyMaterialsService = tStudyMaterialsService;
        this.mtUserloginService = mtUserloginService;
        this.mSubjectService = mSubjectService;
        this.tFacultiesService = tFacultiesService;
        this.mProcessesService = mProcessesService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * 'Upload Study Materials' process (processcode = 37)
     */
    @GetMapping("/upload-study-materials")
    public String renderUploadStudyMaterialPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Upload Study Materials, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 37) &&
            user.getIsfaculty().equalsIgnoreCase("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Upload Study Materials, " + request.getMethod(), user.getUserid()), "page");
        }

        T_Faculties faculty = tFacultiesService.getFaculty(user.getUsercode());
        if (faculty == null) {
            model.addAttribute("msg", "No faculty found for this user.\n" + "Please create one to proceed.");
            return "pages/error/500";
        }

        List<Object[]> subjectList = mSubjectService.getSubjectsList(user.getUsercode());
        List<T_StudyMaterials> studyMaterials = tStudyMaterialsService.getAllStudyMaterials(faculty.getFacultyid());

        model.addAttribute("subs", subjectList);
        model.addAttribute("allmaterials", studyMaterials);
        model.addAttribute("studymaterials", new T_StudyMaterials());

        return "pages/upload-study-materials";
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role A (Local-admin) & U (Coordinator-faculty)
     * isfaculty = 1
     * Endpoint tied with 'Upload Study Materials' process (processcode = 37)
     */
    @PostMapping("/upload-study-materials")
    @ResponseBody
    public String uploadStudyMaterialPost(@ModelAttribute("studymaterials") T_StudyMaterials materials,
                                          @RequestParam(name = "file1", required = false) MultipartFile file,
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
            mProcessesService.isProcessGranted(user.getUsercode(), 37) &&
            user.getIsfaculty().equalsIgnoreCase("1")
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        HashMap<String, String> auditMap = UtilCommon.getClientDetails(request);
        try {
            T_Faculties faculty = tFacultiesService.getFaculty(user.getUsercode());

            if (faculty == null) {
                return "-1";
            }

            materials.setFacultyid(faculty);
            materials.setUploaddate(new java.util.Date());

            if (file != null && !file.isEmpty()) {
                materials.setReldoc(file.getBytes());
            } else {
                if (materials.getStudymaterialid() != null && !materials.getStudymaterialid().isEmpty()) {
                    T_StudyMaterials existing = tStudyMaterialsService.getStudyMaterialDocument(materials.getStudymaterialid());
                    if (existing != null && existing.getReldoc() != null) {
                        materials.setReldoc(existing.getReldoc());
                    }
                }
            }

            String result = tStudyMaterialsService.uploadStudyMaterial(materials);
            if (result != null && !result.isEmpty() && !result.equals("-1")) {
                persistenceLogger.info("T_StudyMaterials saved successfully by userid {}", user.getUserid());
                audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studymaterials saved successfully");

                return "1"; // success
            } else
                throw new PersistenceException();
        } catch (IOException e) {
            logger.error(e.toString());

            return "-1";
        } catch (Exception e) {
            persistenceLogger.error("T_StudyMaterials save failed.\nMessage: {}\nUserid: {}", e.getMessage(), e, user.getUserid());
            audittrailService.logAuditTrail(auditMap, user.getUserid(), "t_studymaterials save failed");

            return "-1"; //failure
        }
    }

    @GetMapping("/getStudyMaterialsListSubject")
    @ResponseBody
    public List<Object[]> getStudyMaterialsBySubject(
            @RequestParam("subjectcode") String subjectcode,
            HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        T_Faculties faculty = tFacultiesService.getFaculty(user.getUsercode());

        return tStudyMaterialsService.getStudyMaterialsListSubjectFaculty(subjectcode, faculty.getFacultyid());
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive to role T (Student)     
     * Download Study Materials
     */
    @GetMapping("/viewStudyMaterialDocument")
    public void viewStudyMaterialDocument(HttpServletResponse response, @RequestParam("sid") String sid) throws IOException {
        T_StudyMaterials sm = tStudyMaterialsService.getStudyMaterialDocument(sid);

        if (sm != null && sm.getReldoc() != null) {
            byte[] fileContent = sm.getReldoc();
            response.reset(); // Good practice to reset response
            response.setContentType("application/pdf");
            response.setContentLength(fileContent.length);

            try (OutputStream out = response.getOutputStream()) {
                out.write(fileContent);
                out.flush();
            } catch (IOException e) {
                System.err.println("Error writing file to output stream. " + e.getMessage());
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Study material document not found.");
        }
    }

    @GetMapping("/getstudymaterials")
    public ResponseEntity<List<T_StudyMaterials>> getStudyMaterials(@RequestParam("subjectcode") String subjectcode) {
        return ResponseEntity.ok(tStudyMaterialsService.getStudyMaterialsListSubject(subjectcode));
    }


// This returns proper named JSON instead of raw Object[] arrays,
// so Flutter never has to guess column indices again.
// ═══════════════════════════════════════════════════════════════════════════

    @GetMapping("/getStudyMaterialsListSubjectJson")
    @ResponseBody
    public List<Map<String, Object>> getStudyMaterialsBySubjectJson(
            @RequestParam("subjectcode") String subjectcode,
            HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(), request.getMethod()), "json");
        }
        T_Faculties faculty = tFacultiesService.getFaculty(user.getUsercode());

        // Reuse the same service method — just wrap each Object[] into a Map
        List<Object[]> rows = tStudyMaterialsService
                .getStudyMaterialsListSubjectFaculty(subjectcode, faculty.getFacultyid());

        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            // Log every index so you can confirm the mapping
            for (int i = 0; i < row.length; i++) {
                System.out.println("ROW[" + i + "] = " + row[i]
                        + " (" + (row[i] == null ? "null" : row[i].getClass().getSimpleName()) + ")");
            }

            for (int i = 0; i < row.length; i++) {
                map.put("col" + i, row[i]);
            }
            result.add(map);
        }
        return result;
    }


    @GetMapping("/getStudentSubjects")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getStudentSubjects(HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(
                            request.getRequestURI(), request.getMethod()), "json");
        }

        // Get subjects for this student based on their usercode
        List<Object[]> rows = mSubjectService.getSubjectsList(user.getUsercode());

        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            // Log to confirm indices
            for (int i = 0; i < row.length; i++) {
                logger.info("SUBJECT ROW[{}] = {}", i, row[i]);
            }
            // Adjust indices after checking logs
            map.put("subjectcode", row[0] != null ? row[0].toString() : "");
            map.put("subjectname", row[1] != null ? row[1].toString() : "");
            result.add(map);
        }

        return ResponseEntity.ok(result);
    }

}
