# System Statechart Diagrams

## Overview
This document contains statechart diagrams that describe the dynamic behavior of the Student Planner application's key components and user interactions.

## 1. Application Lifecycle Statechart

### Application States
```plantuml
@startuml
title Application Lifecycle Statechart

state Application {
  [*] --> Initializing
  Initializing --> LoadingData
  LoadingData --> Ready
  LoadingData --> Error : load failure
  
  Ready --> Running
  Running --> SavingData : auto-save
  SavingData --> Running
  Running --> ShuttingDown : exit
  Running --> Error : critical error
  
  Error --> Recovering
  Recovering --> Ready : recovery successful
  Recovering --> ShuttingDown : recovery failed
  
  ShuttingDown --> [*]
}

note right of Error
  Handles critical errors,
  data corruption, or
  system failures
end note
@enduml
```

### State Descriptions
- **Initializing**: Application startup, loading configuration
- **LoadingData**: Loading user data from CSV files
- **Ready**: Application ready for user interaction
- **Running**: Normal operation mode
- **SavingData**: Auto-saving data to files
- **Error**: Error state for critical failures
- **Recovering**: Attempting to recover from errors
- **ShuttingDown**: Application shutdown process

---

## 2. Student Profile Management Statechart

### Profile States
```plantuml
@startuml
title Student Profile Management Statechart

state ProfileManagement {
  [*] --> NoProfile
  NoProfile --> CreatingProfile : new user
  NoProfile --> LoadingProfile : existing user
  
  CreatingProfile --> EditingProfile
  EditingProfile --> Validating : save
  Validating --> ProfileValid : validation success
  Validating --> EditingProfile : validation failure
  
  LoadingProfile --> ProfileLoaded : success
  LoadingProfile --> Error : load failure
  
  ProfileValid --> ProfileLoaded
  ProfileLoaded --> EditingProfile : edit
  EditingProfile --> Validating : save
  
  ProfileLoaded --> [*] : logout
}

note right of Validating
  Validates:
  - Email format
  - Student ID format
  - Required fields
  - Year range (1-6)
end note
@enduml
```

### State Descriptions
- **NoProfile**: No student profile exists
- **CreatingProfile**: User entering new profile data
- **LoadingProfile**: Loading existing profile from storage
- **EditingProfile**: Modifying profile information
- **Validating**: Validating profile data
- **ProfileValid**: Profile data validated successfully
- **ProfileLoaded**: Profile ready for use
- **Error**: Error loading or saving profile

---

## 3. Course Management Statechart

### Course Management States
```plantuml
@startuml
title Course Management Statechart

state CourseManagement {
  [*] --> CourseList
  CourseList --> AddingCourse : add course
  CourseList --> EditingCourse : select course
  CourseList --> DeletingCourse : delete course
  
  AddingCourse --> EnteringCourseData
  EnteringCourseData --> ValidatingCourse : save
  ValidatingCourse --> CourseAdded : success
  ValidatingCourse --> EnteringCourseData : validation failure
  
  EditingCourse --> LoadingCourseData
  LoadingCourseData --> ModifyingCourseData
  ModifyingCourseData --> ValidatingCourse : save
  ValidatingCourse --> CourseUpdated : success
  
  DeletingCourse --> ConfirmingDelete
  ConfirmingDelete --> CourseDeleted : confirmed
  ConfirmingDelete --> CourseList : cancelled
  
  CourseAdded --> CourseList
  CourseUpdated --> CourseList
  CourseDeleted --> CourseList
}

note right of ValidatingCourse
  Validates:
  - Course code uniqueness
  - Credits range (1-6)
  - Required fields
  - Instructor name format
end note
@enduml
```

### State Descriptions
- **CourseList**: Displaying list of courses
- **AddingCourse**: Initiating course creation
- **EnteringCourseData**: User inputting course details
- **ValidatingCourse**: Validating course data
- **CourseAdded**: Course successfully added
- **EditingCourse**: Initiating course edit
- **LoadingCourseData**: Loading existing course data
- **ModifyingCourseData**: User modifying course details
- **CourseUpdated**: Course successfully updated
- **DeletingCourse**: Initiating course deletion
- **ConfirmingDelete**: Waiting for user confirmation
- **CourseDeleted**: Course successfully deleted

---

## 4. Task Management Statechart

### Task Management States
```plantuml
@startuml
title Task Management Statechart

state TaskManagement {
  [*] --> TaskList
  TaskList --> AddingTask : add task
  TaskList --> EditingTask : select task
  TaskList --> CompletingTask : complete task
  TaskList --> DeletingTask : delete task
  
  AddingTask --> EnteringTaskData
  EnteringTaskData --> ValidatingTask : save
  ValidatingTask --> TaskAdded : success
  ValidatingTask --> EnteringTaskData : validation failure
  
  EditingTask --> LoadingTaskData
  LoadingTaskData --> ModifyingTaskData
  ModifyingTaskData --> ValidatingTask : save
  ValidatingTask --> TaskUpdated : success
  
  CompletingTask --> ConfirmingComplete
  ConfirmingComplete --> TaskCompleted : confirmed
  ConfirmingComplete --> TaskList : cancelled
  
  DeletingTask --> ConfirmingDelete
  ConfirmingDelete --> TaskDeleted : confirmed
  ConfirmingDelete --> TaskList : cancelled
  
  TaskAdded --> TaskList
  TaskUpdated --> TaskList
  TaskCompleted --> TaskList
  TaskDeleted --> TaskList
}

note right of ValidatingTask
  Validates:
  - Due date not in past
  - Priority selection
  - Course existence (if specified)
  - Required fields
end note
@enduml
```

### State Descriptions
- **TaskList**: Displaying list of tasks
- **AddingTask**: Initiating task creation
- **EnteringTaskData**: User inputting task details
- **ValidatingTask**: Validating task data
- **TaskAdded**: Task successfully added
- **EditingTask**: Initiating task edit
- **LoadingTaskData**: Loading existing task data
- **ModifyingTaskData**: User modifying task details
- **TaskUpdated**: Task successfully updated
- **CompletingTask**: Initiating task completion
- **ConfirmingComplete**: Waiting for user confirmation
- **TaskCompleted**: Task marked as complete
- **DeletingTask**: Initiating task deletion
- **ConfirmingDelete**: Waiting for user confirmation
- **TaskDeleted**: Task successfully deleted

---

## 5. Calendar View Statechart

### Calendar View States
```plantuml
@startuml
title Calendar View Statechart

state CalendarView {
  [*] --> MonthView
  MonthView --> WeekView : week view
  MonthView --> DayView : select day
  MonthView --> AddingEvent : add event
  
  WeekView --> MonthView : month view
  WeekView --> DayView : select day
  WeekView --> AddingEvent : add event
  
  DayView --> MonthView : month view
  DayView --> WeekView : week view
  DayView --> AddingEvent : add event
  
  AddingEvent --> EnteringEventData
  EnteringEventData --> ValidatingEvent : save
  ValidatingEvent --> EventAdded : success
  ValidatingEvent --> EnteringEventData : validation failure
  
  EventAdded --> PreviousView
  PreviousView --> MonthView : was month
  PreviousView --> WeekView : was week
  PreviousView --> DayView : was day
}

note right of ValidatingEvent
  Validates:
  - Start time before end time
  - Event duration reasonable
  - No time conflicts
  - Required fields
end note
@enduml
```

### State Descriptions
- **MonthView**: Monthly calendar view
- **WeekView**: Weekly timeline view
- **DayView**: Daily hourly view
- **AddingEvent**: Initiating event creation
- **EnteringEventData**: User inputting event details
- **ValidatingEvent**: Validating event data
- **EventAdded**: Event successfully added
- **PreviousView**: Remembering previous view state

---

## 6. Data Persistence Statechart

### Data Persistence States
```plantuml
@startuml
title Data Persistence Statechart

state DataPersistence {
  [*] --> Idle
  Idle --> Saving : auto-save trigger
  Idle --> Loading : application start
  Idle --> Exporting : user export
  Idle --> Importing : user import
  
  Saving --> WritingFiles
  WritingFiles --> Idle : success
  WritingFiles --> Error : write failure
  
  Loading --> ReadingFiles
  ReadingFiles --> Idle : success
  ReadingFiles --> Error : read failure
  
  Exporting --> CreatingBackup
  CreatingBackup --> Idle : success
  CreatingBackup --> Error : export failure
  
  Importing --> ValidatingImport
  ValidatingImport --> MergingData : valid
  ValidatingImport --> Error : invalid
  MergingData --> Idle : success
  MergingData --> Error : merge failure
  
  Error --> Idle : resolved
}

note right of Error
  Handles:
  - File I/O errors
  - Data corruption
  - Permission issues
  - Invalid formats
end note
@enduml
```

### State Descriptions
- **Idle**: No active persistence operations
- **Saving**: Auto-saving data to files
- **Loading**: Loading data from files
- **Exporting**: Creating data backup
- **Importing**: Restoring from backup
- **WritingFiles**: Writing data to CSV files
- **ReadingFiles**: Reading data from CSV files
- **CreatingBackup**: Creating export file
- **ValidatingImport**: Validating import data
- **MergingData**: Merging imported data
- **Error**: Persistence operation failed

---

## 7. User Interface Navigation Statechart

### UI Navigation States
```plantuml
@startuml
title UI Navigation Statechart

state UINavigation {
  [*] --> MainView
  MainView --> ProfileView : profile tab
  MainView --> CoursesView : courses tab
  MainView --> TasksView : tasks tab
  MainView --> CalendarView : calendar tab
  
  ProfileView --> EditProfileDialog : edit profile
  CoursesView --> CourseDialog : add course
  CoursesView --> CourseDialog : edit course
  TasksView --> TaskDialog : add task
  TasksView --> TaskDialog : edit task
  CalendarView --> EventDialog : add event
  
  EditProfileDialog --> ProfileView : save/cancel
  CourseDialog --> CoursesView : save/cancel
  TaskDialog --> TasksView : save/cancel
  EventDialog --> CalendarView : save/cancel
  
  ProfileView --> MainView : other tab
  CoursesView --> MainView : other tab
  TasksView --> MainView : other tab
  CalendarView --> MainView : other tab
  
  MainView --> ConfirmExit : exit application
  ConfirmExit --> [*] : confirmed
  ConfirmExit --> MainView : cancelled
}
@enduml
```

### State Descriptions
- **MainView**: Main application window
- **ProfileView**: Student profile tab
- **CoursesView**: Course management tab
- **TasksView**: Task management tab
- **CalendarView**: Calendar view tab
- **EditProfileDialog**: Profile editing dialog
- **CourseDialog**: Course creation/editing dialog
- **TaskDialog**: Task creation/editing dialog
- **EventDialog**: Event creation/editing dialog
- **ConfirmExit**: Application exit confirmation

---

## 8. Error Handling Statechart

### Error Handling States
```plantuml
@startuml
title Error Handling Statechart

state ErrorHandling {
  [*] --> NormalOperation
  NormalOperation --> ErrorDetected : exception/error
  
  ErrorDetected --> ClassifyingError
  ClassifyingError --> ValidationError : validation error
  ClassifyingError --> DataError : data error
  ClassifyingError --> SystemError : system error
  ClassifyingError --> UserError : user error
  
  ValidationError --> ShowingValidationMessage
  DataError --> AttemptingRecovery
  SystemError --> ShowingSystemError
  UserError --> ShowingUserMessage
  
  ShowingValidationMessage --> NormalOperation : acknowledged
  ShowingUserMessage --> NormalOperation : acknowledged
  
  AttemptingRecovery --> DataRecovered : success
  AttemptingRecovery --> DataCorrupted : failure
  
  DataRecovered --> NormalOperation
  DataCorrupted --> ShowingDataError
  
  ShowingSystemError --> GracefulShutdown : critical
  ShowingSystemError --> NormalOperation : recoverable
  
  ShowingDataError --> NormalOperation : acknowledged
  
  GracefulShutdown --> [*]
}
@enduml
```

### State Descriptions
- **NormalOperation**: System running normally
- **ErrorDetected**: Error or exception occurred
- **ClassifyingError**: Determining error type
- **ValidationError**: Input validation failed
- **DataError**: Data persistence error
- **SystemError**: Critical system error
- **UserError**: User operation error
- **ShowingValidationMessage**: Displaying validation error
- **ShowingUserMessage**: Displaying user error
- **ShowingSystemError**: Displaying system error
- **ShowingDataError**: Displaying data error
- **AttemptingRecovery**: Trying to recover data
- **DataRecovered**: Recovery successful
- **DataCorrupted**: Recovery failed
- **GracefulShutdown**: Safe application shutdown

---

## Implementation Notes

### State Machine Implementation
The statecharts are implemented using:
- **State Pattern**: Each major component uses state objects
- **Event-Driven Architecture**: State transitions triggered by events
- **Observer Pattern**: UI components observe state changes
- **Command Pattern**: User actions encapsulated as commands

### State Persistence
- Application state saved on each transition
- Recovery mechanisms for critical states
- State history for undo/redo operations
- Error state logging for debugging

### Testing Strategy
- Unit tests for each state transition
- Integration tests for state machine interactions
- UI tests for state visualization
- Error injection tests for robustness

### Performance Considerations
- State transitions optimized for minimal overhead
- Lazy loading of state data
- Caching of frequently accessed states
- Asynchronous state operations where appropriate

These statecharts provide a comprehensive view of the Student Planner application's dynamic behavior and serve as a foundation for both implementation and testing.
