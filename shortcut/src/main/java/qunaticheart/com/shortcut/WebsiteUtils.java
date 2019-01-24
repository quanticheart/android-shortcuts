/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/23 - BOOMMM!
 *  * .
 *
 */

package qunaticheart.com.shortcut;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.PersistableBundle;
import android.os.StrictMode;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.function.BooleanSupplier;

public class WebsiteUtils {


    //==============================================================================================
    //
    // ** Create Shortcut
    //
    //==============================================================================================

    public static void addWebSiteShortcut(final Context context, final String urlAsString) {
        callShortcutManager(context, new BooleanSupplier() {
                    @Override
                    public boolean getAsBoolean() {
                        return new ShortcutHelper(context)
                                .addDynamicShortcuts(
                                        Collections.singletonList(
                                                createShortcutForUrl(
                                                        context, normalizeUrl(urlAsString.toLowerCase()
                                                        ))));
                    }
                }
        );
    }

    //==============================================================================================
    //
    // ** Create ShortcutInfo
    //
    //==============================================================================================

    private static ShortcutInfo createShortcutForUrl(Context context, String urlAsString) {
        Utils.debugLog("createShortcutForUrl: " + urlAsString);
        final ShortcutInfo.Builder b = new ShortcutInfo.Builder(context, urlAsString);
        final Uri uri = Uri.parse(urlAsString);
        b.setIntent(new Intent(Intent.ACTION_VIEW, uri));
        setSiteInformation(context, b, uri);
        setExtras(b);
        return b.build();
    }

    //==============================================================================================
    //
    // ** Call Shortcut Manager
    //
    //==============================================================================================

    public static void callShortcutManager(Context context, BooleanSupplier r) {
        try {
            if (!r.getAsBoolean()) {
                Utils.showToast(context, "Call to ShortcutManager is rate-limited");
            }
        } catch (Exception e) {
            Utils.debugLog("Caught Exception: " + e);
//            Utils.showToast(context, "Error while calling ShortcutManager: " + e.toString());
        }
    }

    //==============================================================================================
    //
    // ** Refactor URL for Shortcut
    //
    //==============================================================================================

    public static String normalizeUrl(String urlAsString) {
        if (urlAsString.startsWith("http://") || urlAsString.startsWith("https://")) {
            return urlAsString;
        } else {
            return "http://" + urlAsString;
        }
    }

    //==============================================================================================
    //
    // ** Download and create Icon for Shortcut
    //
    //==============================================================================================
    public static Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();
        Utils.debugLog("Fetching favicon from: " + iconUri);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            Utils.debugLog("Failed to fetch favicon from " + iconUri + " - " + e);
            return null;
        }
    }

    //==============================================================================================
    //
    // ** Get site informations for Shortcut Builder
    //
    //==============================================================================================
    public static ShortcutInfo.Builder setSiteInformation(Context context, ShortcutInfo.Builder b, Uri uri) {
        // TODO Get the actual site <title> and use it.
        // TODO Set the current locale to accept-language to get localized title.
        b.setShortLabel(uri.getHost());
        b.setLongLabel(uri.toString());

        Bitmap bmp = fetchFavicon(uri);
        if (bmp != null) {
            b.setIcon(Icon.createWithBitmap(bmp));
        } else {
            b.setIcon(Icon.createWithResource(context, R.drawable.link));
        }

        return b;
    }

    //==============================================================================================
    //
    // ** Insert extras in Builder
    //
    //==============================================================================================

    public static final String EXTRA_LAST_REFRESH = "EXTRA_LAST_REFRESH";

    public static ShortcutInfo.Builder setExtras(ShortcutInfo.Builder b) {
        final PersistableBundle extras = new PersistableBundle();
        extras.putLong(EXTRA_LAST_REFRESH, System.currentTimeMillis());
        b.setExtras(extras);
        return b;
    }

}
