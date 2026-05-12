# ModelEvent (Student Planner API Documentation)

## Class ModelEvent

- Constructor Summary Constructors Constructor Description ModelEvent(String eventName, Object source, Object data) Creates a new ModelEvent.
- Method Summary All MethodsInstance MethodsConcrete Methods Modifier and Type Method Description Object getData() Gets the data associated with this event. String getEventName() Gets the name of this event. Object getSource() Gets the source model that generated this event. String toString() Methods inherited from class java.lang.Object clone, equals, finalize, getClass, hashCode, notify, notifyAll, wait, wait, wait

### Constructor Summary

### Method Summary

#### Methods inherited from class java.lang.Object

- Constructor Details ModelEvent public ModelEvent(String eventName, Object source, Object data) Creates a new ModelEvent. Parameters: eventName - The name/type of the event (e.g., "TASK_ADDED", "TASK_UPDATED") source - The model that generated the event data - Optional data associated with the event (can be null)
- Method Details getEventName public String getEventName() Gets the name of this event. Returns: The event name getSource public Object getSource() Gets the source model that generated this event. Returns: The source model getData public Object getData() Gets the data associated with this event. Returns: The event data (may be null) toString public String toString() Overrides: toString in class Object

### Constructor Details

- ModelEvent public ModelEvent(String eventName, Object source, Object data) Creates a new ModelEvent. Parameters: eventName - The name/type of the event (e.g., "TASK_ADDED", "TASK_UPDATED") source - The model that generated the event data - Optional data associated with the event (can be null)

#### ModelEvent

### Method Details

- getEventName public String getEventName() Gets the name of this event. Returns: The event name
- getSource public Object getSource() Gets the source model that generated this event. Returns: The source model
- getData public Object getData() Gets the data associated with this event. Returns: The event data (may be null)
- toString public String toString() Overrides: toString in class Object

#### getEventName

#### getSource

#### getData

#### toString
