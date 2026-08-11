package com.block154.apkilla;

import android.app.Activity;
import android.app.StatusBarManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView statusView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pad = dp(20);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad, pad, pad);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        TextView title = new TextView(this);
        title.setText(R.string.app_name);
        title.setTextSize(30f);
        title.setPadding(0, 0, 0, dp(10));
        root.addView(title);

        TextView description = new TextView(this);
        description.setText(R.string.description);
        description.setTextSize(16f);
        description.setPadding(0, 0, 0, dp(18));
        root.addView(description);

        statusView = new TextView(this);
        statusView.setTextSize(15f);
        statusView.setPadding(0, 0, 0, dp(18));
        root.addView(statusView);

        Button accessibility = new Button(this);
        accessibility.setText(R.string.open_accessibility);
        accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(accessibility);

        Button addTile = new Button(this);
        addTile.setText(R.string.add_tile);
        addTile.setOnClickListener(v -> requestTile());
        LinearLayout.LayoutParams tileParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tileParams.topMargin = dp(10);
        root.addView(addTile, tileParams);

        TextView note = new TextView(this);
        note.setText(R.string.note);
        note.setTextSize(14f);
        LinearLayout.LayoutParams noteParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        noteParams.topMargin = dp(18);
        root.addView(note, noteParams);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        boolean enabled = AccessibilityUtils.isServiceEnabled(this);
        String target = Prefs.getLastTarget(this);
        String status = getString(enabled ? R.string.accessibility_on : R.string.accessibility_off)
                + "\n"
                + getString(R.string.last_target, target == null ? "—" : target);
        statusView.setText(status);
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
                    result -> Toast.makeText(this, R.string.tile_request_done, Toast.LENGTH_SHORT).show()
            );
        } else {
            Toast.makeText(this, R.string.add_tile_manual, Toast.LENGTH_LONG).show();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
