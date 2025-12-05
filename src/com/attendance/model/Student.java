package com.attendance.model;

import java.io.Serializable;

/**
 * Record representing a Student.
 * Demonstrates: Records, Serializable interface, immutability.
 */
public record Student(String id, String name, String rollNo) implements Serializable {
    private static final long serialVersionUID = 1L;
}
