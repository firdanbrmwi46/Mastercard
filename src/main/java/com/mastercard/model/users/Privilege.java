package com.mastercard.model.users;

import com.mastercard.constant.UserPrivilege;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_privileges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege {

    @Id
    @Column(name = "privilege_desc", unique = true, nullable = false)
    private String privilegeDesc;

    private Integer role;

    public UserPrivilege getPrivilegeEnum() {
        return UserPrivilege.fromDbValue(this.privilegeDesc);
    }

    public void setPrivilegeEnum(UserPrivilege privilege) {
        this.privilegeDesc = privilege.getDbValue();
    }
}
