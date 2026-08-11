package com.block154.apkilla;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class KillAccessibilityService extends AccessibilityService {
    private static final long REQUEST_TIMEOUT_MS = 10_000L;
    private static final int MAX_RETRIES = 24;
    private static final long RETRY_DELAY_MS = 80L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Set<String> homePackages = new HashSet<>();
    private boolean automationScheduled = false;
    private boolean waitingForConfirmation = false;
    private long activeRequestAt = -1L;

    private static final List<String> FORCE_STOP_TEXTS = Arrays.asList(
            "force stop",
            "примусово зупинити",
            "зупинити примусово",
            "принудительно остановить",
            "остановить",
            "priverstinai sustabdyti",
            "wymuś zatrzymanie"
    );

    private static final List<String> CONFIRM_TEXTS = Arrays.asList(
            "ok", "okay", "yes", "так", "да",
            "force stop", "примусово зупинити", "зупинити",
            "принудительно остановить", "остановить",
            "priverstinai sustabdyti", "wymuś zatrzymanie"
    );

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        refreshHomePackages();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;

        String packageName = event.getPackageName().toString();
        if (Prefs.hasPendingKill(this)) {
            if (isSettingsPackage(packageName) || "android".equals(packageName)) {
                scheduleAutomation();
            }
            return;
        }

        trackForegroundPackage(packageName);
    }

    @Override
    public void onInterrupt() {
        handler.removeCallbacksAndMessages(null);
        automationScheduled = false;
    }

    private void trackForegroundPackage(String packageName) {
        if ("com.android.systemui".equals(packageName)) {
            return; // Notification shade must not replace the app we came from.
        }

        if (packageName.equals(getPackageName()) || isSettingsPackage(packageName) || isHomePackage(packageName)) {
            Prefs.clearLastTarget(this);
            return;
        }

        if ("android".equals(packageName)) {
            return;
        }

        Prefs.setLastTarget(this, packageName);
    }

    private void scheduleAutomation() {
        if (automationScheduled) return;
        automationScheduled = true;
        handler.post(() -> runAutomation(0));
    }

    private void runAutomation(int attempt) {
        automationScheduled = false;

        String target = Prefs.getPendingTarget(this);
        long requestAt = Prefs.getPendingAt(this);
        if (target == null || requestAt == 0L) return;

        if (System.currentTimeMillis() - requestAt > REQUEST_TIMEOUT_MS) {
            failAutomation();
            return;
        }

        if (activeRequestAt != requestAt) {
            activeRequestAt = requestAt;
            waitingForConfirmation = false;
        }

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root == null) {
            retry(attempt);
            return;
        }

        if (!waitingForConfirmation) {
            AccessibilityNodeInfo forceStop = findForceStopButton(root);
            if (forceStop == null) {
                retry(attempt);
                return;
            }

            if (!forceStop.isEnabled()) {
                finishAutomation(); // Already stopped, nothing else to do.
                return;
            }

            if (clickNode(forceStop)) {
                waitingForConfirmation = true;
                handler.postDelayed(() -> runAutomation(attempt + 1), 60L);
                return;
            }

            retry(attempt);
            return;
        }

        AccessibilityNodeInfo confirm = findConfirmationButton(root);
        if (confirm != null && confirm.isEnabled() && clickNode(confirm)) {
            finishAutomation();
            return;
        }

        // Some OEM builds do not show a confirmation dialog. If the Force stop
        // button is now disabled, the operation already succeeded.
        AccessibilityNodeInfo forceStop = findForceStopButton(root);
        if (forceStop != null && !forceStop.isEnabled()) {
            finishAutomation();
            return;
        }

        retry(attempt);
    }

    private void retry(int attempt) {
        if (attempt >= MAX_RETRIES) {
            failAutomation();
            return;
        }
        automationScheduled = true;
        handler.postDelayed(() -> {
            automationScheduled = false;
            runAutomation(attempt + 1);
        }, RETRY_DELAY_MS);
    }

    private void finishAutomation() {
        Prefs.clearPendingKill(this);
        waitingForConfirmation = false;
        activeRequestAt = -1L;
        handler.postDelayed(() -> performGlobalAction(GLOBAL_ACTION_BACK), 220L);
    }

    private void failAutomation() {
        Prefs.clearPendingKill(this);
        waitingForConfirmation = false;
        activeRequestAt = -1L;
        Toast.makeText(this, R.string.kill_failed, Toast.LENGTH_SHORT).show();
        handler.postDelayed(() -> performGlobalAction(GLOBAL_ACTION_BACK), 150L);
    }

    private AccessibilityNodeInfo findForceStopButton(AccessibilityNodeInfo root) {
        AccessibilityNodeInfo byId = findNodeByIdFragment(root, "force_stop");
        if (byId != null) return byId;

        for (String text : FORCE_STOP_TEXTS) {
            AccessibilityNodeInfo node = findNodeByText(root, text);
            if (node != null) return node;
        }
        return null;
    }

    private AccessibilityNodeInfo findConfirmationButton(AccessibilityNodeInfo root) {
        AccessibilityNodeInfo byExactId = findNodeByExactViewId(root, "android:id/button1");
        if (byExactId != null) return byExactId;

        AccessibilityNodeInfo byId = findNodeByIdFragment(root, "button1");
        if (byId != null) return byId;

        AccessibilityNodeInfo positive = findNodeByIdFragment(root, "positive");
        if (positive != null) return positive;

        for (String text : CONFIRM_TEXTS) {
            AccessibilityNodeInfo node = findNodeByText(root, text);
            if (node != null) return node;
        }
        return null;
    }

    private AccessibilityNodeInfo findNodeByExactViewId(AccessibilityNodeInfo root, String viewId) {
        try {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(viewId);
            if (nodes != null) {
                for (AccessibilityNodeInfo node : nodes) {
                    if (node != null && node.isVisibleToUser()) return node;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private AccessibilityNodeInfo findNodeByIdFragment(AccessibilityNodeInfo root, String fragment) {
        String wanted = fragment.toLowerCase(Locale.ROOT);
        ArrayDeque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.removeFirst();
            String id = node.getViewIdResourceName();
            if (id != null && id.toLowerCase(Locale.ROOT).contains(wanted) && node.isVisibleToUser()) {
                return node;
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) queue.addLast(child);
            }
        }
        return null;
    }

    private AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo root, String wantedText) {
        String wanted = normalize(wantedText);
        ArrayDeque<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.removeFirst();
            CharSequence text = node.getText();
            if (text != null) {
                String value = normalize(text.toString());
                if ((value.equals(wanted) || value.contains(wanted)) && node.isVisibleToUser()) {
                    return node;
                }
            }
            CharSequence description = node.getContentDescription();
            if (description != null) {
                String value = normalize(description.toString());
                if ((value.equals(wanted) || value.contains(wanted)) && node.isVisibleToUser()) {
                    return node;
                }
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) queue.addLast(child);
            }
        }
        return null;
    }

    private boolean clickNode(AccessibilityNodeInfo node) {
        AccessibilityNodeInfo current = node;
        for (int i = 0; i < 5 && current != null; i++) {
            if (current.isClickable() && current.isEnabled()) {
                return current.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            current = current.getParent();
        }
        return false;
    }

    private String normalize(String text) {
        return text.trim().toLowerCase(Locale.ROOT).replace("…", "");
    }

    private boolean isSettingsPackage(String packageName) {
        return "com.android.settings".equals(packageName)
                || "com.oplus.settings".equals(packageName)
                || "com.coloros.settings".equals(packageName)
                || packageName.endsWith(".settings");
    }

    private boolean isHomePackage(String packageName) {
        if (homePackages.isEmpty()) refreshHomePackages();
        if (homePackages.contains(packageName)) return true;
        String lower = packageName.toLowerCase(Locale.ROOT);
        return lower.contains("launcher") && !lower.contains("settings");
    }

    private void refreshHomePackages() {
        homePackages.clear();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo info = getPackageManager().resolveActivity(intent, 0);
        if (info != null && info.activityInfo != null && info.activityInfo.packageName != null) {
            homePackages.add(info.activityInfo.packageName);
        }
    }
}
