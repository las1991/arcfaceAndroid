package com.las.arc_face.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

public class BaseActivity extends Activity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    public Activity getActivity(){
        return this;
    }

    public void showToastMessage(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void showInfoDialog(String message) {
        showInfoDialog(null, message);
    }

    public void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }


}
