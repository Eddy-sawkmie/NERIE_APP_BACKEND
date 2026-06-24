package com.nic.nerie.configs.security.providers;

import com.nic.nerie.mt_userlogin.model.MT_UserloginPrincipal;
import com.nic.nerie.mt_userlogin.service.MT_UserloginDetailsService;
import com.nic.nerie.utils.GenerateSalt;
import com.nic.nerie.utils.SHA256Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final MT_UserloginDetailsService mtUserloginDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomAuthenticationProvider(MT_UserloginDetailsService mtUserloginDetailsService,
                                        PasswordEncoder passwordEncoder) {
        this.mtUserloginDetailsService = mtUserloginDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userid = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (userid == null || userid.isBlank() || password == null || password.isBlank())
            throw new BadCredentialsException("Invalid userid or password");

        MT_UserloginPrincipal userDetails = null;
        try {
            userDetails = (MT_UserloginPrincipal) mtUserloginDetailsService.loadUserByUsername(userid); 
        } catch (UsernameNotFoundException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }

        boolean isValidCredentials;
        // Existing users in the database have their password encrypted using SHA256 and salt.
        if (userDetails.isUseBcrypt() == null || !userDetails.isUseBcrypt()) {
            String salt = GenerateSalt.getSalt();
            String hashedPassword = SHA256Util.getHash(SHA256Util.getHash(password) + salt);
            String storedPassword = SHA256Util.getHash(userDetails.getPassword() + salt);
            isValidCredentials = hashedPassword.equals(storedPassword);
        }
        else
            isValidCredentials = passwordEncoder.matches(password, userDetails.getPassword());

        if (isValidCredentials)
            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

        throw new BadCredentialsException("User doesn't exist");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
