/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/23 - BOOMMM!
 *  * .
 *
 */

package qunaticheart.com.shortcut;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ShortcutInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PersistableBundle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

@TargetApi(Build.VERSION_CODES.N_MR1)
public class AssincTask {

    //==============================================================================================
    //
    // ** Refresh Icons with AssyncTask
    // * Called when the activity starts.  Looks for shortcuts that have been pushed and refreshes
    // * them (but the refresh part isn't implemented yet...).
    //==============================================================================================

    private static final long REFRESH_INTERVAL_MS = 60 * 60 * 1000;

    @SuppressLint("StaticFieldLeak")
    public void refreshShortcuts(final Context context, final boolean force) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                Utils.debugLog("refreshingShortcuts...");

                final long now = System.currentTimeMillis();
                final long staleThreshold = force ? now : now - REFRESH_INTERVAL_MS;

                // Check all existing dynamic and pinned shortcut, and if their last refresh
                // time is older than a certain threshold, update them.

                final List<ShortcutInfo> updateList = new ArrayList<>();
                final ShortcutHelper helper = new ShortcutHelper(context);

                for (ShortcutInfo shortcut : helper.getShortcuts()) {
                    if (shortcut.isImmutable()) {
                        continue;
                    }

                    final PersistableBundle extras = shortcut.getExtras();
                    if (extras != null && extras.getLong(WebsiteUtils.EXTRA_LAST_REFRESH) >= staleThreshold) {
                        // Shortcut still fresh.
                        continue;
                    }

                    Utils.debugLog("Refreshing shortcut: " + shortcut.getId());

                    final ShortcutInfo.Builder b = helper.getShortcutInfo(shortcut.getId());
                    WebsiteUtils.setSiteInformation(context, b, Objects.requireNonNull(Objects.requireNonNull(shortcut.getIntent()).getData()));
                    WebsiteUtils.setExtras(b);
                    updateList.add(b.build());
                }
                // Call update.
                if (updateList.size() > 0) {
                    WebsiteUtils.callShortcutManager(context, new BooleanSupplier() {
                        @Override
                        public boolean getAsBoolean() {
                            return helper.updateShortcuts(updateList);
                        }
                    });
                }

                return null;
            }
        }.execute();
    }

}
