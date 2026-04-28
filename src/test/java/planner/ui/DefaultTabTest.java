package planner.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import planner.model.PlannerModel;
import planner.model.Student;
import planner.persistence.PlannerRepository;
import planner.service.PlannerService;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Test class for Default Tab functionality.
 */
class DefaultTabTest {
    
    @TempDir
    Path tempDir;
    
    private PlannerModel model;
    private PlannerController controller;
    private PlannerView view;
    private PlannerRepository repository;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
        PlannerService service = new PlannerService(model);
        view = new PlannerView(model, controller);
        controller = new PlannerController(model, view, service);
        view.setModel(model);
        view.setController(controller);
        repository = new PlannerRepository();
    }
    
    @Test
    void testDefaultTabPersistence() throws IOException {
        // Test saving and loading default tab preferences
        
        // 1. Save default tab as "Today"
        controller.savePreferences("Today");
        
        // 2. Load and verify
        String loadedDefault = controller.loadPreferences();
        assertEquals("Today", loadedDefault);
        
        // 3. Save different default tab
        controller.savePreferences("Weekly Priorities");
        
        // 4. Load and verify
        loadedDefault = controller.loadPreferences();
        assertEquals("Weekly Priorities", loadedDefault);
        
        // 5. Save invalid tab (should fallback to "Tasks")
        controller.savePreferences("Invalid Tab");
        loadedDefault = controller.loadPreferences();
        assertEquals("Tasks", loadedDefault);
    }
    
    @Test
    void testDefaultTabValidation() throws IOException {
        // Test that only valid tabs are accepted
        
        // Valid tabs should be saved and loaded
        String[] validTabs = {"Tasks", "Today", "Weekly Priorities", "Calendar"};
        
        for (String tab : validTabs) {
            controller.savePreferences(tab);
            String loaded = controller.loadPreferences();
            assertEquals(tab, loaded, "Valid tab '" + tab + "' should be saved and loaded correctly");
        }
    }
    
    @Test
    void testDefaultTabFallback() throws IOException {
        // Test fallback behavior when no preferences exist
        
        // Delete preferences file to simulate first run
        Path preferencesPath = Path.of("planner_data", "preferences.csv");
        try {
            java.nio.file.Files.deleteIfExists(preferencesPath);
        } catch (IOException e) {
            // Ignore if file doesn't exist
        }
        
        // Load preferences (should default to "Tasks")
        String defaultTab = controller.loadPreferences();
        assertEquals("Tasks", defaultTab, "Should default to 'Tasks' when no preferences exist");
    }
    
    @Test
    void testDefaultTabIntegration() throws IOException {
        // Test complete workflow of setting and using default tab
        
        // 1. Set default tab
        controller.savePreferences("Calendar");
        
        // 2. Verify it was saved
        String savedDefault = controller.loadPreferences();
        assertEquals("Calendar", savedDefault);
        
        // 3. Create a student and save
        Student student = new Student("Test", "User", "test@example.com", 
                                     "S99999", Student.AcademicYear.JUNIOR, "Computer Science");
        model.setCurrentStudent(student);
        
        // 4. Verify student is set
        Student currentStudent = model.getCurrentStudent();
        assertNotNull(currentStudent);
        assertEquals("Test", currentStudent.getFirstName());
        
        // 5. Test that default tab preference is independent
        // (Changing student shouldn't affect default tab)
        savedDefault = controller.loadPreferences();
        assertEquals("Calendar", savedDefault, "Default tab should remain unchanged");
    }
    
    @Test
    void testDefaultTabEdgeCases() throws IOException {
        // Test edge cases and error handling
        
        // Test null input (should not crash)
        assertDoesNotThrow(() -> {
            controller.savePreferences(null);
        });
        
        // Load after saving null (should fallback to "Tasks")
        String defaultTab = controller.loadPreferences();
        assertEquals("Tasks", defaultTab, "Should fallback to 'Tasks' when null is saved");
        
        // Test empty string input
        assertDoesNotThrow(() -> {
            controller.savePreferences("");
        });
        
        // Load after saving empty string (should fallback to "Tasks")
        defaultTab = controller.loadPreferences();
        assertEquals("Tasks", defaultTab, "Should fallback to 'Tasks' when empty string is saved");
    }
}
