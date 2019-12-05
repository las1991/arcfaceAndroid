package com.las.arc_face.faceserver;

import android.graphics.*;
import android.os.Environment;
import android.util.Log;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.las.arc_face.util.ImageUtil;
import com.las.arc_face.util.RectUtil;
import com.las.arc_face.util.student.StudentInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.las.arc_face.faceserver.FaceServer.IMG_SUFFIX;

public class FaceRegister {
    private static final String TAG = FaceRegister.class.getSimpleName();

    public static String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "arcfacedemo";
    private static final String SAVE_FEATURE_DIR = "register" + File.separator + "faceFeatures";
    private static final String FEATURE_DIR = ROOT_PATH + File.separator + SAVE_FEATURE_DIR;
    public static final String SAVE_IMG_DIR = "register" + File.separator + "faceImgs";
    public static final String IMG_DIR = ROOT_PATH + File.separator + SAVE_IMG_DIR;

    static {
        //特征存储的文件夹
        File featureDir = new File(FEATURE_DIR);
        if (!featureDir.exists()) {
            featureDir.mkdirs();
        }

        //图片存储的文件夹
        File imgDir = new File(IMG_DIR);
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
    }

    public static StudentInfo register(FaceEngine faceEngine, byte[] nv21, int width, int height, StudentInfo student) {
        if (faceEngine == null || nv21 == null || width % 4 != 0 || nv21.length != width * height * 3 / 2) {
            Log.e(TAG, "register: param error !");
            return null;
        }

        //1.人脸检测
        List<FaceInfo> faceInfoList = new ArrayList<>();
        int code = faceEngine.detectFaces(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList);
        if (code == ErrorInfo.MOK && faceInfoList.size() > 0) {
            FaceFeature faceFeature = new FaceFeature();

            //2.特征提取
            code = faceEngine.extractFaceFeature(nv21, width, height, FaceEngine.CP_PAF_NV21, faceInfoList.get(0), faceFeature);
            ByteArrayOutputStream fosImage = new ByteArrayOutputStream();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try {
                //3.保存注册结果（注册图、特征数据）
                if (code == ErrorInfo.MOK) {
                    YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
                    //为了美观，扩大rect截取注册图
                    Rect cropRect = RectUtil.getBestRect(width, height, faceInfoList.get(0).getRect());
                    if (cropRect == null) {
                        return null;
                    }

                    yuvImage.compressToJpeg(cropRect, 100, stream);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());

                    //判断人脸旋转角度，若不为0度则旋转注册图
                    if (bitmap != null) {
                        switch (faceInfoList.get(0).getOrient()) {
                            case FaceEngine.ASF_OC_0:
                                break;
                            case FaceEngine.ASF_OC_90:
                                bitmap = ImageUtil.getRotateBitmap(bitmap, 90);
                                break;
                            case FaceEngine.ASF_OC_180:
                                bitmap = ImageUtil.getRotateBitmap(bitmap, 180);
                                break;
                            case FaceEngine.ASF_OC_270:
                                bitmap = ImageUtil.getRotateBitmap(bitmap, 270);
                                break;
                            default:
                                break;
                        }

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosImage);
                    } else {
                        Log.e(TAG, "bitmap is null");
                        yuvImage.compressToJpeg(cropRect, 100, fosImage);
                    }

                    byte[] faceData = fosImage.toByteArray();

//                    saveImg2File(student, fosImage);
//                    saveFaceFeature2File(student, faceFeature.getFeatureData());

                    student.setFaceData(faceData);
                    student.setFeatureData(faceFeature.getFeatureData());

                    return student;
                }
                Log.e(TAG, "register: error code :" + code);
            } finally {
                try {
                    if (null != stream) stream.close();
                } catch (IOException e) {
                }
                try {
                    if (null != fosImage) fosImage.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    private static void saveImg2File(StudentInfo student, ByteArrayOutputStream outputStream) {
        String name = student.getName() + student.getId();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(IMG_DIR + File.separator + name + IMG_SUFFIX);
            outputStream.writeTo(fos);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private static void saveFaceFeature2File(StudentInfo student, byte[] data) {
        String name = student.getName() + student.getId();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(FEATURE_DIR + File.separator + name);
            fos.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }

    }

}
