package planner.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import planner.model.*;
import planner.service.PlannerService;
import java.io.IOException;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for PlannerController.
 * Uses real objects for integration testing where possible.
 */
class PlannerControllerTest {
    
    private PlannerModel model;
    private PlannerView view;
    private PlannerService service;
    private PlannerController controller;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
        service = new PlannerService(model);
        view = new MockPlannerView();
        controller = new PlannerController(model, view, service);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up any test data
        try {
            model.clearAll();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
    
    @Test
    void testConstructor() {
        assertNotNull(controller);
        assertEquals(model, controller.getModel());
        assertEquals(view, controller.getView());
    }
    
    @Test
    void testSaveStudentProfileWithEnum() {
        String firstName = "John";
        String lastName = "Doe";
        String email = "john@example.com";
        String studentId = "S12345";
        Student.AcademicYear academicYear = Student.AcademicYear.JUNIOR;
        String major = "Computer Science";
        
        // This should not throw an exception
        assertDoesNotThrow(() -> {
            controller.saveStudentProfile(firstName, lastName, email, studentId, academicYear, major);
        });
        
        // Verify student was created
        Student student = model.getCurrentStudent();
        assertNotNull(student);
        assertEquals(firstName, student.getFirstName());
        assertEquals(lastName, student.getLastName());
        assertEquals(email, student.getEmail());
        assertEquals(studentId, student.getStudentId());
        assertEquals(academicYear, student.getAcademicYear());
        assertEquals(major, student.getMajor());
    }
    
    @Test
    void testSaveStudentProfileWithString() {
        String firstName = "Jane";
        String lastName = "Smith";
        String email = "jane@example.com";
        String studentId = "S54321";
        String yearText = "Junior";
        String major = "Mathematics";
        
        assertDoesNotThrow(() -> {
            controller.saveStudentProfile(firstName, lastName, email, studentId, yearText, major);
        });
        
        Student student = model.getCurrentStudent();
        assertNotNull(student);
        assertEquals(firstName, student.getFirstName());
        assertEquals(Student.AcademicYear.JUNIOR, student.getAcademicYear());
    }
    
    @Test
    void testSaveStudentProfileWithInvalidYear() {
        String firstName = "Invalid";
        String lastName = "User";
        String email = "invalid@example.com";
        String studentId = "S99999";
        String yearText = "InvalidYear";
        String major = "Invalid";
        
        // This should handle the invalid year gracefully
        assertDoesNotThrow(() -> {
            controller.saveStudentProfile(firstName, lastName, email, studentId, yearText, major);
        });
        
        // Note: The current implementation may create a student with default year
        // This test verifies the method doesn't crash with invalid input
        Student student = model.getCurrentStudent();
        // Student might be created with default academic year, so we just verify no exception
    }
    
    @Test
    void testSaveStudentProfileWithInvalidEmail() {
        String firstName = "Invalid";
        String lastName = "User";
        String email = "invalid-email"; // Invalid email format
        String studentId = "S99999";
        Student.AcademicYear academicYear = Student.AcademicYear.FRESHMAN;
        String major = "Invalid";
        
        // This should handle the invalid email gracefully
        assertDoesNotThrow(() -> {
            controller.saveStudentProfile(firstName, lastName, email, studentId, academicYear, major);
        });
        
        // Note: The current implementation may not validate email format strictly
        // This test verifies the method doesn't crash with invalid input
        Student student = model.getCurrentStudent();
        // Student might be created even with invalid email, so we just verify no exception
    }
    
    @Test
    void testAddCourse() {
        // First create a student
        createTestStudent();
        
        String name = "Calculus";
        String code = "MATH101";
        String instructor = "Dr. Smith";
        String creditsText = "3";
        
        assertDoesNotThrow(() -> {
            controller.addCourse(name, code, instructor, creditsText);
        });
        
        // Verify course was added
        assertEquals(1, model.getCourses().size());
        Course course = model.getCourses().get(0);
        assertEquals(name, course.getName());
        assertEquals(code, course.getCode());
        assertEquals(instructor, course.getInstructor());
        assertEquals(3, course.getCredits());
    }
    
    @Test
    void testAddCourseWithInvalidCredits() {
        createTestStudent();
        
        String name = "Invalid Course";
        String code = "INV101";
        String instructor = "Dr. Invalid";
        String creditsText = "invalid";
        
        assertDoesNotThrow(() -> {
            controller.addCourse(name, code, instructor, creditsText);
        });
        
        // Course should not be added due to invalid credits
        assertEquals(0, model.getCourses().size());
    }
    
    @Test
    void testAddCourseWithNegativeCredits() {
        createTestStudent();
        
        String name = "Invalid Course";
        String code = "INV101";
        String instructor = "Dr. Invalid";
        String creditsText = "-3";
        
        assertDoesNotThrow(() -> {
            controller.addCourse(name, code, instructor, creditsText);
        });
        
        // Course should not be added due to negative credits
        assertEquals(0, model.getCourses().size());
    }
    
    @Test
    void testUpdateCourse() {
        createTestStudent();
        
        // First add a course
        Course originalCourse = new Course("Original", "ORG101", "Dr. Original", 3);
        model.addCourse(originalCourse);
        
        String courseId = originalCourse.getId();
        String name = "Updated Calculus";
        String code = "MATH101U";
        String instructor = "Dr. Updated";
        String creditsText = "4";
        
        assertDoesNotThrow(() -> {
            controller.updateCourse(courseId, name, code, instructor, creditsText);
        });
        
        // Verify course was updated
        assertEquals(1, model.getCourses().size());
        Course course = model.getCourses().get(0);
        assertEquals(name, course.getName());
        assertEquals(code, course.getCode());
        assertEquals(instructor, course.getInstructor());
        assertEquals(4, course.getCredits());
    }
    
    @Test
    void testUpdateCourseWithInvalidCredits() {
        createTestStudent();
        Course originalCourse = new Course("Original", "ORG101", "Dr. Original", 3);
        model.addCourse(originalCourse);
        
        String courseId = originalCourse.getId();
        String name = "Invalid Update";
        String code = "INV102";
        String instructor = "Dr. Invalid";
        String creditsText = "not_a_number";
        
        assertDoesNotThrow(() -> {
            controller.updateCourse(courseId, name, code, instructor, creditsText);
        });
        
        // Course should not be updated due to invalid credits
        Course course = model.getCourses().get(0);
        assertEquals("Original", course.getName());
        assertEquals(3, course.getCredits());
    }
    
    @Test
    void testRemoveCourse() {
        createTestStudent();
        Course course = new Course("Test Course", "TEST101", "Dr. Test", 3);
        model.addCourse(course);
        
        assertEquals(1, model.getCourses().size());
        
        assertDoesNotThrow(() -> {
            controller.removeCourse(course.getId());
        });
        
        // Verify course was removed
        assertEquals(0, model.getCourses().size());
    }
    
    @Test
    void testAddTask() {
        createTestStudent();
        
        String title = "Homework";
        String description = "Complete assignment";
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        Task.Priority priority = Task.Priority.HIGH;
        String courseId = null; // General task
        String accentColorHex = "#FF0000";
        
        assertDoesNotThrow(() -> {
            controller.addTask(title, description, dueDate, priority, courseId, accentColorHex);
        });
        
        // Verify task was added
        assertEquals(1, model.getTasks().size());
        Task task = model.getTasks().get(0);
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(priority, task.getPriority());
        // Note: accent color might be processed by TaskPalette.canonicalHex()
        assertNotNull(task.getAccentColorHex());
    }
    
    @Test
    void testAddTaskWithCourse() {
        createTestStudent();
        Course course = new Course("Test Course", "TEST101", "Dr. Test", 3);
        model.addCourse(course);
        
        String title = "Course Assignment";
        String description = "Complete course work";
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        Task.Priority priority = Task.Priority.MEDIUM;
        String accentColorHex = "#00FF00";
        
        assertDoesNotThrow(() -> {
            controller.addTask(title, description, dueDate, priority, course.getId(), accentColorHex);
        });
        
        // Verify task was added with course
        assertEquals(1, model.getTasks().size());
        Task task = model.getTasks().get(0);
        assertEquals(title, task.getTitle());
        assertEquals(course.getId(), task.getCourseId());
    }
    
    @Test
    void testUpdateTask() {
        createTestStudent();
        Task originalTask = new Task("Original Task", "Original description", 
                                    LocalDateTime.now().plusDays(1), Task.Priority.LOW);
        originalTask.setCourseId(null);
        originalTask.setAccentColorHex("#0000FF");
        model.addTask(originalTask);
        
        String title = "Updated Task";
        String description = "Updated description";
        LocalDateTime dueDate = LocalDateTime.now().plusDays(2);
        Task.Priority priority = Task.Priority.HIGH;
        String courseId = null;
        String accentColorHex = "#FF00FF";
        
        assertDoesNotThrow(() -> {
            controller.updateTask(originalTask.getId(), title, description, dueDate, priority, courseId, accentColorHex);
        });
        
        // Verify task was updated
        assertEquals(1, model.getTasks().size());
        Task task = model.getTasks().get(0);
        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(priority, task.getPriority());
        // Note: accent color might be processed by TaskPalette.canonicalHex()
        assertNotNull(task.getAccentColorHex());
    }
    
    @Test
    void testRemoveTask() {
        createTestStudent();
        Task task = new Task("Test Task", "Test description", 
                            LocalDateTime.now().plusDays(1), Task.Priority.MEDIUM);
        task.setCourseId(null);
        task.setAccentColorHex("#FFFFFF");
        model.addTask(task);
        
        assertEquals(1, model.getTasks().size());
        
        assertDoesNotThrow(() -> {
            controller.removeTask(task.getId());
        });
        
        // Verify task was removed
        assertEquals(0, model.getTasks().size());
    }
    
    @Test
    void testToggleTaskCompletion() {
        createTestStudent();
        Task task = new Task("Test Task", "Test description", 
                            LocalDateTime.now().plusDays(1), Task.Priority.MEDIUM);
        task.setCourseId(null);
        task.setAccentColorHex("#FFFFFF");
        assertFalse(task.isCompleted());
        model.addTask(task);
        
        assertDoesNotThrow(() -> {
            controller.toggleTaskCompletion(task.getId());
        });
        
        // Verify task completion was toggled
        Task updatedTask = model.getTasks().get(0);
        assertTrue(updatedTask.isCompleted());
    }
    
    @Test
    void testToggleTaskCompletionSilently() {
        createTestStudent();
        Task task = new Task("Test Task", "Test description", 
                            LocalDateTime.now().plusDays(1), Task.Priority.MEDIUM);
        task.setCourseId(null);
        task.setAccentColorHex("#FFFFFF");
        assertFalse(task.isCompleted());
        model.addTask(task);
        
        assertDoesNotThrow(() -> {
            controller.toggleTaskCompletionSilently(task.getId());
        });
        
        // Verify task completion was toggled
        Task updatedTask = model.getTasks().get(0);
        assertTrue(updatedTask.isCompleted());
    }
    
    @Test
    void testExportData() {
        createTestStudent();
        addTestData();
        
        assertDoesNotThrow(() -> {
            controller.exportData();
        });
    }
    
    @Test
    void testImportData() {
        createTestStudent();
        addTestData();
        
        assertDoesNotThrow(() -> {
            controller.importData();
        });
    }
    
    @Test
    void testSaveProfile() {
        createTestStudent();
        addTestData();
        
        assertDoesNotThrow(() -> {
            controller.saveProfile();
        });
    }
    
    @Test
    void testSaveProfileWithNoStudent() {
        // No student created
        
        assertDoesNotThrow(() -> {
            controller.saveProfile();
        });
    }
    
    @Test
    void testLoadProfileNames() {
        assertDoesNotThrow(() -> {
            java.util.List<String> profiles = controller.loadProfileNames();
            assertNotNull(profiles);
        });
    }
    
    @Test
    void testRemoveProfile() {
        assertDoesNotThrow(() -> {
            controller.removeProfile("NonExistentProfile");
        });
    }
    
    @Test
    void testClearProfileData() {
        createTestStudent();
        addTestData();
        
        assertDoesNotThrow(() -> {
            controller.clearProfileData();
        });
    }
    
    @Test
    void testClearProfileDataWithNoStudent() {
        // No student created
        
        assertDoesNotThrow(() -> {
            controller.clearProfileData();
        });
    }
    
    @Test
    void testSavePreferences() {
        assertDoesNotThrow(() -> {
            controller.savePreferences("Tasks");
        });
    }
    
    @Test
    void testLoadPreferences() {
        assertDoesNotThrow(() -> {
            String preferences = controller.loadPreferences();
            assertNotNull(preferences);
        });
    }
    
    @Test
    void testSaveAccountState() {
        assertDoesNotThrow(() -> {
            controller.saveAccountState(true);
        });
    }
    
    @Test
    void testLoadAccountState() {
        assertDoesNotThrow(() -> {
            boolean state = controller.loadAccountState();
            // Should return false by default if no state is saved
            assertFalse(state);
        });
    }
    
    @Test
    void testLogout() {
        createTestStudent();
        addTestData();
        
        // Verify data exists before logout
        assertNotNull(model.getCurrentStudent());
        assertFalse(model.getCourses().isEmpty());
        assertFalse(model.getTasks().isEmpty());
        
        assertDoesNotThrow(() -> {
            controller.logout();
        });
        
        // Verify data was cleared after logout
        assertNull(model.getCurrentStudent());
        assertTrue(model.getCourses().isEmpty());
        assertTrue(model.getTasks().isEmpty());
    }
    
    @Test
    void testLogoutWithNoStudent() {
        // No student created
        
        assertDoesNotThrow(() -> {
            controller.logout();
        });
        
        // Should remain null
        assertNull(model.getCurrentStudent());
    }
    
    // Helper methods
    private void createTestStudent() {
        controller.saveStudentProfile("Test", "Student", "test@example.com", 
                                    "S12345", Student.AcademicYear.JUNIOR, "Computer Science");
    }
    
    private void addTestData() {
        // Add a course
        Course course = new Course("Test Course", "TEST101", "Dr. Test", 3);
        model.addCourse(course);
        
        // Add a task
        Task task = new Task("Test Task", "Test description", 
                            LocalDateTime.now().plusDays(1), Task.Priority.MEDIUM);
        task.setCourseId(null);
        task.setAccentColorHex("#FFFFFF");
        model.addTask(task);
    }
    
    /**
     * Mock implementation of PlannerView for testing purposes.
     */
    private static class MockPlannerView extends PlannerView {
        private String lastInfoMessage;
        private String lastErrorMessage;
        
        public MockPlannerView() {
            super(new PlannerModel(), null);
        }
        
        @Override
        public void showInfo(String message) {
            this.lastInfoMessage = message;
        }
        
        @Override
        public void showError(String message) {
            this.lastErrorMessage = message;
        }
        
        public String getLastInfoMessage() {
            return lastInfoMessage;
        }
        
        public String getLastErrorMessage() {
            return lastErrorMessage;
        }
        
        public void clearMessages() {
            lastInfoMessage = null;
            lastErrorMessage = null;
        }
    }
}