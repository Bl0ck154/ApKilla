package com.block154.apkilla;

import android.content.Context;

final class TargetResolver {
    private static final long TILE_RECENT_FALLBACK_MS = 15 * 60 * 1000L;

    private TargetResolver() {}

    static String resolveForTile(Context context) {
        String target = validLastTarget(context);
        if (target == null) return null;
        if (Prefs.isTargetForeground(context)) return target;

        // Preserve a recently tracked target only during a real Accessibility
        // service disconnect/rebind. While the service is healthy, Home and
        // Settings explicitly invalidate the foreground target.
        if (KillAccessibilityService.isConnected()) return null;
        return Prefs.isTargetRecent(context, TILE_RECENT_FALLBACK_MS) ? target : null;
    }

    private static String validLastTarget(Context context) {
        String target = Prefs.getLastTarget(context);
        if (target == null || target.trim().isEmpty() || isUnsafeTarget(context, target)) {
            return null;
        }
        return target;
    }

    static boolean isUnsafeTarget(Context context, String packageName) {
        return packageName.equals(context.getPackageName())
                || packageName.equals("android")
                || packageName.equals("com.android.systemui")
                || packageName.equals("com.android.settings")
                || packageName.equals("com.oplus.settings")
                || packageName.equals("com.coloros.settings");
    }
}
