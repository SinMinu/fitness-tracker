package com.fitness.tracker.controller;

import com.fitness.tracker.model.JwtRequest;
import com.fitness.tracker.model.JwtResponse;
import com.fitness.tracker.model.User;
import com.fitness.tracker.service.CustomUserDetailsService;
import com.fitness.tracker.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class JwtAuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final String jwt = jwtUtil.generateToken(authenticationRequest.getUsername());
        User user = userDetailsService.findUserByUsername(authenticationRequest.getUsername());
        return ResponseEntity.ok(new JwtResponse(jwt, String.valueOf(user.getId())));
    }

}
