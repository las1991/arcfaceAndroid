package com.las.arc_face.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtil {
    private static final String APP_NAME = "ArcFaceDemo";
    private static final String TRACK_ID = "trackID";
    private static final String LOAD_STUDENT_URL = "loadStudentUrl";
    private static final String CHECK_IN_CALLBACK_URL = "checkInCallbackUrl";

    public static void setTrackId(Context context, int trackId) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt(TRACK_ID, trackId)
                .apply();
    }

    public static int getTrackId(Context context) {
        if (context == null) {
            return 0;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(TRACK_ID, 0);
    }

    public static void setStudentManagerUrl(Context context, String url) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(LOAD_STUDENT_URL, url)
                .apply();
    }

    public static String getStudentManagerUrl(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LOAD_STUDENT_URL, "");
    }

    public static void setCheckInCallbackUrl(Context context, String url) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(CHECK_IN_CALLBACK_URL, url)
                .apply();
    }

    public static String getCheckInCallbackUrl(Context context) {
        if (context == null) {
            return "";
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(CHECK_IN_CALLBACK_URL, "");
    }

}
