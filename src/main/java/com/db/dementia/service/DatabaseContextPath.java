package com.db.dementia.service;

public final class DatabaseContextPath {
    private DatabaseContextPath(){}

    public static final String USER_NODE = "/user/%s";

    public static final String EMERGENCY_CONTACT_USER_NODE = "/emergency-contacts/%s";
    public static final String EMERGENCY_CONTACT_NODE = EMERGENCY_CONTACT_USER_NODE + "/%s";

    public static final String GALLERY_USER_NODE = "/gallery/%s/files";

    public static final String EVENTS_USER_NODE = "/events/%s";
    public static final String EVENTS_NODE = EVENTS_USER_NODE + "/%s";
}
