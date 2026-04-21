package mvc;

/**
 * Core Model interface for the MVC framework.
 * Defines the contract for all model components that can notify observers of state changes.
 */
public interface Model {
    /**
     * Notifies all registered listeners of a model state change.
     * @param event The ModelEvent describing what changed
     */
    void notifyChanged(ModelEvent event);
}
