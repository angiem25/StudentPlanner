package mvc;

/**
 * Core View interface for the MVC framework.
 * Defines the contract for all view components that display model data and capture user input.
 */
public interface View {
    /**
     * Gets the model associated with this view.
     * @return The Model instance this view is displaying
     */
    Model getModel();
    
    /**
     * Gets the controller that handles user interactions for this view.
     * @return The Controller instance managing this view
     */
    Controller getController();
}
