package com.las.arc_face.listener;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class OrientationSensorListener implements SensorEventListener {
    private static final int DATA_X = 0;
    private static final int DATA_Y = 1;
    private static final int DATA_Z = 2;

    public static final int ORIENTATION_UNKNOWN = -1;

    public static Handler getRotateHandler(Activity activity) {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 888) {
                    int orientation = msg.arg1;
                    if (orientation > 45 && orientation < 135) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        Log.d("RotateHandler", "横屏翻转");
                    } else if (orientation > 135 && orientation < 225) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                        Log.d("RotateHandler", "竖屏翻转");
                    } else if (orientation > 225 && orientation < 315) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        Log.d("RotateHandler", "横屏");
                    } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        Log.d("RotateHandler", "竖屏");
                    }
                }
                super.handleMessage(msg);
            }
        };
    }

    private Handler rotateHandler;

    public OrientationSensorListener(Handler handler) {
        rotateHandler = handler;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float[] values = sensorEvent.values;
        int orientation = ORIENTATION_UNKNOWN;
        float X = -values[DATA_X];
        float Y = -values[DATA_Y];
        float Z = -values[DATA_Z];
        float magnitude = X * X + Y * Y;
        // Don't trust the angle if the magnitude is small compared to the y value
        if (magnitude * 4 >= Z * Z) {
            float OneEightyOverPi = 57.29577957855f;
            float angle = (float) Math.atan2(-Y, X) * OneEightyOverPi;
            orientation = 90 - (int) Math.round(angle);
            // normalize to 0 - 359 range
            while (orientation >= 360) {
                orientation -= 360;
            }
            while (orientation < 0) {
                orientation += 360;
            }
        }

        if (rotateHandler != null) {
            rotateHandler.obtainMessage(888, orientation, 0).sendToTarget();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
