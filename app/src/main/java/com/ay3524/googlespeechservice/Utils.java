package com.ay3524.googlespeechservice;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
    public static String ARRAY_STORAGE_WORDS[] = {"storage", "memory"};
    public static String ARRAY_CAMERA_WORDS[] = {"camera", "photo"};

    /**
     * Gives boolean whether the device is connected to internet or not.
     *
     * @param context Context from where this method is called.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Gives boolean whether the device is connected to internet or not.
     *
     * @param arr String array of corresponding words to check.
     */
    public static boolean checkForWord(String userString, String arr[]) {
        for (String item : arr) {
            if (userString.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gives boolean whether the given explicit intent can be handled by the device or not.
     *
     * @param intent  Intent for that action.
     * @param context Context from where this method is called.
     */
    public static boolean checkForExplicitIntent(Intent intent, Context context) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }
}
