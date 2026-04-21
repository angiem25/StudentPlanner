package mvc;

public interface Controller {
    void setModel(Model model);
    Model getModel();
    View getView();
    void setView(View view);
}
