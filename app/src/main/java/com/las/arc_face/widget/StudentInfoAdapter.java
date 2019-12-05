package com.las.arc_face.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.las.arc_face.R;
import com.las.arc_face.model.Student;
import com.las.arc_face.util.student.StudentInfo;
import com.las.arc_face.util.student.StudentInfoDao;

import java.util.List;
import java.util.Objects;

public class StudentInfoAdapter extends RecyclerView.Adapter<StudentInfoAdapter.StudentInfoHolder> {

    private final Context mContext;

    public interface OnRecyclerViewListener {
        void onItemClick(int position);

        boolean onItemLongClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private StudentInfoDao studentInfoDao;

    // 子项布局的id
    private List<Student> students;
    private LayoutInflater inflater;

    public StudentInfoAdapter(Context context, List<Student> students) {
        this.mContext=context;
        inflater = LayoutInflater.from(context);
        this.students = students;
        this.studentInfoDao = new StudentInfoDao(context);
    }

    @NonNull
    @Override
    public StudentInfoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = inflater.inflate(R.layout.item_student, null, false);
        StudentInfoHolder viewHolder = new StudentInfoHolder(itemView);
        viewHolder.position = i;
        viewHolder.avatarImage = itemView.findViewById(R.id.student_avatar_image);
        viewHolder.faceImage = itemView.findViewById(R.id.student_face_image);
        // 通过id得到文本空间实例
        viewHolder.name = itemView.findViewById(R.id.student_name);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentInfoHolder studentInfoHolder, int position) {
        if (students == null) {
            return;
        }
        Student student = students.get(position);

        StudentInfo studentInfo = studentInfoDao.queryByStudentId(student.getId());
        if (studentInfo == null) {
            studentInfo = new StudentInfo(student);
        }
        if (!Objects.equals(student.getAvatar(), studentInfo.getAvatar())) {
            studentInfo.setAvatar(student.getAvatar());
            studentInfo.setFaceData(null);
            studentInfo.setFeatureData(null);
        }
        studentInfoDao.saveOrUpdate(studentInfo);

        Glide.with(mContext)
                .load(studentInfo.getFaceData())
                .into(studentInfoHolder.faceImage);
        Glide.with(mContext)
                .load(studentInfo.getAvatar())
                .into(studentInfoHolder.avatarImage);
        // 文本控件设置文本内容
        studentInfoHolder.name.setText(studentInfo.getName());
    }

    @Override
    public int getItemCount() {
        return students == null ? 0 : students.size();
    }

    // 内部类
    class StudentInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        int position;
        ImageView avatarImage;
        ImageView faceImage;
        TextView name;

        public StudentInfoHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(getLayoutPosition());
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != onRecyclerViewListener) {
                return onRecyclerViewListener.onItemLongClick(position);
            }
            return false;
        }
    }
}
