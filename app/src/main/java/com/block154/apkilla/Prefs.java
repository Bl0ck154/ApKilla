package com.block154.apkilla;

import android.content.Context;
import android.content.SharedPreferences;

final class Prefs {
    private static final String FILE = "state";
    private static final String KEY_LAST_TARGET = "last_target";
    private static final String KEY_LAST_TARGET_AT = "last_target_at";
    private static final String KEY_TARGET_IS_FOREGROUND = "target_is_foreground";
    private static final String KEY_PENDING_TARGET = "pending_target";
    private static final String KEY_PENDING_AT = "pending_at";
    private static final String KEY_TILE_ADDED = "tile_added";

    private Prefs() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    static String getLastTarget(Context context) {
        return prefs(context).getString(KEY_LAST_TARGET, null);
    }

    static long getLastTargetAt(Context context) {
        return prefs(context).getLong(KEY_LAST_TARGET_AT, 0L);
    }

    static void setForegroundTarget(Context context, String packageName) {
        prefs(context).edit()
                .putString(KEY_LAST_TARGET, packageName)
                .putLong(KEY_LAST_TARGET_AT, System.currentTimeMillis())
                .putBoolean(KEY_TARGET_IS_FOREGROUND, true)
                .apply();
    }

    static boolean isTargetForeground(Context context) {
        return prefs(context).getBoolean(KEY_TARGET_IS_FOREGROUND, false);
    }

    static boolean isTargetRecent(Context context, long maxAgeMs) {
        long lastSeen = getLastTargetAt(context);
        return lastSeen > 0L && System.currentTimeMillis() - lastSeen <= maxAgeMs;
    }

    static void markNoForegroundTarget(Context context) {
        prefs(context).edit()
                .putBoolean(KEY_TARGET_IS_FOREGROUND, false)
                .apply();
    }

    static void requestKill(Context context, String packageName) {
        prefs(context).edit()
                .putString(KEY_PENDING_TARGET, packageName)
                .putLong(KEY_PENDING_AT, System.currentTimeMillis())
                .apply();
    }

    static boolean hasPendingKill(Context context) {
        return getPendingTarget(context) != null;
    }

    static String getPendingTarget(Context context) {
        return prefs(context).getString(KEY_PENDING_TARGET, null);
    }

    static long getPendingAt(Context context) {
        return prefs(context).getLong(KEY_PENDING_AT, 0L);
    }

    static void clearPendingKill(Context context) {
        prefs(context).edit()
                .remove(KEY_PENDING_TARGET)
                .remove(KEY_PENDING_AT)
                .apply();
    }

    static boolean isTileAdded(Context context) {
        return prefs(context).getBoolean(KEY_TILE_ADDED, false);
    }

    static void setTileAdded(Context context, boolean added) {
        prefs(context).edit().putBoolean(KEY_TILE_ADDED, added).apply();
    }
}
