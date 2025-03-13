package com.mastercard.model.users;

import com.mastercard.constant.TableName;
import com.mastercard.constant.UserPrivilege;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = TableName.PRIVILEGE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Privilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long privilege;

    private Integer role;

    @Column(name = "privilege_desc", unique = true, nullable = false)
    private String privilegeDesc;

    public UserPrivilege getPrivilegeEnum() {
        return UserPrivilege.valueOf(this.privilegeDesc);
    }

    public void setPrivilegeEnum(UserPrivilege privilege) {
        this.privilegeDesc = privilege.name();
    }
}
