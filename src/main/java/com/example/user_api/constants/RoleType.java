package com.example.user_api.constants;

public enum RoleType {
    ADMIN("admin"),
    USER("user");

    private final String  value;

    RoleType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
