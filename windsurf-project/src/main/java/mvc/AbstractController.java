package mvc;

/**
 * Abstract base class for Controller implementations.
 * Provides basic controller functionality with model and view references.
 */
public abstract class AbstractController implements Controller {
    private Model model;
    private View view;
    
    /**
     * Creates a new AbstractController with the specified model and view.
     * @param model The model this controller will manage
     * @param view The view this controller will handle
     */
    public AbstractController(Model model, View view) {
        this.model = model;
        this.view = view;
    }
    
    @Override
    public Model getModel() {
        return model;
    }
    
    @Override
    public View getView() {
        return view;
    }
    
    /**
     * Sets the model for this controller.
     * @param model The new model
     */
    public void setModel(Model model) {
        this.model = model;
    }
    
    /**
     * Sets the view for this controller.
     * @param view The new view
     */
    public void setView(View view) {
        this.view = view;
    }
}
