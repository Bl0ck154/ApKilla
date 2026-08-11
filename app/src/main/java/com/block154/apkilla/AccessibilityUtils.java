package com.block154.apkilla;

import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

final class AccessibilityUtils {
    private AccessibilityUtils() {}

    static boolean isServiceEnabled(Context context) {
        ComponentName component = new ComponentName(context, KillAccessibilityService.class);
        String expectedLong = component.flattenToString();
        String expectedShort = component.flattenToShortString();
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
