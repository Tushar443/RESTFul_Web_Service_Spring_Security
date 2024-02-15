package com.example.demo.Security;

import com.example.demo.Repository.UserRepo;
import com.example.demo.ws.io.Entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final UserRepo userRepo;

    public AuthorizationFilter(AuthenticationManager authenticationManager, UserRepo userRepo) {
        super(authenticationManager);
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("----------AuthorizationFilter == doFilterInternal() Method Call----------");
        String header = request.getHeader(SecurityConstants.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        System.out.println("----------AuthorizationFilter == getAuthentication() Method Call----------");
        String authorizationHeader = request.getHeader(SecurityConstants.HEADER_STRING);
        if (authorizationHeader == null) {
            return null;
        }
        String token = authorizationHeader.replace(SecurityConstants.TOKEN_PREFIX, "");

        byte[] secreteKeyBytes = SecurityConstants.getTokenSecret().getBytes();
        SecretKey secretKey = Keys.hmacShaKeyFor(secreteKeyBytes);// new SecretKeySpec(secreteKeyBytes, SignatureAlgorithm.HS512.getJcaName());

        JwtParser jwtParser = Jwts.parser().verifyWith(secretKey).build();

        Claims claims = jwtParser.parseSignedClaims(token).getPayload();
        String user = (String) claims.get("sub");
        /** Another way to verify token
         *  String token = request.getHeader(HEADER_STRING);
         *
         *         if (token != null) {
         *             // parse the token.
         *             String user = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
         *                     .build()
         *                     .verify(token.replace(TOKEN_PREFIX, ""))
         *                     .getSubject();
         *        }
         */
        if (user != null) {
            UserEntity userEntity = userRepo.findUserByEmail(user);
            UserPrinciple userPrinciple = new UserPrinciple(userEntity);
            return new UsernamePasswordAuthenticationToken(user, null, userPrinciple.getAuthorities());
        }
        return null;
    }
}
