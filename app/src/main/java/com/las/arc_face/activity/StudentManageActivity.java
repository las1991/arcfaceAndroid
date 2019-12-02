package com.las.arc_face.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.las.arc_face.R;
import com.las.arc_face.model.Student;
import com.las.arc_face.model.StudentAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentManageActivity extends Activity implements AdapterView.OnItemClickListener {
    private ListView listview;
    // 模拟数据
    private List<Student> dataList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_manage);
        init();
    }

    private void init() {
        dataList = initData();
        // 设置adapter(所在的activity,使用的显示样式,数据源)
        ListAdapter adapter = new StudentAdapter(StudentManageActivity.this,
                R.layout.item_student, dataList);
        listview = findViewById(R.id.listview);
        listview.setAdapter(adapter);
        // 绑定item点击事件
        listview.setOnItemClickListener(this);
    }

    private List initData() {
        List<Student> students = new ArrayList();
        // 初始化数据
        for (int i = 1; i <= 20; i++) {
            Student student = new Student();
            student.setId(i);
            student.setName("学生" + i);
            student.setAvatar("http://10.253.9.56/" + i + ".jpg");
            students.add(student);
        }
        return students;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(StudentManageActivity.this, "点击了第" + position + "条数据", Toast.LENGTH_SHORT).show();
    }
}