package com.nic.nerie.configs.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey skey;

    @Value("${JWT_EXP_SERVER}")
    private Long tokenExpirationMillis;  

    public JwtService(@Value("${JWT_KEY}") String skey) {
        this.skey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(skey));
    }

    public String generateToken(String userid) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userid)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenExpirationMillis))
                .and()
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        return this.skey;
    }

    public String extractUseridFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUseridFromToken(token);

        // isn't testing username again redundant?
        return userDetails.getUsername().equals(username) && !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        return extractTokenExpiration(token).before(new Date());
    }

    private Date extractTokenExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     *
     *  The second argument to the method is an instance variable of interface type Function that takes
     *  as input an instance claims of type Claims and produces as output an instance of generic type T.
     *  We apply the function claimResolver providing the context of claims and return immediately.
     *  Since it's returning an instance of generic type T and that is the only generic used in the
     *  whole method explains the T and <T> respectively. Close enough?
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
