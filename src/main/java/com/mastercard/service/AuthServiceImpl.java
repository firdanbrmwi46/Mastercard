package com.mastercard.service;

import com.mastercard.DTO.request.AuthRequest;
import com.mastercard.DTO.response.LoginResponse;
import com.mastercard.constant.UserPrivilege;
import com.mastercard.model.users.Privilege;
import com.mastercard.model.users.User;
import com.mastercard.repository.UserRepository;
import com.mastercard.service.impl.AuthService;
import com.mastercard.service.impl.JwtService;
import com.mastercard.service.impl.PrivilegeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PrivilegeService privilegeService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.management.username-admin}")
    private String AdminUsername;

    @Value("${app.management.password-admin}")
    private String AdminPassword;

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initialSuperAdmin() {
        Optional<User> currentAccount = userRepository.findByUsername(AdminUsername);
        if (currentAccount.isPresent()) {
            LOGGER.info("Superadmin '{}' already exists.", AdminUsername);
            return;
        }

        Privilege superadmin = privilegeService.getOrSave(UserPrivilege.SUPERADMIN);
        Privilege adminBD = privilegeService.getOrSave(UserPrivilege.ADMIN_BD);
        Privilege md = privilegeService.getOrSave(UserPrivilege.MD);
        Privilege leaderMD = privilegeService.getOrSave(UserPrivilege.LEADER_MD);
        Privilege adminValidator = privilegeService.getOrSave(UserPrivilege.ADMIN_VALIDATOR);
        Privilege leaderValidator = privilegeService.getOrSave(UserPrivilege.LEADER_VALIDATOR);

        User user = User.builder()
                .username(AdminUsername)
                .password(passwordEncoder.encode(AdminPassword))
//                .privilege((java.util.Set<Privilege>) List.of(superadmin, adminBD, md, leaderMD, adminValidator, leaderValidator)) // ✅ Ganti `role` jadi `privilege`
                .privilege(superadmin)
                .isEnable(true)
                .build();

        userRepository.save(user);
        LOGGER.info("Superadmin '{}' has been created successfully.", AdminUsername);
    }

    @Override
    public LoginResponse login(AuthRequest request) {
        String hashedPassword = encodeMD5(request.getPassword());

        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.getPassword().equals(hashedPassword)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }

            String token = jwtService.generateToken(user);
            return LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
//                .privilege(authenticatedUser.getPrivilege().stream()
//                        .map(Privilege::getPrivilegeDesc)
//                        .toList())
                    .privilege(user.getPrivilege().getPrivilegeDesc())
                    .build();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
    }

    private User authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return (User) authentication.getPrincipal();
    }

    private static String encodeMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for(byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error encoding password", e);
        }
    }

}
