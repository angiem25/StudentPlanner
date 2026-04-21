package mvc;

/**
 * Core Controller interface for the MVC framework.
 * Defines the contract for all controller components that handle user interactions and coordinate model updates.
 */
public interface Controller {
    /**
     * Gets the model that this controller manages.
     * @return The Model instance this controller operates on
     */
    Model getModel();
    
    /**
     * Gets the view that this controller manages.
     * @return The View instance this controller handles
     */
    View getView();
}
