package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Event class.
 * Tests event data management, time overlap detection, and date operations.
 */
class EventTest {
    
    private Event event;
    private LocalDateTime testStartTime;
    private LocalDateTime testEndTime;
    
    @BeforeEach
    void setUp() {
        testStartTime = LocalDateTime.of(2024, 1, 15, 10, 0);
        testEndTime = LocalDateTime.of(2024, 1, 15, 11, 0);
        event = new Event("Study Session", "Math homework review", testStartTime, testEndTime);
    }
    
    @Test
    @DisplayName("Should create event with valid data")
    void testEventCreation() {
        assertEquals("Study Session", event.getTitle());
        assertEquals("Math homework review", event.getDescription());
        assertEquals(testStartTime, event.getStartDateTime());
        assertEquals(testEndTime, event.getEndDateTime());
        assertNotNull(event.getId());
    }
    
    @Test
    @DisplayName("Should generate unique IDs for different events")
    void testUniqueIds() {
        Event event2 = new Event("Another Event", "Description", testStartTime, testEndTime);
        assertNotEquals(event.getId(), event2.getId());
    }
    
    @Test
    @DisplayName("Should update event information")
    void testSetters() {
        LocalDateTime newStart = LocalDateTime.of(2024, 1, 16, 14, 0);
        LocalDateTime newEnd = LocalDateTime.of(2024, 1, 16, 15, 0);
        
        event.setTitle("Updated Event");
        event.setDescription("Updated description");
        event.setStartDateTime(newStart);
        event.setEndDateTime(newEnd);
        
        assertEquals("Updated Event", event.getTitle());
        assertEquals("Updated description", event.getDescription());
        assertEquals(newStart, event.getStartDateTime());
        assertEquals(newEnd, event.getEndDateTime());
    }
    
    @Test
    @DisplayName("Should detect overlapping events correctly")
    void testOverlapsWith() {
        // Test overlapping range
        assertTrue(event.overlapsWith(
            LocalDateTime.of(2024, 1, 15, 10, 30),
            LocalDateTime.of(2024, 1, 15, 11, 30)
        ));
        
        // Test non-overlapping range (before)
        assertFalse(event.overlapsWith(
            LocalDateTime.of(2024, 1, 15, 8, 0),
            LocalDateTime.of(2024, 1, 15, 9, 0)
        ));
        
        // Test non-overlapping range (after)
        assertFalse(event.overlapsWith(
            LocalDateTime.of(2024, 1, 15, 12, 0),
            LocalDateTime.of(2024, 1, 15, 13, 0)
        ));
        
        // Test exact boundary overlap
        assertTrue(event.overlapsWith(
            LocalDateTime.of(2024, 1, 15, 11, 0),
            LocalDateTime.of(2024, 1, 15, 12, 0)
        ));
        
        assertTrue(event.overlapsWith(
            LocalDateTime.of(2024, 1, 15, 9, 0),
            LocalDateTime.of(2024, 1, 15, 10, 0)
        ));
    }
    
    @Test
    @DisplayName("Should check if event occurs on specific date")
    void testOccursOn() {
        LocalDate eventDate = LocalDate.of(2024, 1, 15);
        LocalDate differentDate = LocalDate.of(2024, 1, 16);
        
        assertTrue(event.occursOn(eventDate));
        assertFalse(event.occursOn(differentDate));
        
        // Test multi-day event
        LocalDateTime multiDayStart = LocalDateTime.of(2024, 1, 15, 20, 0);
        LocalDateTime multiDayEnd = LocalDateTime.of(2024, 1, 16, 2, 0);
        Event multiDayEvent = new Event("Late Study", "Multi-day session", multiDayStart, multiDayEnd);
        
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2024, 1, 15)));
        assertTrue(multiDayEvent.occursOn(LocalDate.of(2024, 1, 16)));
        assertFalse(multiDayEvent.occursOn(LocalDate.of(2024, 1, 17)));
    }
    
    @Test
    @DisplayName("Should handle toString correctly")
    void testToString() {
        String toString = event.toString();
        assertTrue(toString.contains("Study Session"));
        assertTrue(toString.contains("Math homework review"));
        assertTrue(toString.contains("startDateTime=" + testStartTime));
        assertTrue(toString.contains("endDateTime=" + testEndTime));
    }
    
    @Test
    @DisplayName("Should implement equals based on ID")
    void testEquals() {
        Event sameEvent = new Event("Study Session", "Math homework review", testStartTime, testEndTime);
        assertNotEquals(event, sameEvent); // Different IDs
        
        // Create an event with the same ID using the constructor that accepts ID
        Event sameIdEvent = new Event(event.getId(), "Different", "Description", testStartTime, testEndTime);
        assertEquals(event, sameIdEvent);
    }
    
    @Test
    @DisplayName("Should implement hashCode based on ID")
    void testHashCode() {
        Event sameEvent = new Event("Study Session", "Math homework review", testStartTime, testEndTime);
        assertNotEquals(event.hashCode(), sameEvent.hashCode()); // Different IDs
        
        Event sameIdEvent = new Event(event.getId(), "Different", "Description", testStartTime, testEndTime);
        assertEquals(event.hashCode(), sameIdEvent.hashCode());
    }
    
    @Test
    @DisplayName("Should not equal null")
    void testNotEqualsNull() {
        assertNotEquals(event, null);
    }
    
    @Test
    @DisplayName("Should not equal different type")
    void testNotEqualsDifferentType() {
        assertNotEquals(event, "string");
    }
    
    @Test
    @DisplayName("Should equal itself")
    void testEqualsSelf() {
        assertEquals(event, event);
    }
    
    @Test
    @DisplayName("Should handle event with ID constructor")
    void testEventWithIdConstructor() {
        String testId = "test-event-id-123";
        LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        LocalDateTime end = LocalDateTime.of(2024, 2, 1, 10, 0);
        Event eventWithId = new Event(testId, "Test Event", "Test Description", start, end);
        
        assertEquals(testId, eventWithId.getId());
        assertEquals("Test Event", eventWithId.getTitle());
        assertEquals("Test Description", eventWithId.getDescription());
        assertEquals(start, eventWithId.getStartDateTime());
        assertEquals(end, eventWithId.getEndDateTime());
    }
    
    @Test
    @DisplayName("Should handle edge cases for overlap detection")
    void testOverlapEdgeCases() {
        // Test same time event
        assertTrue(event.overlapsWith(testStartTime, testEndTime));
        
        // Test containing event
        assertTrue(event.overlapsWith(
            LocalDateTime.of(2024, 1, 15, 9, 0),
            LocalDateTime.of(2024, 1, 15, 12, 0)
        ));
        
        // Test contained event
        assertTrue(event.overlapsWith(
            LocalDateTime.of(2024, 1, 15, 10, 15),
            LocalDateTime.of(2024, 1, 15, 10, 45)
        ));
        
        // Test zero-duration event
        LocalDateTime sameTime = LocalDateTime.of(2024, 1, 15, 10, 30);
        assertTrue(event.overlapsWith(sameTime, sameTime));
    }
}