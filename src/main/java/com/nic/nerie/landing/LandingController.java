package com.nic.nerie.landing;

import com.nic.nerie.captcha.service.CaptchaService;
import com.nic.nerie.m_financialyear.service.M_FinancialYearService;
import com.nic.nerie.mt_programdetails.service.MT_ProgramDetailsService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LandingController {
    private final MT_ProgramDetailsService mtProgramDetailsService;
    private final CaptchaService captchaService; // 1. Add Service Field
    private final M_FinancialYearService mFinancialYearService;

    @Autowired
    public LandingController(MT_ProgramDetailsService mtProgramDetailsService,
                             CaptchaService captchaService, // 2. Update Constructor
                            M_FinancialYearService mFinancialYearService) { 
        this.mtProgramDetailsService = mtProgramDetailsService;
        this.captchaService = captchaService;
        this.mFinancialYearService = mFinancialYearService;
    }

    /* Captcha available on ALL pages in this controller */
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        // This ensures the login modal has a valid captcha immediately on page load
        if (!model.containsAttribute("captchaPrincipal")) {
            model.addAttribute("captchaPrincipal", captchaService.generateNewCaptcha());
        }
    }

    /*
     * Public endpoint
     */
    @GetMapping(value={"/index","/"})
    public String renderIndexPage(Model model) {
        model.addAttribute("currentPage", "home");
        model.addAttribute("ongoingprogramlist", mtProgramDetailsService.getOngoingProgramList(0, 3, 0));
        model.addAttribute("upcomingprogramlist", mtProgramDetailsService.getUpcomingProgramList(0, 3, 0));
        model.addAttribute("completedprogramlist", mtProgramDetailsService.getCompletedProgramList(0, 3, 0));
        model.addAttribute("countongoing", mtProgramDetailsService.getCountOngoingProgram());
        model.addAttribute("countupcoming", mtProgramDetailsService.getCountUpcomingProgram());
        model.addAttribute("countcompleted", mtProgramDetailsService.getCountCompletedProgram());

//        model.addAttribute("Ongoing", mtProgramDetailsService.getDashboardOngoing("132", "2", "A"));
//        model.addAttribute("Upcoming", mtProgramDetailsService.getDashboardUpcoming("132", "2", "A"));
//        model.addAttribute("Completed", mtProgramDetailsService.getCompletedProgDetailsForPublic("2"));

        model.addAttribute("Ongoing", mtProgramDetailsService.getLandingDashboardOngoing());
        model.addAttribute("Upcoming", mtProgramDetailsService.getLandingDashboardUpcoming());
        // model.addAttribute("Completed", mtProgramDetailsService.getLandingCompletedProgDetails());
        
        // 1. Fetch the Financial Year list
        List<Object[]> fylist = mFinancialYearService.getfy();
        model.addAttribute("fylist", fylist);

        // 2. 
        // The list is ordered newer-to-older (DESC), so we use fylist.get(fylist.size() - 1)
        if (fylist != null && !fylist.isEmpty()) {
            Object[] defaultFy = fylist.get(0); 
            
            // fyvalue is at index 4 (e.g., "2024-04##2025-03")
            String fyValue = (String) defaultFy[4]; 
            
            if (fyValue != null && fyValue.contains("##")) {
                String[] parts = fyValue.split("##");
                String fystart = parts[0]; // "2024-04"
                String fyend = parts[1];   // "2025-03"
                
                // 3. Pass the split values into your query
                model.addAttribute("Completed", mtProgramDetailsService.getLandingCompletedProgDetailsByFy(fystart, fyend));
            } else {
                model.addAttribute("Completed", null); // Fallback if data is malformed
            }
        } else {
            model.addAttribute("Completed", null); // Fallback if list is empty
        }

        return "pages/landing/home";
    }

    /*
     * Public endpoint
     */
    @GetMapping("/about")
    public String renderAboutPage(Model model) {
        model.addAttribute("currentPage", "about");
        return "pages/landing/about";
    }

    /*
     * Public endpoint
     */
    @GetMapping("/about/blog/{id}")
    public String renderBlogPage(@PathVariable String id, Model model) {

        model.addAttribute("currentPage", "about");

        return "pages/landing/blog" + id;
    }

    /*
     * NOTE:
     * Participant Registration logic is located in T_ParticipantsController.
     * See endpoints: /participants/register (GET/POST)
     */

    /*
     * NOTE:
     * Faculty Research List logic is located in T_FacultiesController.
     * See endpoints: /faculty/research-list (GET/POST)
     */

    /*
     * NOTE:
     * Login logic is located in AuthenticationController.
     * See endpoints: /login (GET/POST)
     */

    @GetMapping("/programs/completed-by-fy")
    @ResponseBody
    public ResponseEntity<List<Object[]>> getCompletedProgramsByFy(
            @RequestParam("fystart") String fystart,
            @RequestParam("fyend") String fyend) {
        
        // Call the repository method we just created
        List<Object[]> programs = mtProgramDetailsService.getLandingCompletedProgDetailsByFy(fystart, fyend);
        return ResponseEntity.ok(programs);
    }
}
