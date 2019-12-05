package com.las.arc_face.faceserver;


import com.las.arc_face.util.student.StudentInfo;

public class CompareResult {
    private StudentInfo studentInfo;
    private float similar;
    private int trackId;

    public CompareResult(StudentInfo studentInfo, float similar) {
        this.studentInfo = studentInfo;
        this.similar = similar;
    }


    public StudentInfo getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(StudentInfo studentInfo) {
        this.studentInfo = studentInfo;
    }

    public float getSimilar() {
        return similar;
    }

    public void setSimilar(float similar) {
        this.similar = similar;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }
    
}
