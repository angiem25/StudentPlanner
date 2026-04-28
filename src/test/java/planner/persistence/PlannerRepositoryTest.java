package planner.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import planner.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for PlannerRepository class.
 * Tests file-based persistence operations using temporary directories.
 */
class PlannerRepositoryTest {
    
    @TempDir
    Path tempDir;
    
    private TestPlannerRepository repository;
    private PlannerModel model;
    private Student testStudent;
    private Course testCourse;
    private Task testTask;
    private Event testEvent;
    
    @BeforeEach
    void setUp() throws IOException {
        repository = new TestPlannerRepository(tempDir.toString());
        model = new PlannerModel();
        
        testStudent = new Student("John", "Doe", "john@example.com", "S12345", 2, "Computer Science");
        testCourse = new Course("Introduction to Programming", "CS101", "Dr. Smith", 3);
        testTask = new Task("Complete assignment", "Finish chapter 5", 
                           LocalDateTime.now().plusDays(7), Task.Priority.HIGH);
        testEvent = new Event("Study Group", "Group meeting for CS101",
                             LocalDateTime.now().plusDays(1).withHour(14),
                             LocalDateTime.now().plusDays(1).withHour(16));
    }
    
    @AfterEach
    void tearDown() throws IOException {
        try {
            repository.deleteAllData();
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }
    
    // Save and Load Tests
    @Test
    @DisplayName("Should save and load student data")
    void testSaveAndLoadStudent() throws IOException {
        model.setCurrentStudent(testStudent);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertNotNull(newModel.getCurrentStudent());
        assertEquals("John", newModel.getCurrentStudent().getFirstName());
        assertEquals("Doe", newModel.getCurrentStudent().getLastName());
        assertEquals("john@example.com", newModel.getCurrentStudent().getEmail());
    }
    
    @Test
    @DisplayName("Should save and load course data")
    void testSaveAndLoadCourses() throws IOException {
        model.addCourse(testCourse);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getCourses().size());
        Course loaded = newModel.getCourses().get(0);
        assertEquals("Introduction to Programming", loaded.getName());
        assertEquals("CS101", loaded.getCode());
        assertEquals("Dr. Smith", loaded.getInstructor());
        assertEquals(3, loaded.getCredits());
    }
    
    @Test
    @DisplayName("Should save and load task data")
    void testSaveAndLoadTasks() throws IOException {
        model.addTask(testTask);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getTasks().size());
        Task loaded = newModel.getTasks().get(0);
        assertEquals("Complete assignment", loaded.getTitle());
        assertEquals("Finish chapter 5", loaded.getDescription());
        assertEquals(Task.Priority.HIGH, loaded.getPriority());
    }
    
    @Test
    @DisplayName("Should save and load event data")
    void testSaveAndLoadEvents() throws IOException {
        model.addEvent(testEvent);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getEvents().size());
        Event loaded = newModel.getEvents().get(0);
        assertEquals("Study Group", loaded.getTitle());
        assertEquals("Group meeting for CS101", loaded.getDescription());
    }
    
    @Test
    @DisplayName("Should save and load complete model")
    void testSaveAndLoadCompleteModel() throws IOException {
        model.setCurrentStudent(testStudent);
        model.addCourse(testCourse);
        testTask.setCourseId(testCourse.getId());
        model.addTask(testTask);
        model.addEvent(testEvent);
        
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertNotNull(newModel.getCurrentStudent());
        assertEquals(1, newModel.getCourses().size());
        assertEquals(1, newModel.getTasks().size());
        assertEquals(1, newModel.getEvents().size());
    }
    
    // Edge Cases Tests
    @Test
    @DisplayName("Should handle empty model save/load")
    void testSaveAndLoadEmptyModel() throws IOException {
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertNull(newModel.getCurrentStudent());
        assertEquals(0, newModel.getCourses().size());
        assertEquals(0, newModel.getTasks().size());
        assertEquals(0, newModel.getEvents().size());
    }
    
    @Test
    @DisplayName("Should handle null student save")
    void testSaveNullStudent() throws IOException {
        model.setCurrentStudent(null);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertNull(newModel.getCurrentStudent());
    }
    
    @Test
    @DisplayName("Should load when data directory doesn't exist")
    void testLoadWhenNoDataDirectory() throws IOException {
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        // Should not throw exception, just return empty model
        assertEquals(0, newModel.getCourses().size());
    }
    
    // CSV Escaping Tests
    @Test
    @DisplayName("Should handle course names with commas")
    void testCourseNameWithComma() throws IOException {
        Course courseWithComma = new Course("Introduction, to Programming", "CS101", "Dr. Smith", 3);
        model.addCourse(courseWithComma);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getCourses().size());
        assertEquals("Introduction, to Programming", newModel.getCourses().get(0).getName());
    }
    
    @Test
    @DisplayName("Should handle task descriptions with commas")
    void testTaskDescriptionWithComma() throws IOException {
        Task taskWithComma = new Task("Task", "Finish chapter 5, and 6", 
                                      LocalDateTime.now().plusDays(7), Task.Priority.HIGH);
        model.addTask(taskWithComma);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getTasks().size());
        assertEquals("Finish chapter 5, and 6", newModel.getTasks().get(0).getDescription());
    }
    
    @Test
    @DisplayName("Should handle task with null course ID")
    void testTaskWithNullCourseId() throws IOException {
        testTask.setCourseId(null);
        model.addTask(testTask);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getTasks().size());
        assertNull(newModel.getTasks().get(0).getCourseId());
    }
    
    @Test
    @DisplayName("Should handle task with accent color")
    void testTaskWithAccentColor() throws IOException {
        testTask.setAccentColorHex("#FF5733");
        model.addTask(testTask);
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getTasks().size());
        assertEquals("#FF5733", newModel.getTasks().get(0).getAccentColorHex());
    }
    
    // Multiple Items Tests
    @Test
    @DisplayName("Should save and load multiple courses")
    void testMultipleCourses() throws IOException {
        model.addCourse(testCourse);
        model.addCourse(new Course("Math", "MATH101", "Dr. Jones", 4));
        model.addCourse(new Course("Physics", "PHYS101", "Dr. Brown", 3));
        
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(3, newModel.getCourses().size());
    }
    
    @Test
    @DisplayName("Should save and load multiple tasks")
    void testMultipleTasks() throws IOException {
        model.addTask(testTask);
        model.addTask(new Task("Task 2", "Description 2", 
                              LocalDateTime.now().plusDays(5), Task.Priority.MEDIUM));
        model.addTask(new Task("Task 3", "Description 3", 
                              LocalDateTime.now().plusDays(3), Task.Priority.LOW));
        
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(3, newModel.getTasks().size());
    }
    
    // Task Completion State Tests
    @Test
    @DisplayName("Should preserve task completion state")
    void testTaskCompletionState() throws IOException {
        testTask.complete();
        model.addTask(testTask);
        
        repository.savePlannerData(model);
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getTasks().size());
        assertTrue(newModel.getTasks().get(0).isCompleted());
    }
    
    // Delete All Data Tests
    @Test
    @DisplayName("Should delete all data files")
    void testDeleteAllData() throws IOException {
        model.setCurrentStudent(testStudent);
        model.addCourse(testCourse);
        model.addTask(testTask);
        model.addEvent(testEvent);
        
        repository.savePlannerData(model);
        repository.deleteAllData();
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertNull(newModel.getCurrentStudent());
        assertEquals(0, newModel.getCourses().size());
        assertEquals(0, newModel.getTasks().size());
        assertEquals(0, newModel.getEvents().size());
    }
    
    @Test
    @DisplayName("Should handle delete when no data exists")
    void testDeleteAllDataWhenEmpty() throws IOException {
        assertDoesNotThrow(() -> repository.deleteAllData());
    }
    
    // File Corruption Resilience Tests
    @Test
    @DisplayName("Should handle malformed CSV lines gracefully")
    void testMalformedCsvLine() throws IOException {
        model.addCourse(testCourse);
        repository.savePlannerData(model);
        
        // Corrupt the courses file
        Path coursesFile = tempDir.resolve("courses.csv");
        if (Files.exists(coursesFile)) {
            List<String> lines = Files.readAllLines(coursesFile);
            lines.add("invalid,csv,data");
            Files.write(coursesFile, lines);
        }
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        // Should load valid data, skip invalid lines
        assertTrue(newModel.getCourses().size() >= 1);
    }
    
    @Test
    @DisplayName("Should handle empty lines in CSV files")
    void testEmptyLinesInCsv() throws IOException {
        model.addCourse(testCourse);
        repository.savePlannerData(model);
        
        // Add empty lines to courses file
        Path coursesFile = tempDir.resolve("courses.csv");
        if (Files.exists(coursesFile)) {
            List<String> lines = Files.readAllLines(coursesFile);
            lines.add("");
            lines.add("   ");
            Files.write(coursesFile, lines);
        }
        
        PlannerModel newModel = new PlannerModel();
        repository.loadPlannerData(newModel);
        
        assertEquals(1, newModel.getCourses().size());
    }
}
