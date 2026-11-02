package com.block154.apkilla;

import android.content.Context;
import android.content.SharedPreferences;
import java.text.DateFormat;
import java.util.Date;

/** Local bounded outcome history; no package names, screen text or network. */
final class StopHistory {
    private static final String FILE = "operation_history";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_LAST = "last";
    private static final int LIMIT = 20;

    private StopHistory() {}

    static void record(Context context, String reason) {
        SharedPreferences prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        String entry = now + "|" + reason.replace("\n", " ").replace("|", "/");
        String history = prefs.getString(KEY_ITEMS, "");
        String[] previous = history.isEmpty() ? new String[0] : history.split("\n");
        StringBuilder combined = new StringBuilder(entry);
        for (int i = 0; i < previous.length && i < LIMIT - 1; i++) {
            combined.append('\n').append(previous[i]);
        }
        prefs.edit().putString(KEY_ITEMS, combined.toString()).putString(KEY_LAST, entry).apply();
    }

    static String lastSummary(Context context) {
        String last = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
                .getString(KEY_LAST, null);
        if (last == null) return context.getString(R.string.status_no_operations);
        int split = last.indexOf('|');
        if (split < 1) return context.getString(R.string.status_no_operations);
        try {
            long at = Long.parseLong(last.substring(0, split));
            return last.substring(split + 1) + " · "
                    + DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(at));
        } catch (RuntimeException e) {
            return context.getString(R.string.status_no_operations);
        }
    }

    static String exportLocal(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getString(KEY_ITEMS, "");
    }
}
