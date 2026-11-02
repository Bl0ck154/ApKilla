package com.block154.apkilla;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayDeque;

/**
 * Fail-closed guard before pressing Settings' Force stop button.
 * Unknown manufacturer layouts must not result in a click on the wrong app.
 */
final class AppInfoVerifier {
    private static final int MAX_NODES = 450;

    private AppInfoVerifier() {}

    static boolean isExpectedTarget(Context context, AccessibilityNodeInfo root, String target) {
        if (root == null || target == null || target.isEmpty()) return false;

        // Android 11+ package visibility can hide some third-party labels.
        // The manifest scopes visibility to launchable apps; when a label is unavailable, only
        // the exact package identifier in the visible header is sufficient.
        String label = null;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(target, 0);
            CharSequence name = pm.getApplicationLabel(info);
            if (name != null && !name.toString().trim().isEmpty()) label = name.toString().trim();
        } catch (PackageManager.NameNotFoundException | RuntimeException ignored) {
            // Fail closed if Settings does not explicitly identify the target package.
        }

        Rect screen = new Rect();
        root.getBoundsInScreen(screen);
        if (screen.height() <= 0) return false;
        int topLimit = screen.top + screen.height() * 46 / 100;

        ArrayDeque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        int visited = 0;
        while (!queue.isEmpty() && visited++ < MAX_NODES) {
            AccessibilityNodeInfo node = queue.removeFirst();
            if (node.isVisibleToUser()) {
                Rect bounds = new Rect();
                node.getBoundsInScreen(bounds);
                boolean inHeader = !bounds.isEmpty() && bounds.top >= screen.top
                        && bounds.centerY() <= topLimit;
                if (inHeader && (StopPolicy.matchesAppInfoHeader(target, label,
                        node.getText() == null ? null : node.getText().toString(), true)
                        || StopPolicy.matchesAppInfoHeader(target, label,
                        node.getContentDescription() == null
                                ? null : node.getContentDescription().toString(), true))) {
                    return true;
                }
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) queue.addLast(child);
            }
        }
        return false;
    }
}
