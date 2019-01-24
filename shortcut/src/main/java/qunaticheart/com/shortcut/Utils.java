/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/23 - BOOMMM!
 *  * .
 *
 */
package qunaticheart.com.shortcut;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class Utils {

    //==============================================================================================
    //
    // ** Init Vars
    //
    //==============================================================================================

    private static final String TAG = "Shortcut Library Alert";

    //==============================================================================================
    //
    // ** Library Utils
    //
    //==============================================================================================

    static void showToast(final Context context, final String message) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void debugLog(String textLog) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, textLog);
        }
    }
}
