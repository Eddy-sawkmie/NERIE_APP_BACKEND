package com.nic.nerie.mt_userlogin.service;

import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.model.MT_UserloginPrincipal;
import com.nic.nerie.mt_userlogin.repository.MT_UserloginRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MT_UserloginDetailsService implements UserDetailsService {
    private final MT_UserloginRepository mtUserloginRepository;

    @Autowired
    public MT_UserloginDetailsService(MT_UserloginRepository mtUserloginRepository) {
        this.mtUserloginRepository = mtUserloginRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
        Optional<MT_Userlogin> userOptional = mtUserloginRepository.findByUserId(userid.trim());

        if (userOptional.isPresent()) {
            MT_Userlogin user = userOptional.get();
            return new MT_UserloginPrincipal(user);
        }

        throw new UsernameNotFoundException("User with userid " + userid + " was not found!");
    }

}
