package mvc;

/**
 * Represents an event that occurs when a model's state changes.
 * Encapsulates information about what changed in the model.
 */
public class ModelEvent {
    private final String eventName;
    private final Object source;
    private final Object data;
    
    /**
     * Creates a new ModelEvent.
     * @param eventName The name/type of the event (e.g., "TASK_ADDED", "TASK_UPDATED")
     * @param source The model that generated the event
     * @param data Optional data associated with the event (can be null)
     */
    public ModelEvent(String eventName, Object source, Object data) {
        this.eventName = eventName;
        this.source = source;
        this.data = data;
    }
    
    /**
     * Gets the name of this event.
     * @return The event name
     */
    public String getEventName() {
        return eventName;
    }
    
    /**
     * Gets the source model that generated this event.
     * @return The source model
     */
    public Object getSource() {
        return source;
    }
    
    /**
     * Gets the data associated with this event.
     * @return The event data (may be null)
     */
    public Object getData() {
        return data;
    }
    
    @Override
    public String toString() {
        return "ModelEvent{" +
                "eventName='" + eventName + '\'' +
                ", source=" + source +
                ", data=" + data +
                '}';
    }
}
