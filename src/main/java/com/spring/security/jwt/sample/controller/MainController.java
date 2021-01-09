package com.spring.security.jwt.sample.controller;

import com.spring.security.jwt.sample.config.MyUserDetailsService;
import com.spring.security.jwt.sample.model.AuthenticationRequest;
import com.spring.security.jwt.sample.model.AuthenticationResponse;
import com.spring.security.jwt.sample.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController {
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService myUserDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public MainController(AuthenticationManager authenticationManager,
                          MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.myUserDetailsService = myUserDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping(path = "/hello")
    public ResponseEntity<?> hello() {
        return ResponseEntity.ok(Map.of("word", "hello"));
    }

    @PostMapping(path = "/auth")
    public ResponseEntity<?> auth(@RequestBody AuthenticationRequest authenticationRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException exception) {
            return new ResponseEntity<>("Bad credentials", HttpStatus.FORBIDDEN);
        }

        final UserDetails userDetails = this.myUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
