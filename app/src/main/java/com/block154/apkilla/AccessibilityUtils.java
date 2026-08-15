package com.block154.apkilla;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

final class AccessibilityUtils {
    private AccessibilityUtils() {}

    static boolean isServiceEnabled(Context context) {
        if (KillAccessibilityService.isConnected()) return true;

        ComponentName component = new ComponentName(context, KillAccessibilityService.class);
        String expectedLong = component.flattenToString();
        String expectedShort = component.flattenToShortString();

        try {
            AccessibilityManager manager =
                    (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
            if (manager != null && manager.isEnabled()) {
                List<AccessibilityServiceInfo> enabledServices =
                        manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);
                if (enabledServices != null) {
                    for (AccessibilityServiceInfo info : enabledServices) {
                        if (info == null || info.getId() == null) continue;
                        String id = info.getId();
                        if (expectedLong.equalsIgnoreCase(id) || expectedShort.equalsIgnoreCase(id)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        // OEM fallback: some builds return an incomplete AccessibilityManager list
        // briefly while Settings/SystemUI is transitioning.
        String enabled = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        if (TextUtils.isEmpty(enabled)) return false;

        TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(':');
        splitter.setString(enabled);
        while (splitter.hasNext()) {
            String item = splitter.next();
            if (expectedLong.equalsIgnoreCase(item) || expectedShort.equalsIgnoreCase(item)) {
                return true;
            }
        }
        return false;
    }
}
