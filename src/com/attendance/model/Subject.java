package com.attendance.model;

import java.io.Serializable;

/**
 * Record representing a Subject/Course.
 * Demonstrates: Records, Serializable.
 */
public record Subject(String code, String name) implements Serializable {
    private static final long serialVersionUID = 1L;
}
