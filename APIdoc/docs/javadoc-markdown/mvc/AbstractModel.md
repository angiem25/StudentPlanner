# AbstractModel (Student Planner API Documentation)

## Class AbstractModel

- Constructor Summary Constructors Constructor Description AbstractModel()
- Method Summary All MethodsInstance MethodsConcrete Methods Modifier and Type Method Description void addModelListener(ModelListener listener) Adds a listener to be notified of model changes. protected void fireModelChanged(String eventName) Convenience method to notify listeners of a simple event without data. protected void fireModelChanged(String eventName, Object data) Convenience method to notify listeners of an event with data. void notifyChanged(ModelEvent event) Notifies all registered listeners of a model state change. void removeModelListener(ModelListener listener) Removes a listener from model change notifications. Methods inherited from class java.lang.Object clone, equals, finalize, getClass, hashCode, notify, notifyAll, toString, wait, wait, wait

### Constructor Summary

### Method Summary

#### Methods inherited from class java.lang.Object

- Constructor Details AbstractModel public AbstractModel()
- Method Details addModelListener public void addModelListener(ModelListener listener) Adds a listener to be notified of model changes. Parameters: listener - The listener to add removeModelListener public void removeModelListener(ModelListener listener) Removes a listener from model change notifications. Parameters: listener - The listener to remove notifyChanged public void notifyChanged(ModelEvent event) Notifies all registered listeners of a model state change. Specified by: notifyChanged in interface Model Parameters: event - The ModelEvent describing what changed fireModelChanged protected void fireModelChanged(String eventName) Convenience method to notify listeners of a simple event without data. Parameters: eventName - The name of the event fireModelChanged protected void fireModelChanged(String eventName, Object data) Convenience method to notify listeners of an event with data. Parameters: eventName - The name of the event data - The data associated with the event

### Constructor Details

- AbstractModel public AbstractModel()

#### AbstractModel

### Method Details

- addModelListener public void addModelListener(ModelListener listener) Adds a listener to be notified of model changes. Parameters: listener - The listener to add
- removeModelListener public void removeModelListener(ModelListener listener) Removes a listener from model change notifications. Parameters: listener - The listener to remove
- notifyChanged public void notifyChanged(ModelEvent event) Notifies all registered listeners of a model state change. Specified by: notifyChanged in interface Model Parameters: event - The ModelEvent describing what changed
- fireModelChanged protected void fireModelChanged(String eventName) Convenience method to notify listeners of a simple event without data. Parameters: eventName - The name of the event
- fireModelChanged protected void fireModelChanged(String eventName, Object data) Convenience method to notify listeners of an event with data. Parameters: eventName - The name of the event data - The data associated with the event

#### addModelListener

#### removeModelListener

#### notifyChanged

#### fireModelChanged

#### fireModelChanged
