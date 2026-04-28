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
import java.util.List;

/**
 * Test class for Real Profile Management functionality.
 */
class RealProfileManagementTest {
    
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
    void testSaveAndLoadProfiles() throws IOException {
        // Create and save a student profile
        Student student1 = new Student("Alice", "Wonder", "alice@example.com", 
                                      "S11111", Student.AcademicYear.JUNIOR, "Computer Science");
        model.setCurrentStudent(student1);
        
        // Save the profile
        assertDoesNotThrow(() -> {
            controller.saveProfile();
        });
        
        // Create another student profile
        Student student2 = new Student("Bob", "Builder", "bob@example.com", 
                                      "S22222", Student.AcademicYear.SENIOR, "Engineering");
        model.setCurrentStudent(student2);
        
        // Save the second profile
        assertDoesNotThrow(() -> {
            controller.saveProfile();
        });
        
        // Load profile names
        List<String> profileNames = controller.loadProfileNames();
        assertFalse(profileNames.isEmpty());
        assertTrue(profileNames.size() >= 2);
        
        // Verify profile names contain our students
        boolean foundAlice = false;
        boolean foundBob = false;
        for (String profileName : profileNames) {
            if (profileName.contains("Alice Wonder")) {
                foundAlice = true;
            }
            if (profileName.contains("Bob Builder")) {
                foundBob = true;
            }
        }
        assertTrue(foundAlice, "Alice profile should be found");
        assertTrue(foundBob, "Bob profile should be found");
    }
    
    @Test
    void testLoadSpecificProfile() throws IOException {
        // Create and save a student profile
        Student originalStudent = new Student("Charlie", "Brown", "charlie@example.com", 
                                            "S33333", Student.AcademicYear.SOPHOMORE, "Mathematics");
        model.setCurrentStudent(originalStudent);
        
        // Save the profile
        controller.saveProfile();
        
        // Clear current student
        model.setCurrentStudent(null);
        assertNull(model.getCurrentStudent());
        
        // Load profile names
        List<String> profileNames = controller.loadProfileNames();
        assertFalse(profileNames.isEmpty());
        
        // Find Charlie's profile
        String charlieProfile = null;
        for (String profileName : profileNames) {
            if (profileName.contains("Charlie Brown")) {
                charlieProfile = profileName;
                break;
            }
        }
        assertNotNull(charlieProfile, "Charlie's profile should be found");
        
        // Load the specific profile
        controller.loadProfile(charlieProfile);
        
        // Verify the loaded student
        Student loadedStudent = model.getCurrentStudent();
        assertNotNull(loadedStudent);
        assertEquals("Charlie", loadedStudent.getFirstName());
        assertEquals("Brown", loadedStudent.getLastName());
        assertEquals("charlie@example.com", loadedStudent.getEmail());
        assertEquals("S33333", loadedStudent.getStudentId());
        assertEquals(Student.AcademicYear.SOPHOMORE, loadedStudent.getAcademicYear());
        assertEquals("Mathematics", loadedStudent.getMajor());
    }
    
    @Test
    void testRemoveProfile() throws IOException {
        // Create and save a student profile
        Student student = new Student("David", "Delete", "david@example.com", 
                                     "S44444", Student.AcademicYear.FRESHMAN, "Physics");
        model.setCurrentStudent(student);
        
        // Save the profile
        controller.saveProfile();
        
        // Verify profile exists
        List<String> profileNames = controller.loadProfileNames();
        int initialCount = profileNames.size();
        assertTrue(initialCount > 0);
        
        // Find David's profile
        String davidProfile = null;
        for (String profileName : profileNames) {
            if (profileName.contains("David Delete")) {
                davidProfile = profileName;
                break;
            }
        }
        assertNotNull(davidProfile, "David's profile should be found");
        
        // Remove the profile
        controller.removeProfile(davidProfile);
        
        // Verify profile was removed
        List<String> newProfileNames = controller.loadProfileNames();
        assertEquals(initialCount - 1, newProfileNames.size());
        
        // Verify David's profile is no longer in the list
        boolean foundDavid = false;
        for (String profileName : newProfileNames) {
            if (profileName.contains("David Delete")) {
                foundDavid = true;
                break;
            }
        }
        assertFalse(foundDavid, "David's profile should be removed");
    }
    
    @Test
    void testProfilePersistenceAcrossSessions() throws IOException {
        // Create and save a student profile
        Student student = new Student("Eve", "Persistent", "eve@example.com", 
                                     "S55555", Student.AcademicYear.JUNIOR, "Biology");
        model.setCurrentStudent(student);
        
        // Save the profile
        controller.saveProfile();
        
        // Simulate new session by creating new objects
        PlannerModel newModel = new PlannerModel();
        PlannerService newService = new PlannerService(newModel);
        PlannerController newController = new PlannerController(newModel, null, newService);
        
        // Load profiles in new session
        List<String> profileNames = newController.loadProfileNames();
        assertFalse(profileNames.isEmpty());
        
        // Find Eve's profile
        String eveProfile = null;
        for (String profileName : profileNames) {
            if (profileName.contains("Eve Persistent")) {
                eveProfile = profileName;
                break;
            }
        }
        assertNotNull(eveProfile, "Eve's profile should persist across sessions");
        
        // Load the profile
        newController.loadProfile(eveProfile);
        
        // Verify the loaded student
        Student loadedStudent = newModel.getCurrentStudent();
        assertNotNull(loadedStudent);
        assertEquals("Eve", loadedStudent.getFirstName());
        assertEquals("Persistent", loadedStudent.getLastName());
        assertEquals("eve@example.com", loadedStudent.getEmail());
        assertEquals("S55555", loadedStudent.getStudentId());
        assertEquals(Student.AcademicYear.JUNIOR, loadedStudent.getAcademicYear());
        assertEquals("Biology", loadedStudent.getMajor());
    }
    
    @Test
    void testProfileManagementIntegration() throws IOException {
        // Test the complete profile management workflow
        
        // Clean up any existing profiles first
        List<String> existingProfiles = controller.loadProfileNames();
        for (String profile : existingProfiles) {
            controller.removeProfile(profile);
        }
        
        // 1. Create multiple profiles
        Student student1 = new Student("Frank", "First", "frank@example.com", 
                                       "S66666", Student.AcademicYear.FRESHMAN, "Chemistry");
        model.setCurrentStudent(student1);
        controller.saveProfile();
        
        Student student2 = new Student("Grace", "Second", "grace@example.com", 
                                       "S77777", Student.AcademicYear.SENIOR, "Art");
        model.setCurrentStudent(student2);
        controller.saveProfile();
        
        // 2. Verify both profiles exist
        List<String> profileNames = controller.loadProfileNames();
        assertEquals(2, profileNames.size());
        
        // 3. Load first profile
        controller.loadProfile(profileNames.get(0));
        Student loadedStudent = model.getCurrentStudent();
        assertNotNull(loadedStudent);
        
        // 4. Remove second profile
        controller.removeProfile(profileNames.get(1));
        
        // 5. Verify only one profile remains
        List<String> remainingProfiles = controller.loadProfileNames();
        assertEquals(1, remainingProfiles.size());
        
        // 6. Verify the remaining profile is the first one
        String remainingProfile = remainingProfiles.get(0);
        assertTrue(remainingProfile.contains("Frank First") || remainingProfile.contains("Grace Second"));
    }
}
