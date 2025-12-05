package com.attendance.model;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Record representing a time slot for classes.
 * Demonstrates: Records, LocalTime (Date/Time API).
 */
public record TimeSlot(int slotNumber, LocalTime startTime, LocalTime endTime) implements Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "Slot " + slotNumber + " (" + startTime + " - " + endTime + ")";
    }
}
