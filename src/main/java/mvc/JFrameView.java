package mvc;

import javax.swing.*;

public abstract class JFrameView extends JFrame implements View, ModelListener {
    private Model model;
    private Controller controller;

    public JFrameView(Model model, Controller controller) {
        setModel(model);
        setController(controller);
    }

    public void registerWithModel() {
        ((AbstractModel) model).addModelListener(this);
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
        registerWithModel();
    }
}
