package com.mastercard.model.users;

import com.mastercard.constant.Status;
import com.mastercard.model.StatusConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    private String nik;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @Convert(converter = StatusConverter.class)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;  // Menggunakan Enum Langsung

    private String createdBy;

    @CreationTimestamp
    @Column(updatable = false, name = "created_date")
    private Date createdDate;

    private String tlCode;
    private String tlName;
    private String foto;

    @Column(name = "is_enable", nullable = false)
    private boolean isEnable = true;

//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "user_privileges",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "privilege_id")
//    )
//    private Set<Privilege> privilege;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "privilege")
    private Privilege privilege;

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return privilege.stream()
//                .map(priv -> new SimpleGrantedAuthority("PRIV_" + priv.getPrivilegeDesc()))
//                .collect(Collectors.toSet());
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(privilege == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + privilege.getPrivilegeDesc()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnable;
    }

    public void setUserId(Long id) {
    }

    public Optional<Object> getPrivileges() {
        return null;
    }
}
