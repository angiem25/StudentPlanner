package mvc;

public interface View {
    Controller getController();
    void setController(Controller controller);
    Model getModel();
    void setModel(Model model);
}
