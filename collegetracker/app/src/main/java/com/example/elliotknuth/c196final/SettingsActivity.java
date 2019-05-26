package com.example.elliotknuth.c196final;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    SQLiteDatabase database;
    DBOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        Button clearTermsButton = findViewById(R.id.buttonClearTerms);
        Button clearCoursesButton = findViewById(R.id.buttonClearCourses);
        Button clearAssessmentsButton = findViewById(R.id.buttonClearAssessments);
        Button clearAllButton = findViewById(R.id.buttonClearAll);

        clearTermsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if (Validations.coursesTableEmpty())
				{
                    database.delete(Contracts.TermEntry.TABLE_NAME, null, null);
                }
                else
                    Toast.makeText(getApplicationContext(), "Error: Items still in Courses", Toast.LENGTH_LONG).show();
            }
        });

        clearCoursesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				if (Validations.assessmentsTableEmpty())
				{
                    database.delete(Contracts.CourseEntry.TABLE_NAME, null, null);
                }
                else
                    Toast.makeText(getApplicationContext(), "Error: Items still in Assessments", Toast.LENGTH_LONG).show();
            }
        });

        clearAssessmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				DatabaseHandler.getInstance().clearTable(Contracts.AssessmentEntry.TABLE_NAME);
            }
        });
	}
}
