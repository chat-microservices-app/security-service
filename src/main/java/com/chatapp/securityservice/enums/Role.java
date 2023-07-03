package com.chatapp.securityservice.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public enum Role {

    PREFIX("ROLE_"),
    SERVICE("SERVICE"),

    USER("USER");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static enum Permission {
        SERVICE_DETAILS("service.details", Role.SERVICE),
        SERVICE_LOGIN("service.login", Role.SERVICE),
        SERVICE_REGISTER("service.register", Role.SERVICE),
        USER_READ("user.read", Role.USER),

        USER_WRITE("user.write", Role.USER),

        USER_DELETE("user.delete", Role.USER),

        CHAT_CREATE("chat.create", Role.USER),
        CHAT_READ("chat.read", Role.USER),
        CHAT_UPDATE("chat.update", Role.USER);


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
