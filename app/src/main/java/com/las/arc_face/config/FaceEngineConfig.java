package com.las.arc_face.config;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;

public class FaceEngineConfig {

    public static final int MAX_DETECT_NUM_DEFAULT = 10;
    public static final int MAX_DETECT_NUM_LIVE = 1;


    public static final float SIMILAR_THRESHOLD = 0.8F;



    private static DetectFaceOrientPriority orient2Priority(int orient) {
        switch (orient) {
            case 90:
                return DetectFaceOrientPriority.ASF_OP_90_ONLY;
            case 180:
                return DetectFaceOrientPriority.ASF_OP_180_ONLY;
            case 270:
                return DetectFaceOrientPriority.ASF_OP_270_ONLY;
            case 360:
                return DetectFaceOrientPriority.ASF_OP_ALL_OUT;
            default:
                return DetectFaceOrientPriority.ASF_OP_0_ONLY;
        }
    }

    public static FaceEngineConfig video(int orient) {
        return new FaceEngineConfig(DetectMode.ASF_DETECT_MODE_VIDEO, orient2Priority(orient), 16, MAX_DETECT_NUM_DEFAULT, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
    }

    public static FaceEngineConfig image(int orient) {
        return new FaceEngineConfig(DetectMode.ASF_DETECT_MODE_IMAGE, orient2Priority(orient), 16, MAX_DETECT_NUM_DEFAULT, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT);
    }

    public static FaceEngineConfig videoLive(int orient) {
        return new FaceEngineConfig(DetectMode.ASF_DETECT_MODE_VIDEO, orient2Priority(orient), 16, MAX_DETECT_NUM_DEFAULT, FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_LIVENESS);
    }

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
