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
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Integration test class for Default Tab functionality with actual application.
 */
class DefaultTabIntegrationTest {
    
    @TempDir
    Path tempDir;
    
    @Test
    void testDefaultTabWithToday() throws IOException {
        // Test setting "Today" as default and verifying it opens correctly
        
        // 1. Create a preferences file with "Today" as default in the correct directory
        Path dataDir = Path.of("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Clean up any existing file
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        String preferencesContent = "DEFAULT_TAB,Today";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify that preference is saved correctly
        assertTrue(Files.exists(preferencesFile));
        String savedContent = Files.readString(preferencesFile);
        assertEquals(preferencesContent, savedContent);
        
        // 3. Load preference using repository
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Today", loadedDefault);
        
        // 4. Clean up
        Files.deleteIfExists(preferencesFile);
    }
    
    @Test
    void testDefaultTabWithWeeklyPriorities() throws IOException {
        // Test setting "Weekly Priorities" as default and verifying it opens correctly
        
        // 1. Create a preferences file with "Weekly Priorities" as default in the correct directory
        Path dataDir = Path.of("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Clean up any existing file
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        String preferencesContent = "DEFAULT_TAB,Weekly Priorities";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify the preference is saved correctly
        assertTrue(Files.exists(preferencesFile));
        String savedContent = Files.readString(preferencesFile);
        assertEquals(preferencesContent, savedContent);
        
        // 3. Load preference using repository
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Weekly Priorities", loadedDefault);
        
        // 4. Clean up
        Files.deleteIfExists(preferencesFile);
    }
    
    @Test
    void testDefaultTabWithCalendar() throws IOException {
        // Test setting "Calendar" as default and verifying it opens correctly
        
        // 1. Create a preferences file with "Calendar" as default in the correct directory
        Path dataDir = Path.of("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Clean up any existing file
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        String preferencesContent = "DEFAULT_TAB,Calendar";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify the preference is saved correctly
        assertTrue(Files.exists(preferencesFile));
        String savedContent = Files.readString(preferencesFile);
        assertEquals(preferencesContent, savedContent);
        
        // 3. Load preference using repository
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Calendar", loadedDefault);
        
        // 4. Clean up
        Files.deleteIfExists(preferencesFile);
    }
    
    @Test
    void testDefaultTabWithTasks() throws IOException {
        // Test setting "Tasks" as default and verifying it opens correctly
        
        // 1. Create a preferences file with "Tasks" as default in the correct directory
        Path dataDir = Path.of("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Clean up any existing file
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        String preferencesContent = "DEFAULT_TAB,Tasks";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify the preference is saved correctly
        assertTrue(Files.exists(preferencesFile));
        String savedContent = Files.readString(preferencesFile);
        assertEquals(preferencesContent, savedContent);
        
        // 3. Load preference using repository
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
        
        // 4. Clean up
        Files.deleteIfExists(preferencesFile);
    }
    
    @Test
    void testDefaultTabFallbackToTasks() throws IOException {
        // Test that when no preferences exist, it defaults to "Tasks"
        
        // 1. Ensure no preferences file exists in the correct directory
        Path dataDir = Path.of("planner_data");
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Delete preferences file if it exists
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
    }
    
    @Test
    void testDefaultTabWithInvalidTab() throws IOException {
        // Test that invalid tab names fall back to "Tasks"
        
        // 1. Create a preferences file with invalid tab in the correct directory
        Path dataDir = Path.of("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Clean up any existing file
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        String preferencesContent = "DEFAULT_TAB,InvalidTab";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
        
        // 3. Clean up
        Files.deleteIfExists(preferencesFile);
    }
    
    @Test
    void testDefaultTabWithNullInput() throws IOException {
        // Test that null input falls back to "Tasks"
        
        // 1. Create a preferences file with null in the correct directory
        Path dataDir = Path.of("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Clean up any existing file
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        String preferencesContent = "DEFAULT_TAB,null";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
        
        // 3. Clean up
        Files.deleteIfExists(preferencesFile);
    }
    
    @Test
    void testDefaultTabWithEmptyInput() throws IOException {
        // Test that empty input falls back to "Tasks"
        
        // 1. Create a preferences file with empty string in the correct directory
        Path dataDir = Path.of("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Clean up any existing file
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        String preferencesContent = "DEFAULT_TAB,";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
        
        // 3. Clean up
        Files.deleteIfExists(preferencesFile);
    }
}
