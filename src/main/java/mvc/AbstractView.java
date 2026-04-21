package mvc;

/**
 * Abstract base class for View implementations.
 * Provides basic view functionality with model and controller references.
 */
public abstract class AbstractView implements View, ModelListener {
    private Model model;
    private Controller controller;
    
    /**
     * Creates a new AbstractView with the specified model and controller.
     * @param model The model this view will display
     * @param controller The controller that handles user interactions
     */
    public AbstractView(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        if (model instanceof AbstractModel) {
            ((AbstractModel) model).addModelListener(this);
        }
    }
    
    @Override
    public Model getModel() {
        return model;
    }
    
    @Override
    public Controller getController() {
        return controller;
    }
    
    /**
     * Sets the model for this view.
     * @param model The new model
     */
    public void setModel(Model model) {
        if (this.model instanceof AbstractModel) {
            ((AbstractModel) this.model).removeModelListener(this);
        }
        this.model = model;
        if (model instanceof AbstractModel) {
            ((AbstractModel) model).addModelListener(this);
        }
        modelChanged(null); // Trigger refresh
    }
    
    /**
     * Sets the controller for this view.
     * @param controller The new controller
     */
    public void setController(Controller controller) {
        this.controller = controller;
    }
}
