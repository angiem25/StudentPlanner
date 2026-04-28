# PROMPT K-4: TASK COLOR PALETTE (CUSTOMIZATION + PERSISTENCE)

## **MANDATORY**: Implement and document task color customization across task list and calendar views.

## Module of Thought

**Phase**: UI Feature Addition  
**Purpose**: Add user-selectable color coding for tasks (primary + secondary palette)  
**Dependencies**: PROMPT-F (User Interface), PROMPT-G (Persistence), PROMPT-K2 (Calendar Views)

## Feature Requirements (Implemented)

**R3.1**: Basic color palette with primary and secondary colors  
**R3.2**: User can click a color  
**R3.3**: Task toggle/preview updates to clicked color  
**R3.4**: User can click another color to change again  
**R3.5**: Color is saved when Add Task or Update Task is clicked  

**Default**: Red (`#E53935`)

---

## What Changed in Code

### New File Added

```
src/main/java/planner/model/TaskPalette.java
```

**Purpose**:
- Defines allowed palette choices in one model-level location
- Provides canonicalization/fallback logic for color values
- Prevents invalid/unrecognized colors from being persisted

### Existing Files Updated

1. `src/main/java/planner/model/Task.java`
   - Added `accentColorHex` field
   - Added `DEFAULT_ACCENT_COLOR_HEX = "#E53935"`
   - Added getter/setter for accent color with default fallback
   - Included accent color in `toString()`

2. `src/main/java/planner/ui/AppTheme.java`
   - Added helper methods for palette + color conversion:
     - `taskPaletteHexStrings()`
     - `colorFromHex(String)`
     - `taskAccentChipBackground(Color)`
     - `taskAccentChipForeground(Color)`
     - `contrastingForeground(Color)`
   - Added color-mixing utility methods for light/dark theme compatibility

3. `src/main/java/planner/service/PlannerService.java`
   - Extended task add/update operations to accept `accentColorHex`
   - Canonicalizes color via `TaskPalette.canonicalHex(...)` before save

4. `src/main/java/planner/ui/PlannerController.java`
   - Updated `addTask(...)` and `updateTask(...)` signatures to forward accent color

5. `src/main/java/planner/ui/PlannerView.java`
   - Added task color form state: `taskFormAccentHex`
   - Added clickable swatches from `TaskPalette.HEX_CHOICES`
   - Added task color preview toggle and reset button
   - Wires selected color into Add/Update task actions
   - Loads selected task color into form when editing
   - Task list renderer now reflects each task's accent color

6. `src/main/java/planner/persistence/PlannerRepository.java`
   - `tasks.csv` now stores an additional accent color column
   - Backward compatible load logic:
     - old rows (7 columns) still load
     - new rows (8 columns) restore color value

7. `src/main/java/planner/ui/calendar/CalendarView.java`
   - Task labels now use per-task accent colors
   - Day/Week timeline also displays tasks by due hour using same accent logic

---

## How the Color Palette Feature Works

## 1) Palette Source
- Palette is defined in `TaskPalette.HEX_CHOICES`
- Includes 8 colors (primary + secondary style choices)
- Red is first/default option

## 2) Form Interaction
- User opens Tasks tab
- User clicks a swatch
- `taskFormAccentHex` updates to chosen value
- Preview toggle updates immediately to show chosen color
- User may click any other swatch to switch again
- Reset returns selection to default red

## 3) Save Flow
- On **Add Task** or **Update Task**, view passes `taskFormAccentHex` to controller
- Controller forwards to service
- Service canonicalizes with `TaskPalette.canonicalHex(...)`
- Model stores valid hex on `Task.accentColorHex`
- Repository persists it to `tasks.csv`

## 4) Render Flow
- Task list cell renderer gets each task's accent hex
- AppTheme computes background/foreground colors (theme-aware)
- Calendar task chips use same accent rendering
- Completed tasks override accent with completed (gray) styling

---

## Logic Rationale (Why These Choices)

1. **Model-level palette class (`TaskPalette`)**
   - Keeps domain validation independent from Swing/UI code
   - Makes service validation deterministic and testable

2. **Hex-based storage**
   - Human-readable in CSV
   - Easy to serialize/deserialize
   - Stable across UI refactors

3. **Canonicalization in service layer**
   - Business rule enforcement in one place
   - Protects model/repository from invalid UI or future API inputs

4. **Theme-aware rendering helpers**
   - Same accent appears readable in light and dark mode
   - Prevents low-contrast text issues

5. **Backward-compatible repository load**
   - Existing users with old `tasks.csv` do not lose data or crash on upgrade

---

## Suggested Test Cases

## A) Functional UI Tests

1. **Default color**
   - Create task without touching swatches
   - Expected: task shows red accent in list/calendar

2. **Select and save color**
   - Choose blue swatch, add task
   - Expected: new task appears blue in list/calendar

3. **Change color on update**
   - Select existing task, choose green, click Update
   - Expected: same task changes to green

4. **Switch colors repeatedly**
   - Click multiple swatches before saving
   - Expected: preview updates every click, final selected color is saved

5. **Reset behavior**
   - Pick non-red color, click Reset, save
   - Expected: saved color is default red

## B) Persistence Tests

1. **Save and relaunch**
   - Create tasks with different colors, close/reopen app
   - Expected: colors restored from `tasks.csv`

2. **Backward compatibility**
   - Load old 7-column `tasks.csv`
   - Expected: tasks load with default red, no errors

## C) Logic/Validation Tests (Unit-oriented)

1. `TaskPalette.canonicalHex(null)` -> default red  
2. `TaskPalette.canonicalHex("invalid")` -> default red  
3. Known palette value keeps canonical formatting  
4. `Task.setAccentColorHex(null/blank)` -> default red  
5. Service add/update always stores canonical palette value

## D) Theme/Accessibility Tests

1. Light mode contrast remains readable for all swatches  
2. Dark mode contrast remains readable for all swatches  
3. Completed task gray style still takes priority over accent

---

## Verification Checklist

- [ ] New `TaskPalette` file exists and is referenced by service/UI
- [ ] Task model contains `accentColorHex` with default fallback
- [ ] Add Task and Update Task both persist selected color
- [ ] Task list rendering uses accent for incomplete tasks
- [ ] Calendar rendering uses accent for tasks
- [ ] `tasks.csv` includes accent column on save
- [ ] Older `tasks.csv` format still loads safely
- [ ] Reset returns form color to default red

---

**Next (Optional)**:
- Add custom color picker dialog beyond fixed palette
- Add legend/filter by color in Tasks tab
- Add color usage analytics (count by color)
