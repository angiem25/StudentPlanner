# PROMPT K5: TIMER PRESETS (30 / 60 / 90 MIN)

## **MANDATORY**: Add one-click preset durations to the study timer.

## Module of Thought

**Phase**: UI Feature Addition  
**Purpose**: Improve timer usability by reducing manual input for common study durations  
**Dependencies**: PROMPT-K3 (Study Timer)

## Feature Scope

Add preset controls in `TimerPanel` so users can instantly choose:
- `30 min`
- `60 min`
- `90 min`

Presets should:
- update input and display immediately
- preserve existing start/pause/reset behavior
- be blocked while a timer is running (to avoid accidental mid-session changes)

---

## Implementation Summary

### File Updated

```
src/main/java/planner/ui/timer/TimerPanel.java
```

### New UI Components
- `preset30Button`
- `preset60Button`
- `preset90Button`
- A `Presets:` row placed below the minutes input

### New Logic
- Added `applyPresetMinutes(int minutes)`:
  1. If timer is running, show info dialog and do not change values
  2. If idle, set `minutesInput` to preset value
  3. Set `totalSeconds` and `remainingSeconds`
  4. Refresh display (`MM:SS`) immediately

---

## How Preset Logic Works

1. User clicks preset button (`30`, `60`, or `90`)
2. Action listener calls `applyPresetMinutes(...)`
3. Method checks timer state:
   - **Running**: prompt user to reset/finish current timer first
   - **Idle**: apply preset to input + countdown state + display
4. User clicks Start to begin countdown from selected preset

This keeps timer state consistent and avoids partial session corruption.

---

## Why This Design

1. **Single helper method**
   - Keeps button handlers tiny and consistent
   - Centralizes validation/guard logic

2. **Guard during running sessions**
   - Prevents accidental preset swaps mid-countdown
   - Avoids confusion around pause/resume state

3. **Immediate display update**
   - User can confirm chosen duration before pressing Start
   - Better UX than updating only after Start

4. **No persistence added**
   - Presets are interaction shortcuts, not long-term data
   - Keeps timer component lightweight

---

## Test Cases

### A) Happy Path
1. Open Timer tab
2. Click `30 min`
3. Verify input shows `30` and display shows `30:00`
4. Click Start and verify countdown begins from `30:00`

Repeat for `60 min` and `90 min`.

### B) Running Guard
1. Start a timer
2. While running, click `60 min`
3. Verify info message appears
4. Verify running countdown is unchanged

### C) Pause + Preset Guard
1. Start timer, then Pause
2. Click `90 min`
3. Verify preset is still blocked while session is active
4. Click Reset, then click `90 min`
5. Verify preset applies correctly after reset

### D) Reset Consistency
1. Click `60 min`
2. Click Start, then Reset
3. Verify display returns to `60:00` (current input value behavior)

---

## Verification Checklist

- [ ] Preset buttons are visible in Timer tab
- [ ] Clicking preset updates input field
- [ ] Clicking preset updates display before Start
- [ ] Running timer blocks preset changes
- [ ] Existing Start/Pause/Reset behavior still works
- [ ] No compile errors after update

---

**Next (Optional)**:
- Add selected-preset highlight state
- Add user-configurable preset list
- Add Pomodoro cycle presets (25/5, 50/10)
