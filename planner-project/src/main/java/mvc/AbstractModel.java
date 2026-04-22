package mvc;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for Model implementations.
 * Provides observer pattern support with listener management.
 */
public abstract class AbstractModel implements Model {
    private final List<ModelListener> listeners = new ArrayList<>();
    
    /**
     * Adds a listener to be notified of model changes.
     * @param listener The listener to add
     */
    public void addModelListener(ModelListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a listener from model change notifications.
     * @param listener The listener to remove
     */
    public void removeModelListener(ModelListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies all registered listeners of a model state change.
     * @param event The ModelEvent describing what changed
     */
    @Override
    public void notifyChanged(ModelEvent event) {
        for (ModelListener listener : listeners) {
            listener.modelChanged(event);
        }
    }
    
    /**
     * Convenience method to notify listeners of a simple event without data.
     * @param eventName The name of the event
     */
    protected void fireModelChanged(String eventName) {
        fireModelChanged(eventName, null);
    }
    
    /**
     * Convenience method to notify listeners of an event with data.
     * @param eventName The name of the event
     * @param data The data associated with the event
     */
    protected void fireModelChanged(String eventName, Object data) {
        ModelEvent event = new ModelEvent(eventName, this, data);
        notifyChanged(event);
    }
}
