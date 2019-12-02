package com.las.arc_face.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import com.las.arc_face.R;
import com.las.arc_face.faceserver.FaceServer;
import com.las.arc_face.util.ImageUtil;
import com.las.arc_face.widget.ProgressDialog;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 批量注册页面
 */
public class FaceManageActivity extends AppCompatActivity {
    //注册图所在的目录
    private static final String ROOT_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "arcfacedemo";
    private static final String REGISTER_DIR = ROOT_DIR + File.separator + "register";
    private static final String REGISTER_FAILED_DIR = ROOT_DIR + File.separator + "failed";
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private FaceServer faceServer = new FaceServer();

    private TextView tvNotificationRegisterResult;

    ProgressDialog progressDialog = null;
    private static final int ACTION_REQUEST_PERMISSIONS = 0x001;
    private static String[] NEEDED_PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_manage);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        tvNotificationRegisterResult = findViewById(R.id.notification_register_result);
        progressDialog = new ProgressDialog(this);
        faceServer.init(this);
    }

    @Override
    protected void onDestroy() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        faceServer.unInit();
        super.onDestroy();
    }

    public void batchRegister(View view) {
        if (checkPermissions(NEEDED_PERMISSIONS)) {
            doRegister();
        } else {
            ActivityCompat.requestPermissions(this, NEEDED_PERMISSIONS, ACTION_REQUEST_PERMISSIONS);
        }
    }

    private void doRegister() {
        File dir = new File(REGISTER_DIR);
        if (!dir.exists()) {
            Toast.makeText(this, "path \n" + REGISTER_DIR + "\n is not exists", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dir.isDirectory()) {
            Toast.makeText(this, "path \n" + REGISTER_DIR + "\n is not a directory", Toast.LENGTH_SHORT).show();
            return;
        }
        final File[] jpgFiles = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FaceServer.IMG_SUFFIX);
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final int totalCount = jpgFiles.length;

                int successCount = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMaxProgress(totalCount);
                        progressDialog.show();
                        tvNotificationRegisterResult.setText("");
                        tvNotificationRegisterResult.append("process start " + totalCount + " files,please wait\n\n");
                    }
                });
                for (int i = 0; i < totalCount; i++) {
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog != null) {
                                progressDialog.refreshProgress(finalI);
                            }
                        }
                    });
                    final File jpgFile = jpgFiles[i];
                    try {
                        Log.i(FaceManageActivity.class.getSimpleName(), "process " + jpgFile.getName());
                        Bitmap bitmap = BitmapFactory.decodeFile(jpgFile.getAbsolutePath());
                        if (bitmap == null) {
                            Log.i(FaceManageActivity.class.getSimpleName(), jpgFile.getName() + "decodeFile fail");
                            File failedFile = new File(REGISTER_FAILED_DIR + File.separator + jpgFile.getName());
                            if (!failedFile.getParentFile().exists()) {
                                failedFile.getParentFile().mkdirs();
                            }
                            jpgFile.renameTo(failedFile);
                            Files.copy(jpgFile.toPath(), failedFile.toPath());
                            continue;
                        }
                        bitmap = ImageUtil.alignBitmapForNv21(bitmap);
                        if (bitmap == null) {
                            Log.i(FaceManageActivity.class.getSimpleName(), jpgFile.getName() + "alignBitmapForNv21 fail");
                            File failedFile = new File(REGISTER_FAILED_DIR + File.separator + jpgFile.getName());
                            if (!failedFile.getParentFile().exists()) {
                                failedFile.getParentFile().mkdirs();
                            }
                            Files.copy(jpgFile.toPath(), failedFile.toPath());
                            continue;
                        }
                        byte[] nv21 = ImageUtil.bitmapToNv21(bitmap, bitmap.getWidth(), bitmap.getHeight());
                        if (nv21 == null) {
                            Log.i(FaceManageActivity.class.getSimpleName(), jpgFile.getName() + "bitmapToNv21 fail");
                        }
                        boolean success = faceServer.register(FaceManageActivity.this, nv21, bitmap.getWidth(), bitmap.getHeight(),
                                jpgFile.getName().substring(0, jpgFile.getName().lastIndexOf(".")));
                        if (!success) {
                            Log.e(FaceManageActivity.class.getSimpleName(), jpgFile.getName() + " FaceServer register fail");
                            File failedFile = new File(REGISTER_FAILED_DIR + File.separator + jpgFile.getName());
                            if (!failedFile.getParentFile().exists()) {
                                failedFile.getParentFile().mkdirs();
                            }
                            Files.copy(jpgFile.toPath(), failedFile.toPath());
                        } else {
                            successCount++;
                        }
                    } catch (Exception e) {
                        Log.e(FaceManageActivity.class.getSimpleName(), jpgFile.getName() + "register fail ", e);
                    }
                }
                final int finalSuccessCount = successCount;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        tvNotificationRegisterResult.append("process finished!\ntotal count = " + totalCount + "\nsuccess count = " + finalSuccessCount + "\nfailed count = " + (totalCount - finalSuccessCount)
                                + "\nfailed images are in directory '" + REGISTER_FAILED_DIR + "'");
                    }
                });
                Log.i(FaceManageActivity.class.getSimpleName(), "run: " + executorService.isShutdown());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACTION_REQUEST_PERMISSIONS) {
            boolean isAllGranted = true;
            for (int grantResult : grantResults) {
                isAllGranted &= (grantResult == PackageManager.PERMISSION_GRANTED);
            }
            if (isAllGranted) {
                doRegister();
            } else {
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissions(String[] neededPermissions) {
        if (neededPermissions == null || neededPermissions.length == 0) {
            return true;
        }
        boolean allGranted = true;
        for (String neededPermission : neededPermissions) {
            allGranted &= ContextCompat.checkSelfPermission(this.getApplicationContext(), neededPermission) == PackageManager.PERMISSION_GRANTED;
        }
        return allGranted;
    }

    public void clearFaces(View view) {
        int faceNum = faceServer.getFaceNumber(this);
        if (faceNum == 0) {
            Toast.makeText(this, R.string.no_face_need_to_delete, Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.notification)
                    .setMessage(getString(R.string.confirm_delete, faceNum))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int deleteCount = faceServer.clearAllFaces(FaceManageActivity.this);
                            Toast.makeText(FaceManageActivity.this, deleteCount + " faces cleared!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.show();
        }
    }
}
