//Provides functionality to open a database and constants to call various tables and
package com.example.elliotknuth.c196final;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.elliotknuth.c196final.Contracts.AlarmEntry;
import com.example.elliotknuth.c196final.Contracts.AssessmentEntry;
import com.example.elliotknuth.c196final.Contracts.CourseEntry;
import com.example.elliotknuth.c196final.Contracts.TermEntry;

public class DBOpenHelper extends SQLiteOpenHelper {
    //Constants for db name and version
    private static final String DATABASE_NAME = "c196final.db";
	private static final int DATABASE_VERSION = 5;

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_CREATE =
            "CREATE TABLE " + TermEntry.TABLE_NAME + " (" +
                    TermEntry._ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TermEntry.COLUMN_TERM + " TEXT NOT NULL, " +
                    TermEntry.COLUMN_START_DATE + " TEXT NOT NULL, " +
                    TermEntry.COLUMN_END_DATE + " TEXT NOT NULL" +
                    ")";

        db.execSQL(TABLE_CREATE);

        TABLE_CREATE = "CREATE TABLE " + CourseEntry.TABLE_NAME + " (" + TermEntry._ID +
					   " INTEGER PRIMARY KEY AUTOINCREMENT, " + CourseEntry.COLUMN_NUMBER +
					   " TEXT NOT NULL, " + CourseEntry.COLUMN_NAME + " TEXT NOT NULL, " +
					   CourseEntry.COLUMN_TERM + " TEXT NOT NULL, " +
					   CourseEntry.COLUMN_START_DATE + " TEXT, " + CourseEntry.COLUMN_END_DATE +
					   " TEXT, " + CourseEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
					   CourseEntry.COLUMN_OBJECTIVE_PREASSESSMENT + " TEXT, " +
					   CourseEntry.COLUMN_OBJECTIVE_ASSESSMENT + " TEXT, " +
					   CourseEntry.COLUMN_PERFORMANCE_PREASSESSMENT + " TEXT, " +
					   CourseEntry.COLUMN_PERFORMANCE_ASSESSMENT + " TEXT, " +
					   CourseEntry.COLUMN_MENTOR_NAME + " TEXT, " +
					   CourseEntry.COLUMN_MENTOR_EMAIL + " TEXT, " +
					   CourseEntry.COLUMN_MENTOR_PHONE + " TEXT, " + CourseEntry.COLUMN_NOTES +
					   " TEXT, " + "FOREIGN KEY(" + CourseEntry.COLUMN_TERM + ") REFERENCES " +
					   TermEntry.TABLE_NAME + " (" + TermEntry._ID + ")" + ");";

        db.execSQL(TABLE_CREATE);

        TABLE_CREATE = "CREATE TABLE " + AssessmentEntry.TABLE_NAME + " (" + AssessmentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					   AssessmentEntry.COLUMN_COURSE + " TEXT NOT NULL, " + AssessmentEntry.ASSESSMENT_NAME +
					   " TEXT NOT NULL, " + AssessmentEntry.COLUMN_ASSESSMENT_TYPE +
					   " TEXT NOT NULL, " + AssessmentEntry.COLUMN_NOTES + " TEXT," +
					   "FOREIGN KEY(" + AssessmentEntry.COLUMN_COURSE + ") REFERENCES " +
					   CourseEntry.TABLE_NAME + "(" + CourseEntry._ID + ")" + ")";

		db.execSQL(TABLE_CREATE);

		TABLE_CREATE = "CREATE TABLE " + AlarmEntry.TABLE_NAME + " (" + AlarmEntry._ID +
					   " INTEGER PRIMARY KEY AUTOINCREMENT, " + AlarmEntry.COLUMN_TYPE +
					   " TEXT NOT NULL, " + AlarmEntry.COLUMN_ITEM_ID + " TEXT NOT NULL, " +
					   AlarmEntry.COLUMN_DATE + " TEXT NOT NULL, " + AlarmEntry.COLUMN_TIME +
					   " TEXT NOT NULL" + ")";

        db.execSQL(TABLE_CREATE);
    }

	@Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TermEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CourseEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AssessmentEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + AlarmEntry.TABLE_NAME);
        onCreate(db);
    }
}
