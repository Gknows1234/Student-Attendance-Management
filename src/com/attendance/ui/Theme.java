package com.attendance.ui;

import java.awt.*;

/**
 * Simple theme class for consistent UI styling.
 * Demonstrates: Static fields, encapsulation.
 */
public final class Theme {
    public static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    public static final Color SECONDARY_COLOR = new Color(245, 245, 245);
    public static final Color TEXT_COLOR = new Color(50, 50, 50);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 18);
    public static final Font REGULAR_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private Theme() {
    } // Prevent instantiation
}
