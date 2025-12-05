package com.attendance;

import com.attendance.model.*;
import com.attendance.service.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.UUID;

public class DataSeeder {
    public static void main(String[] args) {
        try {
            System.out.println("Initializing Service...");
            DataManager dataManager = new SerializedDataManager();
            AttendanceService service = new AttendanceService(dataManager);

            // 1. Add Subjects
            System.out.println("Adding Subjects...");
            addSubjectSafe(service, new Subject("CS101", "Core Java"));
            addSubjectSafe(service, new Subject("MAT101", "Calculus"));
            addSubjectSafe(service, new Subject("PHY101", "Physics"));
            addSubjectSafe(service, new Subject("ENG101", "English"));

            // 2. Add Students
            System.out.println("Adding Students...");
            addStudentSafe(service, new Student("S001", "Alice Smith", "101"));
            addStudentSafe(service, new Student("S002", "Bob Jones", "102"));
            addStudentSafe(service, new Student("S003", "Charlie Brown", "103"));
            addStudentSafe(service, new Student("S006", "Yajnesh", "1208"));
            addStudentSafe(service, new Student("S007", "Shreeyas", "1165"));
            addStudentSafe(service, new Student("S008", "Sheldon", "1179"));
            addStudentSafe(service, new Student("S009", "Aaryan", "1209"));

            // 3. Create Timetable
            System.out.println("Creating Timetable...");
            // Monday
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.MONDAY, 1, "CS101")); // 9-10
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.MONDAY, 2, "MAT101")); // 10-11
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.MONDAY, 3, "PHY101")); // 11-12

            // Tuesday
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.TUESDAY, 1, "MAT101"));
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.TUESDAY, 2, "CS101"));

            // Wednesday
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.WEDNESDAY, 1, "ENG101"));
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.WEDNESDAY, 2, "PHY101"));

            // Thursday (Today if user runs it on Thursday, otherwise just sample)
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.THURSDAY, 1, "CS101"));
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.THURSDAY, 2, "MAT101"));

            // Friday
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.FRIDAY, 1, "PHY101"));
            addTimetableSafe(service, new TimetableEntry(DayOfWeek.FRIDAY, 2, "ENG101"));

            // 4. Mark some attendance
            System.out.println("Marking Sample Attendance...");
            LocalDate today = LocalDate.now();

            // Mark Yajnesh Present
            markAttendanceSafe(service, "S006", "CS101", today, 1, true);
            markAttendanceSafe(service, "S006", "MAT101", today, 2, true);

            // Mark Shreeyas Absent for one slot
            markAttendanceSafe(service, "S007", "CS101", today, 1, true);
            markAttendanceSafe(service, "S007", "MAT101", today, 2, false); // Absent

            // Mark Sheldon Absent for both
            markAttendanceSafe(service, "S008", "CS101", today, 1, false);
            markAttendanceSafe(service, "S008", "MAT101", today, 2, false);

            // Mark Aaryan Present
            markAttendanceSafe(service, "S009", "CS101", today, 1, true);
            markAttendanceSafe(service, "S009", "MAT101", today, 2, true);

            System.out.println("Seeding Complete!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void markAttendanceSafe(AttendanceService service, String studentId, String subjectCode,
            LocalDate date, int slot, boolean present) {
        try {
            service.markAttendance(studentId, subjectCode, date, slot, present);
        } catch (Exception e) {
            System.out.println("Skipping Attendance for " + studentId + ": " + e.getMessage());
        }
    }

    private static void addSubjectSafe(AttendanceService service, Subject s) {
        try {
            service.addSubject(s);
        } catch (Exception e) {
            System.out.println("Skipping " + s.name() + ": " + e.getMessage());
        }
    }

    private static void addStudentSafe(AttendanceService service, Student s) {
        try {
            service.addStudent(s);
        } catch (Exception e) {
            System.out.println("Skipping " + s.name() + ": " + e.getMessage());
        }
    }

    private static void addTimetableSafe(AttendanceService service, TimetableEntry t) {
        try {
            service.addTimetableEntry(t);
        } catch (Exception e) {
            System.out.println("Skipping Slot " + t.slotNumber() + ": " + e.getMessage());
        }
    }

}
