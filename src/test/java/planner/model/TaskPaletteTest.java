package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for TaskPalette class.
 * Tests task color palette management and hex color validation.
 */
class TaskPaletteTest {
    
    @BeforeEach
    void setUp() {
        // Reset any static state if needed
    }
    
    @Test
    @DisplayName("Should canonicalize hex colors correctly")
    void testCanonicalHex() {
        // Test valid palette colors
        assertEquals("#E53935", TaskPalette.canonicalHex("#E53935"));
        assertEquals("#1E88E5", TaskPalette.canonicalHex("#1E88E5"));
        assertEquals("#43A047", TaskPalette.canonicalHex("#43A047"));
        
        // Test case insensitive
        assertEquals("#E53935", TaskPalette.canonicalHex("#e53935"));
        assertEquals("#E53935", TaskPalette.canonicalHex("  #e53935  "));
        
        // Test invalid colors (should return default)
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("#FF0000"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("FF0000"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex(null));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex(""));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("   "));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("invalid"));
    }
    
    @Test
    @DisplayName("Should handle invalid hex colors")
    void testInvalidHexColors() {
        // Test null input
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex(null));
        
        // Test empty string
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex(""));
        
        // Test whitespace only
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("   "));
        
        // Test invalid hex format
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("FF000"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("FF00000"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("GG0000"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("invalid"));
        
        // Test hex with invalid characters
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("#FF00ZZ"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("#Hello!"));
    }
    
    @Test
    @DisplayName("Should handle short hex format")
    void testShortHexFormat() {
        // Test 3-digit hex format (should return default since not in palette)
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("#F00"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("#0F0"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("#00F"));
        
        // Test 3-digit without #
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("F00"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("0F0"));
        assertEquals(Task.DEFAULT_ACCENT_COLOR_HEX, TaskPalette.canonicalHex("00F"));
    }
    
    @Test
    @DisplayName("Should check if hex is in palette")
    void testContainsHex() {
        // Test valid palette colors
        assertTrue(TaskPalette.containsHex("#E53935"));
        assertTrue(TaskPalette.containsHex("#1E88E5"));
        assertTrue(TaskPalette.containsHex("#43A047"));
        assertTrue(TaskPalette.containsHex("#FB8C00"));
        assertTrue(TaskPalette.containsHex("#8E24AA"));
        assertTrue(TaskPalette.containsHex("#00897B"));
        assertTrue(TaskPalette.containsHex("#FDD835"));
        assertTrue(TaskPalette.containsHex("#3949AB"));
        
        // Test case insensitive
        assertTrue(TaskPalette.containsHex("#e53935"));
        assertTrue(TaskPalette.containsHex("#E53935".toLowerCase()));
        
        // Test invalid colors
        assertFalse(TaskPalette.containsHex("#FF0000"));
        assertFalse(TaskPalette.containsHex("#00FF00"));
        assertFalse(TaskPalette.containsHex("FF0000"));
        assertFalse(TaskPalette.containsHex(null));
        assertFalse(TaskPalette.containsHex(""));
        assertFalse(TaskPalette.containsHex("   "));
        assertFalse(TaskPalette.containsHex("invalid"));
    }
    
    @Test
    @DisplayName("Should get predefined palette colors")
    void testGetPaletteColors() {
        String[] colors = TaskPalette.HEX_CHOICES;
        
        assertNotNull(colors);
        assertTrue(colors.length > 0);
        
        // Should contain expected colors
        assertTrue(TaskPalette.containsHex(colors[0]));
        assertTrue(TaskPalette.containsHex(colors[colors.length - 1]));
        
        // All colors should be valid hex format
        for (String color : colors) {
            assertNotNull(color);
            assertTrue(color.startsWith("#"));
            assertEquals(7, color.length()); // #RRGGBB format
        }
    }
    
    @Test
    @DisplayName("Should handle edge cases for color validation")
    void testColorValidationEdgeCases() {
        // Test with extra whitespace
        assertTrue(TaskPalette.containsHex("  #E53935  "));
        assertTrue(TaskPalette.containsHex("\t#1E88E5\t"));
        assertTrue(TaskPalette.containsHex("\n#43A047\n"));
        
        // Test with mixed whitespace and invalid characters
        assertFalse(TaskPalette.containsHex("  #FF00ZZ  "));
        assertFalse(TaskPalette.containsHex("\t#Hello!\t"));
        
        // Test boundary cases
        assertFalse(TaskPalette.containsHex("#")); // Just #
        assertFalse(TaskPalette.containsHex("#F")); // One digit
        assertFalse(TaskPalette.containsHex("#FF")); // Two digits
        assertFalse(TaskPalette.containsHex("#FFF")); // Three digits
        assertFalse(TaskPalette.containsHex("#FFF0")); // Four digits
        assertFalse(TaskPalette.containsHex("#FFFF0")); // Five digits
        assertFalse(TaskPalette.containsHex("#FFFFF0")); // Six digits
        assertFalse(TaskPalette.containsHex("#FFFFFF0")); // Seven digits
    }
}