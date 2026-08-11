package com.block154.apkilla;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class KillTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        if (isLocked()) {
            unlockAndRun(this::handleClick);
        } else {
            handleClick();
        }
    }

    private void handleClick() {
        if (!AccessibilityUtils.isServiceEnabled(this)) {
            Toast.makeText(this, R.string.enable_accessibility_first, Toast.LENGTH_LONG).show();
            launchAndCollapse(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            return;
        }

        String target = Prefs.getLastTarget(this);
        if (target == null || target.isBlank() || isUnsafeTarget(target)) {
            Toast.makeText(this, R.string.no_target, Toast.LENGTH_SHORT).show();
            return;
        }

        Prefs.requestKill(this, target);

        Intent details = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + target))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchAndCollapse(details);
    }

    private boolean isUnsafeTarget(String packageName) {
        return packageName.equals(getPackageName())
                || packageName.equals("android")
                || packageName.equals("com.android.systemui")
                || packageName.equals("com.android.settings")
                || packageName.equals("com.oplus.settings")
                || packageName.equals("com.coloros.settings");
    }

    private void launchAndCollapse(Intent intent) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                PendingIntent pendingIntent = PendingIntent.getActivity(
                        this,
                        1001,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                );
                startActivityAndCollapse(pendingIntent);
            } else {
                startActivityAndCollapse(intent);
            }
        } catch (Exception e) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void updateTile() {
        Tile tile = getQsTile();
        if (tile == null) return;

        boolean enabled = AccessibilityUtils.isServiceEnabled(this);
        String target = Prefs.getLastTarget(this);
        boolean ready = enabled && target != null && !target.isBlank() && !isUnsafeTarget(target);

        tile.setState(ready ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.setLabel(getString(R.string.tile_label));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.setSubtitle(ready ? getString(R.string.tile_ready) : getString(R.string.tile_not_ready));
        }
        tile.updateTile();
    }
}
