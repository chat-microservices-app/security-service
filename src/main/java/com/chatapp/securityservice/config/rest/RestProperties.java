package com.chatapp.securityservice.config.rest;

public final class RestProperties {

    public static final String ROOT = "/api";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final class USER {

        public static final String USER = "/users";
        public static final String REGISTER = "/register";
        public static final String LOGIN = "/login";
        public static final String DETAILS = "/details";
    }

    public static final class AUTH {


        public static final String ROOT = "/auth";

        public static final String LOGIN = "/login";

        public static final String REGISTER = "/register";

        public static final String REFRESH_TOKEN = "/refresh-token";

        public static final String CHECK_TOKEN = "/check-token";
    }
}
