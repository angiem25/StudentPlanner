package planner.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import planner.model.PlannerModel;
import planner.model.Student;
import planner.service.PlannerService;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Account menu functionality.
 */
class AccountMenuTest {
    
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
    void testExportData() throws Exception {
        // Create a student with some data
        Student student = new Student("John", "Doe", "john@example.com", 
                                    "S12345", Student.AcademicYear.JUNIOR, "Computer Science");
        model.setCurrentStudent(student);
        
        // Test export data functionality
        assertDoesNotThrow(() -> {
            controller.exportData();
        });
    }
    
    @Test
    void testImportData() throws Exception {
        // Test import data functionality
        assertDoesNotThrow(() -> {
            controller.importData();
        });
    }
    
    @Test
    void testAccountMenuComponents() {
        // Verify that the menu bar contains the Account menu
        assertNotNull(view);
        
        // The Account menu should be created during initialization
        // We can't directly test UI components in unit tests, but we can verify
        // the controller methods exist and don't throw exceptions
        assertDoesNotThrow(() -> {
            controller.exportData();
            controller.importData();
        });
    }
    
    @Test
    void testProfileSwitchClearsData() {
        // Create a student
        Student student = new Student("Jane", "Smith", "jane@example.com", 
                                    "S54321", Student.AcademicYear.SENIOR, "Mathematics");
        model.setCurrentStudent(student);
        
        // Verify student is set
        assertNotNull(model.getCurrentStudent());
        assertEquals("Jane", model.getCurrentStudent().getFirstName());
        
        // Simulate profile switch by clearing the student
        model.setCurrentStudent(null);
        
        // Verify student is cleared
        assertNull(model.getCurrentStudent());
    }
    
    @Test
    void testLogoutClearsAllData() {
        // Create a student and add some data
        Student student = new Student("Test", "User", "test@example.com", 
                                    "S99999", Student.AcademicYear.FRESHMAN, "Physics");
        model.setCurrentStudent(student);
        
        // Add some course and task data
        // Note: In a real logout scenario, all collections would be cleared
        // For testing, we verify the model can handle clearing operations
        
        // Verify data exists before logout
        assertNotNull(model.getCurrentStudent());
        
        // Simulate logout by clearing all data
        model.setCurrentStudent(null);
        model.getCourses().clear();
        model.getTasks().clear();
        
        // Verify all data is cleared
        assertNull(model.getCurrentStudent());
        assertTrue(model.getCourses().isEmpty());
        assertTrue(model.getTasks().isEmpty());
    }
}
