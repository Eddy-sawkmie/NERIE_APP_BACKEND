package com.nic.nerie.configs.security.filters;


import com.nic.nerie.configs.security.service.JwtService;
import com.nic.nerie.mt_userlogin.service.MT_UserloginDetailsService;

import org.springframework.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final ApplicationContext applicationContext;

    @Autowired
    public JwtFilter(JwtService jwtService, ApplicationContext applicationContext) {
        this.jwtService = jwtService;
        this.applicationContext = applicationContext;
    }

    /**
     * Extracts JWT from Header first, then Cookies.
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Try to get token from Header (Mobile/API)
        String jwtToken = getJwtFromRequestHeader(request);

        // If not in header, try Cookies (Web/Thymeleaf)
        if (jwtToken == null) {
            jwtToken = getJwtFromCookies(request);
        }

        if (jwtToken != null) {
            String userid = jwtService.extractUseridFromToken(jwtToken);

            /**
             * If a valid userid is found in the token, but this specific request hasn't been
             * authenticated yet (SecurityContext is null), we must validate the token.
             * * If valid, we manually authenticate the request by populating the SecurityContext.
             * Then, we pass it down the filter chain so the Controller can actually process it.
             */
            if (userid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = applicationContext.getBean(MT_UserloginDetailsService.class)
                        .loadUserByUsername(userid);

                if (jwtService.validateToken(jwtToken, userDetails)) {
                    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    /*
     *  Extracts JWT token from the header in the request or null if not found
     */
    private String getJwtFromRequestHeader(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /*
     *  Extracts JWT token from the cookies in the request or null if not found
     */
    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            Optional<Cookie> jwtCookie = Arrays.stream(request.getCookies())
                    .filter(cookie -> "neriejwt".equals(cookie.getName()))
                    .findFirst();
            return jwtCookie.map(Cookie::getValue).orElse(null);
        }
        return null;
    }
}
