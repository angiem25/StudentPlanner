# PROMPT K7: WEEKLY PRIORITIES CHECKLIST

## **MANDATORY**: Add a weekly priorities tab that looks and behaves like a traditional checklist.

## Module of Thought

**Phase**: UI Feature Addition  
**Purpose**: Surface the most important tasks due this week in a fast, checklist-oriented view  
**Dependencies**: PROMPT-F (UI), PROMPT-D (Task model), PROMPT-E (service/controller flow)

## Feature Requirements

- Show tasks due in the current week
- Sort tasks by importance, then due date
- Render each task with a clickable checkbox next to its name
- Clicking the checkbox marks task complete/incomplete
- Completed tasks remain visible and display as checked
- Tab stays synchronized with task updates from anywhere else in the app

---

## Implementation Summary

### File Updated

```
src/main/java/planner/ui/PlannerView.java
src/main/java/planner/ui/PlannerController.java
```

### UI Structure

- New tab: `Weekly Priorities`
- Range label for the active week
- `JList<Task>` backed by `DefaultListModel<Task>`
- Custom `ListCellRenderer<Task>` that renders each row as a `JCheckBox`

### Checklist Logic

1. Filter tasks to current week (`Monday` through `Sunday`)
2. Sort by:
   - `HIGH`
   - `MEDIUM`
   - `LOW`
   - then by due date/time
3. Render each task row as a checkbox
4. Clicking near the checkbox toggles task completion through controller logic
5. Model events refresh the list automatically

---

## Design Decisions

1. **Checkbox renderer**
   - Gives the tab a familiar checklist appearance
   - Keeps task title and due details on one row

2. **Silent toggle method in controller**
   - Prevents success popup dialogs on every checkbox click
   - Makes checklist interaction feel immediate and natural

3. **Keep completed tasks visible**
   - Matches traditional checklist behavior
   - Lets users see what was finished this week

4. **Observer-driven refresh**
   - If task is completed from another tab, this tab updates automatically

---

## Suggested Test Cases

1. Add tasks due this week with mixed priorities -> verify sorted order  
2. Click checkbox on incomplete task -> verify it becomes completed  
3. Click checkbox again -> verify it becomes incomplete  
4. Complete task from `Tasks` tab -> verify checklist updates automatically  
5. Completed task remains visible and appears checked  

---

## Verification Checklist

- [ ] Weekly Priorities tab exists
- [ ] Tasks are shown as checkbox rows
- [ ] Checkbox click toggles completion
- [ ] Completed tasks display as checked
- [ ] Weekly list refreshes after task events
- [ ] No compile/lint issues
