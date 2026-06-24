package com.nic.nerie.mt_program_members.controller;

import com.nic.nerie.mt_program_members.service.MT_ProgramMembersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/program-members")
public class MT_ProgramMembersController {

    private final MT_ProgramMembersService mtProgramMembersService;

    @Autowired
    public MT_ProgramMembersController(MT_ProgramMembersService mtProgramMembersService) {
        this.mtProgramMembersService = mtProgramMembersService;
    }

    @PostMapping("/get-program-members")
    @ResponseBody
    public List<Map<String, String>> getProgramMembers(@RequestParam("programcode") String programcode,
                                                       @RequestParam("phaseid") String phaseid) {
        List<Object[]> members = mtProgramMembersService.getMembersByProgramAndPhase(programcode,phaseid);
        List<Map<String, String>> response = new ArrayList<>();
        for (Object[] member : members) {
            Map<String, String> map = new HashMap<>();
            map.put("usercode", member[0].toString());
            map.put("username", member[1].toString());
            map.put("pmid", member[2].toString());
            response.add(map);
        }
        return response;
    }

}
