package com.jobtracker.model;

public enum Status {
    APPLIED,
    INTERVIEWING,
    OFFER,
    REJECTED,
    WITHDRAWN;

    boolean isActive(){ return this == APPLIED || this == INTERVIEWING;}
}
