package com.nic.nerie.audittrail.controller;

import com.nic.nerie.audittrail.model.Audittrail;
import com.nic.nerie.audittrail.service.AudittrailService;
import com.nic.nerie.exceptions.MyAuthenticationCredentialsNotFoundException;
import com.nic.nerie.exceptions.MyAuthorizationDeniedException;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.service.MT_UserloginService;
import com.nic.nerie.utils.ExceptionUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin")
public class AudittrailController {
    private final MT_UserloginService mtUserloginService;
    private final AudittrailService audittrailService;

    @Autowired
    public AudittrailController(MT_UserloginService mtUserloginService, AudittrailService audittrailService) {
        this.mtUserloginService = mtUserloginService;
        this.audittrailService = audittrailService;
    }

    /*
     * Secured endpoint
     * Exclusive to role S (Admin)
     * Profile > Audit Trail
     */
    @GetMapping("/audittrail")
    public String getAuditTrail(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword", required = false) String keyword,
            HttpServletRequest request
    ) {
        if (userDetails == null) {
            throw new MyAuthenticationCredentialsNotFoundException(
                ExceptionUtil.generateUnAuthenticatedMessage(request.getRequestURI(), "Audit Trail, " + request.getMethod()), "page");
        }

        String username = userDetails.getUsername();
        MT_Userlogin currentUser = mtUserloginService.findByUserId(username);

        if ("S".equals(currentUser.getUserrole())) {
            int currentPage = Math.max(page, 1);
            PageRequest pageable = PageRequest.of(currentPage - 1, size);

            Page<Audittrail> auditPage = audittrailService.getAuditTrailPage(keyword, pageable);

            model.addAttribute("auditPage", auditPage);
            model.addAttribute("keyword", keyword);

            // Pagination
            int totalPages = auditPage.getTotalPages();
            if (totalPages > 0) {
                int windowSize = 5;
                int startPage = Math.max(1, currentPage - windowSize / 2);
                int endPage = Math.min(totalPages, startPage + windowSize - 1);

                if (endPage == totalPages) {
                    startPage = Math.max(1, endPage - windowSize + 1);
                }

                List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                        .boxed()
                        .collect(Collectors.toList());
                model.addAttribute("pageNumbers", pageNumbers);
            }

            return "pages/admin/audit-trail";
        } else {
            throw new MyAuthorizationDeniedException(
                ExceptionUtil.generateAuthorizationDeniedMessage(request.getRequestURI(), "Audit Trail, " + request.getMethod(), currentUser.getUserid()), "page");
        }
    }
}
