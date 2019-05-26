package com.example.elliotknuth.c196final;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class AssessmentActivity extends AppCompatActivity
{
	private Spinner course;
	private EditText assessmentName;
	private Spinner assessmentType;
	private EditText assessmentNotes;
	private TextView textAssessmentType;
	private TextView textAssessmentCourse;
	private TextView assessmentId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assessment);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		assessmentId = findViewById(R.id.assessmentId);
		course = findViewById(R.id.inputAssessmentCourse);
		assessmentName = findViewById(R.id.inputAssessmentName);
		assessmentType = findViewById(R.id.inputAssessmentType);
		assessmentNotes = findViewById(R.id.inputAssessmentNotes);
		textAssessmentCourse = findViewById(R.id.textAssessmentCourse);
		textAssessmentType = findViewById(R.id.textAssessmentType);


		toggleEditable();

	}

	private void toggleEditable()
	{
		Intent intent = getIntent();
		if (intent.getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.VIEW))
		{
			DatabaseHandler.AssessmentObject assessment =
					DatabaseHandler.getInstance().getAssessmentById(intent.getStringExtra(Contracts.AssessmentEntry._ID));

			getSupportActionBar().setTitle("View Assessment");

			assessmentId.setText(assessment.getId());
			assessmentName.setText(assessment.getAssessmentName());
			assessmentNotes.setText(assessment.getNotes());
			textAssessmentType.setText(assessment.getAssessmentType());
			textAssessmentType.setVisibility(View.VISIBLE);
			textAssessmentCourse.setText(assessment.getCourseNumber());
			textAssessmentCourse.setVisibility(View.VISIBLE);
			course.setVisibility(View.INVISIBLE);
			assessmentType.setVisibility(View.INVISIBLE);
			assessmentName.setEnabled(false);
			assessmentNotes.setEnabled(false);
		}
		else
		{
			if (intent.getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.EDIT))
			{
				getSupportActionBar().setTitle("Edit Assessment");

				final DatabaseHandler.AssessmentObject assessment =
						DatabaseHandler.getInstance().getAssessmentById(intent.getStringExtra(Contracts.AssessmentEntry._ID));

				assessmentId.setText(assessment.getId());
				assessmentName.setText(assessment.getAssessmentName());
				assessmentNotes.setText(assessment.getNotes());
				String[] assessmentTypes = getResources().getStringArray(R.array.assessment_types);
				for (int i = 0 ; i < assessmentTypes.length ; i++)
				{
					if (assessment.getAssessmentType().equals(assessmentTypes[i]))
					{
						final int index = i;

						assessmentType.post(new Runnable()
						{
							@Override
							public void run()
							{

								assessmentType.setSelection(index);
							}
						});
					}
				}
				course.post(new Runnable()
				{
					@Override
					public void run()
					{
						course.setSelection(DatabaseHandler.getInstance().getCoursePositionInArray(assessment.getCourseId()));
					}
				});


				textAssessmentCourse.setVisibility(View.INVISIBLE);
				textAssessmentType.setVisibility(View.INVISIBLE);
				course.setVisibility(View.VISIBLE);
				assessmentType.setVisibility(View.VISIBLE);
				assessmentName.setEnabled(true);
				assessmentNotes.setEnabled(true);
			}
			else
			{
				getSupportActionBar().setTitle("Add Assessment");
			}

			initializeCourseSpinner();

			course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
				{
					if (position == course.getCount() - 1)
					{
						Intent addNewCourse = new Intent(AssessmentActivity.this, CourseActivity.class);
						addNewCourse.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.ADD);
						view.getContext().startActivity(addNewCourse);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{

				}
			});

			Spinner typeSpinner = findViewById(R.id.inputAssessmentType);
			ArrayAdapter<CharSequence> adapter =
					ArrayAdapter.createFromResource(this, R.array.assessment_types, android.R.layout.simple_spinner_dropdown_item);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			typeSpinner.setAdapter(adapter);
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if (course.getCount() == DatabaseHandler.getInstance().getAllCourses().size())
		{
			initializeCourseSpinner();
			course.setSelection(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.VIEW))
		{
			getMenuInflater().inflate(R.menu.view, menu);
			menu.findItem(R.id.action_share).setVisible(true);
		}
		else
		{
			getMenuInflater().inflate(R.menu.add, menu);
		}

		return true;
	}

	private void toggleOptionsMenu()
	{
		invalidateOptionsMenu();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.EDIT))
				{
					getIntent().putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.VIEW);
					toggleEditable();
					return true;
				}
				return false;
			case R.id.action_ok:
				addItem();
				break;
			case R.id.action_edit:
				getIntent().putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.EDIT);
				toggleEditable();
				break;
			case R.id.action_delete:
				DatabaseHandler.getInstance().delete(Contracts.CourseEntry.TABLE_NAME, Integer.parseInt(assessmentId.getText().toString()));
				finish();
				break;
			case R.id.action_share:
				Intent smsIntent = new Intent(Intent.ACTION_SEND);
				smsIntent.setType("text/plain");
				smsIntent.putExtra("sms_body", "These are my notes for Assessment \"".concat(assessmentName.getText().toString()).concat("\":\n\n")
																					 .concat(assessmentNotes.getText().toString()));
				smsIntent.putExtra(Intent.EXTRA_SUBJECT, "Notes for Assessment \"".concat(assessmentName.getText().toString()).concat("\""));
				smsIntent.putExtra(Intent.EXTRA_TEXT,
								   "These are my notes for Assessment \"".concat(assessmentName.getText().toString()).concat("\":\n\n")
																		 .concat(assessmentNotes.getText().toString()).concat("\n"));
				startActivity(Intent.createChooser(smsIntent, "Choose an E-Mail or SMS program"));

		}

		return super.onOptionsItemSelected(item);
	}

	private void initializeCourseSpinner()
	{
		ArrayAdapter<String> courseSpinnerAdapter =
				new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, populateCoursesList());
		courseSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		course.setAdapter(courseSpinnerAdapter);
	}

	private List<String> populateCoursesList()
	{
		ArrayList<DatabaseHandler.CourseObject> coursesCursor = DatabaseHandler.getInstance().getAllCourses();
		ArrayList<String> coursesList = new ArrayList<>();

		for (DatabaseHandler.CourseObject course : coursesCursor)
		{
			coursesList.add(course.getCourseNumber().concat(" - ").concat(course.getCourseName()));
		}

		if (coursesList.size() == 0)
		{
			coursesList.add("No Courses Available");
		}

		coursesList.add("Add New Course");
		return coursesList;

	}

	@Override
	public void onBackPressed()
	{
		finish();
	}

	public void addItem()
	{
		DatabaseHandler.AssessmentObject object =
				DatabaseHandler.getInstance(AssessmentActivity.this)
						.new AssessmentObject(assessmentId.getText().toString(), course.getSelectedItemPosition(),
											  assessmentName.getText().toString(), assessmentType.getSelectedItem().toString(),
											  assessmentNotes.getText().toString());
		if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.ADD))
		{
			DatabaseHandler.getInstance().insert(object);
		}
		else if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.EDIT))
		{
			DatabaseHandler.getInstance().update(object);
		}

		finish();
	}

	public class checkForNewCourses extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... voids)
		{
			initializeCourseSpinner();
			course.setSelection(course.getCount() - 2);

			return null;
		}
	}
}


