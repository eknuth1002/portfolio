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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class TermActivity extends AppCompatActivity
{

	private EditText termNumber;
	private EditText termStartDate;
	private EditText termEndDate;
	private String dateClicked;
	private String selectedDate = "";
	private Boolean calenderShowing = false;
	private TextView termStartLabel;
	private TextView termEndLabel;
	private TextView termNumberLabel;
	private TextView termId;
	private TextView textTermStartDate;
	private TextView textTermEndDate;
	private ImageView termStartCalender;
	private ImageView termEndCalender;
	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager recyclerLayoutManager;
	private CourseRecyclerViewAdapter adapter;
	private TextView coursesInTermLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_term);

		termNumberLabel = findViewById(R.id.labelTermNumber);
		termNumber = findViewById(R.id.inputTermNumber);
		termStartDate = findViewById(R.id.inputTermStartDate);
		termStartLabel = findViewById(R.id.labelTermStartDate);
		termEndDate = findViewById(R.id.inputTermEndDate);
		termEndLabel = findViewById(R.id.labelTermEndDate);
		termId = findViewById(R.id.termId);
		termStartCalender = findViewById(R.id.imgTermStartDate);
		termEndCalender = findViewById(R.id.imgTermEndDate);
		coursesInTermLabel = findViewById(R.id.labelCoursesInTerm);

		toggleEditable();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void addItem()
	{
		if (!termStartDate.getText().toString().equals("") && !termEndDate.getText().toString().equals("") &&
			!termNumber.getText().toString().equals(""))
		{

			DatabaseHandler.TermObject object = DatabaseHandler.getInstance()
					.new TermObject(termId.getText().toString(), termNumber.getText().toString(), termStartDate.getText().toString(),
									termEndDate.getText().toString());


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

		else
		{
			if (termNumber.getText().toString().equals(""))
			{
				termNumberLabel.setTextColor(Color.RED);
			}
			else
			{
				termNumberLabel.setTextColor(Color.BLACK);
			}

			if (termStartDate.getText().toString().equals(""))
			{
				termStartLabel.setTextColor(Color.RED);
			}
			else
			{
				termStartLabel.setTextColor(Color.BLACK);
			}

			if (termEndDate.getText().toString().equals(""))
			{
				termEndLabel.setTextColor(Color.RED);
			}
			else
			{
				termEndLabel.setTextColor(Color.BLACK);
			}

			Toast.makeText(this, "Please fix the items in Red", Toast.LENGTH_LONG).show();
		}


	}

	private void toggleEditable()
	{
		Intent intent = getIntent();

		recyclerView = findViewById(R.id.coursesInTermRecyclerView);
		recyclerLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(recyclerLayoutManager);
		adapter = new CourseRecyclerViewAdapter(this, DatabaseHandler.getInstance().getCoursesInTerm(intent.getStringExtra(Contracts.TermEntry
																																   ._ID)));
		recyclerView.setAdapter(adapter);

		if (intent.getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.VIEW))
		{
			DatabaseHandler.TermObject term = DatabaseHandler.getInstance().getTermById(intent.getStringExtra(Contracts.TermEntry._ID));
			termId.setText(term.getId());
			termNumber.setEnabled(false);
			termNumber.setText(term.getTerm());
			termStartDate.setText(term.getStartDate());
			termEndDate.setText(term.getEndDate());
			termStartDate.setClickable(false);
			termEndDate.setClickable(false);
			termStartCalender.setVisibility(View.INVISIBLE);
			termEndCalender.setVisibility(View.INVISIBLE);
			recyclerView.setVisibility(View.VISIBLE);
			coursesInTermLabel.setVisibility(View.VISIBLE);
			getSupportActionBar().setTitle("View Term");
		}
		else
		{
			if (intent.getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.EDIT))
			{
				DatabaseHandler.TermObject term = DatabaseHandler.getInstance().getTermById(intent.getStringExtra(Contracts.TermEntry._ID));
				termId.setText(term.getId());
				termNumber.setEnabled(true);
				termNumber.setText(term.getTerm());
				termStartDate.setText(term.getStartDate());
				termEndDate.setText(term.getEndDate());

				getSupportActionBar().setTitle("Edit Term");
			}
			else
			{
				getSupportActionBar().setTitle("Add Term");
			}

			termStartCalender.setVisibility(View.VISIBLE);
			termEndCalender.setVisibility(View.VISIBLE);
			recyclerView.setVisibility(View.INVISIBLE);
			coursesInTermLabel.setVisibility(View.INVISIBLE);
			termStartDate.setClickable(true);
			termEndDate.setClickable(true);

			final ConstraintLayout layoutCalendar = findViewById(R.id.layoutTermsCalendar);
			final CalendarView courseCalendar = findViewById(R.id.termsCalendarView);
			courseCalendar.setMinDate(Calendar.getInstance().getTimeInMillis());

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
						termStartDate.setText(selectedDate);
					}
					if (dateClicked.equals("end"))
					{
						termEndDate.setText(selectedDate);
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
					layoutCalendar.setVisibility(View.GONE);
					toggleOptionsMenu();
				}
			});

			termStartCalender.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					hideKeyboard();
					dateClicked = "start";
					toggleOptionsMenu();
					layoutCalendar.setVisibility(View.VISIBLE);
				}
			});

			termEndCalender.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					hideKeyboard();
					dateClicked = "end";
					toggleOptionsMenu();
					layoutCalendar.setVisibility(View.VISIBLE);
				}
			});

			termStartDate.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					hideKeyboard();
					dateClicked = "start";
					toggleOptionsMenu();
					try
					{
						if (termStartDate.getText().toString().length() == 0)
						{
							courseCalendar.setDate(courseCalendar.getMinDate());
						}
						else
						{

							courseCalendar.setDate(dateFormatter.parse(termStartDate.getText().toString()).getTime());
						}
					}
					catch (ParseException e)
					{
						e.printStackTrace();
						courseCalendar.setDate(courseCalendar.getMinDate());
					}

					layoutCalendar.setVisibility(View.VISIBLE);
				}
			});

			termEndDate.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					hideKeyboard();
					dateClicked = "end";
					toggleOptionsMenu();
					try
					{
						if (termEndDate.getText().toString().length() == 0)
						{
							courseCalendar.setDate(courseCalendar.getMinDate());
						}
						else
						{

							courseCalendar.setDate(dateFormatter.parse(termEndDate.getText().toString()).getTime());
						}
					}
					catch (ParseException e)
					{
						e.printStackTrace();
						courseCalendar.setDate(courseCalendar.getMinDate());
					}

					layoutCalendar.setVisibility(View.VISIBLE);
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (getIntent().getStringExtra(Contracts.ACTIONS.action).equals(Contracts.ACTIONS.VIEW))
		{
			getMenuInflater().inflate(R.menu.view, menu);

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
					toggleOptionsMenu();
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
				String termInCourse = Validations.termExistsInCourse(termId.getText().toString());
				if (termInCourse == null)
				{
					DatabaseHandler.getInstance().delete(Contracts.TermEntry.TABLE_NAME, Integer.parseInt(termId.getText().toString()));
					finish();
				}
				else
				{
					Toast.makeText(this, "This term cannot be deleted because it is in course \"" + termInCourse + "\"", Toast.LENGTH_LONG).show();
				}
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void hideKeyboard()
	{

		View view = this.getCurrentFocus();
		if (view != null)
		{
			InputMethodManager inputManager =
					(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
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
}
