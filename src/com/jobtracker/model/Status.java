package com.jobtracker.model;

public enum Status {
    APPLIED,
    INTERVIEWING,
    OFFER,
    REJECTED,
    WITHDRAWN;

    public boolean isActive(){ return this == APPLIED || this == INTERVIEWING;}
}
