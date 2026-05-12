package mvc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AbstractController.
 */
class AbstractControllerTest {
    
    private TestModel model;
    private TestView view;
    private TestController controller;
    
    @BeforeEach
    void setUp() {
        model = new TestModel();
        view = new TestView();
        controller = new TestController(model, view);
    }
    
    @Test
    void testConstructor() {
        assertNotNull(controller);
        assertEquals(model, controller.getModel());
        assertEquals(view, controller.getView());
    }
    
    @Test
    void testGetModel() {
        assertEquals(model, controller.getModel());
    }
    
    @Test
    void testGetView() {
        assertEquals(view, controller.getView());
    }
    
    @Test
    void testSetModel() {
        TestModel newModel = new TestModel();
        controller.setModel(newModel);
        assertEquals(newModel, controller.getModel());
    }
    
    @Test
    void testSetView() {
        TestView newView = new TestView();
        controller.setView(newView);
        assertEquals(newView, controller.getView());
    }
    
    @Test
    void testSetModelWithNull() {
        controller.setModel(null);
        assertNull(controller.getModel());
    }
    
    @Test
    void testSetViewWithNull() {
        controller.setView(null);
        assertNull(controller.getView());
    }
    
    /**
     * Concrete test implementation of Model.
     */
    private static class TestModel implements Model {
        @Override
        public void notifyChanged(ModelEvent event) {
            // Test implementation - does nothing
        }
    }
    
    /**
     * Concrete test implementation of View.
     */
    private static class TestView implements View {
        private Model model;
        private Controller controller;
        
        @Override
        public Model getModel() {
            return model;
        }
        
        @Override
        public Controller getController() {
            return controller;
        }
        
        public void setModel(Model model) {
            this.model = model;
        }
        
        public void setController(Controller controller) {
            this.controller = controller;
        }
    }
    
    /**
     * Concrete test implementation of AbstractController.
     */
    private static class TestController extends AbstractController {
        public TestController(Model model, View view) {
            super(model, view);
        }
    }
}
