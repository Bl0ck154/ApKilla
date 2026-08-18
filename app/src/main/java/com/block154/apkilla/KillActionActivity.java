package com.block154.apkilla;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class KillActionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        runAction();
    }

    private void runAction() {
        if (!AccessibilityUtils.isServiceEnabled(this)) {
            Toast.makeText(this, R.string.enable_accessibility_first, Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            finish();
            return;
        }

        String target = TargetResolver.resolveLastBeforeHome(this);
        if (target == null) {
            Toast.makeText(this, R.string.no_home_target, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Consume the snapshot before opening Settings so a second tap cannot
        // accidentally reuse an old app after the first kill completes.
        Prefs.clearLastHomeTarget(this);
        Prefs.requestKill(this, target);
        Intent details = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                .setData(Uri.parse("package:" + target))
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(details);
        finish();
    }
}
