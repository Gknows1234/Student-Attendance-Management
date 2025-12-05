package com.attendance.ui;

import com.attendance.exception.AttendanceException;
import com.attendance.service.AttendanceService;
import com.attendance.service.SerializedDataManager;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window with tabbed panels.
 * Demonstrates: JTabbedPane, exception handling in UI.
 */
public class MainFrame extends JFrame {
    private AttendanceService service;

    public MainFrame() {
        setTitle("Attendance Management System");
        setSize(800, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        try {
            service = new AttendanceService(new SerializedDataManager());
        } catch (AttendanceException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Students", new StudentPanel(service));
        tabbedPane.addTab("Timetable", new TimetablePanel(service));
        tabbedPane.addTab("Attendance", new AttendanceMarkingPanel(service));
        tabbedPane.addTab("Reports", new ReportPanel(service));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
