package com.block154.apkilla;

import android.content.Context;
import android.provider.Settings;

/** Resolves only a live foreground app; persisted targets are never a fallback. */
final class TargetResolver {
    private TargetResolver() {}

    static String resolveForTile(Context context) {
        String target = Prefs.getLastTarget(context);
        if (!StopPolicy.isUsableTarget(
                KillAccessibilityService.isConnected(),
                Prefs.isTargetForeground(context),
                isUnsafeTarget(context, target))) {
            return null;
        }
        return target;
    }

    static boolean isInputMethodTarget(Context context, String packageName) {
        if (packageName == null) return false;
        try {
            String ime = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.DEFAULT_INPUT_METHOD);
            return StopPolicy.isInputMethodPackage(packageName, ime);
        } catch (RuntimeException ignored) {
            return false;
        }
    }

    static boolean isUnsafeTarget(Context context, String packageName) {
        if (packageName == null || packageName.trim().isEmpty()) return true;
        String lower = packageName.toLowerCase(java.util.Locale.ROOT);
        return packageName.equals(context.getPackageName())
                || isInputMethodTarget(context, packageName)
                || lower.equals("android")
                || lower.equals("com.android.systemui")
                || lower.equals("com.android.settings")
                || lower.equals("com.oplus.settings")
                || lower.equals("com.coloros.settings")
                || lower.equals("com.android.permissioncontroller")
                || lower.equals("com.google.android.permissioncontroller")
                || lower.equals("com.android.phone")
                || lower.endsWith(".settings")
                || lower.contains("launcher")
                || lower.contains("quickstep")
                || lower.contains("trebuchet");
    }
}
