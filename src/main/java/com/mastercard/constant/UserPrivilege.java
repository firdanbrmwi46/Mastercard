package com.mastercard.constant;

public enum UserPrivilege {
    SUPERADMIN("Superadmin"),
    ADMIN_BD("Admin BD"),
    MD("MD"),
    LEADER_MD("Leader MD"),
    ADMIN_VALIDATOR("Admin Validator"),
    LEADER_VALIDATOR("Leader Validator"),
    TEAM_LEADER("Team Leader");  // Menambahkan role Team Leader

    private final String dbValue;

    UserPrivilege(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static UserPrivilege fromDbValue(String value) {
        try {
            return UserPrivilege.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid privilege value");
        }
    }
}

