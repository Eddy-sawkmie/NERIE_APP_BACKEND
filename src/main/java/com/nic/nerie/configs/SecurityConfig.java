package com.nic.nerie.configs;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nic.nerie.configs.security.filters.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request -> request
                        // Single Authority Routes

                        // Coordinator
                        .requestMatchers("/faculty-research-profile/post", "/research-paper/delete","/research-paper/update","/research-paper/add")
                        .hasAuthority("U")
                        
                        // Participant
                        .requestMatchers(
                                "/participant/edit-profile", "/participant/update-profile", "/program/accepted-rejected-programs",
                                "/program/save-accept-program", "/program/save-reject-program", "/program/my-programs", "/program/apply-for-programs", "/program/apply-program-by-participant",
                                "/participant/feedback/write-overall-feedback", "/participant/feedback/save-overall-feedback",
                                "/pre-post-test/view", "/pre-post-test/answer/submit", "/pre-post-test/view-scores", "/program-materials/download-material").hasAuthority("P")

                        // Admin
                        .requestMatchers(
                                "/offices/createoffice", "/offices/saveOfficeDetails",
                                "/reports/report-office-list", "/reports/report-coordinator-list", "/reports/report-course-list",
                                "/admin/audittrail", "/role-processes/map", "/role-processes/save", "/role-processes/getProcessesByRole").hasAuthority("S")
                        // Student
                        .requestMatchers(
                                "/subjects/viewsubjects", "/feedbacks/postsubjectfeedback",
                                "/assignments/viewassignments", "/assignments/viewassignmentsubmission", "/assignments/edit-student-assignment",
                                "/subjects/viewstudymaterials",
                                "/attendance/viewattendance", "/attendance/getattendance",
                                "/student-leaves/application", "/student-leaves/submit-application",
                                "/students/profile", "/students/info", "/assignments/uploadstudentassignment",
                                "/internal-evaluation-marks/viewmarks", "/internal-evaluation-marks/my-internal-marks").hasAuthority("T")
                        // Principal-Director
                        .requestMatchers(
                                "/program/principal-director/manage", "/program-details/principal-director/accept",
                                "/program-details/principal-director/reject", "/program-details/principal-director/delete",
                                "/notifications/manage", "/notifications/upload", "/notifications/delete").hasAuthority("Z")
                        // Multiple Authority Routes
                        // Local Admin + Coordinator-Faculty
                        .requestMatchers(
                                "/course-categories/manage", "/course-categories/save",
                                "/qualification-subjects/manage", "/qualification-subjects/save", "/qualification-subjects/map", "/qualification-subjects/map/save",
                                "/venue-rooms/manage", "/venue-rooms/save",
                                "/holidays/init", "/holidays/save", "/holidays/remove",
                                "/program/manage", "/program/inst/save", "/program/batch/save", "/activities/save",
                                "/resource-persons/create", "/resource-persons/save", "/resource-persons/map", "/resource-persons/map/save",
                                "/program-materials/manage", "/program-materials/save", "/program-members/get-program-members", "/program-details/update", "/program-details/save", "/program-details/form-data", // New Endpoints
                                "/timetable/program-timetable/create", "/timetable/program-timetable/save",
                                "/participant/manage", "/participant/create", "/participant/remove", "/participant/attendance/manage", "/participant/attendance/save",
                                "/reports/report-attendance-list", "/reports/ReportAttendance",
                                "/course-categories/create-academic-courses", "/course-academics/saveMapDepartmentCourse",
                                "/faculties/register-faculties", "/faculties/createEditFaculty",  "/faculties/by-courses",
                                "/students/manage", "/students/save", "/students/promotion-student-list", "/students/promoteStudent",
                                "/student-leaves/student-leave-reports", "/student-leaves/ReportStudentLeaveList",
                                "/attendance/upload-attendance", "/attendance/view-student-attendance",
                                "/attendance/faculty-subjects",
                                "/attendance/getStudentsListBasedOnSubjectCode",
                                "/study-materials/upload-study-materials", "/departments/saveDepartments", "/course-academics/getListOfCourse",   "/course-categories/getAllJson",
                                "/departments/getAllJson",
                                "/departments/api/list",
                                "/tests/create-tests", "/tests/saveTestDetails",
                                "/internal-evaluation-marks/upload-internal-evaluation-marks", "/internal-evaluation-marks/saveTestDetails", "/internal-evaluation-marks/by-subject",
                                "/assignments/upload-assignment", "/assignments/editAssignment", "/assignments/view-submitted-assignment",
                                "/assignments/viewStudentUploadAssignmentDocument", "/assignments/saveStudentAssignmentMarks", "/assignments/students-by-subject",
                                "/pre-post-test/create", "/pre-post-test/save", "/pre-post-test/questions/count", "/pre-post-test/questions/save",
                                "/program/accept-reject-course-participant", "/program/participant-details",
                                "/program/getMyprogramsacceptedAdmin", "/program/getMyprogramsrejectedAdmin", "/program/getOngoingAdmin", "/program/getUpcomingAdmin", "/program/getClosedAdmin",
                                "/program/getMyprogramsacceptedCoor", "/program/getMyprogramsrejectedCoor", "/program/getOngoingCoor", "/program/getUpcomingCoor", "/program/getClosedCoor",
                                "/program/program-participant-list-details","/participants", "/participants/data", "/alumni/manage", "/alumni/save").hasAnyAuthority("A", "U")

                        // Local Admin + Admin
                        .requestMatchers("/designations/manage", "/designations/save").hasAnyAuthority("A", "S")
                        // Student + Coordinator-Faculty
                        .requestMatchers("/assignments/viewAssignmentDocument").hasAnyAuthority("T", "U")
                        // Local Admin + Principal-Director
                        .requestMatchers(
                                "/program/close", "/program-details/phase/close",
                                "/program/reopen", "/program/reopen-phase").hasAnyAuthority("A", "Z")
                        // Local Admin + Admin + Coordinator-Faculty
                        .requestMatchers(
                                "/venues/manage", "/venues/save",
                                "/qualifications/manage", "/qualifications/save",
                                "/subjects/create-academic-subjects", "/subjects/saveNewSubject", "/reports/Report",
                                "/qualification-subjects/get-mp-subjects").hasAnyAuthority("A", "S", "U")
                        // Local Admin + Coordinator-Faculty + Principal-Director
                        .requestMatchers(
                                "/student-leaves/approve-student-leave", "/student-leaves/approveLeaveApplication", "/student-leaves/rejectLeaveApplication",
                                "/student-leaves/view-leave-application-details", "/student-leaves/ReportStudentLeaveList", "/reports/report-program-schedule",
                                "/reports/scheduleReport", "/reports/report-participant-resource-list", "/reports/ReportLA", "/student-leaves/view-student-leave-history",
                                "/student-leaves/deleteStudentLeave").hasAnyAuthority("A", "U", "Z")
                        // Local Admin + Admin + Principal-Director
                        .requestMatchers("/users/manage", "/users/save").hasAnyAuthority("A", "S", "Z")
                        // Admin + Student + Coordinator-Faculty
                        .requestMatchers("/study-materials/viewStudyMaterialDocument").hasAnyAuthority("S", "T", "U")
                        // Local Admin + Student + Coordinator-Faculty + Principal-Director
                        .requestMatchers("/student-leaves/view-approval").hasAnyAuthority("A", "T", "U", "Z")
                        //Coordinator-Faculty + Participant
                        .requestMatchers( "/participant/feedback/daily-feedback/list", "/participant/feedback/daily-feedback/get", "/participant/feedback/view-overall-feedback", "/participant/feedback/overall-feedback/list").hasAnyAuthority( "U", "P")
                        //Coordinator-Faculty + Student + Participant
                        .requestMatchers("/notifications/mynotifications").hasAnyAuthority("U", "T", "P")
                        // Public Routes
                        // Landing Routes
                        .requestMatchers(
                                "/","/index", "/about", "/login", "/about/blog/**", "/captcha/**", "/participants/**",
                                "/program-details/getMoreOngoingProgramList", "/program-details/getMoreUpcomingProgramList", "/program-details/getMoreUpcomingProgramList",
                                "/reports/publicReport", "/program-details/getMoreCompletedProgramList", "/faculties/research-list", "/faculties/research-details",
                                "/program-details/getAllProgramDetailsBasedOnProgramCode", "/phase-more-details/getPhaseMoreDetailsBasedOnPhaseId", "/activities/getActivityBasedOnPhaseId",
                                "/users/forget-password","/users/check-user-role", "/users/login-reset-password","/programs/completed-by-fy","/program-materials/list").permitAll()
                        // Static Resources
                        .requestMatchers("/resources/**", "/static/**", "/lily/**", "/tempscripts/**", "/vendor/**", "/fontawesome-free", "/webfonts/**", "/assets/**","/css/**","/images/**","/js/**").permitAll()
                        // Error routes
                        .requestMatchers("/error/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("neriejwt")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // Check if the request is an API call (e.g. from Flutter)
                            String authHeader = request.getHeader("Authorization");
                            String acceptHeader = request.getHeader("Accept");

                            boolean isApiRequest = (authHeader != null && authHeader.startsWith("Bearer ")) ||
                                    (acceptHeader != null && acceptHeader.contains("application/json"));

                            if (isApiRequest) {
                                // For Flutter: Return 200 OK JSON
                                response.setStatus(HttpServletResponse.SC_OK);
                                response.setContentType("application/json");
                                response.getWriter().write("{\"message\": \"Logout successful\"}");
                                response.getWriter().flush();
                            } else {
                                // For Web App: Redirect to index
                                response.sendRedirect("/index");
                            }
                        })
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}