package planner;

import java.time.LocalDate;
import mvc.AbstractController;
import mvc.JFrameView;

public class PlannerController extends AbstractController {
    public PlannerController() {
        setModel(new PlannerModel());
        setView(new PlannerView((PlannerModel) getModel(), this));
        ((JFrameView) getView()).setVisible(true);
    }

    public void operation(String option) {
        if (option.equals(PlannerView.MINUS)) {
            ((PlannerModel) getModel()).subtract();
        } else if (option.equals(PlannerView.PLUS)) {
            ((PlannerModel) getModel()).add();
        } else if (option.equals(PlannerView.CLEAR)) {
            ((PlannerModel) getModel()).clear();
        } else if (option.equals(PlannerView.EQUALS)) {
            ((PlannerModel) getModel()).equals();
        } else {
            ((PlannerModel) getModel()).store(Integer.parseInt(option));
        }
    }

    public void addTodo(String todo) {
        ((PlannerModel) getModel()).addTodoForToday(todo);
    }

    public void addEvent(LocalDate date, String eventText) {
        ((PlannerModel) getModel()).addEvent(date, eventText);
    }
}
