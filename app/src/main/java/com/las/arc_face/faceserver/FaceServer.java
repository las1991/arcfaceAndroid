package com.las.arc_face.faceserver;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;
import com.las.arc_face.activity.ChooseFunctionActivity;
import com.las.arc_face.common.Constants;
import com.las.arc_face.config.FaceEngineConfig;
import com.las.arc_face.util.ConfigUtil;
import com.las.arc_face.util.student.StudentInfo;
import com.las.arc_face.util.student.StudentInfoDao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 人脸库操作类，包含注册和搜索
 */
public class FaceServer {
    public static final String IMG_SUFFIX = ".jpg";
    public static String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "arcfacedemo";
    public static final String SAVE_IMG_DIR = "register" + File.separator + "imgs";

    private static final String TAG = "FaceServer";
    private static final String SAVE_FEATURE_DIR = "register" + File.separator + "features";

    private static List<StudentInfo> faceRegisterInfoList;

    private FaceEngine faceEngine = null;

    private StudentInfoDao studentInfoDao;

    private FaceEngineConfig config;

    /**
     * 是否正在搜索人脸，保证搜索操作单线程进行
     */
    private boolean isProcessing = false;

    public boolean init(Context context) {
        return init(context, FaceEngineConfig.video(0));
    }

    /**
     * 初始化
     * @param context 上下文对象
     * @param config 配置
     * @return 是否初始化成功
     */
    public boolean init(Context context, FaceEngineConfig config) {
        if (null == this.config) {
            this.config = config;
        }
        if (context == null) {
            return false;
        }
        synchronized (this) {
            if (studentInfoDao == null) {
                studentInfoDao = new StudentInfoDao(context);
            }
            if (faceRegisterInfoList == null) {
                faceRegisterInfoList = new ArrayList<>();
            }
            if (faceEngine == null) {
                faceEngine = new FaceEngine();
                int engineCode = 0;
                ActiveFileInfo activeFileInfo = new ActiveFileInfo();
                engineCode = FaceEngine.getActiveFileInfo(context, activeFileInfo);
                if (engineCode == ErrorInfo.MOK) {
                    Log.i(TAG, "activeFileInfo is : " + activeFileInfo);
                } else if (engineCode == ErrorInfo.MERR_ASF_ACTIVE_FILE_NO_EXIST) {
                    engineCode = faceEngine.active(context, Constants.APP_ID, Constants.SDK_KEY);
                    if (engineCode != ErrorInfo.MOK) {
                        Log.e(TAG, "active: failed! code = " + engineCode);
                        return false;
                    }
                } else {
                    Log.i(TAG, "getActiveFileInfo failed, code is  : " + engineCode);
                }

                engineCode = faceEngine.init(context, config.getDetectMode(), config.getDetectFaceOrientPriority(), config.getDetectFaceScaleVal(), config.getDetectFaceMaxNum(), config.getCombinedMask());
                if (engineCode == ErrorInfo.MOK) {
                    initFaceList(context);
                    return true;
                } else {
                    faceEngine = null;
                    Log.e(TAG, "init: failed! code = " + engineCode);
                    return false;
                }
            }

            return false;
        }
    }

    /**
     * 销毁
     */
    public void unInit() {
        synchronized (this) {
            if (faceRegisterInfoList != null) {
                faceRegisterInfoList.clear();
                faceRegisterInfoList = null;
            }
            if (faceEngine != null) {
                faceEngine.unInit();
                faceEngine = null;
            }
        }
    }

    /**
     * 初始化人脸特征数据以及人脸特征数据对应的注册图
     *
     * @param context 上下文对象
     */
    private void initFaceList(Context context) {
        synchronized (this) {
            faceRegisterInfoList.addAll(studentInfoDao.queryAll());
        }
    }

    public int getFaceNumber(Context context) {
        synchronized (this) {
            if (context == null) {
                return 0;
            }
            if (ROOT_PATH == null) {
                ROOT_PATH = context.getFilesDir().getAbsolutePath();
            }

            File featureFileDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR);
            int featureCount = 0;
            if (featureFileDir.exists() && featureFileDir.isDirectory()) {
                String[] featureFiles = featureFileDir.list();
                featureCount = featureFiles == null ? 0 : featureFiles.length;
            }
            int imageCount = 0;
            File imgFileDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR);
            if (imgFileDir.exists() && imgFileDir.isDirectory()) {
                String[] imageFiles = imgFileDir.list();
                imageCount = imageFiles == null ? 0 : imageFiles.length;
            }
            return featureCount > imageCount ? imageCount : featureCount;
        }
    }

    public int clearAllFaces(Context context) {
        synchronized (this) {
            if (context == null) {
                return 0;
            }
            if (ROOT_PATH == null) {
                ROOT_PATH = context.getFilesDir().getAbsolutePath();
            }
            if (faceRegisterInfoList != null) {
                faceRegisterInfoList.clear();
            }
            File featureFileDir = new File(ROOT_PATH + File.separator + SAVE_FEATURE_DIR);
            int deletedFeatureCount = 0;
            if (featureFileDir.exists() && featureFileDir.isDirectory()) {
                File[] featureFiles = featureFileDir.listFiles();
                if (featureFiles != null && featureFiles.length > 0) {
                    for (File featureFile : featureFiles) {
                        if (featureFile.delete()) {
                            deletedFeatureCount++;
                        }
                    }
                }
            }
            int deletedImageCount = 0;
            File imgFileDir = new File(ROOT_PATH + File.separator + SAVE_IMG_DIR);
            if (imgFileDir.exists() && imgFileDir.isDirectory()) {
                File[] imgFiles = imgFileDir.listFiles();
                if (imgFiles != null && imgFiles.length > 0) {
                    for (File imgFile : imgFiles) {
                        if (imgFile.delete()) {
                            deletedImageCount++;
                        }
                    }
                }
            }
            return deletedFeatureCount > deletedImageCount ? deletedImageCount : deletedFeatureCount;
        }
    }

    /**
     * 注册人脸
     *
     * @param context 上下文对象
     * @param nv21    NV21数据
     * @param width   NV21宽度
     * @param height  NV21高度
     * @param student 保存的名字，可为空
     * @return 是否注册成功
     */
    public boolean register(Context context, byte[] nv21, int width, int height, StudentInfo student) {
        synchronized (this) {
            if (student == null || faceEngine == null || context == null || nv21 == null || width % 4 != 0 || nv21.length != width * height * 3 / 2) {
                Log.e(TAG, "register: param null");
                return false;
            }

            StudentInfo studentInfo = FaceRegister.register(faceEngine, nv21, width, height, student);
            if (null == studentInfo) {
                return false;
            }
            faceRegisterInfoList.add(studentInfo);
            return true;
        }
    }

    /**
     * 在特征库中搜索
     *
     * @param faceFeature 传入特征数据
     * @return 比对结果
     */
    public CompareResult getTopOfFaceLib(FaceFeature faceFeature) {
        if (faceEngine == null || isProcessing || faceFeature == null || faceRegisterInfoList == null || faceRegisterInfoList.size() == 0) {
            return null;
        }
        FaceFeature tempFaceFeature = new FaceFeature();
        FaceSimilar faceSimilar = new FaceSimilar();
        float maxSimilar = 0;
        int maxSimilarIndex = -1;
        isProcessing = true;
        for (int n = 0; n < 100; n++) {
            for (int i = 0; i < faceRegisterInfoList.size(); i++) {
                tempFaceFeature.setFeatureData(faceRegisterInfoList.get(i).getFeatureData());
                faceEngine.compareFaceFeature(faceFeature, tempFaceFeature, faceSimilar);
                if (faceSimilar.getScore() > maxSimilar) {
                    maxSimilar = faceSimilar.getScore();
                    maxSimilarIndex = i;
                }
            }
        }
        isProcessing = false;
        if (maxSimilarIndex != -1) {
            StudentInfo studentInfo = faceRegisterInfoList.get(maxSimilarIndex);
            return new CompareResult(studentInfo, maxSimilar);
        }
        return null;
    }
}
