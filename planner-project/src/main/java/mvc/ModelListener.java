package mvc;

/**
 * Interface for objects that want to listen to model state changes.
 * Implements the observer pattern for MVC components.
 */
public interface ModelListener {
    /**
     * Called when a model's state changes.
     * @param event The ModelEvent describing what changed
     */
    void modelChanged(ModelEvent event);
}
