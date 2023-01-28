package ru.vsu.csf.asashina.marketserver.model.enums;

public enum RoleName {

    USER("USER"),
    ADMIN("ADMIN");

    private final String name;

    RoleName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
