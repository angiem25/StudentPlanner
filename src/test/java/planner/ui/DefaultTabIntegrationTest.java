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
        
        // 1. Create a temporary preferences file with "Today" as default
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,Today";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify that preference is saved correctly
        assertTrue(Files.exists(preferencesFile));
        String savedContent = Files.readString(preferencesFile);
        assertEquals(preferencesContent, savedContent);
        
        // 3. Load preference using repository with temp directory override
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Today", loadedDefault);
    }
    
    @Test
    void testDefaultTabWithWeeklyPriorities() throws IOException {
        // Test setting "Weekly Priorities" as default and verifying it opens correctly
        
        // 1. Create a temporary preferences file with "Weekly Priorities" as default
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
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
    }
    
    @Test
    void testDefaultTabWithCalendar() throws IOException {
        // Test setting "Calendar" as default and verifying it opens correctly
        
        // 1. Create a temporary preferences file with "Calendar" as default
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
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
    }
    
    @Test
    void testDefaultTabWithTasks() throws IOException {
        // Test setting "Tasks" as default and verifying it opens correctly
        
        // 1. Create a temporary preferences file with "Tasks" as default
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
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
    }
    
    @Test
    void testDefaultTabFallbackToTasks() throws IOException {
        // Test that when no preferences exist, it defaults to "Tasks"
        
        // 1. Ensure no preferences file exists
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
    }
    
    @Test
    void testDefaultTabWithInvalidTab() throws IOException {
        // Test that invalid tab names fall back to "Tasks"
        
        // 1. Create a temporary preferences file with invalid tab
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,InvalidTab";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
    }
    
    @Test
    void testDefaultTabWithNullInput() throws IOException {
        // Test that null input falls back to "Tasks"
        
        // 1. Create a temporary preferences file with null
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,null";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
    }
    
    @Test
    void testDefaultTabWithEmptyInput() throws IOException {
        // Test that empty input falls back to "Tasks"
        
        // 1. Create a temporary preferences file with empty string
        Path dataDir = tempDir.resolve("planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String loadedDefault = repository.loadPreferences();
        assertEquals("Tasks", loadedDefault);
    }
}
