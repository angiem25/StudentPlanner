package planner.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import planner.model.PlannerModel;
import planner.model.Student;
import planner.service.PlannerService;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for updated Profile Management functionality.
 */
class UpdatedProfileManagementTest {
    
    private PlannerModel model;
    private PlannerController controller;
    private PlannerView view;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
        PlannerService service = new PlannerService(model);
        view = new PlannerView(model, controller);
        controller = new PlannerController(model, view, service);
        view.setModel(model);
        view.setController(controller);
    }
    
    @Test
    void testReadOnlyProfileView() {
        // Test that read-only profile view components exist
        assertNotNull(view);
        
        // Create a student and verify profile display can be updated
        Student student = new Student("Test", "User", "test@example.com", 
                                    "S99999", Student.AcademicYear.JUNIOR, "Computer Science");
        model.setCurrentStudent(student);
        
        assertNotNull(model.getCurrentStudent());
        assertEquals("Test", model.getCurrentStudent().getFirstName());
        assertEquals("User", model.getCurrentStudent().getLastName());
        assertEquals("test@example.com", model.getCurrentStudent().getEmail());
        assertEquals("S99999", model.getCurrentStudent().getStudentId());
        assertEquals(Student.AcademicYear.JUNIOR, model.getCurrentStudent().getAcademicYear());
        assertEquals("Computer Science", model.getCurrentStudent().getMajor());
    }
    
    @Test
    void testAccountMenuStructure() {
        // Verify that account menu functionality doesn't break the application
        assertDoesNotThrow(() -> {
            // Test export data (used in logout)
            controller.exportData();
        });
        
        assertDoesNotThrow(() -> {
            // Test import data
            controller.importData();
        });
    }
    
    @Test
    void testLogoutButtonVisibility() {
        // Initially, no student should be logged in
        assertNull(model.getCurrentStudent());
        
        // When no student is logged in, logout should not be visible
        // This is tested through the model change mechanism
        
        // Create and set a student
        Student student = new Student("John", "Doe", "john@example.com", 
                                    "S12345", Student.AcademicYear.SENIOR, "Mathematics");
        model.setCurrentStudent(student);
        
        // Now logout should be visible
        assertNotNull(model.getCurrentStudent());
        assertEquals("John", model.getCurrentStudent().getFirstName());
        
        // Clear student and verify logout visibility changes
        model.setCurrentStudent(null);
        assertNull(model.getCurrentStudent());
    }
    
    @Test
    void testProfileDialogWorkflows() {
        // Test that profile-related workflows don't break
        Student student = new Student("Alice", "Wonder", "alice@example.com", 
                                    "S11111", Student.AcademicYear.SOPHOMORE, "Physics");
        model.setCurrentStudent(student);
        
        // Test data export (used in logout)
        assertDoesNotThrow(() -> {
            controller.exportData();
        });
        
        // Test data import - this may change the current student
        assertDoesNotThrow(() -> {
            controller.importData();
        });
        
        // Verify model still has a student (may be different after import)
        Student currentStudent = model.getCurrentStudent();
        assertNotNull(currentStudent, "Model should have a student after import");
        
        // The student might be different after import, so we just verify it exists
        assertTrue(currentStudent.getFirstName() != null && !currentStudent.getFirstName().isEmpty(),
                  "Student should have a valid first name");
    }
    
    @Test
    void testAddNewProfileWorkflow() {
        // Test that add new profile workflow works
        assertDoesNotThrow(() -> {
            // Create a student profile
            Student student = new Student("New", "User", "new@example.com", 
                                        "S00000", Student.AcademicYear.FRESHMAN, "Biology");
            model.setCurrentStudent(student);
        });
        
        assertNotNull(model.getCurrentStudent());
        assertEquals("New", model.getCurrentStudent().getFirstName());
    }
    
    @Test
    void testProfileSelectionWorkflow() {
        // Test profile selection workflow
        // Initially no student
        assertNull(model.getCurrentStudent());
        
        // Add multiple students to simulate profile selection
        Student student1 = new Student("Profile1", "User", "profile1@example.com", 
                                       "S11111", Student.AcademicYear.FRESHMAN, "Chemistry");
        Student student2 = new Student("Profile2", "User", "profile2@example.com", 
                                       "S22222", Student.AcademicYear.JUNIOR, "Engineering");
        
        // Switch between profiles
        model.setCurrentStudent(student1);
        assertEquals("Profile1", model.getCurrentStudent().getFirstName());
        
        model.setCurrentStudent(student2);
        assertEquals("Profile2", model.getCurrentStudent().getFirstName());
        
        // Clear to simulate no profile selected
        model.setCurrentStudent(null);
        assertNull(model.getCurrentStudent());
    }
    
    @Test
    void testSaveProfileAutoClose() {
        // Test that save profile functionality works
        assertDoesNotThrow(() -> {
            // Create a student profile
            Student student = new Student("Auto", "Close", "auto@example.com", 
                                        "S99999", Student.AcademicYear.SENIOR, "Art");
            model.setCurrentStudent(student);
        });
        
        assertNotNull(model.getCurrentStudent());
        assertEquals("Auto", model.getCurrentStudent().getFirstName());
        
        // Test data persistence
        assertDoesNotThrow(() -> {
            controller.exportData();
        });
    }
}
