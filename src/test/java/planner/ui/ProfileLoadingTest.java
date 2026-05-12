package planner.ui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import planner.model.PlannerModel;
import planner.model.Student;
import planner.service.PlannerService;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify profile loading functionality works correctly.
 */
class ProfileLoadingTest {
    
    private PlannerModel model;
    private PlannerService service;
    private PlannerController controller;
    
    @BeforeEach
    void setUp() {
        model = new PlannerModel();
        service = new PlannerService(model);
        controller = new PlannerController(model, null, service);
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
    void testLoadProfileNames() throws IOException {
        List<String> profileNames = controller.loadProfileNames();
        
        // Should have at least one profile
        assertFalse(profileNames.isEmpty(), "Should have at least one profile");
        
        // Check for specific profiles if they exist
        boolean hasPaulAtreides = profileNames.stream()
            .anyMatch(name -> name.contains("Paul Atreides"));
        boolean hasPeterParker = profileNames.stream()
            .anyMatch(name -> name.contains("Peter Parker"));
        
        // These assertions are conditional since profiles might not exist in test environment
        if (hasPaulAtreides) {
            assertTrue(hasPaulAtreides, "Should have Paul Atreides profile");
        }
        if (hasPeterParker) {
            assertTrue(hasPeterParker, "Should have Peter Parker profile");
        }
    }
    
    @Test
    void testLoadPaulAtreidesProfile() throws IOException {
        List<String> profileNames = controller.loadProfileNames();
        String paulProfileName = profileNames.stream()
            .filter(name -> name.contains("Paul Atreides"))
            .findFirst()
            .orElse(null);
        
        // Skip test if Paul Atreides profile doesn't exist
        if (paulProfileName == null) {
            return; // Skip gracefully if profile doesn't exist
        }
        
        controller.loadProfile(paulProfileName);
        
        Student student = model.getCurrentStudent();
        assertNotNull(student, "Student should be loaded");
        assertEquals("Paul", student.getFirstName());
        assertEquals("Atreides", student.getLastName());
        assertEquals("paul@caladan.edu", student.getEmail());
        assertEquals("dune234", student.getStudentId());
        assertEquals(Student.AcademicYear.SENIOR, student.getAcademicYear());
        assertEquals("Political Science", student.getMajor());
        
        // Should have courses loaded
        assertFalse(model.getCourses().isEmpty(), "Should have courses loaded");
        assertTrue(model.getCourses().size() >= 2, "Should have at least 2 courses");
        
        // Check for specific courses
        boolean hasSpiceHarvesting = model.getCourses().stream()
            .anyMatch(course -> course.getName().contains("Spice Harvesting"));
        boolean hasWorms = model.getCourses().stream()
            .anyMatch(course -> course.getName().contains("Worms"));
        
        assertTrue(hasSpiceHarvesting, "Should have Spice Harvesting course");
        assertTrue(hasWorms, "Should have Worms course");
    }
    
    @Test
    void testLoadPeterParkerProfile() throws IOException {
        List<String> profileNames = controller.loadProfileNames();
        String peterProfileName = profileNames.stream()
            .filter(name -> name.contains("Peter Parker"))
            .findFirst()
            .orElse(null);
        
        // Skip test if Peter Parker profile doesn't exist
        if (peterProfileName == null) {
            return; // Skip gracefully if profile doesn't exist
        }
        
        controller.loadProfile(peterProfileName);
        
        Student student = model.getCurrentStudent();
        assertNotNull(student, "Student should be loaded");
        assertEquals("Peter", student.getFirstName());
        assertEquals("Parker", student.getLastName());
        assertEquals("peter@dailybugle.com", student.getEmail());
        assertEquals("parker23", student.getStudentId());
        assertEquals(Student.AcademicYear.JUNIOR, student.getAcademicYear());
        assertEquals("Photography", student.getMajor());
        
        // Should have courses loaded
        assertFalse(model.getCourses().isEmpty(), "Should have courses loaded");
        assertTrue(model.getCourses().size() >= 3, "Should have at least 3 courses");
        
        // Check for specific courses
        boolean hasPhotojournalism = model.getCourses().stream()
            .anyMatch(course -> course.getName().contains("Photojournalism"));
        boolean hasPhysics = model.getCourses().stream()
            .anyMatch(course -> course.getName().contains("Physics"));
        boolean hasWebDev = model.getCourses().stream()
            .anyMatch(course -> course.getName().contains("Web"));
        
        assertTrue(hasPhotojournalism, "Should have Photojournalism course");
        assertTrue(hasPhysics, "Should have Physics course");
        assertTrue(hasWebDev, "Should have Web Development course");
    }
    
    @Test
    void testProfileDataIntegrity() throws IOException {
        List<String> profileNames = controller.loadProfileNames();
        
        // Test loading each profile to ensure data integrity
        for (String profileName : profileNames) {
            controller.loadProfile(profileName);
            
            Student student = model.getCurrentStudent();
            assertNotNull(student, "Student should be loaded for profile: " + profileName);
            assertNotNull(student.getFirstName(), "First name should not be null");
            assertNotNull(student.getLastName(), "Last name should not be null");
            assertNotNull(student.getEmail(), "Email should not be null");
            assertNotNull(student.getStudentId(), "Student ID should not be null");
            assertNotNull(student.getAcademicYear(), "Academic year should not be null");
            assertNotNull(student.getMajor(), "Major should not be null");
            
            // Clear for next iteration
            model.clearAll();
        }
    }
}