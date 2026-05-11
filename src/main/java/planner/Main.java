package planner;

import planner.model.PlannerModel;
import planner.persistence.PlannerRepository;
import planner.service.PlannerService;
import planner.ui.AppTheme;
import planner.ui.PlannerController;
import planner.ui.PlannerView;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point for the Student Planner application.
 * Initializes the MVC components and starts the application.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    /**
     * Main method to start the application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            AppTheme.loadPersistedTheme();
            AppTheme.installLookAndFeel();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set application look and feel", e);
        }
        
        // Initialize MVC components
        PlannerModel model = new PlannerModel();
        PlannerView view = new PlannerView(model, null); // Will set controller after creation
        PlannerService service = new PlannerService(model);
        PlannerController controller = new PlannerController(model, view, service);
        
        // Set the controller reference in the view
        view.setController(controller);
        
        // Load existing data if available
        PlannerRepository repository = new PlannerRepository();
        try {
            // Load account state first to determine if user should be logged in
            boolean isLoggedIn = repository.loadAccountState();
            System.out.println("DEBUG: Account state loaded, isLoggedIn = " + isLoggedIn);
                        if (isLoggedIn) {
                // If user was logged in, load their profile data
                System.out.println("DEBUG: Loading planner data for logged in user");
                repository.loadPlannerData(model);
                LOGGER.info("Planner data loaded successfully");
                System.out.println("DEBUG: Current student after load: " + (model.getCurrentStudent() != null ? model.getCurrentStudent().getFirstName() + " " + model.getCurrentStudent().getLastName() : "null"));
            } else {
                // If user was logged out, start with clean state
                LOGGER.info("Planner data loaded successfully (clean state)");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not load planner data", e);
        }
        
        // Add shutdown hook to save data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                AppTheme.persistTheme();
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not persist theme preference", e);
            }
            try {
                // Only save if model has data and student is logged in
                if (model.getCurrentStudent() != null && !model.getCourses().isEmpty()) {
                    repository.savePlannerData(model);
                    LOGGER.info("Planner data saved successfully");
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not save planner data", e);
            }
        }));
        
        LOGGER.info("Student Planner application started");
    }
}
