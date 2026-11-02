package com.block154.apkilla;

/** Pure Java rules shared by Android code and local unit tests. */
final class StopPolicy {
    private StopPolicy() {}

    static boolean isExpired(long requestedAt, long now, long timeoutMs) {
        // A future time can result from a clock adjustment, reboot, or corrupt state.
        return requestedAt <= 0 || requestedAt > now || now - requestedAt >= timeoutMs;
    }

    static boolean isInputMethodPackage(String packageName, String imeComponent) {
        return packageName != null && imeComponent != null
                && imeComponent.startsWith(packageName + "/");
    }

    static boolean isUsableTarget(boolean connected, boolean foreground, boolean unsafe) {
        return connected && foreground && !unsafe;
    }

    /** Require the expected package or app label in the upper App info header. */
    static boolean matchesAppInfoHeader(String expectedPackage, String expectedLabel,
                                        String visibleText, boolean headerRegion) {
        if (!headerRegion || expectedPackage == null || expectedPackage.isEmpty()
                || visibleText == null || visibleText.trim().isEmpty()) return false;
        String value = visibleText.trim();
        return expectedPackage.equals(value)
                || (expectedLabel != null && !expectedLabel.trim().isEmpty()
                && expectedLabel.trim().equalsIgnoreCase(value));
    }
}
