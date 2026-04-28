package planner.model;

/**
 * Primary and secondary hex colors users can assign to tasks for list and calendar coding.
 */
public final class TaskPalette {

    /** Default first entry matches {@link Task#DEFAULT_ACCENT_COLOR_HEX}. */
    public static final String[] HEX_CHOICES = {
            "#E53935", "#1E88E5", "#43A047",
            "#FB8C00", "#8E24AA", "#00897B",
            "#FDD835", "#3949AB"
    };

    private TaskPalette() {
    }

    public static boolean containsHex(String hex) {
        if (hex == null || hex.isBlank()) {
            return false;
        }
        String t = hex.trim();
        for (String p : HEX_CHOICES) {
            if (p.equalsIgnoreCase(t)) {
                return true;
            }
        }
        return false;
    }

    /** Canonical form from palette; unknown values fall back to default red. */
    public static String canonicalHex(String hex) {
        if (hex == null || hex.isBlank()) {
            return Task.DEFAULT_ACCENT_COLOR_HEX;
        }
        String t = hex.trim();
        for (String p : HEX_CHOICES) {
            if (p.equalsIgnoreCase(t)) {
                return p;
            }
        }
        return Task.DEFAULT_ACCENT_COLOR_HEX;
    }
}
