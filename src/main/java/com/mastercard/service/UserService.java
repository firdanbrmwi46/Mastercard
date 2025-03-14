package com.mastercard.service;

import com.mastercard.DTO.request.UserRequest;
import com.mastercard.model.users.Privilege;
import com.mastercard.model.users.User;
import com.mastercard.repository.PrivilegeRepository;
import com.mastercard.repository.UserRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.List;
import java.util.Optional;

import static com.mastercard.service.AuthServiceImpl.encodeMD5;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;
    private final AuthServiceImpl authServiceImpl;
    private Class<Object> authentication;

    public User addUser(UserRequest request, String createdBy) {
        LOGGER.info("Menambahkan user baru: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException(" Username sudah terdaftar!");
        }

        String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();


        // Ambil Privilege dari database
        @NotBlank(message = "Privilege tidak boleh kosong") String privilege = String.valueOf(privilegeRepository.findByPrivilegeDesc(request.getPrivilege())
                .orElseThrow(() -> new RuntimeException("Privilege tidak ditemukan!")));

        User user = new User();
        user.setNik(request.getNik());
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(encodeMD5(request.getPassword()));
        user.setPrivilege(privilege);

        // Handle team leader
        if ("4".equals(request.getPrivilege()) && request.getTeamLeader() != null) {
            String[] tlData = request.getTeamLeader().split(",");
            user.setTlCode(tlData.length > 0 ? tlData[0] : "");
            user.setTlName(tlData.length > 1 ? tlData[1] : "");
        }

        user.setCreatedBy(createdBy);
        User savedUser = userRepository.save(user);
        LOGGER.info("User {} berhasil ditambahkan dengan ID: {}", savedUser.getName(), savedUser.getId());

        return savedUser;
    }

    // Ambil Semua User
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    // Ambil User Berdasarkan ID
    public Optional<User> getUserById(Long id) {

        return userRepository.findById(id);
    }

    //  Simpan atau Update User
    public User saveUser(User user) {
        LOGGER.info(" Menyimpan user dengan username: {}", user.getUsername());
        return userRepository.save(user);
    }

    // Cek Apakah Username Sudah Terdaftar
    public boolean existsByUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        LOGGER.info(" Apakah username {} sudah ada? {}", username, exists);
        return exists;
    }

    // Hapus User Berdasarkan ID
    public void deleteUser(Long id) {
        LOGGER.info("Menghapus user dengan ID: {}", id);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User tidak ditemukan!");
        }
        userRepository.deleteById(id);
        LOGGER.info("User dengan ID {} berhasil dihapus", id);
    }
}
