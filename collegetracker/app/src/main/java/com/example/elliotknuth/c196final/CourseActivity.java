package com.example.elliotknuth.c196final;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CourseActivity extends AppCompatActivity
{
	private EditText courseNumber;
	private EditText courseName;
	private EditText courseStartDate;
	private EditText courseEndDate;
	private Spinner courseStatusSpinner;
	private EditText courseMentorName;
	private EditText courseMentorPhone;
	private EditText courseMentorEmail;
	private EditText courseNotes;
	private Spinner courseTermSpinner;
	private List<String> termsList = new ArrayList<>();
	private String dateClicked;
	private String selectedDate = "";
	private ConstraintLayout courseInfo;
	private RecyclerView courseAssessmentsView;
	private Boolean calenderShowing = false;
	private AssessmentRecyclerViewAdapter assessmentsAdapter;
	private TextView courseID;
	private TextView textCourseTerm;
	private TextView textCourseStatus;
	private TextView assessmentsInCourseLabel;
	private RecyclerView assessmentsInCourseRecyclerView;
	private RecyclerView.LayoutManager recyclerLayoutManager;
	private AssessmentRecyclerViewAdapter adapter;
	private TextView labelCourseNumber;
	private TextView labelCourseName;
	private TextView labelCourseStartDate;
	private TextView labelCourseEndDate;
	private TextView labelCourseMentorName;
	private TextView labelCourseMentorPhone;
	private TextView labelCourseMentorEmail;



	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setTitle("Add Course");

		toggleEditable();
	}

	private void toggleEditable()
	{
		courseStartDate = findViewById(R.id.inputCourseStartDate);
		courseEndDate = findViewById(R.id.inputCourseEndDate);
		courseMentorName = findViewById(R.id.inputMentorName);
		courseMentorPhone = findViewById(R.id.inputMentorPhone);
		courseMentorEmail = findViewById(R.id.inputMentorEmail);
		courseNotes = findViewById(R.id.inputCourseNotes);
		ImageView courseStartCalender = findViewById(R.id.imgCourseStartCalander);
		ImageView courseEndCalender = findViewById(R.id.imgCourseEndCalander);
		courseInfo = findViewById(R.id.courseInfo);
		courseID = findViewById(R.id.courseId);
		courseID.setText("");
		courseNumber = findViewById(R.id.inputCourseNumber);
		courseName = findViewById(R.id.inputCourseName);
		textCourseTerm = findViewById(R.id.textCourseTerm);
		textCourseStatus = findViewById(R.id.textCourseStatus);
		courseTermSpinner = findViewById(R.id.spinnerTerms);
		courseStatusSpinner = findViewById(R.id.spinnerStatus);
		assessmentsInCourseLabel = findViewById(R.id.assessmentsInCourseLabel);
		assessmentsInCourseRecyclerView = findViewById(R.id.assessmentsInCourseRecyclerView);
		courseAssessmentsView = findViewById(R.id.assessmentsInCourseRecyclerView);
		labelCourseNumber = findViewById(R.id.labelCourseNumber);
		labelCourseName = findViewById(R.id.labelCourseName);
		labelCourseStartDate = findViewById(R.id.labelCourseStartDate);
		labelCourseEndDate = findViewById(R.id.labelCourseEndDate);
		labelCourseMentorName = findViewById(R.id.labelCourseMentorName);
		labelCourseMentorPhone = findViewById(R.id.labelCourseMentorPhone);
		labelCourseMentorEmail = findViewById(R.id.labelCourseMentorEmail);
		labelCourseNumber.setTextColor(Color.BLACK);
		labelCourseName.setTextColor(Color.BLACK);
		labelCourseStartDate.setTextColor(Color.BLACK);
		labelCourseEndDate.setTextColor(Color.BLACK);


		if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.VIEW))
		{

			recyclerLayoutManager = new LinearLayoutManager(this);
			assessmentsInCourseRecyclerView.setLayoutManager(recyclerLayoutManager);
			adapter = new AssessmentRecyclerViewAdapter(this, DatabaseHandler.getInstance().getAllAssessmentsForCourse(
					getIntent().getStringExtra(Contracts.CourseEntry._ID)));
			assessmentsInCourseRecyclerView.setAdapter(adapter);

			getSupportActionBar().setTitle("View Course");

			DatabaseHandler.CourseObject course = DatabaseHandler.getInstance().getCourseById(getIntent().getStringExtra(Contracts.CourseEntry._ID));
			if (!course.getCourseStatus().equals(getResources().getStringArray(R.array.course_status)[0]))
			{
				courseInfo.setVisibility(View.VISIBLE);
			}
			else
			{
				courseInfo.setVisibility(View.GONE);
			}
			courseID.setText(getIntent().getStringExtra(Contracts.CourseEntry._ID));
			courseNumber.setText(course.getCourseNumber());
			courseNumber.setEnabled(false);
			courseName.setText(course.getCourseName());
			courseName.setEnabled(false);
			textCourseTerm.setText(course.getCourseTerm());
			textCourseStatus.setText(course.getCourseStatus());
			courseStartDate.setText(course.getCourseStartDate());
			courseEndDate.setText(course.getCourseEndDate());
			courseMentorName.setText(course.getCourseMentorName());
			courseMentorPhone.setText(course.getCourseMentorPhone());
			courseMentorEmail.setText(course.getCourseMentorEmail());
			courseNotes.setText(course.getCourseNotes());

			assessmentsInCourseLabel.setVisibility(View.VISIBLE);
			assessmentsInCourseRecyclerView.setVisibility(View.VISIBLE);
			textCourseStatus.setVisibility(View.VISIBLE);
			textCourseTerm.setVisibility(View.VISIBLE);
			courseTermSpinner.setVisibility(View.INVISIBLE);
			courseStatusSpinner.setVisibility(View.INVISIBLE);
		}
		else
		{


			textCourseStatus.setVisibility(View.INVISIBLE);
			textCourseTerm.setVisibility(View.INVISIBLE);
			courseTermSpinner.setVisibility(View.VISIBLE);
			courseStatusSpinner.setVisibility(View.VISIBLE);
			assessmentsInCourseLabel.setVisibility(View.INVISIBLE);
			assessmentsInCourseRecyclerView.setVisibility(View.INVISIBLE);
			courseName.setEnabled(true);
			courseNumber.setEnabled(true);

			if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.EDIT))
			{
				getSupportActionBar().setTitle("Edit Course");

				final DatabaseHandler.CourseObject course =
						DatabaseHandler.getInstance().getCourseById(getIntent().getStringExtra(Contracts.CourseEntry._ID));

				courseID.setText(getIntent().getStringExtra(Contracts.CourseEntry._ID));
				courseNumber.setText(course.getCourseNumber());
				courseName.setText(course.getCourseName());
				courseTermSpinner.post(new Runnable()
				{
					@Override
					public void run()
					{
						courseTermSpinner.setSelection(DatabaseHandler.getInstance().getTermPositionInArray(course.getTermId()));
					}
				});
				String[] statuses = getResources().getStringArray(R.array.course_status);
				for (int i = 0 ; i < statuses.length ; i++)
				{
					if (course.getCourseStatus().equals(statuses[i]))
					{
						final int index = i;
						courseStatusSpinner.post(new Runnable()
						{
							@Override
							public void run()
							{
								courseStatusSpinner.setSelection(index);
							}
						});
					}
					break;
				}

				courseStartDate.setText(course.getCourseStartDate());
				courseEndDate.setText(course.getCourseEndDate());
				courseMentorName.setText(course.getCourseMentorName());
				courseMentorPhone.setText(course.getCourseMentorPhone());
				courseMentorEmail.setText(course.getCourseMentorEmail());

			}
			else
			{
				getSupportActionBar().setTitle("Add Course");
			}

			populateTermsList();

			final ConstraintLayout layoutCalendar = findViewById(R.id.layoutCalendar);

			final CalendarView courseCalendar = findViewById(R.id.termsCalendarView);
			courseCalendar.setMinDate(Calendar.getInstance().getTimeInMillis());
			courseCalendar.setDate(Calendar.getInstance().getTimeInMillis());

			final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
			Date date = Calendar.getInstance().getTime();
			selectedDate = dateFormatter.format(date);

			courseCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
			{
				@Override
				public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth)
				{
					selectedDate = "";
					selectedDate =
							(String.valueOf(month + 1)).concat("/").concat(String.valueOf(dayOfMonth)).concat("/").concat(String.valueOf(year));
					Log.i("Date", selectedDate);
				}
			});
			Button okButton = findViewById(R.id.termsOkButton);
			Button cancelButton = findViewById(R.id.termsCancelButton);

			okButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if (dateClicked.equals("start"))
					{
						courseStartDate.setText(selectedDate);
					}
					if (dateClicked.equals("end"))
					{
						courseEndDate.setText(selectedDate);
					}
					layoutCalendar.setVisibility(View.GONE);
					toggleOptionsMenu();
				}
			});

			cancelButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					courseCalendar.setDate(courseCalendar.getMinDate());
					layoutCalendar.setVisibility(View.GONE);
					toggleOptionsMenu();
				}
			});

			courseStatusSpinner.setOnTouchListener(new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					hideKeyboard();
					return false;
				}
			});

			courseTermSpinner.setOnTouchListener(new View.OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					hideKeyboard();
					return false;
				}
			});

			courseStartCalender.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					toggleOptionsMenu();
					dateClicked = "start";
					hideKeyboard();
					layoutCalendar.setVisibility(View.VISIBLE);
				}
			});

			courseEndCalender.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					toggleOptionsMenu();
					dateClicked = "end";
					hideKeyboard();
					layoutCalendar.setVisibility(View.VISIBLE);
				}
			});


			courseStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
				{
					if (position > 0)
					{
						courseInfo.setVisibility(View.VISIBLE);

					}
					else
					{
						courseInfo.setVisibility(View.GONE);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{

				}
			});

			courseTermSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
				{
					if (position == courseTermSpinner.getCount() - 1)
					{
						Intent intent = new Intent(CourseActivity.this, TermActivity.class);
						intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.ADD);
						startActivity(intent);

					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{

				}
			});

		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (courseTermSpinner.getCount() == DatabaseHandler.getInstance().getAllTerms().size())
		{
			populateTermsList();
			courseTermSpinner.post(new Runnable()
			{
				@Override
				public void run()
				{
					courseTermSpinner.setSelection(0);
				}
			});
		}
		//assessmentsAdapter.swapCursor(getAllAssessments());
	}

	private void addItem()
	{
		boolean noDataErrorsPresent = true;

		if (courseNumber.getText().toString().trim().isEmpty())
		{
			noDataErrorsPresent = false;
			labelCourseNumber.setTextColor(Color.RED);

			Toast.makeText(this, "Course Number cannot be blank",
						   Toast.LENGTH_LONG).show();
		}
		else
		{
			labelCourseNumber.setTextColor(Color.BLACK);
		}

		if (courseName.getText().toString().trim().isEmpty())
		{
			noDataErrorsPresent = false;
			labelCourseName.setTextColor(Color.RED);

			Toast.makeText(this, "Course Name cannot be blank",
						   Toast.LENGTH_LONG).show();
		}
		else
		{
			labelCourseName.setTextColor(Color.BLACK);
		}

		if (courseStatusSpinner.getSelectedItemPosition() > 0)
		{


			if (courseStartDate.getText().toString().trim().isEmpty())
			{
				noDataErrorsPresent = false;
				labelCourseStartDate.setTextColor(Color.RED);
				Toast.makeText(this, "Start Date cannot be blank",
							   Toast.LENGTH_LONG).show();
			}
			else
			{
				labelCourseStartDate.setTextColor(Color.BLACK);
			}

			if (courseEndDate.getText().toString().trim().isEmpty())
			{
				noDataErrorsPresent = false;
				labelCourseEndDate.setTextColor(Color.RED);
				Toast.makeText(this, "End Date cannot be blank",
							   Toast.LENGTH_LONG).show();
			}
			else
			{
				labelCourseEndDate.setTextColor(Color.BLACK);
			}

			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

			try
			{
				if (dateFormatter.parse(courseStartDate.getText().toString()).after(dateFormatter.parse(courseEndDate.getText().toString())))
				{
					noDataErrorsPresent = false;



					labelCourseStartDate.setTextColor(Color.RED);
					labelCourseEndDate.setTextColor(Color.RED);

					Toast.makeText(this, "Start Date must be before or equal to End Date",
								   Toast.LENGTH_LONG).show();
				}
				else
				{
					labelCourseStartDate.setTextColor(Color.BLACK);
					labelCourseEndDate.setTextColor(Color.BLACK);
				}
			}
			catch (ParseException e)
			{
				noDataErrorsPresent = false;
			}
		}

		if (noDataErrorsPresent)
		{

			DatabaseHandler.CourseObject object = DatabaseHandler.getInstance()
					.new CourseObject(courseID.getText().toString(), courseNumber.getText().toString(),
									  courseName.getText().toString(),
									  courseTermSpinner.getSelectedItemPosition(),
									  courseStatusSpinner.getSelectedItem().toString(),
									  courseStartDate.getText().toString(),
									  courseEndDate.getText().toString(),
									  courseMentorName.getText().toString(),
									  courseMentorPhone.getText().toString(),
									  courseMentorEmail.getText().toString(),
									  courseNotes.getText().toString());

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


	}

	private void hideKeyboard()
	{

		View view = this.getCurrentFocus();
		if (view != null)
		{
			view.clearFocus();
			InputMethodManager inputManager =
					(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.VIEW))
		{
			getMenuInflater().inflate(R.menu.view, menu);
			menu.findItem(R.id.action_share).setVisible(true);

			return true;
		}

		if (calenderShowing == false)
		{
			getMenuInflater().inflate(R.menu.add, menu);

		}

		return true;
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
				String courseHasAssessment = Validations.courseContainingAssessment(courseID.getText().toString());
				if (courseHasAssessment == null)
				{
					DatabaseHandler.getInstance().delete(Contracts.CourseEntry.TABLE_NAME, Integer.parseInt(courseID.getText().toString()));
					finish();
				}
				else
				{
					Toast.makeText(this, "This course cannot be deleted because it contains assessment \"" + courseHasAssessment + "\"",
								   Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.action_share:
				Intent smsIntent = new Intent(Intent.ACTION_SEND);
				smsIntent.setType("text/plain");
				smsIntent.putExtra("sms_body", "These are my notes for Course \"".concat(courseNumber.getText().toString()).concat("\":\n\n")
																				 .concat(courseNotes.getText().toString()));
				smsIntent.putExtra(Intent.EXTRA_SUBJECT, "Notes for Course \"".concat(courseNumber.getText().toString()).concat("\""));
				smsIntent.putExtra(Intent.EXTRA_TEXT, "These are my notes for Course \"".concat(courseNumber.getText().toString()).concat("\":\n\n")
																						.concat(courseNotes.getText().toString()).concat("\n\n"));
				startActivity(Intent.createChooser(smsIntent, "Choose an E-Mail or SMS program"));

				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void populateTermsList()
	{

		ArrayList<String> terms = DatabaseHandler.getInstance().getTermsNameList();
		termsList.clear();
		termsList.addAll(terms);
		if (termsList.size() == 0)
		{
			termsList.add("No Terms available");
		}
		termsList.add("Add new term");


		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, termsList);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		courseTermSpinner.setAdapter(spinnerAdapter);
		ArrayAdapter<CharSequence> adapter =
				ArrayAdapter.createFromResource(this, R.array.course_status, android.R.layout.simple_spinner_dropdown_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		courseStatusSpinner.setAdapter(adapter);
	}

	private void toggleOptionsMenu()
	{
		if (calenderShowing == true)
		{
			calenderShowing = false;
			invalidateOptionsMenu();
		}
		else
		{
			calenderShowing = true;
			invalidateOptionsMenu();
		}
	}

	@Override
	public void onBackPressed()
	{

		finish();
	}
}
