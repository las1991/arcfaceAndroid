package com.las.arc_face.model;

public class FaceRegisterInfo {
    private byte[] featureData;
    private byte[] faceData;
    private Student student;

    public FaceRegisterInfo(byte[] faceFeature,byte[] faceData, Student student) {
        this.featureData = faceFeature;
        this.faceData = faceData;
        this.student = student;
    }

    public byte[] getFeatureData() {
        return featureData;
    }

    public void setFeatureData(byte[] featureData) {
        this.featureData = featureData;
    }

    public byte[] getFaceData() {
        return faceData;
    }

    public void setFaceData(byte[] faceData) {
        this.faceData = faceData;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
