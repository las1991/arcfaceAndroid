package com.las.arc_face.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.arcsoft.face.enums.DetectFaceOrientPriority;

public class ConfigUtil {
    private static final String APP_NAME = "ArcFaceDemo";
    private static final String TRACK_ID = "trackID";
    private static final String FT_ORIENT = "ftOrient";

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

    public static void setFtOrient(Context context, DetectFaceOrientPriority ftOrient) {
        if (context == null) {
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(FT_ORIENT, ftOrient.name())
                .apply();
    }

    public static String getFtOrient(Context context) {
        if (context == null) {
            return DetectFaceOrientPriority.ASF_OP_ALL_OUT.name();
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(FT_ORIENT) && sharedPreferences.getAll().get(FT_ORIENT) instanceof Integer) {
            return DetectFaceOrientPriority.ASF_OP_ALL_OUT.name();
        }
        return sharedPreferences.getString(FT_ORIENT, DetectFaceOrientPriority.ASF_OP_ALL_OUT.name());
    }
}
