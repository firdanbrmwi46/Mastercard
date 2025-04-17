package com.mastercard.controller;

import com.mastercard.DTO.request.UserRequest;
import com.mastercard.constant.UserPrivilege;
import com.mastercard.details.CustomUserDetails;
import com.mastercard.model.users.Privilege;
import com.mastercard.model.users.User;

import com.mastercard.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRequest request, Authentication authentication) {
        try {
            String createdBy = "SYSTEM";
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                createdBy = userDetails.getRealName();
            }

            // Convert privilege string to UserPrivilege enum
            UserPrivilege userPrivilegeEnum = UserPrivilege.fromDbValue(request.getPrivilege());

            // Validasi apakah privilege adalah MD, jika ya, maka teamLeader harus diisi
            if (userPrivilegeEnum == UserPrivilege.MD && (request.getTeamLeader() == null || request.getTeamLeader().isEmpty())) {
                return ResponseEntity.badRequest().body("Team Leader harus diisi jika privilege adalah MD");
            }

            // Set the privilege in Privilege object
            Privilege privilege = new Privilege();
            privilege.setPrivilegeEnum(userPrivilegeEnum);  // Assign the correct UserPrivilege to Privilege object

            User user = new User();
            user.setNik(request.getNik());
            user.setName(request.getName());
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setPrivilege(privilege);
            user.setCreatedBy(createdBy);

            // Jika privilege MD, maka teamLeader harus diisi
            if (userPrivilegeEnum == UserPrivilege.MD) {
                user.setTlName(request.getTeamLeader());
            }

            userService.addUser(request, createdBy);  // Call your service to save the user

            return ResponseEntity.ok().body("User berhasil ditambahkan: " + user.getUsername());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody UserRequest request,
            Authentication authentication) {
        try {
            // Cek apakah user dengan ID tersebut ada
            Optional<User> existingUserOpt = userService.getUserById(id);
            if (!existingUserOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User dengan ID " + id + " tidak ditemukan.");
            }

            User existingUser = existingUserOpt.get();

            // Ambil informasi siapa yang mengedit (menggunakan realName jika tersedia)
            String updatedBy = "SYSTEM";
            if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                updatedBy = userDetails.getRealName();
            }

            // Update data berdasarkan request
            existingUser.setNik(request.getNik());
            existingUser.setName(request.getName());
            existingUser.setUsername(request.getUsername());
            existingUser.setStatus(request.getStatus());
            existingUser.setPrivilege(request.getPrivilege());
            existingUser.setUpdatedBy(updatedBy);

            // Simpan perubahan
            User updatedUser = userService.saveUser(existingUser);

            return ResponseEntity.ok().body("User berhasil diperbarui: " + updatedUser.getUsername());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Terjadi kesalahan: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.ok("User berhasil dihapus");
    }
}
