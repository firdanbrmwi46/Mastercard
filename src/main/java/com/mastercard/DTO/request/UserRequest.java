package com.mastercard.DTO.request;

import com.mastercard.constant.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    @NotBlank(message = "NIK tidak boleh kosong")
    @Size(min = 4, message = "NIK minimal 4 karakter")
    private String nik;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 4, message = "Username minimal 4 karakter")
    private String username;

    @NotBlank(message = "Nama tidak boleh kosong")
    private String name;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 4, message = "Password minimal 4 karakter")
    private String password;

    @NotBlank(message = "Privilege tidak boleh kosong")
    private String privilege;

    private String teamLeader;

    public Status getStatus() {
        return null;
    }
}
