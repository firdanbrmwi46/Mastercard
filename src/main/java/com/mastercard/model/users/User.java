package com.mastercard.model.users;

import com.mastercard.constant.Status;
import com.mastercard.constant.UserPrivilege;
import com.mastercard.model.StatusConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

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
    private Status status = Status.ACTIVE;

    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @CreationTimestamp
    @Column(updatable = false, name = "created_date")
    private Date createdDate;

    private String tlCode;
    private String tlName;
    private String foto;

    @Column(name = "is_enable", nullable = false)
    private boolean isEnable = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "privilege", referencedColumnName = "privilege_desc")
    private Privilege privilege;

    // Optional constructor (can be removed if unused)
    public User(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (privilege == null) {
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

    // Method untuk set privilege menjadi TEAM_LEADER
    public void setPrivilegeToTeamLeader() {
        this.privilege = new Privilege();
        this.privilege.setPrivilegeEnum(UserPrivilege.TEAM_LEADER);
    }

    // Untuk set privilege dari String (misal dari form input)
    public void setPrivilege(String privilegeDesc) {
        if (privilegeDesc == null) {
            this.privilege = null;
            return;
        }
        this.privilege = new Privilege();
        this.privilege.setPrivilegeEnum(UserPrivilege.fromDbValue(privilegeDesc));
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
    }
}
