
/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/23 - BOOMMM!
 *  * .
 *
 */

package qunaticheart.com.shortcut;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class ShortcutHelper {

    //==============================================================================================
    //
    // ** Init Vars
    //
    //==============================================================================================

    private static final String TAG = "Shortcut Helper";
    private final Context mContext;
    private final ShortcutManager mShortcutManager;

    //==============================================================================================
    //
    // ** Constructor
    //
    //==============================================================================================

    public ShortcutHelper(Context context) {
        mContext = context;
        mShortcutManager = mContext.getSystemService(ShortcutManager.class);
    }

    //==============================================================================================
    //
    // ** Create shortcutManager and add Shortcut
    //
    //==============================================================================================

    private void shortcutManager(Context context, ShortcutInfo shortcut) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mShortcutManager.setDynamicShortcuts(Collections.singletonList(shortcut));
        }
    }

    //==============================================================================================
    //
    // ** Dinamic Shortcut
    //
    //==============================================================================================

    public void createShotcut(String shortcutID,
                              String shortLabel,
                              String longLabel,
                              int icon,
                              Intent intentAction) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            shortcutManager(mContext, new ShortcutInfo.Builder(mContext, shortcutID)
                    .setShortLabel(shortLabel)
                    .setLongLabel(longLabel)
                    .setIcon(Icon.createWithResource(mContext, icon))
                    .setIntent(intentAction)
                    .build());
        }
    }

    //==============================================================================================
    //
    // ** create Dinamic Shortcut
    //
    //==============================================================================================

    public void createHomescreenShortcut(ShortcutInfo shortcutInfo, Bitmap icon) {

//        Intent intent2 = new Intent(mContext, classTest);
//        intent2.setAction("toggle_intent");
//        intent2.putExtra("id", 1);

        ShortcutInfo shortcut2 = new ShortcutInfo.Builder(mContext, shortcutInfo.getId())
                .setIntent(Objects.requireNonNull(shortcutInfo.getIntent()))
                .setShortLabel(Objects.requireNonNull(shortcutInfo.getShortLabel()))
                .setLongLabel(Objects.requireNonNull(shortcutInfo.getLongLabel()))
                .setDisabledMessage(mContext.getResources().getString(R.string.disable_shortcut))
                .setIcon(Icon.createWithBitmap(icon))
                .build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Intent pinnedShortcutCallbackIntent = mShortcutManager.createShortcutResultIntent(shortcut2);
            PendingIntent successCallback = PendingIntent.getBroadcast(mContext, 0, pinnedShortcutCallbackIntent, 0);
            mShortcutManager.requestPinShortcut(shortcut2, successCallback.getIntentSender());
        }

    }


    public void createDinamicShortcut() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mShortcutManager.isRequestPinShortcutSupported()) {
                // Assumes there's already a shortcut with the ID "my-shortcut".
                // The shortcut must be enabled.
                ShortcutInfo pinShortcutInfo =
                        new ShortcutInfo.Builder(mContext, "my-shortcut").build();

                // Create the PendingIntent object only if your app needs to be notified
                // that the user allowed the shortcut to be pinned. Note that, if the
                // pinning operation fails, your app isn't notified. We assume here that the
                // app has implemented a method called createShortcutResultIntent() that
                // returns a broadcast intent.
                Intent pinnedShortcutCallbackIntent =
                        mShortcutManager.createShortcutResultIntent(pinShortcutInfo);

                // Configure the intent so that your app's broadcast receiver gets
                // the callback successfully.For details, see PendingIntent.getBroadcast().
                PendingIntent successCallback = PendingIntent.getBroadcast(mContext, /* request code */ 0,
                        pinnedShortcutCallbackIntent, /* flags */ 0);

                mShortcutManager.requestPinShortcut(pinShortcutInfo,
                        successCallback.getIntentSender());
            }
        }

    }

    //==============================================================================================
    //
    // ** Return all mutable shortcuts from this app self.
    //
    //==============================================================================================

    public List<ShortcutInfo> getShortcuts() {
        // Load mutable dynamic shortcuts and pinned shortcuts and put them into a single list
        // removing duplicates.

        final List<ShortcutInfo> ret = new ArrayList<>();
        final HashSet<String> seenKeys = new HashSet<>();

        // Check existing shortcuts
        for (ShortcutInfo shortcut : mShortcutManager.getDynamicShortcuts()) {
            if (!shortcut.isImmutable()) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }
        for (ShortcutInfo shortcut : mShortcutManager.getPinnedShortcuts()) {
            if (!shortcut.isImmutable() && !seenKeys.contains(shortcut.getId())) {
                ret.add(shortcut);
                seenKeys.add(shortcut.getId());
            }
        }
        return ret;
    }

    //==============================================================================================
    //
    // ** Shortcuts Remove, Disable and Enable
    //
    //==============================================================================================

    public void removeShortcut(ShortcutInfo shortcut) {
        mShortcutManager.removeDynamicShortcuts(Arrays.asList(shortcut.getId()));
        if (shortcut.isPinned())
            disableShortcut(shortcut);
    }

    public void disableShortcut(ShortcutInfo shortcut) {
        mShortcutManager.disableShortcuts(Arrays.asList(shortcut.getId()));
    }

    public void enableShortcut(ShortcutInfo shortcut) {
        mShortcutManager.enableShortcuts(Arrays.asList(shortcut.getId()));
    }

    //==============================================================================================
    //
    // **
    //
    //==============================================================================================

    public void maybeRestoreAllDynamicShortcuts() {
        if (mShortcutManager.getDynamicShortcuts().size() == 0) {
            // NOTE: If this application is always supposed to have dynamic shortcuts, then publish
            // them here.
            // Note when an application is "restored" on a new device, all dynamic shortcuts
            // will *not* be restored but the pinned shortcuts *will*.
        }
    }

    public void reportShortcutUsed(String id) {
        mShortcutManager.reportShortcutUsed(id);
    }

    public boolean updateShortcuts(List<ShortcutInfo> updateList) {
        return mShortcutManager.updateShortcuts(updateList);
    }

    public boolean addDynamicShortcuts(List<ShortcutInfo> shortcutInfos) {

        for (int i = 0; i < shortcutInfos.size(); i++) {
            String id = shortcutInfos.get(i).getId();
            List<ShortcutInfo> verify = getShortcuts();

            for (int j = 0; j < verify.size(); j++) {
                if (verify.get(j).getId().equals(id)){
                    Utils.showToast(mContext, "Shortcut Exists");
                    shortcutInfos.remove(i);
                }
            }

        }
        return mShortcutManager.addDynamicShortcuts(shortcutInfos);
    }

    //==============================================================================================
    //
    // ** Get Shortcut by ID
    //
    //==============================================================================================

    public ShortcutInfo.Builder getShortcutInfo(String id) {
        return new ShortcutInfo.Builder(mContext, id);
    }

    //==============================================================================================
    //
    // ** Get Shortcut type
    //
    //==============================================================================================

    public static String getType(ShortcutInfo shortcut) {
        final StringBuilder sb = new StringBuilder();
        String sep = "";
        if (shortcut.isDynamic()) {
            sb.append(sep);
            sb.append("Dynamic");
            sep = ", ";
        }
        if (shortcut.isPinned()) {
            sb.append(sep);
            sb.append("Pinned");
            sep = ", ";
        }
        if (!shortcut.isEnabled()) {
            sb.append(sep);
            sb.append("Disabled");
            sep = ", ";
        }
        return sb.toString();
    }

    //==============================================================================================
    //
    // ** init Vars
    //
    //==============================================================================================

    private static String shortcutID = "new_shortcut_01";
    private static String shortLabel = "Criar Leilão";
    private static String longLabel = "Criar novo leilão agora!";
    private static String shortcutDisabledMessage = "Este atalho so é liberado apos o login!";
//    private static int icon = R.drawable.link;

    //==============================================================================================
    //
    // ** Fixed Shortcut Test
    //
    //==============================================================================================

    public void createShotcutTest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            shortcutManager(mContext, new ShortcutInfo.Builder(mContext, shortcutID)
                    .setShortLabel(shortLabel)
                    .setLongLabel(longLabel)
                    .setDisabledMessage(shortcutDisabledMessage)

                    .setIcon(Icon.createWithResource(mContext, R.drawable.link))
                    .setIntent(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://www.google.co.in")))
                    .build());
        }
    }

}
