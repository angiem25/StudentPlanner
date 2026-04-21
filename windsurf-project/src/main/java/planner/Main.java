package planner;

import planner.model.PlannerModel;
import planner.persistence.PlannerRepository;
import planner.service.PlannerService;
import planner.ui.PlannerController;
import planner.ui.PlannerView;

import javax.swing.*;
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
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set system look and feel", e);
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
            repository.loadPlannerData(model);
            LOGGER.info("Planner data loaded successfully");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not load planner data", e);
        }
        
        // Add shutdown hook to save data
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                repository.savePlannerData(model);
                LOGGER.info("Planner data saved successfully");
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Could not save planner data", e);
            }
        }));
        
        LOGGER.info("Student Planner application started");
    }
}
