package com.las.arc_face.config;

import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;

public class FaceEngineConfig {

    private DetectMode detectMode;
    private DetectFaceOrientPriority detectFaceOrientPriority;
    private int detectFaceScaleVal;
    private int detectFaceMaxNum;
    private int combinedMask;

    public FaceEngineConfig(DetectMode detectMode, DetectFaceOrientPriority detectFaceOrientPriority, int detectFaceScaleVal, int detectFaceMaxNum, int combinedMask) {
        this.detectMode = detectMode;
        this.detectFaceOrientPriority = detectFaceOrientPriority;
        this.detectFaceScaleVal = detectFaceScaleVal;
        this.detectFaceMaxNum = detectFaceMaxNum;
        this.combinedMask = combinedMask;
    }

    public DetectMode getDetectMode() {
        return detectMode;
    }

    public void setDetectMode(DetectMode detectMode) {
        this.detectMode = detectMode;
    }

    public DetectFaceOrientPriority getDetectFaceOrientPriority() {
        return detectFaceOrientPriority;
    }

    public void setDetectFaceOrientPriority(DetectFaceOrientPriority detectFaceOrientPriority) {
        this.detectFaceOrientPriority = detectFaceOrientPriority;
    }

    public int getDetectFaceScaleVal() {
        return detectFaceScaleVal;
    }

    public void setDetectFaceScaleVal(int detectFaceScaleVal) {
        this.detectFaceScaleVal = detectFaceScaleVal;
    }

    public int getDetectFaceMaxNum() {
        return detectFaceMaxNum;
    }

    public void setDetectFaceMaxNum(int detectFaceMaxNum) {
        this.detectFaceMaxNum = detectFaceMaxNum;
    }

    public int getCombinedMask() {
        return combinedMask;
    }

    public void setCombinedMask(int combinedMask) {
        this.combinedMask = combinedMask;
    }
}
