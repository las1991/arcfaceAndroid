package com.las.arc_face.util.student;

import com.las.arc_face.model.Student;

import java.util.Arrays;

public class StudentInfo {
    public static final String TABLE_NAME = "t_student";
    public static String[] COLUMNS = StudentInfo.COLUMN.columns();

    public StudentInfo() {
    }

    public StudentInfo(Student student) {
        this.setStudentId(student.getId());
        this.setName(student.getName());
        this.setAvatar(student.getAvatar());
    }

    public enum COLUMN {
        id, studentId, name, avatar, faceData, featureData;

        public static String[] columns() {
            String[] val = new String[COLUMN.values().length];
            for (int i = 0; i < val.length; i++) {
                val[i] = COLUMN.values()[i].name();
            }
            return val;
        }
    }

    private Long id;
    private Integer studentId;
    private String name;
    private String avatar;
    private byte[] faceData;
    private byte[] featureData;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public byte[] getFaceData() {
        return faceData;
    }

    public void setFaceData(byte[] faceData) {
        this.faceData = faceData;
    }

    public byte[] getFeatureData() {
        return featureData;
    }

    public void setFeatureData(byte[] featureData) {
        this.featureData = featureData;
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", faceData=" + Arrays.toString(faceData) +
                ", featureData=" + Arrays.toString(featureData) +
                '}';
    }
}
