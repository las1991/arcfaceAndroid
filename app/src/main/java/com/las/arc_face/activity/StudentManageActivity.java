package com.las.arc_face.activity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.las.arc_face.R;
import com.las.arc_face.config.FaceEngineConfig;
import com.las.arc_face.faceserver.FaceServer;
import com.las.arc_face.model.Student;
import com.las.arc_face.request.CharsetStringRequest;
import com.las.arc_face.util.ImageUtil;
import com.las.arc_face.util.student.StudentInfo;
import com.las.arc_face.util.student.StudentInfoDao;
import com.las.arc_face.widget.StudentInfoAdapter;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class StudentManageActivity extends BaseActivity implements StudentInfoAdapter.OnRecyclerViewListener {
    private static final String TAG = StudentManageActivity.class.getSimpleName();
    private RecyclerView studentInfoView;
    private StudentInfoAdapter studentInfoAdapter;
    // 模拟数据
    private List<Student> dataList;

    private TextView tvNotice;

    /**
     * 提示对话框
     */
    private AlertDialog progressDialog;

    private FaceServer faceServer = new FaceServer();

    private StudentInfoDao studentInfoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_manage);
        faceServer.init(this, FaceEngineConfig.image(360));
        studentInfoDao = new StudentInfoDao(this);

        // 设置adapter(所在的activity,使用的显示样式,数据源)
        dataList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            Student student = new Student();
            student.setId(i);
            student.setName("x学生" + i);
            student.setAvatar("http://10.253.9.56/" + i + ".jpg");
            dataList.add(student);
        }
        studentInfoAdapter = new StudentInfoAdapter(StudentManageActivity.this, dataList);
        studentInfoView = findViewById(R.id.studentInfoView);
        studentInfoView.setAdapter(studentInfoAdapter);
        studentInfoView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        studentInfoView.setLayoutManager(new LinearLayoutManager(this));
        studentInfoAdapter.setOnRecyclerViewListener(this);

        tvNotice = findViewById(R.id.tv_notice);
        progressDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.processing)
                .setView(new ProgressBar(this))
                .create();
//        initData();
    }

    private void initData() {
        Log.i(TAG, "init: ");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.253.9.56/students.json";

        CharsetStringRequest stringRequest = new CharsetStringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                Type type = new TypeToken<List<Student>>() {
                }.getType();
                List<Student> students = gson.fromJson(response, type);
                dataList.addAll(students);
                studentInfoAdapter.notifyItemInserted(dataList.size() - 1);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: ", error);
                List<Student> students = new LinkedList<>();
                for (int i = 1; i <= 20; i++) {
                    Student student = new Student();
                    student.setId(i);
                    student.setName("x学生" + i);
                    student.setAvatar("http://10.253.9.56/" + i + ".jpg");
                    students.add(student);
                }
                dataList.addAll(students);
                studentInfoAdapter.notifyItemInserted(dataList.size() - 1);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    protected void onDestroy() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;

        unInitEngine();
        super.onDestroy();
    }

    /**
     * 销毁引擎
     */
    private void unInitEngine() {
        if (faceServer != null) {
            faceServer.unInit();
        }
    }

    /**
     * 展示提示信息并且关闭提示框
     *
     * @param stringBuilder 带格式的提示文字
     */
    private void appendNotificationAndFinish(final SpannableStringBuilder stringBuilder) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvNotice != null && stringBuilder != null) {
                    tvNotice.setText(stringBuilder);
                }
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void appendNotification(final SpannableStringBuilder stringBuilder) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvNotice != null) {
                    tvNotice.setText(stringBuilder);
                }
            }
        });
    }

    /**
     * 追加提示信息
     *
     * @param stringBuilder 提示的字符串的存放对象
     * @param styleSpan     添加的字符串的格式
     * @param strings       字符串数组
     */
    private void addNotificationInfo(SpannableStringBuilder stringBuilder, ParcelableSpan styleSpan, String... strings) {
        if (stringBuilder == null || strings == null || strings.length == 0) {
            return;
        }
        int startLength = stringBuilder.length();
        for (String string : strings) {
            stringBuilder.append(string);
        }
        int endLength = stringBuilder.length();
        if (styleSpan != null) {
            stringBuilder.setSpan(styleSpan, startLength, endLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void batchUnRegister(View view) {
        for (int pos = 0; pos < studentInfoAdapter.getItemCount(); pos++) {
            dataList.get(pos);
        }
    }

    public void batchRegister(final View view) {
        view.setClickable(false);
        if (progressDialog == null || progressDialog.isShowing()) {
            return;
        }
        if (null == studentInfoAdapter) {
            return;
        }

        progressDialog.show();

        //图像转化操作和部分引擎调用比较耗时，建议放子线程操作
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int pos = 0; pos < studentInfoAdapter.getItemCount(); pos++) {
                    processImage(pos);
                    emitter.onNext(pos);
                }
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.i(TAG, "onSubscribe: " + d);
                    }

                    @Override
                    public void onNext(Integer o) {
                        Log.i(TAG, "onNext: " + o);
                        studentInfoAdapter.notifyItemChanged((int) o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                        appendNotificationAndFinish(null);
                        view.setClickable(true);
                    }
                });
    }

    /**
     * 主要操作逻辑部分
     */
    public void processImage(int pos) {
        Log.i(TAG, "processImage: " + pos);
        final SpannableStringBuilder notificationSpannableStringBuilder = new SpannableStringBuilder();

        Student student = dataList.get(pos);
        if (null == student) {
            addNotificationInfo(notificationSpannableStringBuilder, null, " student is null!");
            appendNotification(notificationSpannableStringBuilder);
            return;
        }
        StudentInfo studentInfo = studentInfoDao.queryByStudentId(student.getId());

        if (null == studentInfo) {
            studentInfo = new StudentInfo(student);

            if (!Objects.equals(student.getAvatar(), studentInfo.getAvatar())) {
                studentInfo.setAvatar(student.getAvatar());
                studentInfo.setFaceData(null);
                studentInfo.setFeatureData(null);
            }
            studentInfoDao.saveOrUpdate(studentInfo);
        }
        if (studentInfo.getFeatureData() != null) {
            addNotificationInfo(notificationSpannableStringBuilder, null, " AvatarFile is not null!");
            appendNotification(notificationSpannableStringBuilder);
            return;
        }
        FutureTarget<Bitmap> futureTarget =
                Glide.with(this)
                        .asBitmap()
                        .load(studentInfo.getAvatar())
                        .submit();
        Bitmap bitmap = null;
        try {
            bitmap = futureTarget.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * 1.准备操作（校验，显示，获取BGR）
         */
        bitmap = ImageUtil.alignBitmapForNv21(bitmap);
        if (bitmap == null) {
            Log.e(TAG, "alignBitmapForNv21 fail");
            return;
        }
        byte[] nv21 = ImageUtil.bitmapToNv21(bitmap, bitmap.getWidth(), bitmap.getHeight());
        if (nv21 == null) {
            Log.e(TAG, "bitmapToNv21 fail");
            return;
        }
        boolean success = faceServer.register(StudentManageActivity.this, nv21, bitmap.getWidth(), bitmap.getHeight(), studentInfo);

        if (!success) {
            addNotificationInfo(notificationSpannableStringBuilder, null, student.getName(), " FaceServer register fail", "\n");
            Log.e(TAG, student.getName() + " FaceServer register fail");
        } else {
            studentInfoDao.saveOrUpdate(studentInfo);
            addNotificationInfo(notificationSpannableStringBuilder, null, student.getName(), " FaceServer register success", "\n");
        }

        appendNotification(notificationSpannableStringBuilder);
    }


    @Override
    public void onItemClick(int position) {
        if (progressDialog == null || progressDialog.isShowing()) {
            return;
        }
        if (null == studentInfoAdapter) {
            return;
        }

        progressDialog.show();
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                processImage(position);
                appendNotificationAndFinish(null);
            }
        });
//        Toast.makeText(StudentManageActivity.this, "点击了第" + position + "条数据", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(int position) {
        return false;
    }
}