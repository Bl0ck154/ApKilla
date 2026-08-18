package com.block154.apkilla;

import android.content.Context;

final class TargetResolver {
    private static final long TILE_RECENT_FALLBACK_MS = 15 * 60 * 1000L;
    static final long SHORTCUT_HOME_TARGET_MAX_AGE_MS = 5 * 60 * 1000L;

    private TargetResolver() {}

    static String resolveForTile(Context context) {
        String target = validLastTarget(context);
        if (target == null) return null;
        if (Prefs.isTargetForeground(context)) return target;

        // Preserve the old safety behavior while the Accessibility service is
        // healthy: home/settings explicitly invalidate the foreground target.
        // Only fall back to a recent target during a real service disconnect/rebind.
        if (KillAccessibilityService.isConnected()) return null;
        return Prefs.isTargetRecent(context, TILE_RECENT_FALLBACK_MS) ? target : null;
    }

    static String resolveLastBeforeHome(Context context) {
        String target = Prefs.getLastHomeTarget(context);
        if (target == null || target.trim().isEmpty() || isUnsafeTarget(context, target)) {
            return null;
        }
        return Prefs.isLastHomeTargetRecent(context, SHORTCUT_HOME_TARGET_MAX_AGE_MS)
                ? target
                : null;
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
