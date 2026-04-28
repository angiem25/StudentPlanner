# Student Planner

A modular MVC-based student planner application built with Java Swing. This application helps students manage their courses, tasks, and academic information using a clean, modular architecture.

## Features

- **Student Profile Management**: Create and manage student information including name, email, student ID, year, and major
- **Course Management**: Add, update, and remove courses with instructor and credit information
- **Task Management**: Create, organize, and track tasks with due dates, priorities, and course associations
- **Task Color Palette**: Assign color-coded accents to tasks and see the same color in task list and calendar
- **Study Timer Presets**: One-click timer presets for 30, 60, and 90 minute sessions
- **Due Date Reminder Popups**: Daily task reminder popups start 7 days before due date for incomplete tasks
- **Weekly Priorities Checklist**: Dedicated weekly tab with checkbox-style important tasks due this week
- **Data Persistence**: Automatic saving and loading of all planner data
- **Modular Architecture**: Clean separation of concerns using the MVC pattern

## Architecture

The application follows a modular MVC (Model-View-Controller) architecture:

### Core MVC Framework
- **Model**: Core interfaces and abstract classes for data management
- **View**: UI components for displaying data and capturing user input
- **Controller**: Business logic coordination between model and view
- **Observer Pattern**: Automatic UI updates when data changes

### Application Modules
- **Domain Models**: Student, Course, Task entities with proper validation
- **Service Layer**: Business logic and data validation
- **Persistence Layer**: File-based data storage and retrieval
- **UI Layer**: Swing-based user interface with tabbed navigation

## Project Structure

```
src/main/java/
├── mvc/                          # Core MVC framework
│   ├── Model.java               # Model interface
│   ├── View.java                # View interface
│   ├── Controller.java          # Controller interface
│   ├── AbstractModel.java       # Base model with observer support
│   ├── AbstractView.java        # Base view implementation
│   ├── AbstractController.java   # Base controller implementation
│   ├── ModelEvent.java          # Event objects for model changes
│   └── ModelListener.java       # Observer interface
├── planner/
│   ├── Main.java                # Application entry point
│   ├── model/                   # Domain models
│   │   ├── Student.java         # Student entity
│   │   ├── Course.java          # Course entity
│   │   ├── Task.java            # Task entity
│   │   └── PlannerModel.java    # Main model implementation
│   ├── service/                 # Business logic
│   │   └── PlannerService.java  # Service layer
│   ├── ui/                      # User interface
│   │   ├── PlannerView.java     # Main view implementation
│   │   └── PlannerController.java # Main controller
│   └── persistence/             # Data storage
│       └── PlannerRepository.java # File-based repository
```

## Requirements

- Java 17 or higher
- Maven 3.6 or higher

## Installation and Running

### Using Maven

1. Clone or download the project
2. Navigate to the project directory
3. Compile the project:
   ```bash
   mvn clean compile
   ```

4. Run the application:
   ```bash
   mvn exec:java -Dexec.mainClass="planner.Main"
   ```

### Creating Executable JAR

1. Build the project:
   ```bash
   mvn clean package
   ```

2. Run the executable JAR:
   ```bash
   java -jar target/student-planner-1.0.0.jar
   ```

## Usage

### Student Profile
1. Click on the "Student Profile" tab
2. Fill in your personal information
3. Click "Save Student Profile" to save

### Course Management
1. Click on the "Courses" tab
2. Enter course details (name, code, instructor, credits)
3. Click "Add Course" to add a new course
4. Select a course from the list to edit or remove it

### Task Management
1. Click on the "Tasks" tab
2. Enter task details (title, description, due date, priority)
3. Optionally associate the task with a course
4. Pick a task color from the palette (default is red)
5. Click "Add Task" to create a new task
6. Use "Toggle Complete" to mark tasks as completed
7. Select a task from the list to edit/remove and change color

### Timer
1. Click on the "Timer" tab
2. Enter minutes manually or use preset buttons (`30 min`, `60 min`, `90 min`)
3. Click "Start" to begin countdown
4. Use "Pause/Resume" and "Reset" as needed
5. On completion, audio + visual alerts are shown

### Due Date Reminder Popups
1. Keep tasks incomplete to receive reminders
2. Popups appear daily starting 7 days before each task due date
3. Reminder status shows `Due in X days`, `Due tomorrow`, or `Due today`
4. Each task is shown once per day to avoid popup spam
5. Marking task complete stops reminder popups for that task

### Weekly Priorities Checklist
1. Click the `Weekly Priorities` tab
2. Review the most important tasks due in the current week
3. Click the checkbox next to a task to mark it complete/incomplete
4. Completed tasks stay visible and appear checked off automatically
5. The checklist updates when tasks are added, edited, or completed elsewhere

## Data Storage

The application automatically saves all data to a `planner_data` directory in the same location where the application is run. Data is stored in CSV format for easy backup and portability.

- `student.csv`: Student profile information
- `courses.csv`: Course information
- `tasks.csv`: Task information with completion status and task accent color

## Design Principles

This application demonstrates several important software design principles:

- **Separation of Concerns**: Clear separation between data, business logic, and presentation
- **Observer Pattern**: Automatic UI updates when model data changes
- **Modularity**: Each component has a single responsibility
- **Extensibility**: Easy to add new features without modifying existing code
- **Testability**: Components can be tested independently

## Future Enhancements

Potential improvements for future versions:

- Custom color picker in addition to fixed palette
- Grade tracking and GPA calculation
- Course schedule visualization
- Export/import functionality
- Cloud synchronization
- Mobile application
- Advanced filtering and search capabilities
- Task categorization and tagging

## Contributing

When contributing to this project, please follow the existing architectural patterns and coding standards. The modular MVC structure should be maintained to ensure consistency and maintainability.

## License

This project is provided as an educational example of modular Java application development.
