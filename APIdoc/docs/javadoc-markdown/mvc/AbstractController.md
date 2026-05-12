# AbstractController (Student Planner API Documentation)

## Class AbstractController

- Constructor Summary Constructors Constructor Description AbstractController(Model model, View view) Creates a new AbstractController with the specified model and view.
- Method Summary All MethodsInstance MethodsConcrete Methods Modifier and Type Method Description Model getModel() Gets the model that this controller manages. View getView() Gets the view that this controller manages. void setModel(Model model) Sets the model for this controller. void setView(View view) Sets the view for this controller. Methods inherited from class java.lang.Object clone, equals, finalize, getClass, hashCode, notify, notifyAll, toString, wait, wait, wait

### Constructor Summary

### Method Summary

#### Methods inherited from class java.lang.Object

- Constructor Details AbstractController public AbstractController(Model model, View view) Creates a new AbstractController with the specified model and view. Parameters: model - The model this controller will manage view - The view this controller will handle
- Method Details getModel public Model getModel() Description copied from interface: Controller Gets the model that this controller manages. Specified by: getModel in interface Controller Returns: The Model instance this controller operates on getView public View getView() Description copied from interface: Controller Gets the view that this controller manages. Specified by: getView in interface Controller Returns: The View instance this controller handles setModel public void setModel(Model model) Sets the model for this controller. Parameters: model - The new model setView public void setView(View view) Sets the view for this controller. Parameters: view - The new view

### Constructor Details

- AbstractController public AbstractController(Model model, View view) Creates a new AbstractController with the specified model and view. Parameters: model - The model this controller will manage view - The view this controller will handle

#### AbstractController

### Method Details

- getModel public Model getModel() Description copied from interface: Controller Gets the model that this controller manages. Specified by: getModel in interface Controller Returns: The Model instance this controller operates on
- getView public View getView() Description copied from interface: Controller Gets the view that this controller manages. Specified by: getView in interface Controller Returns: The View instance this controller handles
- setModel public void setModel(Model model) Sets the model for this controller. Parameters: model - The new model
- setView public void setView(View view) Sets the view for this controller. Parameters: view - The new view

#### getModel

#### getView

#### setModel

#### setView
