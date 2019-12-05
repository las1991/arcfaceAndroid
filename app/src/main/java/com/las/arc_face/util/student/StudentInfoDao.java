package com.las.arc_face.util.student;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentInfoDao {
    private static final String TAG = StudentInfoDao.class.getSimpleName();

    private Context context;
    private StudentInfoDbHelper dbHelper;

    public StudentInfoDao(Context context) {
        this.context = context;
        dbHelper = new StudentInfoDbHelper(context);
    }

    public long insert(StudentInfo studentInfo) {
        Log.i(TAG, "insert: " + studentInfo);
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            contentValues.put(StudentInfo.COLUMN.studentId.name(), studentInfo.getStudentId());
            contentValues.put(StudentInfo.COLUMN.name.name(), studentInfo.getName());
            contentValues.put(StudentInfo.COLUMN.avatar.name(), studentInfo.getAvatar());
            contentValues.put(StudentInfo.COLUMN.faceData.name(), studentInfo.getFaceData());
            contentValues.put(StudentInfo.COLUMN.featureData.name(), studentInfo.getFeatureData());
            long id = db.insertOrThrow(StudentInfo.TABLE_NAME, null, contentValues);
            studentInfo.setId(id);
            db.setTransactionSuccessful();
            return id;
        } catch (SQLiteConstraintException e) {
            Toast.makeText(context, "主键重复", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return -1;
    }

    public StudentInfo queryByStudentId(int studentId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(StudentInfo.TABLE_NAME,
                    StudentInfo.COLUMNS,
                    StudentInfo.COLUMN.studentId.name() + " = ?",
                    new String[]{String.valueOf(studentId)},
                    null, null, null);
            if (cursor.moveToFirst()) {
                StudentInfo studentInfo = parseStudent(cursor);
                return studentInfo;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    public List<StudentInfo> queryAll() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(StudentInfo.TABLE_NAME, StudentInfo.COLUMNS, null, null, null, null, null);

            if (cursor.getCount() > 0) {
                List<StudentInfo> studentInfos = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    studentInfos.add(parseStudent(cursor));
                }
                return studentInfos;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return Collections.emptyList();
    }

    public boolean updateStudent(StudentInfo studentInfo) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            // update Orders set OrderPrice = 800 where Id = 6
            ContentValues cv = new ContentValues();
            cv.put("studentId", studentInfo.getStudentId());
            cv.put("name", studentInfo.getName());
            cv.put("avatar", studentInfo.getAvatar());
            cv.put("faceData", studentInfo.getFaceData());
            cv.put("featureData", studentInfo.getFeatureData());
            db.update(StudentInfo.TABLE_NAME,
                    cv,
                    StudentInfo.COLUMN.id.name() + " = ?",
                    new String[]{String.valueOf(studentInfo.getId())});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return false;
    }

    public void saveOrUpdate(StudentInfo studentInfo) {
        if (studentInfo.getId() != null && null != queryByStudentId(studentInfo.getStudentId())) {
            updateStudent(studentInfo);
        } else {
            insert(studentInfo);
        }
    }

    public int deleteByStudentId(int studentId) {
        SQLiteDatabase db = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            String[] args = {String.valueOf(studentId)};
            int row = db.delete(StudentInfo.TABLE_NAME, StudentInfo.COLUMN.studentId.name() + "=?", args);
            db.setTransactionSuccessful();
            return row;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
        return -1;
    }

    private StudentInfo parseStudent(Cursor cursor) {
        StudentInfo studentInfo = new StudentInfo();
        studentInfo.setId(cursor.getLong(cursor.getColumnIndex(StudentInfo.COLUMN.id.name())));
        studentInfo.setStudentId(cursor.getInt(cursor.getColumnIndex(StudentInfo.COLUMN.studentId.name())));
        studentInfo.setAvatar(cursor.getString(cursor.getColumnIndex(StudentInfo.COLUMN.avatar.name())));
        studentInfo.setName(cursor.getString(cursor.getColumnIndex(StudentInfo.COLUMN.name.name())));
        studentInfo.setFaceData(cursor.getBlob(cursor.getColumnIndex(StudentInfo.COLUMN.faceData.name())));
        studentInfo.setFeatureData(cursor.getBlob(cursor.getColumnIndex(StudentInfo.COLUMN.featureData.name())));
        return studentInfo;
    }


}
