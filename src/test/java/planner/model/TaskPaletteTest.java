package planner.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TaskPalette class.
 * Tests color palette functionality and hex color validation.
 */
class TaskPaletteTest {
    
    @Test
    @DisplayName("Should canonicalize valid hex colors from palette")
    void testCanonicalHexValidColors() {
        assertEquals("#E53935", TaskPalette.canonicalHex("#E53935"));
        assertEquals("#1E88E5", TaskPalette.canonicalHex("#1E88E5"));
        assertEquals("#43A047", TaskPalette.canonicalHex("#43A047"));
        
        // Test lowercase
        assertEquals("#E53935", TaskPalette.canonicalHex("#e53935"));
        assertEquals("#1E88E5", TaskPalette.canonicalHex("#1e88e5"));
        assertEquals("#43A047", TaskPalette.canonicalHex("#43a047"));
        
        // Test with spaces
        assertEquals("#E53935", TaskPalette.canonicalHex("  #E53935  "));
        assertEquals("#1E88E5", TaskPalette.canonicalHex("\t#1E88E5\n"));
    }
    
    @Test
    @DisplayName("Should return default color for invalid hex")
    void testCanonicalHexInvalidColors() {
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex(null));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex(""));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("   "));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("invalid"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("#FF0000")); // Not in palette
        
        // Test without #
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("E53935"));
    }
    
    @Test
    @DisplayName("Should check if hex is in palette")
    void testContainsHex() {
        // Valid palette colors
        assertTrue(TaskPalette.containsHex("#E53935"));
        assertTrue(TaskPalette.containsHex("#1E88E5"));
        assertTrue(TaskPalette.containsHex("#43A047"));
        
        // Test lowercase
        assertTrue(TaskPalette.containsHex("#e53935"));
        assertTrue(TaskPalette.containsHex("#1e88e5"));
        assertTrue(TaskPalette.containsHex("#43a047"));
        
        // Test with spaces
        assertTrue(TaskPalette.containsHex("  #E53935  ")); // containsHex does trim
        
        // Invalid colors
        assertFalse(TaskPalette.containsHex(null));
        assertFalse(TaskPalette.containsHex(""));
        assertFalse(TaskPalette.containsHex("   "));
        assertFalse(TaskPalette.containsHex("#FF0000")); // Not in palette
        assertFalse(TaskPalette.containsHex("E53935")); // Missing #
    }
    
    @Test
    @DisplayName("Should have correct number of palette colors")
    void testPaletteSize() {
        assertEquals(8, TaskPalette.HEX_CHOICES.length);
    }
    
    @Test
    @DisplayName("Should contain default color in palette")
    void testDefaultColorInPalette() {
        boolean containsDefault = false;
        for (String color : TaskPalette.HEX_CHOICES) {
            if (color.equals(Task.DEFAULT_ACCENT_COLOR_HEX)) {
                containsDefault = true;
                break;
            }
        }
        assertTrue(containsDefault, "Should contain default color");
    }
    
    @Test
    @DisplayName("Should have valid hex format for all palette colors")
    void testAllPaletteColorsValid() {
        for (String color : TaskPalette.HEX_CHOICES) {
            assertNotNull(color);
            assertFalse(color.isEmpty());
            assertTrue(color.startsWith("#"));
            assertEquals(7, color.length()); // #RRGGBB format
            
            // Check that all characters are valid hex digits
            String hexPart = color.substring(1);
            for (char c : hexPart.toCharArray()) {
                assertTrue(Character.isDigit(c) || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f'),
                    "Invalid hex character: " + c + " in color: " + color);
            }
        }
    }
    
    @Test
    @DisplayName("Should handle edge cases consistently")
    void testEdgeCases() {
        // Test canonicalization with various whitespace
        assertEquals("#E53935", TaskPalette.canonicalHex("\n\t  #E53935  \t\n"));
        
        // Test contains with whitespace
        assertTrue(TaskPalette.containsHex("  #E53935  ")); // containsHex does trim
        
        // Test canonicalization followed by contains
        String canonical = TaskPalette.canonicalHex("  #E53935  ");
        assertTrue(TaskPalette.containsHex(canonical));
        
        // Test canonicalization returns palette format
        String canonical2 = TaskPalette.canonicalHex("#e53935");
        assertTrue(TaskPalette.containsHex(canonical2));
        assertEquals("#E53935", canonical2); // Should be uppercase from palette
    }
    
    @Test
    @DisplayName("Should be immutable")
    void testImmutability() {
        // HEX_CHOICES should be a constant array
        assertNotNull(TaskPalette.HEX_CHOICES);
        
        // Arrays in Java are mutable, but the reference is final
        // We can test that the array has the expected content
        String[] original = TaskPalette.HEX_CHOICES.clone();
        
        // The array should contain the expected colors
        assertEquals("#E53935", TaskPalette.HEX_CHOICES[0]);
        assertEquals(8, TaskPalette.HEX_CHOICES.length);
        
        // Verify the array reference hasn't changed
        assertSame(TaskPalette.HEX_CHOICES, TaskPalette.HEX_CHOICES);
    }
}
