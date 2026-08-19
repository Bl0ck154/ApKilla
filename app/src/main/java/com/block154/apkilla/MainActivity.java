package com.block154.apkilla;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ShortcutManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(11, 13, 18);
    private static final int CARD = Color.rgb(22, 25, 33);
    private static final int TEXT = Color.rgb(244, 246, 250);
    private static final int MUTED = Color.rgb(157, 164, 178);
    private static final int ACCENT = Color.rgb(255, 94, 91);
    private static final int GREEN = Color.rgb(83, 207, 143);
    private static final int AMBER = Color.rgb(255, 186, 73);

    private final Handler handler = new Handler(Looper.getMainLooper());

    private TextView badgeView;
    private TextView automationValue;
    private TextView tileValue;
    private TextView setupLabel;
    private LinearLayout setupCard;
    private View accessibilityAction;
    private View tileAction;
    private View setupDivider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableLegacyKillLastShortcuts();
        configureWindow();
        setContentView(buildUi());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
        KillTileService.requestRefresh(this);
        handler.postDelayed(this::refreshStatus, 450L);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void disableLegacyKillLastShortcuts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return;
        try {
            ShortcutManager manager = getSystemService(ShortcutManager.class);
            if (manager != null) {
                manager.disableShortcuts(
                        Arrays.asList("kill_last", "kill_last_pinned"),
                        getString(R.string.shortcut_removed)
                );
            }
        } catch (Exception ignored) {
        }
    }

    private void configureWindow() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(BG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false);
        }
    }

    private View buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setBackgroundColor(BG);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(24), dp(20), dp(32));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        scroll.setOnApplyWindowInsetsListener((v, insets) -> {
            content.setPadding(
                    dp(20),
                    dp(24) + insets.getSystemWindowInsetTop(),
                    dp(20),
                    dp(32) + insets.getSystemWindowInsetBottom()
            );
            return insets;
        });

        TextView title = label(getString(R.string.app_name), 34f, TEXT, Typeface.BOLD);
        content.addView(title);

        LinearLayout headerLine = new LinearLayout(this);
        headerLine.setGravity(Gravity.CENTER_VERTICAL);
        headerLine.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        headerParams.topMargin = dp(6);
        content.addView(headerLine, headerParams);

        TextView subtitle = label(getString(R.string.hero_subtitle), 15f, MUTED, Typeface.NORMAL);
        headerLine.addView(subtitle, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        badgeView = label("", 11f, TEXT, Typeface.BOLD);
        badgeView.setGravity(Gravity.CENTER);
        badgeView.setPadding(dp(10), dp(6), dp(10), dp(6));
        headerLine.addView(badgeView);

        TextView hero = label(getString(R.string.hero_description), 16f, TEXT, Typeface.NORMAL);
        hero.setLineSpacing(0f, 1.22f);
        LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        heroParams.topMargin = dp(24);
        content.addView(hero, heroParams);

        content.addView(sectionLabel(getString(R.string.section_status)), sectionParams(30));
        LinearLayout statusCard = card();
        automationValue = addStatusRow(statusCard, getString(R.string.status_automation), getString(R.string.status_checking));
        addDivider(statusCard);
        tileValue = addStatusRow(statusCard, getString(R.string.status_quick_settings), getString(R.string.status_checking));
        content.addView(statusCard);

        setupLabel = sectionLabel(getString(R.string.section_setup));
        content.addView(setupLabel, sectionParams(26));
        setupCard = card();
        accessibilityAction = addActionRow(
                setupCard,
                getString(R.string.action_enable_automation),
                getString(R.string.action_enable_automation_desc),
                true,
                v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        );
        setupDivider = addDivider(setupCard);
        tileAction = addActionRow(
                setupCard,
                getString(R.string.action_add_tile),
                getString(R.string.action_add_tile_desc),
                true,
                v -> requestTile()
        );
        content.addView(setupCard);

        content.addView(sectionLabel(getString(R.string.section_quick_access)), sectionParams(26));
        LinearLayout quickCard = card();
        addActionRow(
                quickCard,
                getString(R.string.action_repair_tile),
                getString(R.string.action_repair_tile_desc),
                false,
                v -> requestTile()
        );
        addDivider(quickCard);
        addActionRow(
                quickCard,
                getString(R.string.action_accessibility_settings),
                getString(R.string.action_accessibility_settings_desc),
                false,
                v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        );
        content.addView(quickCard);

        TextView note = label(getString(R.string.note), 13f, MUTED, Typeface.NORMAL);
        note.setLineSpacing(0f, 1.18f);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        noteParams.topMargin = dp(22);
        noteParams.bottomMargin = dp(12);
        content.addView(note, noteParams);

        return scroll;
    }

    private void refreshStatus() {
        boolean enabled = AccessibilityUtils.isServiceEnabled(this);
        boolean connected = KillAccessibilityService.isConnected();
        boolean tileAdded = Prefs.isTileAdded(this);

        if (enabled) {
            automationValue.setText(connected ? R.string.status_on : R.string.status_enabled_reconnecting);
            automationValue.setTextColor(connected ? GREEN : AMBER);
        } else {
            automationValue.setText(R.string.status_off);
            automationValue.setTextColor(AMBER);
        }

        tileValue.setText(tileAdded ? R.string.status_added : R.string.status_not_confirmed);
        tileValue.setTextColor(tileAdded ? GREEN : MUTED);

        accessibilityAction.setVisibility(enabled ? View.GONE : View.VISIBLE);
        tileAction.setVisibility(tileAdded ? View.GONE : View.VISIBLE);
        setupDivider.setVisibility(!enabled && !tileAdded ? View.VISIBLE : View.GONE);
        boolean setupComplete = enabled && tileAdded;
        setupLabel.setVisibility(setupComplete ? View.GONE : View.VISIBLE);
        setupCard.setVisibility(setupComplete ? View.GONE : View.VISIBLE);

        badgeView.setText(setupComplete ? R.string.badge_ready : R.string.badge_setup);
        badgeView.setTextColor(setupComplete ? GREEN : AMBER);
        badgeView.setBackground(roundRect(setupComplete ? Color.rgb(25, 58, 44) : Color.rgb(63, 49, 24), 999));
    }

    private void requestTile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            StatusBarManager statusBarManager = getSystemService(StatusBarManager.class);
            if (statusBarManager == null) {
                Toast.makeText(this, R.string.add_tile_manual, Toast.LENGTH_LONG).show();
                return;
            }
            ComponentName component = new ComponentName(this, KillTileService.class);
            statusBarManager.requestAddTileService(
                    component,
                    getString(R.string.tile_label),
                    Icon.createWithResource(this, R.drawable.ic_kill),
                    getMainExecutor(),
                    result -> {
                        if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED
                                || result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED) {
                            Prefs.setTileAdded(this, true);
                            Toast.makeText(this, R.string.tile_ready_to_use, Toast.LENGTH_SHORT).show();
                        } else if (result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED) {
                            Prefs.setTileAdded(this, false);
                        } else {
                            Toast.makeText(this, R.string.tile_request_failed, Toast.LENGTH_SHORT).show();
                        }
                        refreshStatus();
                    }
            );
        } else {
            Toast.makeText(this, R.string.add_tile_manual, Toast.LENGTH_LONG).show();
        }
    }

    private TextView addStatusRow(LinearLayout parent, String title, String initialValue) {
        LinearLayout row = new LinearLayout(this);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(15), dp(16), dp(15));

        TextView left = label(title, 15f, TEXT, Typeface.NORMAL);
        row.addView(left, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView right = label(initialValue, 14f, MUTED, Typeface.BOLD);
        right.setGravity(Gravity.END);
        row.addView(right);
        parent.addView(row);
        return right;
    }

    private View addActionRow(LinearLayout parent, String title, String description, boolean primary, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(15), dp(14), dp(15));
        row.setBackground(roundRect(Color.TRANSPARENT, 16));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(listener);

        LinearLayout textColumn = new LinearLayout(this);
        textColumn.setOrientation(LinearLayout.VERTICAL);
        row.addView(textColumn, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView name = label(title, 15f, primary ? TEXT : Color.rgb(226, 229, 236), Typeface.BOLD);
        textColumn.addView(name);

        TextView desc = label(description, 13f, MUTED, Typeface.NORMAL);
        desc.setPadding(0, dp(4), dp(8), 0);
        desc.setLineSpacing(0f, 1.1f);
        textColumn.addView(desc);

        TextView arrow = label("›", 30f, primary ? ACCENT : MUTED, Typeface.NORMAL);
        arrow.setGravity(Gravity.CENTER);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(28), ViewGroup.LayoutParams.WRAP_CONTENT));

        parent.addView(row);
        return row;
    }

    private LinearLayout card() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(roundRect(CARD, 18));
        card.setClipToOutline(true);
        card.setElevation(dp(1));
        return card;
    }

    private View addDivider(LinearLayout parent) {
        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(43, 47, 58));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(1)
        );
        params.leftMargin = dp(16);
        params.rightMargin = dp(16);
        parent.addView(divider, params);
        return divider;
    }

    private TextView sectionLabel(String text) {
        TextView view = label(text, 12f, MUTED, Typeface.BOLD);
        view.setLetterSpacing(0.12f);
        return view;
    }

    private LinearLayout.LayoutParams sectionParams(int topDp) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(topDp);
        params.bottomMargin = dp(9);
        return params;
    }

    private TextView label(String text, float size, int color, int style) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setTypeface(Typeface.create("sans", style));
        view.setIncludeFontPadding(false);
        return view;
    }

    private GradientDrawable roundRect(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
