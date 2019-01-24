/*
 *
 *  * Copyright(c) Developed by John Alves at 2019/1/24 - BOOMMM!
 *  * .
 *
 */

package qunaticheart.com.shortcut;

import android.os.Build;

@SuppressWarnings("all")
public class AndroidUtil {

//    SDK_INT value      Build.VERSION_CODES       Human Version Name
//    1                  BASE                      Android 1.0 (no codename)
//    2                  BASE_1_1                  Android 1.1 Petit Four
//    3                  CUPCAKE                   Android 1.5 Cupcake
//    4                  DONUT                     Android 1.6 Donut
//    5                  ECLAIR                    Android 2.0 Eclair
//    6                  ECLAIR_0_1                Android 2.0.1 Eclair
//    7                  ECLAIR_MR1                Android 2.1 Eclair
//    8                  FROYO                     Android 2.2 Froyo
//    9                  GINGERBREAD               Android 2.3 Gingerbread
//   10                  GINGERBREAD_MR1           Android 2.3.3 Gingerbread
//   11                  HONEYCOMB                 Android 3.0 Honeycomb
//   12                  HONEYCOMB_MR1             Android 3.1 Honeycomb
//   13                  HONEYCOMB_MR2             Android 3.2 Honeycomb
//   14                  ICE_CREAM_SANDWICH        Android 4.0 Ice Cream Sandwich
//   15                  ICE_CREAM_SANDWICH_MR1    Android 4.0.3 Ice Cream Sandwich
//   16                  JELLY_BEAN                Android 4.1 Jellybean
//   17                  JELLY_BEAN_MR1            Android 4.2 Jellybean
//   18                  JELLY_BEAN_MR2            Android 4.3 Jellybean
//   19                  KITKAT                    Android 4.4 KitKat
//   20                  KITKAT_WATCH              Android 4.4 KitKat Watch
//   21                  LOLLIPOP                  Android 5.0 Lollipop
//   22                  LOLLIPOP_MR1              Android 5.1 Lollipop
//   23                  M                         Android 6.0 Marshmallow
//   24                  N                         Android 7.0 Nougat
//   25                  N_MR1                     Android 7.1.1 Nougat
//   26                  O                         Android 8.0 Oreo
//   27                  O_MR1                     Android 8.1 Oreo
//   28                  P                         Android 9.0 Pie
//   10000               CUR_DEVELOPMENT           Current Development Version

    //==============================================================================================
    //
    // ** Base Levels
    //
    //==============================================================================================

    public static int BASE = 1;
    public static int BASE_1_1 = 2;
    public static int CUPCAKE = 3;
    public static int DONUT = 4;
    public static int ECLAIR = 5;
    public static int ECLAIR_0_1 = 6;
    public static int ECLAIR_MR1 = 7;
    public static int FROYO = 8;
    public static int GINGERBREAD = 9;
    public static int GINGERBREAD_MR1 = 10;
    public static int HONEYCOMB = 11;
    public static int HONEYCOMB_MR1 = 12;
    public static int HONEYCOMB_MR2 = 13;
    public static int ICE_CREAM_SANDWICH = 14;
    public static int ICE_CREAM_SANDWICH_MR1 = 15;
    public static int JELLY_BEAN = 16;
    public static int JELLY_BEAN_MR1 = 17;
    public static int JELLY_BEAN_MR2 = 18;
    public static int KITKAT = 19;
    public static int KITKAT_WATCH = 20;
    public static int LOLLIPOP = 21;
    public static int LOLLIPOP_MR1 = 22;
    public static int M = 23;
    public static int N = 24;
    public static int N_MR1 = 25;
    public static int O = 26;
    public static int O_MR1 = 27;
    public static int P = 28;

    //==============================================================================================
    //
    // ** Dev Level
    //
    //==============================================================================================

    public static int CUR_DEVELOPMENT = 10000;

    //==============================================================================================
    //
    // ** This API Level
    //
    //==============================================================================================

    public static int ANDROID_LEVEL = Build.VERSION.SDK_INT;

    //==============================================================================================
    //
    // ** Class Utils
    //
    //==============================================================================================

    public static boolean verifyVesionMin(int minVersion) {
        return ANDROID_LEVEL >= minVersion;
    }

    public static boolean verifyVesionMax(int maxVersion) {
        return ANDROID_LEVEL <= maxVersion;
    }

    public static boolean verifyVesion(int minVersion, int maxVersion) {
        return ANDROID_LEVEL >= minVersion && ANDROID_LEVEL <= maxVersion;
    }
}
