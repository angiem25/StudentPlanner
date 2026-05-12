# AbstractView (Student Planner API Documentation)

## Class AbstractView

- Constructor Summary Constructors Constructor Description AbstractView(Model model, Controller controller) Creates a new AbstractView with the specified model and controller.
- Method Summary All MethodsInstance MethodsConcrete Methods Modifier and Type Method Description Controller getController() Gets the controller that handles user interactions for this view. Model getModel() Gets the model associated with this view. void setController(Controller controller) Sets the controller for this view. void setModel(Model model) Sets the model for this view. Methods inherited from class java.lang.Object clone, equals, finalize, getClass, hashCode, notify, notifyAll, toString, wait, wait, wait Methods inherited from interface mvc.ModelListener modelChanged

### Constructor Summary

### Method Summary

#### Methods inherited from class java.lang.Object

#### Methods inherited from interface mvc.ModelListener

- Constructor Details AbstractView public AbstractView(Model model, Controller controller) Creates a new AbstractView with the specified model and controller. Parameters: model - The model this view will display controller - The controller that handles user interactions
- Method Details getModel public Model getModel() Description copied from interface: View Gets the model associated with this view. Specified by: getModel in interface View Returns: The Model instance this view is displaying getController public Controller getController() Description copied from interface: View Gets the controller that handles user interactions for this view. Specified by: getController in interface View Returns: The Controller instance managing this view setModel public void setModel(Model model) Sets the model for this view. Parameters: model - The new model setController public void setController(Controller controller) Sets the controller for this view. Parameters: controller - The new controller

### Constructor Details

- AbstractView public AbstractView(Model model, Controller controller) Creates a new AbstractView with the specified model and controller. Parameters: model - The model this view will display controller - The controller that handles user interactions

#### AbstractView

### Method Details

- getModel public Model getModel() Description copied from interface: View Gets the model associated with this view. Specified by: getModel in interface View Returns: The Model instance this view is displaying
- getController public Controller getController() Description copied from interface: View Gets the controller that handles user interactions for this view. Specified by: getController in interface View Returns: The Controller instance managing this view
- setModel public void setModel(Model model) Sets the model for this view. Parameters: model - The new model
- setController public void setController(Controller controller) Sets the controller for this view. Parameters: controller - The new controller

#### getModel

#### getController

#### setModel

#### setController
