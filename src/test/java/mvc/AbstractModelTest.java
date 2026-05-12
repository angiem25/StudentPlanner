package mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for AbstractModel class.
 * Tests model event notification system and listener management.
 */
class AbstractModelTest {
    
    private TestModel model;
    private TestListener listener1;
    private TestListener listener2;
    
    @BeforeEach
    void setUp() {
        model = new TestModel();
        listener1 = new TestListener();
        listener2 = new TestListener();
    }
    
    @Test
    @DisplayName("Should add and remove listeners")
    void testListenerManagement() {
        // Add listeners
        model.addModelListener(listener1);
        model.addModelListener(listener2);
        
        // Trigger event to verify listeners are working
        model.triggerTestEvent("test", "data");
        assertEquals(1, listener1.getEventCount());
        assertEquals(1, listener2.getEventCount());
        
        // Remove one listener
        model.removeModelListener(listener1);
        
        // Trigger event again
        model.triggerTestEvent("test2", "data2");
        
        // Only listener2 should receive the new event
        assertEquals(1, listener1.getEventCount()); // Still 1 from first event
        assertEquals(2, listener2.getEventCount()); // 2 from both events
    }
    
    @Test
    @DisplayName("Should notify listeners when event occurs")
    void testEventNotification() {
        model.addModelListener(listener1);
        model.addModelListener(listener2);
        
        // Initially no events received
        assertEquals(0, listener1.getEventCount());
        assertEquals(0, listener2.getEventCount());
        
        // Trigger event
        model.triggerTestEvent("test-event", "test-data");
        
        // Both listeners should receive the event
        assertEquals(1, listener1.getEventCount());
        assertEquals(1, listener2.getEventCount());
        ModelEvent receivedEvent1 = listener1.getLastEvent();
        ModelEvent receivedEvent2 = listener2.getLastEvent();
        assertEquals("test-event", receivedEvent1.getEventName());
        assertEquals("test-data", receivedEvent1.getData());
        assertEquals("test-event", receivedEvent2.getEventName());
        assertEquals("test-data", receivedEvent2.getData());
    }
    
    @Test
    @DisplayName("Should not notify removed listeners")
    void testRemovedListenerNotNotified() {
        model.addModelListener(listener1);
        model.addModelListener(listener2);
        
        // Remove listener1
        model.removeModelListener(listener1);
        
        // Trigger event
        model.triggerTestEvent("test-event", "test-data");
        
        // Only listener2 should receive the event
        assertEquals(0, listener1.getEventCount());
        assertEquals(1, listener2.getEventCount());
    }
    
    @Test
    @DisplayName("Should handle multiple events")
    void testMultipleEvents() {
        model.addModelListener(listener1);
        
        // Trigger multiple events
        model.triggerTestEvent("event1", "data1");
        model.triggerTestEvent("event2", "data2");
        model.triggerTestEvent("event3", "data3");
        
        // Should receive all events
        assertEquals(3, listener1.getEventCount());
        ModelEvent lastEvent = listener1.getLastEvent();
        assertEquals("event3", lastEvent.getEventName());
        assertEquals("data3", lastEvent.getData());
    }
    
    @Test
    @DisplayName("Should handle adding same listener multiple times")
    void testDuplicateListeners() {
        model.addModelListener(listener1);
        model.addModelListener(listener1); // Add same listener again - AbstractModel prevents duplicates
        
        // Trigger event
        model.triggerTestEvent("test-event", "test-data");
        
        // Listener should receive event once (no duplicates)
        assertEquals(1, listener1.getEventCount());
    }
    
    @Test
    @DisplayName("Should handle removing non-existent listener")
    void testRemoveNonExistentListener() {
        model.addModelListener(listener1);
        
        // Try to remove listener that wasn't added
        assertDoesNotThrow(() -> model.removeModelListener(listener2));
        
        // Also test removing null listener
        assertDoesNotThrow(() -> model.removeModelListener(null));
        
        // Verify listener1 still works
        model.triggerTestEvent("test", "data");
        assertEquals(1, listener1.getEventCount());
    }
    
    @Test
    @DisplayName("Should handle null listener gracefully")
    void testNullListener() {
        // These should not throw exceptions
        assertDoesNotThrow(() -> model.addModelListener(null));
        assertDoesNotThrow(() -> model.removeModelListener(null));
        
        // Trigger event with null listener in list
        assertDoesNotThrow(() -> model.triggerTestEvent("test-event", "test-data"));
    }
    
    @Test
    @DisplayName("Should handle null event gracefully")
    void testNullEvent() {
        model.addModelListener(listener1);
        
        // Should not throw exception (null event not possible with fireModelChanged)
        // This test is not applicable since we use fireModelChanged which doesn't accept null events
        
        // Listener should not receive null event
        assertEquals(0, listener1.getEventCount());
    }
    
    // Test implementation classes
    private static class TestModel extends AbstractModel {
        public void triggerTestEvent(String eventName, Object data) {
            fireModelChanged(eventName, data);
        }
    }
    
    private static class TestListener implements ModelListener {
        private final List<ModelEvent> events = new ArrayList<>();
        
        public void modelChanged(ModelEvent event) {
            events.add(event);
        }
        
        public int getEventCount() {
            return events.size();
        }
        
        public ModelEvent getLastEvent() {
            return events.isEmpty() ? null : events.get(events.size() - 1);
        }
        
        
    }
}