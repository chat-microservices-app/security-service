package com.chatapp.securityservice.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum Role {
    SERVICE("ROLE_SERVICE"),

    USER("ROLE_USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }


    public static enum Permission  {
        SERVICE_DETAILS("service.details", Role.SERVICE),
        SERVICE_LOGIN("service.login", Role.SERVICE),
        SERVICE_REGISTER("service.register", Role.SERVICE),
        USER_READ("user.read", Role.USER),

        USER_WRITE("user.write", Role.USER),

        USER_DELETE("user.delete", Role.USER);


        private final String permission;

        private final Role belongsToRole;

        Permission(String permission, Role service) {
            this.permission = permission;
            this.belongsToRole = service;
        }

        public Role getBelongsToRole() {
            return belongsToRole;
        }

        public String getPermission() {
            return permission;
        }

        public static List<String> getAllPermissionsByRoleType(Role roleType) {
            List<String> permissions = new ArrayList<>();
            for (Permission permission : Permission.values()) {
                if (Objects.equals(permission.getBelongsToRole(), roleType)) {
                    permissions.add(permission.getPermission());
                }
            }
            return permissions;
        }
    }
}