package com.db.dementia.service;

public final class DatabaseContextPath {
    private DatabaseContextPath(){}

    public static final String USER_NODE = "/user/%s";
    public static final String EMERGENCY_CONTACT_USER_NODE = "/emergency-contacts/%s";
    public static final String EMERGENCY_CONTACT_NODE = EMERGENCY_CONTACT_USER_NODE + "/%s";
}
