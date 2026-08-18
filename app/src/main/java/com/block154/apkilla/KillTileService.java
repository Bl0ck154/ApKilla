package com.block154.apkilla;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

public class KillTileService extends TileService {

    static void requestRefresh(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                TileService.requestListeningState(
                        context.getApplicationContext(),
                        new ComponentName(context, KillTileService.class)
                );
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Prefs.setTileAdded(this, true);
        updateTile();
    }

    @Override
    public void onTileRemoved() {
        Prefs.setTileAdded(this, false);
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        // If onStartListening() is called, the tile exists. This also repairs our
        // local state after an app update or reboot where onTileAdded() is not called.
        Prefs.setTileAdded(this, true);
        updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();
        Prefs.setTileAdded(this, true);
        updateTile();
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

        String target = TargetResolver.resolveForTile(this);
        if (target == null) {
            Toast.makeText(this, R.string.no_target, Toast.LENGTH_SHORT).show();
            updateTile();
            return;
        }

        Prefs.requestKill(this, target);

        Intent details = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + target))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchAndCollapse(details);
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

        // This is an action tile, not an on/off switch. Keep it interactable even
        // while setup is incomplete; tapping it can take the user to Accessibility.
        // Some OEM SystemUI builds visually over-disable STATE_INACTIVE tiles.
        tile.setState(Tile.STATE_ACTIVE);
        tile.setIcon(Icon.createWithResource(this, R.drawable.ic_kill));
        tile.setLabel(getString(R.string.tile_label));
        tile.setContentDescription(getString(R.string.tile_label));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            tile.setSubtitle(enabled ? getString(R.string.tile_ready) : getString(R.string.tile_setup_required));
        }
        tile.updateTile();
    }
}
