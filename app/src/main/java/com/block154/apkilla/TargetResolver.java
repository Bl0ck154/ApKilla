package com.block154.apkilla;

import android.content.Context;

final class TargetResolver {
    private static final long TILE_RECENT_FALLBACK_MS = 15 * 60 * 1000L;
    static final long SHORTCUT_RECENT_FALLBACK_MS = 30 * 60 * 1000L;

    private TargetResolver() {}

    static String resolveForTile(Context context) {
        String target = validLastTarget(context);
        if (target == null) return null;
        if (Prefs.isTargetForeground(context)) return target;
        return Prefs.isTargetRecent(context, TILE_RECENT_FALLBACK_MS) ? target : null;
    }

    static String resolveRecent(Context context, long maxAgeMs) {
        String target = validLastTarget(context);
        if (target == null) return null;
        return Prefs.isTargetForeground(context) || Prefs.isTargetRecent(context, maxAgeMs)
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
