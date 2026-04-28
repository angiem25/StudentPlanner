package planner.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for Event class.
 * Tests event data management and time-based operations.
 */
class EventTest {
    
    private Event event;
    private LocalDateTime testStartTime;
    private LocalDateTime testEndTime;
    
    @BeforeEach
    void setUp() {
        testStartTime = LocalDateTime.now().plusDays(1).withHour(14).withMinute(0);
        testEndTime = testStartTime.plusHours(2);
        event = new Event("Study Group", "Group meeting for CS101", testStartTime, testEndTime);
    }
    
    @Test
    @DisplayName("Should create event with valid data")
    void testEventCreation() {
        assertEquals("Study Group", event.getTitle());
        assertEquals("Group meeting for CS101", event.getDescription());
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
        LocalDateTime newStart = testStartTime.plusDays(1);
        LocalDateTime newEnd = newStart.plusHours(3);
        
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
    @DisplayName("Should check if event occurs on specific date")
    void testOccursOn() {
        java.time.LocalDate eventDate = testStartTime.toLocalDate();
        assertTrue(event.occursOn(eventDate));
        
        java.time.LocalDate differentDate = eventDate.plusDays(1);
        assertFalse(event.occursOn(differentDate));
    }
    
    @Test
    @DisplayName("Should check if event overlaps with time range")
    void testOverlapsWith() {
        LocalDateTime rangeStart = testStartTime.minusHours(1);
        LocalDateTime rangeEnd = testEndTime.plusHours(1);
        
        assertTrue(event.overlapsWith(rangeStart, rangeEnd));
        
        // Non-overlapping range before event
        LocalDateTime beforeStart = testStartTime.minusHours(2);
        LocalDateTime beforeEnd = testStartTime.minusHours(1);
        assertFalse(event.overlapsWith(beforeStart, beforeEnd));
        
        // Non-overlapping range after event
        LocalDateTime afterStart = testEndTime.plusHours(1);
        LocalDateTime afterEnd = testEndTime.plusHours(2);
        assertFalse(event.overlapsWith(afterStart, afterEnd));
        
        // Range that starts exactly when event starts
        assertTrue(event.overlapsWith(testStartTime, testStartTime.plusHours(1)));
        
        // Range that ends exactly when event ends
        assertTrue(event.overlapsWith(testEndTime.minusHours(1), testEndTime));
    }
    
    @Test
    @DisplayName("Should handle toString correctly")
    void testToString() {
        String toString = event.toString();
        assertTrue(toString.contains("Study Group"));
        assertTrue(toString.contains("Group meeting for CS101"));
    }
    
    @Test
    @DisplayName("Should implement equals based on ID")
    void testEquals() {
        Event sameEvent = new Event("Study Group", "Group meeting for CS101", testStartTime, testEndTime);
        assertNotEquals(event, sameEvent); // Different IDs
        
        // Create an event with the same ID using the constructor that accepts ID
        Event sameIdEvent = new Event(event.getId(), "Different", "Description", testStartTime, testEndTime);
        assertEquals(event, sameIdEvent);
    }
    
    @Test
    @DisplayName("Should implement hashCode based on ID")
    void testHashCode() {
        Event sameEvent = new Event("Study Group", "Group meeting for CS101", testStartTime, testEndTime);
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
        LocalDateTime newStart = testStartTime.plusDays(2);
        LocalDateTime newEnd = newStart.plusHours(1);
        Event eventWithId = new Event(testId, "Test Event", "Test Description", newStart, newEnd);
        
        assertEquals(testId, eventWithId.getId());
        assertEquals("Test Event", eventWithId.getTitle());
        assertEquals("Test Description", eventWithId.getDescription());
        assertEquals(newStart, eventWithId.getStartDateTime());
        assertEquals(newEnd, eventWithId.getEndDateTime());
    }
    
    @Test
    @DisplayName("Should handle multi-day events")
    void testMultiDayEvents() {
        LocalDateTime multiDayStart = testStartTime;
        LocalDateTime multiDayEnd = multiDayStart.plusDays(2).withHour(18);
        Event multiDayEvent = new Event("Conference", "Multi-day conference", multiDayStart, multiDayEnd);
        
        // Should occur on both days
        assertTrue(multiDayEvent.occursOn(multiDayStart.toLocalDate()));
        assertTrue(multiDayEvent.occursOn(multiDayStart.toLocalDate().plusDays(1)));
        assertTrue(multiDayEvent.occursOn(multiDayEnd.toLocalDate()));
        
        // Should not occur on other days
        assertFalse(multiDayEvent.occursOn(multiDayStart.toLocalDate().minusDays(1)));
        assertFalse(multiDayEvent.occursOn(multiDayEnd.toLocalDate().plusDays(1)));
    }
    
    @Test
    @DisplayName("Should handle edge case overlapping")
    void testEdgeCaseOverlapping() {
        // Event from 14:00 to 16:00
        LocalDateTime rangeStart = testStartTime.withHour(16).withMinute(0); // Exactly when event ends
        LocalDateTime rangeEnd = testStartTime.withHour(18).withMinute(0); // After event ends
        
        assertTrue(event.overlapsWith(rangeStart, rangeEnd)); // Should overlap (end boundary inclusive)
        
        // Range that starts exactly when event starts
        rangeStart = testStartTime;
        rangeEnd = testStartTime.withHour(15).withMinute(0);
        assertTrue(event.overlapsWith(rangeStart, rangeEnd)); // Should overlap
        
        // Range that ends exactly when event starts (should overlap due to boundary conditions)
        rangeStart = testStartTime.withHour(12).withMinute(0);
        rangeEnd = testStartTime;
        assertTrue(event.overlapsWith(rangeStart, rangeEnd)); // Should overlap
    }
}
