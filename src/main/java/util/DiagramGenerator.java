package util;

import net.sourceforge.plantuml.SourceStringReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DiagramGenerator {

    /**
     * Generates a class diagram (PNG) of the project's key classes.
     */
    public static void generateClassDiagram(String outputPath) throws Exception {
        String uml = """
                @startuml
                skinparam classAttributeIconSize 0
                skinparam backgroundColor transparent

                package "mvc (Framework)" {
                    interface Model {
                        +notifyChanged(ModelEvent)
                    }
                    interface View {
                        +getModel()
                        +getController()
                    }
                    interface Controller {
                        +getModel()
                        +getView()
                    }
                    class ModelEvent {
                        +ModelEvent(Object, int, String)
                    }
                    interface ModelListener {
                        +modelChanged(ModelEvent)
                    }
                    abstract AbstractModel {
                        #listeners : List<ModelListener>
                        +addModelListener(ModelListener)
                        +removeModelListener(ModelListener)
                        +notifyChanged(ModelEvent)
                    }
                    abstract AbstractController {
                        #model : Model
                        #view : View
                        +setModel(Model)
                        +setView(View)
                    }
                    abstract JFrameView {
                        +JFrameView(Model, Controller)
                        +modelChanged(ModelEvent)
                    }
                }

                package "planner (Application)" {
                    class Task {
                        -description : String
                        -completed : boolean
                        -dueDate : LocalDate
                        +Task(String)
                        +Task(String, LocalDate)
                        +getDescription()
                        +setDescription(String)
                        +isCompleted()
                        +setCompleted(boolean)
                        +getDueDate()
                        +setDueDate(LocalDate)
                    }
                    class PlannerModel {
                        -tasks : List<Task>
                        +addTask(Task)
                        +updateTask(int, Task)
                        +removeTask(int)
                        +clearTasks()
                        +getTasks()
                        +getTaskCount()
                    }
                    class PlannerController {
                        +actionPerformed(ActionEvent)
                    }
                    class PlannerView {
                        -descriptionField : JTextField
                        -dueDateField : JTextField
                        -taskList : JList<String>
                        +PlannerView(Model, Controller)
                        +getTaskDescription()
                        +getTaskDueDate()
                        +getSelectedTaskIndex()
                        +showError(String)
                        +modelChanged(ModelEvent)
                    }
                    class Main {
                        +main(String[])
                    }
                }

                Model <|.. AbstractModel
                View <|.. JFrameView
                Controller <|.. AbstractController
                ModelListener <|.. JFrameView

                AbstractModel <|-- PlannerModel
                AbstractController <|-- PlannerController
                JFrameView <|-- PlannerView

                ModelEvent --> Model
                ModelListener --> ModelEvent

                PlannerController --> PlannerModel
                PlannerController --> PlannerView
                PlannerView --> PlannerModel
                PlannerModel --> Task

                Main --> PlannerModel
                Main --> PlannerView
                Main --> PlannerController

                @enduml
                """;

        generateDiagram(uml, outputPath);
    }

    /**
     * Generates an MVC interaction diagram showing the flow of control and updates.
     */
    public static void generateMvcDiagram(String outputPath) throws Exception {
        String uml = """
                @startuml
                skinparam componentStyle rectangle
                skinparam backgroundColor transparent

                title MVC Architecture – Student Planner

                package "User Interface" {
                    [PlannerView] as View
                }

                package "Business Logic" {
                    [PlannerController] as Controller
                    [PlannerModel] as Model
                }

                package "Data" {
                    [Task] as Task
                }

                ' User interactions
                View -right-> Controller : actionPerformed()
                Controller -down-> Model : add/update/delete/clear
                Model -down-> Task : manipulate

                ' Observer notification
                Model -up-> View : notifyChanged(ModelEvent)
                View -up-> View : modelChanged()\\n(update GUI)

                ' Dependencies
                View ..> Model : observes
                Controller ..> Model : modifies
                Controller ..> View : reads input

                @enduml
                """;

        generateDiagram(uml, outputPath);
    }

    private static void generateDiagram(String uml, String outputPath) throws Exception {
        SourceStringReader reader = new SourceStringReader(uml);
        try (OutputStream out = new FileOutputStream(outputPath)) {
            reader.outputImage(out);
        }
    }

    /**
     * Convenience method to generate both diagrams at once.
     */
    public static void generateAllDiagrams(String classDiagramPath, String mvcDiagramPath) {
        try {
            Files.createDirectories(Paths.get(classDiagramPath).getParent());
            Files.createDirectories(Paths.get(mvcDiagramPath).getParent());

            generateClassDiagram(classDiagramPath);
            System.out.println("Class diagram saved to: " + classDiagramPath);

            generateMvcDiagram(mvcDiagramPath);
            System.out.println("MVC diagram saved to: " + mvcDiagramPath);
        } catch (Exception e) {
            System.err.println("Diagram generation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Example usage
    public static void main(String[] args) {
        generateAllDiagrams("diagrams/class_diagram.png", "diagrams/mvc_diagram.png");
    }
}
