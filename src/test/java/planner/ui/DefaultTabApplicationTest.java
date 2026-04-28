package planner.ui;

import org.junit.jupiter.api.Test;
import planner.persistence.PlannerRepository;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Test class for Default Tab functionality with actual application.
 */
class DefaultTabApplicationTest {
    
    @Test
    void testDefaultTabWithToday() throws IOException {
        // Test setting "Today" as default and verifying it opens correctly
        
        // 1. Create a temporary preferences file with "Today" as default
        Path dataDir = Path.of("test_planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
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
    }
    
    @Test
    void testDefaultTabWithWeeklyPriorities() throws IOException {
        // Test setting "Weekly Priorities" as default and verifying it opens correctly
        
        // 1. Create a temporary preferences file with "Weekly Priorities" as default
        Path dataDir = Path.of("test_planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,Weekly Priorities";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify that preference is saved correctly
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
        Path dataDir = Path.of("test_planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,Calendar";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify that preference is saved correctly
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
        Path dataDir = Path.of("test_planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,Tasks";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Verify that preference is saved correctly
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
        Path dataDir = Path.of("test_planner_data");
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        // Delete preferences file if it exists
        if (Files.exists(preferencesFile)) {
            Files.delete(preferencesFile);
        }
        
        // 2. Load preference using repository (should default to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String defaultTab = repository.loadPreferences();
        assertEquals("Tasks", defaultTab, "Should default to 'Tasks' when no preferences exist");
    }
    
    @Test
    void testDefaultTabWithInvalidTab() throws IOException {
        // Test that invalid tab names fall back to "Tasks"
        
        // 1. Create a temporary preferences file with invalid tab
        Path dataDir = Path.of("test_planner_data");
        Files.createDirectories(dataDir);
        Path preferencesFile = dataDir.resolve("preferences.csv");
        
        String preferencesContent = "DEFAULT_TAB,InvalidTab";
        Files.write(preferencesFile, preferencesContent.getBytes());
        
        // 2. Load preference using repository (should fallback to "Tasks")
        PlannerRepository repository = new PlannerRepository();
        String defaultTab = repository.loadPreferences();
        assertEquals("Tasks", defaultTab, "Should fallback to 'Tasks' when invalid tab is saved");
    }
}
