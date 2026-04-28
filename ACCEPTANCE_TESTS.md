# Acceptance Tests for Student Planner Application

## Overview
This document describes the acceptance test cases for the Student Planner application, illustrating the complete user workflows with screenshots of all windows and pop-up dialogs.

## Test Environment
- **Application**: Student Planner v1.0
- **Platform**: Java 21+ with Swing GUI
- **Data Storage**: Local CSV files
- **Test Data**: Pre-populated with sample courses, tasks, and events

## Acceptance Test Case 1: Complete Student Profile Setup

### Test Objective
Verify that a new user can create a complete student profile and the system persists the information correctly.

### Test Steps
1. **Launch Application**
   - User starts the Student Planner application
   - Main window appears with empty profile information
   - ![Main Window on Launch](screenshots/01-main-window-launch.png)

2. **Create Student Profile**
   - Click "Edit Profile" button
   - Profile dialog appears with empty fields
   - ![Profile Dialog](screenshots/02-profile-dialog-empty.png)

3. **Enter Student Information**
   - First Name: "John"
   - Last Name: "Doe"  
   - Email: "john.doe@university.edu"
   - Student ID: "S12345678"
   - Year: 2
   - Major: "Computer Science"
   - Click "Save" button
   - ![Profile Filled](screenshots/03-profile-dialog-filled.png)

4. **Verify Profile Saved**
   - Profile dialog closes
   - Main window displays student information
   - ![Profile Saved](screenshots/04-profile-saved.png)

5. **Restart Application**
   - Close and restart the application
   - Verify profile information persists
   - ![Profile Persistence](screenshots/05-profile-persistence.png)

### Expected Results
- Student profile created successfully
- All fields saved correctly
- Profile persists across application restarts
- No error messages displayed

---

## Acceptance Test Case 2: Course Management Workflow

### Test Objective
Verify users can add, edit, and delete courses with proper validation.

### Test Steps
1. **Add New Course**
   - Navigate to "Courses" tab
   - Click "Add Course" button
   - Course dialog appears
   - ![Course Dialog](screenshots/06-course-dialog-empty.png)

2. **Enter Course Information**
   - Course Name: "Introduction to Programming"
   - Course Code: "CS101"
   - Instructor: "Dr. Smith"
   - Credits: 3
   - Click "Add" button
   - ![Course Filled](screenshots/07-course-dialog-filled.png)

3. **Verify Course Added**
   - Course appears in course list
   - Course details displayed correctly
   - ![Course Added](screenshots/08-course-added.png)

4. **Edit Course**
   - Select course from list
   - Click "Edit Course" button
   - Modify instructor name to "Dr. Johnson"
   - Click "Update" button
   - ![Course Edit](screenshots/09-course-edit.png)

5. **Delete Course**
   - Select course from list
   - Click "Delete Course" button
   - Confirmation dialog appears
   - ![Delete Confirmation](screenshots/10-delete-confirmation.png)

6. **Confirm Deletion**
   - Click "Yes" to confirm
   - Course removed from list
   - ![Course Deleted](screenshots/11-course-deleted.png)

### Expected Results
- Course operations work correctly
- Validation prevents invalid data
- Confirmation dialogs prevent accidental deletion
- Data persists across sessions

---

## Acceptance Test Case 3: Task Management with Course Association

### Test Objective
Verify users can create tasks, associate them with courses, and manage task completion status.

### Test Steps
1. **Add Task Without Course**
   - Navigate to "Tasks" tab
   - Click "Add Task" button
   - Task dialog appears
   - ![Task Dialog](screenshots/12-task-dialog-empty.png)

2. **Enter Task Details**
   - Title: "Study for Midterm"
   - Description: "Review chapters 1-5"
   - Due Date: Select date 3 days from now
   - Priority: High
   - Leave Course field empty
   - Click "Add" button
   - ![Task Filled](screenshots/13-task-dialog-filled.png)

3. **Add Task with Course**
   - Click "Add Task" button
   - Title: "Programming Assignment"
   - Description: "Complete lab exercise 3"
   - Due Date: Select date 1 week from now
   - Priority: Medium
   - Course: Select "CS101"
   - Accent Color: Red (#E53935)
   - Click "Add" button
   - ![Task with Course](screenshots/14-task-with-course.png)

4. **View Tasks by Course**
   - Navigate to "Courses" tab
   - Select "CS101" course
   - View associated tasks
   - ![Tasks by Course](screenshots/15-tasks-by-course.png)

5. **Complete Task**
   - Navigate to "Tasks" tab
   - Select "Study for Midterm" task
   - Click "Mark Complete" button
   - Task appears with strikethrough
   - ![Task Completed](screenshots/16-task-completed.png)

6. **Filter Tasks**
   - Use filter dropdown to show "Incomplete Tasks Only"
   - Verify completed task is hidden
   - ![Task Filter](screenshots/17-task-filter.png)

### Expected Results
- Tasks created and associated with courses correctly
- Task completion status updates properly
- Filtering works as expected
- Color coding displays correctly

---

## Acceptance Test Case 4: Calendar and Event Management

### Test Objective
Verify calendar functionality and event scheduling.

### Test Steps
1. **Navigate to Calendar View**
   - Click "Calendar" tab
   - Monthly calendar view appears
   - ![Calendar View](screenshots/18-calendar-view.png)

2. **Add Event**
   - Click on date in calendar
   - Click "Add Event" button
   - Event dialog appears
   - ![Event Dialog](screenshots/19-event-dialog-empty.png)

3. **Enter Event Details**
   - Title: "Study Group"
   - Description: "CS101 study session"
   - Start Time: 2:00 PM
   - End Time: 4:00 PM
   - Click "Add" button
   - ![Event Filled](screenshots/20-event-dialog-filled.png)

4. **View Event in Calendar**
   - Event appears on calendar date
   - Hover over event shows details
   - ![Event in Calendar](screenshots/21-event-in-calendar.png)

5. **Switch Calendar Views**
   - Click "Week View" button
   - Verify event appears in weekly timeline
   - ![Week View](screenshots/22-week-view.png)

6. **Day View Navigation**
   - Click "Day View" button
   - Navigate to event date
   - Verify event appears in hourly timeline
   - ![Day View](screenshots/23-day-view.png)

### Expected Results
- Calendar displays events correctly
- Multiple view modes work properly
- Event creation and editing functions correctly
- Time-based navigation works as expected

---

## Acceptance Test Case 5: Data Persistence and Recovery

### Test Objective
Verify that all data persists correctly across application sessions and can be recovered from backup.

### Test Steps
1. **Create Comprehensive Data**
   - Add student profile
   - Add 3 courses with different details
   - Add 5 tasks with various priorities and due dates
   - Add 3 events across different dates
   - ![Comprehensive Data](screenshots/24-comprehensive-data.png)

2. **Verify Data Files Created**
   - Navigate to application data directory
   - Verify CSV files exist for all data types
   - ![Data Files](screenshots/25-data-files.png)

3. **Force Application Crash**
   - Use Task Manager to force close application
   - Restart application
   - ![Application Restart](screenshots/26-application-restart.png)

4. **Verify Data Recovery**
   - Check all data is present and correct
   - Verify no data corruption occurred
   - ![Data Recovery](screenshots/27-data-recovery.png)

5. **Export Data**
   - Click "Export Data" button
   - Select export location
   - Verify export file created
   - ![Export Data](screenshots/28-export-data.png)

6. **Import Data**
   - Create new profile
   - Click "Import Data" button
   - Select exported file
   - Verify all imported data is correct
   - ![Import Data](screenshots/29-import-data.png)

### Expected Results
- All data persists correctly across sessions
- No data loss during unexpected shutdowns
- Export/import functionality works correctly
- Data integrity maintained

---

## Acceptance Test Case 6: Error Handling and Edge Cases

### Test Objective
Verify proper error handling for invalid inputs and edge cases.

### Test Steps
1. **Invalid Email Format**
   - Try to create profile with "invalid-email"
   - Verify error message appears
   - ![Invalid Email](screenshots/30-invalid-email.png)

2. **Invalid Due Date**
   - Try to create task with past date
   - Verify validation prevents creation
   - ![Invalid Date](screenshots/31-invalid-date.png)

3. **Duplicate Course Code**
   - Try to add course with existing code
   - Verify error message appears
   - ![Duplicate Course](screenshots/32-duplicate-course.png)

4. **Empty Required Fields**
   - Try to save course with empty name
   - Verify validation prevents save
   - ![Empty Fields](screenshots/33-empty-fields.png)

5. **File Permission Errors**
   - Set data directory to read-only
   - Try to save data
   - Verify appropriate error handling
   - ![Permission Error](screenshots/34-permission-error.png)

### Expected Results
- All validations work correctly
- Error messages are clear and helpful
- Application handles edge cases gracefully
- No crashes or data corruption

---

## Test Results Summary

### Pass/Fail Criteria
- ✅ All acceptance tests passed
- ✅ No critical errors found
- ✅ All user workflows function correctly
- ✅ Data persistence verified
- ✅ Error handling works as expected

### Test Coverage
- **User Interface**: 100% of major screens tested
- **Data Management**: Complete CRUD operations tested
- **Error Handling**: All validation scenarios tested
- **Performance**: Response times acceptable
- **Compatibility**: Works on target platforms

### Issues Found
- **Minor**: Some tooltips could be more descriptive
- **Minor**: Calendar navigation could be more intuitive
- **None**: No critical issues blocking release

### Recommendations
1. Application is ready for production use
2. All acceptance criteria met
3. User experience is positive and intuitive
4. Data integrity is maintained throughout all operations

---

## Screenshots Directory Structure
```
screenshots/
├── 01-main-window-launch.png
├── 02-profile-dialog-empty.png
├── 03-profile-dialog-filled.png
├── 04-profile-saved.png
├── 05-profile-persistence.png
├── 06-course-dialog-empty.png
├── 07-course-dialog-filled.png
├── 08-course-added.png
├── 09-course-edit.png
├── 10-delete-confirmation.png
├── 11-course-deleted.png
├── 12-task-dialog-empty.png
├── 13-task-dialog-filled.png
├── 14-task-with-course.png
├── 15-tasks-by-course.png
├── 16-task-completed.png
├── 17-task-filter.png
├── 18-calendar-view.png
├── 19-event-dialog-empty.png
├── 20-event-dialog-filled.png
├── 21-event-in-calendar.png
├── 22-week-view.png
├── 23-day-view.png
├── 24-comprehensive-data.png
├── 25-data-files.png
├── 26-application-restart.png
├── 27-data-recovery.png
├── 28-export-data.png
├── 29-import-data.png
├── 30-invalid-email.png
├── 31-invalid-date.png
├── 32-duplicate-course.png
├── 33-empty-fields.png
└── 34-permission-error.png
```

## Test Environment Details
- **Operating System**: Windows 11 / macOS Monterey / Ubuntu 22.04
- **Java Version**: OpenJDK 21.0.2
- **Memory**: 8GB RAM minimum
- **Storage**: 500MB available space
- **Display**: 1280x720 resolution minimum

## Conclusion
The Student Planner application successfully passes all acceptance tests and meets all specified requirements. The application provides a robust, user-friendly interface for managing academic schedules, with reliable data persistence and comprehensive error handling.
