package com.nic.nerie.report.controller;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_offices.service.M_OfficesService;
import com.nic.nerie.m_processes.service.M_ProcessesService;
import com.nic.nerie.mt_programdetails.service.MT_ProgramDetailsService;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.t_programtimetable.model.T_ProgramTimeTable;
import com.nic.nerie.utils.ExceptionUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final MT_UserloginService mtUserloginService;
    private final M_OfficesService mOfficesService;
    private final MT_ProgramDetailsService mtProgramDetailsService;
    private final DataSource dataSource;
    private final M_ProcessesService mProcessesService;

    @Autowired
    public ReportController(
        MT_UserloginService mtUserloginService,
        M_OfficesService mOfficesService, 
        MT_ProgramDetailsService mtProgramDetailsService, 
        DataSource dataSource,
        M_ProcessesService mProcessesService) {
        this.mtUserloginService = mtUserloginService;
        this.mOfficesService = mOfficesService;
        this.mtProgramDetailsService = mtProgramDetailsService;
        this.dataSource = dataSource;
        this.mProcessesService = mProcessesService;
    }

    /*
     * Secured endpoint
     * Exclusive to role S (Admin)
     * Report generation > Office List
     */
    @GetMapping("/report-office-list")
    public String showReportOfficeList(Model model, HttpServletRequest request) {
        MT_Userlogin currentUser;
        try {
            currentUser = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Office List, " + request.getMethod()), "page");
        }
        
        if ("S".equals(currentUser.getUserrole())) {
            List<M_Offices> officeList = mOfficesService.getOfficeList();
            model.addAttribute("moffices", new M_Offices());
            model.addAttribute("officelist", officeList);
            model.addAttribute("reportTitle", "Office Wise Details List");
            model.addAttribute("reportType", 1);
            
            return "pages/report-list";
        } else {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Office List, " + request.getMethod(), currentUser.getUserid()), "page");
        }
    }

    /*
     * Secured endpoint
     * Exclusive to role S (Admin)
     * Report generation > Coordinator List
     */
    @GetMapping("/report-coordinator-list")
    public String showReportCoordinatorList(Model model, HttpServletRequest request) {
        MT_Userlogin currentUser;
        try {
            currentUser = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Coordinator List, " + request.getMethod()), "page");
        }

        if ("S".equals(currentUser.getUserrole())) {
            List<M_Offices> officeList = mOfficesService.getOfficeList();
            model.addAttribute("moffices", new M_Offices());
            model.addAttribute("officelist", officeList);
            model.addAttribute("reportTitle", "Office Wise Coordinator List");
            model.addAttribute("reportType", 2);
            return "pages/report-list";
        } else {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Coordinator List, " + request.getMethod(), currentUser.getUserid()), "page");
        }
    }

    /*
     * Secured endpoint
     * Exclusive to role S (Admin)
     * Report generation > Program List
     */
    @GetMapping("/report-course-list")
    public String showReportCourseReportList(Model model, HttpServletRequest request) {
        MT_Userlogin currentUser;
        try {
            currentUser = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Program List, " + request.getMethod()), "page");
        }
        
        if ("S".equals(currentUser.getUserrole())) {
            List<M_Offices> officeList = mOfficesService.getOfficeList();
            model.addAttribute("moffices", new M_Offices());
            model.addAttribute("officelist", officeList);
            model.addAttribute("reportTitle", "Office Wise Program List");
            model.addAttribute("reportType", 3);
            return "pages/report-list";
        } else {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Program List, " + request.getMethod(), currentUser.getUserid()), "page");
        }
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local Admin), U (Coordinator-Faculty) & Z (Principal-Director)
     * 'Participant & Resource Person Report' process (processcode = 10)
     */
    @GetMapping("/report-participant-resource-list")
    public String showParticipantResourceReportPage(@ModelAttribute("listrep") T_ProgramTimeTable tprogram,
                                                    @RequestParam(value = "fystart", required = false) String fystart,
                                                    @RequestParam(value = "fyend", required = false) String fyend,
                                                    HttpServletRequest request, 
                                                    Model model) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                    ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Participant & Resource Person Report, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U", "Z").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 10)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Participant & Resource Person Report, " + request.getMethod(), user.getUserid()), "page");
        }
            
        model.addAttribute("fylist", mtProgramDetailsService.getReportFinancialYearLA(user.getMoffices().getOfficecode()));

        if ("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/principal-director-layout");
        }

        return "pages/report-participant-resource-list";
    }

    /*
     * Secured endpoint
     * Endpoint exclusive to role A (Local Admin), U (Coordinator-Faculty) & Z (Principal-Director)
     * 'Program Schedule Report' process (processcode = 24)
     */
    @GetMapping("/report-program-schedule")
    public String showProgramScheduleReportPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Prgram Schedule Report, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U", "Z").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 24)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Prgram Schedule Report, " + request.getMethod(), user.getUserid()), "page");
        }

        model.addAttribute("fylist",
                mtProgramDetailsService.getReportFinancialYearLA(user.getMoffices().getOfficecode()));
        model.addAttribute("programlist", mtProgramDetailsService.listProgramsForTimeTable(user.getUsercode(),
                user.getMoffices().getOfficecode(), user.getUserrole()));

        if ("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        } else {
            model.addAttribute("layoutPath", "layouts/principal-director-layout");
        }

        return "pages/report-program-schedule";
    }

    /*
     * Secured endpoint
     * Endpiont exclusive to role A & U
     * 'Attendance Report' process (processcode = 33)
     */
    @GetMapping("/report-attendance-list")
    public String showAttendanceReportPage(Model model, HttpServletRequest request) {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Attendance Report, " + request.getMethod()), "page");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 33)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Attendance Report, " + request.getMethod(), user.getUserid()), "page");
        }

        model.addAttribute("fylist", mtProgramDetailsService.getReportFinancialYearLA(user.getMoffices().getOfficecode()));

        if("A".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/local-admin-layout");
        } else if ("U".equals(user.getUserrole())) {
            model.addAttribute("layoutPath", "layouts/coordinator-faculty-layout");
        }

        return "pages/report-attendance-list";
    }

    @PostMapping("/get-report-financialyear")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getReportFinancialYear(
            @RequestParam(value = "officecode", required = false) String officecode) {

        if (officecode == null || officecode.trim().isEmpty() || "-1".equals(officecode)) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }

        try {
            List<Object[]> financialYears = mtProgramDetailsService.getFinancialYearsByOfficeCode(officecode);
            return ResponseEntity.ok(financialYears);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/printProgramDetails")
    public void printProgramDetails(
            @RequestParam("programcode") String programcode,
            HttpServletResponse response) throws Exception {

        System.out.println("Program Code: " + programcode);

        // Path to store images
        String imagePath = new ClassPathResource("images").getFile().getAbsolutePath();

        try (Connection connection = dataSource.getConnection()) {

            // Load precompiled JasperReport (.jasper)
            InputStream reportStream = new ClassPathResource("reports/programDetails.jasper").getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            // Prepare parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("programcode", programcode);
            parameters.put("dynamicpath", imagePath);

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Set response headers
            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Program_Details.pdf\"");

            // Export to PDF
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report");
        }
    }

    @GetMapping("/Report")
    public void generateAdaptedReport(
            @RequestParam String status,
            @RequestParam String finyearstart,
            @RequestParam String finyearend,
            @RequestParam String financialyear,
            @RequestParam String officecode,
            HttpServletResponse response) throws Exception {

        String imagePath = new ClassPathResource("images").getFile().getAbsolutePath();

        String reportName = switch (status) {
            case "1" -> "adminofficewisedetaillist.jasper";
            case "2" -> "coursecoordinatorsofficewise.jasper";
            case "3" -> "pastcourselistofficewise.jasper";
            case "4" -> "ongoingcourselistofficewise.jasper";
            case "5" -> "upcomingcourselistofficewise.jasper";
            default -> {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status parameter");
                yield null;
            }
        };

        if (reportName == null) return;

        try (Connection connection = dataSource.getConnection()) {
            InputStream reportStream = new ClassPathResource("reports/" + reportName).getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("finyearstart", finyearstart);
            parameters.put("finyearend", finyearend);
            parameters.put("financialyear", financialyear);
            parameters.put("officecode", officecode);
            parameters.put("dynamicpath", imagePath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Report.pdf\"");

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report");
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive for role A (Local Admin), U (Coordinator-Faculty) & Z (Principal-Director)
     * Endpoint tied with 'Participant & Resource Person Report' process (processcode = 10)
     */
    @GetMapping("/ReportLA")
    public void reportLA(
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "phaseid", required = true) String phaseid,
            @RequestParam(value = "fystart", required = false) String fystart,
            @RequestParam(value = "fyend", required = false) String fyend,
            @RequestParam(value = "participantstatus", required = false) String participantstatus,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U", "Z").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 10)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }
        
        String imagePath = new ClassPathResource("images").getFile().getAbsolutePath();
        String reportName = switch (status) {
            case "1" -> "appliedparticipantslist.jasper";
            case "2" -> "approvedparticipantandpRPlist.jasper";
            case "3" -> "rejectedparticipantlist.jasper";
            case "4" -> "resourcepersonlist.jasper";
            case "5" -> "participantresourcepersonreport.jasper";
            case "6" -> "blankattandanceparticipants.jasper";
            case "7" -> "blankattendanceforresourceperson.jasper";
            case "8" -> "participantFYstatistics.jasper";
            default -> {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status parameter");
                yield null;
            }
        };

        if (reportName == null) return;

        try (Connection connection = dataSource.getConnection()) {
            InputStream reportStream = new ClassPathResource("reports/" + reportName).getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            if(status.equals("8")) {
                parameters.put("participantstatus", participantstatus);
                parameters.put("fystart", fystart);
                parameters.put("fyend", fyend);
            }else{
                parameters.put("phaseid", phaseid);
            }
            parameters.put("dynamicpath", imagePath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Report.pdf\"");

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report");
        }
    }

    /*
     * secured endpoint
     * this endpoint is exclusive for role A (local admin), U (coordinator-faculty) & Z (Principal-Director)
     * Endpoint tied with 'Program Schedule Report' process (processcode = 24)
     */
    @GetMapping("/scheduleReport")
    public void scheduleReport(
            @RequestParam(value = "phaseid", required = true) String phaseid,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        MT_Userlogin user;
        try {
            user = mtUserloginService.getUserloginFromAuthentication();
        } catch (Exception ex) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), request.getMethod()), "json");
        }
        String userRole = user.getRole().getRoleCode().toUpperCase();

        if (!(
            List.of("A", "U", "Z").contains(userRole) &&
            mProcessesService.isProcessGranted(user.getUsercode(), 24)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Prgram Schedule Report, " + request.getMethod(), user.getUserid()), "page");
        }

        String imagePath = new ClassPathResource("images").getFile().getAbsolutePath();
        String reportName = "programscheduletest.jasper";

        try (Connection connection = dataSource.getConnection()) {
            InputStream reportStream = new ClassPathResource("reports/" + reportName).getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("phaseid", phaseid);
            parameters.put("dynamicpath", imagePath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ScheduleReport.pdf\"");

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error generating schedule report: " + e.getMessage());
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive for role A (Local Admin) and U (Coordinator-Faculty)
     * Endpoint tied with 'Attendance Report' process (processcode = 33)
     */
    @GetMapping("/ReportAttendance")
    public void reportAttendance(
            @RequestParam(value = "status", required = true) String status,
            @RequestParam(value = "phaseid", required = true) String phaseid,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
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
            mProcessesService.isProcessGranted(user.getUsercode(), 33)
        )) {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), request.getMethod(), user.getUserid()), "json");
        }

        String imagePath = new ClassPathResource("images").getFile().getAbsolutePath();

        String reportName = switch (status) {
            case "1" -> "attagg_participant.jasper";
            case "2" -> "attdetails.jasper";
            case "3" -> "blankattendanceforresourceperson.jasper";
            default -> {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status parameter: " + status);
                yield null;
            }
        };

        if (reportName == null) return;

        try (Connection connection = dataSource.getConnection();
             InputStream reportStream = new ClassPathResource("reports/" + reportName).getInputStream()) {

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("phaseid", phaseid);
            parameters.put("dynamicpath", imagePath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"AttendanceReport.pdf\"");

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error generating attendance report: " + e.getMessage());
        }
    }

    /*
     * Secured endpoint
     * This endpoint is exclusive for role A (Local Admin) and U (Coordinator-Faculty)
     */
    @GetMapping("/publicReport")
    public void publicReport(
            @RequestParam(value = "phaseid", required = true) String phaseid,
            HttpServletResponse response) throws Exception {
        String imagePath = new ClassPathResource("images").getFile().getAbsolutePath();
        String reportName = "detailcourse.jasper";

        try (Connection connection = dataSource.getConnection()) {
            InputStream reportStream = new ClassPathResource("reports/" + reportName).getInputStream();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("phaseid", phaseid);
            parameters.put("dynamicpath", imagePath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            response.setContentType(MediaType.APPLICATION_PDF_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"Report.pdf\"");

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
            exporter.exportReport();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error generating schedule report: " + e.getMessage());
        }
    }

    //flutter endpoints
    @GetMapping("/api/reports/offices")
    @ResponseBody
    public List<M_Offices> getOfficeList() {
        return mOfficesService.getOfficeList();
    }
}
