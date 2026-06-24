// package com.nic.nerie.m_phases;

// import com.nic.nerie.m_phases.service.M_PhasesService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;

// import java.util.List;

// @Controller
// @RequestMapping("/test")
// public class PhasesTestController {
//     @Autowired
//     private M_PhasesService mPhasesService;

//     @GetMapping("/comp-phases")
//     public ResponseEntity<List<Object[]>> testRecentlyCompletedPhases() {
//         return ResponseEntity.ok(mPhasesService.getDashboardRecentlyCompletedPhasesList(3, 2));
//     }
// }
