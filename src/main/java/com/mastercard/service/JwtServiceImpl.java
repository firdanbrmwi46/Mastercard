package com.mastercard.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mastercard.model.users.Privilege;
import com.mastercard.model.users.User;
import com.mastercard.repository.PrivilegeRepository;
import com.mastercard.service.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.JWTVerifier;
import java.time.Instant;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    private final String JWT_SECRET;
    private final String ISSUER;
    private final long JWT_EXPIRATION;

    public JwtServiceImpl(
            @Value("${app.management.jwt-secret}") String jwtSecret,
            @Value("${app.management.jwt-app-name}") String issuer,
            @Value("${app.management.jwt-expiration}") long expiration
    ) {
        JWT_SECRET = jwtSecret;
        ISSUER = issuer;
        JWT_EXPIRATION = expiration;
    }

    public String parseJwt(String token) {
        if(token != null && token.startsWith("Bearer ")) {
            return token.substring((7));
        }
        return null;
    }

    @Override
    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(Instant.now())
                    .withExpiresAt(Instant.now().plusSeconds(JWT_EXPIRATION))
                    .withSubject(String.valueOf(user.getId()))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while creating JWT token");
        }
    }


    @Override
    public boolean verifyJwtToken(String bearerToken) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();

            String token = parseJwt(bearerToken);
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        return "";
    }

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Override
    public List<Privilege> getPrivilegeFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
            DecodedJWT jwt = verifier.verify(token);

            // Ambil list privilege dalam bentuk string
            List<String> privilegeNames = jwt.getClaim("privileges").asList(String.class);

            // Konversi dari String ke Privilege (mengambil dari database)
            return privilegeRepository.findByPrivilegeDescIn(privilegeNames);
        } catch (Exception e) {
            return List.of();
        }
    }



}