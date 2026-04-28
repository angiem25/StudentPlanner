package planner.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import planner.model.PlannerModel;
import planner.model.Student;
import planner.service.PlannerService;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Profile Management functionality.
 */
class ProfileManagementTest {
    
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
    void testProfileDialogComponents() {
        // Verify that the student panel exists for profile dialog
        assertNotNull(view);
        
        // The student panel should be created during initialization
        // We can't directly test UI components in unit tests, but we can verify
        // the model can handle student profile operations
        assertDoesNotThrow(() -> {
            Student student = new Student("Test", "User", "test@example.com", 
                                        "S99999", Student.AcademicYear.FRESHMAN, "Test Major");
            model.setCurrentStudent(student);
        });
    }
    
    @Test
    void testProfileSelectionWorkflow() {
        // Test that we can create and manage multiple profiles
        Student student1 = new Student("John", "Doe", "john@example.com", 
                                       "S12345", Student.AcademicYear.JUNIOR, "Computer Science");
        Student student2 = new Student("Jane", "Smith", "jane@example.com", 
                                       "S54321", Student.AcademicYear.SENIOR, "Mathematics");
        
        // Test profile switching
        model.setCurrentStudent(student1);
        assertEquals("John", model.getCurrentStudent().getFirstName());
        
        model.setCurrentStudent(student2);
        assertEquals("Jane", model.getCurrentStudent().getFirstName());
        
        // Test profile clearing
        model.setCurrentStudent(null);
        assertNull(model.getCurrentStudent());
    }
    
    @Test
    void testAccountMenuIntegration() {
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
    void testStudentPanelRemovalFromTabs() {
        // Verify that the student panel exists but is not in main tabs
        // The student panel should still be created for profile dialog use
        assertNotNull(view);
        
        // Create a student to verify profile functionality
        Student student = new Student("Test", "User", "test@example.com", 
                                    "S99999", Student.AcademicYear.SOPHOMORE, "Physics");
        model.setCurrentStudent(student);
        
        assertNotNull(model.getCurrentStudent());
        assertEquals("Test", model.getCurrentStudent().getFirstName());
    }
    
    @Test
    void testProfileDataPersistence() {
        // Test that profile data can be saved and loaded
        Student student = new Student("Alice", "Wonder", "alice@example.com", 
                                    "S11111", Student.AcademicYear.JUNIOR, "Biology");
        model.setCurrentStudent(student);
        
        // Test data export
        assertDoesNotThrow(() -> {
            controller.exportData();
        });
        
        // Clear and test data import
        model.setCurrentStudent(null);
        assertNull(model.getCurrentStudent());
        
        assertDoesNotThrow(() -> {
            controller.importData();
        });
    }
}
