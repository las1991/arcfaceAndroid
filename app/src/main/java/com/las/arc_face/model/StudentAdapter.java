package com.las.arc_face.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.las.arc_face.R;

import java.util.List;

public class StudentAdapter extends ArrayAdapter<Student> {
    // 子项布局的id
    private int resourceId;

    public StudentAdapter(Context context, int resource, List<Student> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取当前项的Fruit实例
        Student student = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            // inflate出子项布局，实例化其中的图片控件和文本控件
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            viewHolder = new ViewHolder();
            // 通过id得到图片控件实例
            viewHolder.fruitImage = (ImageView) view.findViewById(R.id.student_avatar_image);
            // 通过id得到文本空间实例
            viewHolder.fruitName = (TextView) view.findViewById(R.id.student_name);
            // 缓存图片控件和文本控件的实例
            view.setTag(viewHolder);
        } else {
            view = convertView;
            // 取出缓存
            viewHolder = (ViewHolder) view.getTag();
        }

        // 直接使用缓存中的图片控件和文本控件的实例
        // 图片控件设置图片资源
        Glide.with(getContext()).asBitmap().load(student.getAvatar()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                viewHolder.fruitImage.setImageBitmap(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

        // 文本控件设置文本内容
        viewHolder.fruitName.setText(student.getName());

        return view;
    }

    // 内部类
    class ViewHolder {
        ImageView fruitImage;
        TextView fruitName;
    }
}
