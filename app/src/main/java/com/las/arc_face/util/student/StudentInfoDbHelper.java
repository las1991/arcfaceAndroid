package com.las.arc_face.util.student;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudentInfoDbHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "myTest.db";

    public static final String CREATE_STUDENT = "create table " + StudentInfo.TABLE_NAME + " ("
            + "id integer primary key autoincrement, "
            + "studentId integer, "
            + "name text, "
            + "avatar real, "
            + "faceData Blob, "
            + "featureData Blob)";

    public StudentInfoDbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + StudentInfo.TABLE_NAME;
        db.execSQL(sql);
        db.execSQL(CREATE_STUDENT);
    }
}
