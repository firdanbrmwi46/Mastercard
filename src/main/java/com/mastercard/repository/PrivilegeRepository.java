package com.mastercard.repository;

import com.mastercard.constant.UserPrivilege;
import com.mastercard.model.users.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Optional<Privilege> findByPrivilegeDesc(String privilegeDesc);
    List<Privilege> findByPrivilegeDescIn(List<String> privilegeDesc);


}

