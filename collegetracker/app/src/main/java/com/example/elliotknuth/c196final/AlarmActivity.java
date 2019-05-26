package com.example.elliotknuth.c196final;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TimePicker;
import android.widget.Toast;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class AlarmActivity extends AppCompatActivity
{

	private EditText alarmDate;
	private Spinner alarmItemSpinner;
	private Spinner alarmTypeSpinner;
	private TextView alarmTime;
	private String selectedDate = "";
	private Boolean calenderShowing = false;
	private TextView alarmItemLabel;
	private TextView alarmId;
	ArrayAdapter<String> typeSpinnerAdapter;
	ArrayAdapter<String> itemSpinnerAdapter;
	private TextView alarmDateLabel;
	private ImageView alarmTimeButton;
	private ImageView alarmDateButton;
	private TimePicker timePicker;
	private Button alarmDateOk;
	private Button alarmDateCancel;
	private Button alarmOk;
	private Button alarmCancel;
	private TextView alarmItemText;
	private TextView alarmTypeText;
	private TextView alarmTimeLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		alarmItemLabel = findViewById(R.id.labelAlarmItem);
		alarmId = findViewById(R.id.alarmId);
		alarmDate = findViewById(R.id.inputAlarmDate);
		alarmDateButton = findViewById(R.id.imgAlarmDate);
		alarmDateLabel = findViewById(R.id.labelAlarmDate);
		alarmItemSpinner = findViewById(R.id.inputItemSpinner);
		alarmTypeSpinner = findViewById(R.id.inputAlarmSpinnerType);
		alarmTime = findViewById(R.id.inputAlarmTime);
		alarmTimeLabel = findViewById(R.id.labelAlarmTime);
		alarmTimeButton = findViewById(R.id.imgAlarmTime);
		timePicker = findViewById(R.id.alarmTimePicker);
		alarmDateOk = findViewById(R.id.alarmOkButton);
		alarmDateCancel = findViewById(R.id.alarmCancelButton);
		alarmOk = findViewById(R.id.alarmTimeOkButton);
		alarmCancel = findViewById(R.id.alarmTimeCancelButton);
		alarmItemText = findViewById(R.id.textAlarmSpinnerItem);
		alarmTypeText = findViewById(R.id.textAlarmSpinnerType);

		final ConstraintLayout alarmTimePicker = findViewById(R.id.layoutAlarmTime);

		alarmTimeButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				toggleOptionsMenu();
				alarmTimePicker.setVisibility(View.VISIBLE);
			}
		});

		alarmOk.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				StringBuilder string = new StringBuilder();

				String meridian = "";
				if (timePicker.getHour() > 12)
				{
					string.append(timePicker.getHour() - 12);
					meridian = " PM";
				}
				else if (timePicker.getHour() == 0)
				{
					string.append("12");
					meridian = " AM";
				}
				else
				{
					string.append(timePicker.getHour());
					meridian = " PM";
				}

				string.append(":");

				if (timePicker.getMinute() < 10)
				{
					string.append("0");
				}

				string.append(timePicker.getMinute());

				string.append(meridian);
				alarmTime.setText(string);

				toggleOptionsMenu();
				alarmTimePicker.setVisibility(View.GONE);
			}
		});

		alarmCancel.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				toggleOptionsMenu();
				alarmTimePicker.setVisibility(View.GONE);
			}
		});


		if (getIntent().getExtras() != null)
		{
			toggleEditable();

		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


	}

	private void toggleEditable()
	{
		Intent intent = getIntent();
		if (intent.getStringExtra(Contracts.ACTIONS.action).equals("view"))
		{


			alarmId.setText(intent.getStringExtra(Contracts.AlarmEntry._ID));

			alarmDate.setText(intent.getStringExtra(Contracts.AlarmEntry.COLUMN_DATE));

			DatabaseHandler.AlarmObject alarm = DatabaseHandler.getInstance().getAlarmById(alarmId.getText().toString());

			getSupportActionBar().setTitle("View Alarm");

			String temp = alarm.getType();
			alarmTypeText.setText(temp.substring(0, 1).toUpperCase().concat(temp.substring(1)));


			temp = alarm.getType() == "Term" ? DatabaseHandler.getInstance().getTermById(alarm.getItemId()).getTerm() :
					DatabaseHandler.getInstance().getCourseById(alarm.getItemId()).getCourseNumber();

			alarmItemText.setText(temp.substring(0, 1).toUpperCase().concat(temp.substring(1)));
			alarmItemLabel.setText(alarmTypeText.getText().toString());
			alarmDate.setText(alarm.getDate());
			alarmTime.setText(alarm.getTime());

			alarmTypeText.setVisibility(View.VISIBLE);
			alarmItemText.setVisibility(View.VISIBLE);
			alarmTypeSpinner.setVisibility(View.INVISIBLE);
			alarmItemSpinner.setVisibility(View.INVISIBLE);
			alarmTimeButton.setVisibility(View.INVISIBLE);
			alarmDateButton.setVisibility(View.INVISIBLE);

		}

		else
		{



			alarmTypeText.setVisibility(View.GONE);
			alarmItemText.setVisibility(View.GONE);
			alarmTypeSpinner.setVisibility(View.VISIBLE);
			alarmItemSpinner.setVisibility(View.VISIBLE);
			alarmTimeButton.setVisibility(View.VISIBLE);
			alarmDateButton.setVisibility(View.VISIBLE);

			setSpinnerData();

			typeSpinnerAdapter =
					new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
											 getApplicationContext().getResources().getStringArray(
													 R.array.alarm_types));
			typeSpinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			alarmTypeSpinner.setAdapter(typeSpinnerAdapter);

			alarmTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
			{
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
				{
					setSpinnerData();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{

				}
			});

			if (intent.getStringExtra(Contracts.ACTIONS.action).equals("edit"))
			{
				final DatabaseHandler.AlarmObject alarm = DatabaseHandler.getInstance().getAlarmById(alarmId.getText().toString());

				getSupportActionBar().setTitle("Edit Alarm");
				alarmId.setText(intent.getStringExtra(Contracts.AlarmEntry._ID));

				String[] alarmTypes = getResources().getStringArray(R.array.assessment_types);
				for (int i = 0 ; i < alarmTypes.length ; i++)
				{
					if (alarmTypes[i].equals(alarm.getType()))
					{
						final int index = i;

						alarmTypeSpinner.post(new Runnable()
						{
							@Override
							public void run()
							{
								alarmTypeSpinner.setSelection(index);
							}
						});
					}
				}
				alarmItemSpinner.post(new Runnable()
				{
					@Override
					public void run()
					{
						if (alarm.getType().equals("Term"))
						{
							alarmItemSpinner.setSelection(DatabaseHandler.getInstance().getTermPositionInArray(alarm.getItemId()));
						}
						else if (alarm.getType().equals("Course"))
						{
							alarmItemSpinner.setSelection(DatabaseHandler.getInstance().getCoursePositionInArray(alarm.getItemId()));
						}
						else if (alarm.getType().equals("Assessment"))
						{
							alarmItemSpinner.setSelection(DatabaseHandler.getInstance().getAssessmentPositionInArray(alarm.getItemId()));
						}
					}
				});
			}
			else
			{
				getSupportActionBar().setTitle("Add Alarm");
			}

			final ConstraintLayout layoutCalendar = findViewById(R.id.layoutAlarmCalendar);
			final CalendarView alarmCalendar = findViewById(R.id.alarmCalendarView);
			alarmCalendar.setMinDate(Calendar.getInstance().getTimeInMillis());


			final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
			Date date;
			try
			{
				if (!alarmId.getText().toString().equals(""))
				{
					date = dateFormatter.parse(DatabaseHandler.getInstance().getAlarmById(
							alarmId.getText().toString()).getDate());

					alarmCalendar.setMaxDate(date.getTime());
				}

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}

			date = Calendar.getInstance().getTime();
			selectedDate = dateFormatter.format(date);

			alarmCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener()
			{
				@Override
				public void onSelectedDayChange(@NonNull CalendarView view, int year, int month,
												int dayOfMonth)
				{
					selectedDate = "";
					selectedDate = (String.valueOf(month + 1)).concat("/")
															  .concat(String.valueOf(dayOfMonth))
															  .concat("/")
															  .concat(String.valueOf(year));
					Log.i("Date", selectedDate);
				}
			});
			alarmDateOk.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					alarmDate.setText(selectedDate);
					layoutCalendar.setVisibility(View.GONE);
					toggleOptionsMenu();
				}
			});

			alarmDateCancel.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					layoutCalendar.setVisibility(View.GONE);
					toggleOptionsMenu();
				}
			});

			alarmDateButton.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					hideKeyboard();
					toggleOptionsMenu();
					try
					{
						if (alarmDate.getText().toString().length() == 0)
						{
							alarmCalendar.setDate(alarmCalendar.getMinDate());
						}
						else
						{

							alarmCalendar.setDate(
									dateFormatter.parse(alarmDate.getText().toString()).getTime());
						}
					}
					catch (ParseException e)
					{
						e.printStackTrace();
						alarmCalendar.setDate(alarmCalendar.getMinDate());
					}

					layoutCalendar.setVisibility(View.VISIBLE);
				}
			});
		}
	}


	private void setSpinnerData()
	{
		if (alarmTypeSpinner.getSelectedItemPosition() == 0)
		{
			itemSpinnerAdapter =
					new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
											 DatabaseHandler.getInstance().getCoursesNameList());

			alarmItemLabel.setText("Course");
		}
		else if (alarmTypeSpinner.getSelectedItemPosition() == 2)
		{
			itemSpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,
														  DatabaseHandler.getInstance().getAssessmentsNameList());

			alarmItemLabel.setText("Assessment");
		}
		else
		{
			itemSpinnerAdapter =
					new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, DatabaseHandler.getInstance().getTermsNameList());

			alarmItemLabel.setText("Term");

		}
		itemSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		alarmItemSpinner.setAdapter(itemSpinnerAdapter);
	}

	private void addItem()
	{
		if (!alarmDate.getText().toString().equals("") && !alarmTime.getText().toString().equals(""))
		{
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");
			try
			{

				Date alarmDateTime = dateFormatter.parse(alarmDate.getText().toString().concat(" ").concat(alarmTime.getText().toString()));

				if (alarmDateTime.before(Calendar.getInstance().getTime()))
				{
					alarmTimeLabel.setTextColor(Color.RED);
					Toast.makeText(this, "The time must be a future time", Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
					alarmTimeLabel.setTextColor(Color.BLACK);
				}

				String itemDate = "";
				if (alarmTypeSpinner.getSelectedItem().toString().equals("Course"))
				{
					itemDate = DatabaseHandler.getInstance().getCourseByPosition(alarmItemSpinner.getSelectedItemPosition()).getCourseEndDate();
				}
				else if (alarmTypeSpinner.getSelectedItem().toString().equals("Term"))
				{
					itemDate = DatabaseHandler.getInstance().getTermByPosition(alarmItemSpinner.getSelectedItemPosition()).getEndDate();
				}
				else if (alarmTypeSpinner.getSelectedItem().toString().equals("Assessment"))
				{
					itemDate = DatabaseHandler.getInstance().getCourseByPosition(alarmItemSpinner.getSelectedItemPosition()).getCourseEndDate();
				}

				if (alarmTypeSpinner.getSelectedItem().toString().equals("Course"))
				{
					if (DatabaseHandler.getInstance().getCourseByPosition(alarmItemSpinner.getSelectedItemPosition()).getCourseEndDate().equals(""))
					{
						Toast.makeText(this, "This course does not have an end date", Toast.LENGTH_LONG).show();
						return;
					}
				}

				else if (alarmTypeSpinner.getSelectedItem().toString().equals("Assessment"))
				{
					if (DatabaseHandler.getInstance().getCourseByPosition(alarmItemSpinner.getSelectedItemPosition()).getCourseEndDate().equals(""))
					{
						Toast.makeText(this, "The course with this assessment does not have an end date", Toast.LENGTH_LONG).show();
						return;
					}
				}

				dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
				//TODO add date parser in MM/DD/YYYY format
				if (dateFormatter.parse(alarmDate.getText().toString()).after(dateFormatter.parse(itemDate)))
				{
					alarmDateLabel.setTextColor(Color.RED);
					Toast.makeText(this,
								   "The alarm date cannot be past the " + alarmTypeSpinner.getSelectedItem().toString().toLowerCase() + "end date",
								   Toast.LENGTH_LONG).show();
					return;
				}
				else
				{
					alarmDateLabel.setTextColor(Color.BLACK);
				}

				DatabaseHandler.AlarmObject object;

				if (alarmTypeSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.alarm_types)[0]))
				{
					object = DatabaseHandler.getInstance()
							.new AlarmObject(alarmId.getText().toString(),
											 DatabaseHandler.getInstance().getCourseByPosition(alarmItemSpinner.getSelectedItemPosition()).getId(),
											 alarmTypeSpinner.getSelectedItem().toString(), alarmDate.getText().toString(),
											 alarmTime.getText().toString());

				}
				else if (alarmTypeSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.alarm_types)[1]))
				{
					object = DatabaseHandler.getInstance()
							.new AlarmObject(alarmId.getText().toString(),
											 DatabaseHandler.getInstance().getTermByPosition(alarmItemSpinner.getSelectedItemPosition()).getId(),
											 alarmTypeSpinner.getSelectedItem().toString(), alarmDate.getText().toString(),
											 alarmTime.getText().toString());

				}
				else
				{
					object = DatabaseHandler.getInstance()
							.new AlarmObject(alarmId.getText().toString(),
											 DatabaseHandler.getInstance().getAssessmentByPosition(alarmItemSpinner.getSelectedItemPosition())
															.getId(), alarmTypeSpinner.getSelectedItem().toString(), alarmDate.getText().toString(),
											 alarmTime.getText().toString());

				}


				if (getIntent().getStringExtra(Contracts.ACTIONS.action) == Contracts.ACTIONS.EDIT)
				{
					Intent intent = new Intent();
					intent.putExtra(Contracts.AlarmEntry._ID, alarmId.getText().toString());
					setResult(1, intent);
					DatabaseHandler.getInstance().update(object);

				}
				else
				{
					DatabaseHandler.getInstance().insert(object);
				}

			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}

			finish();
		}
		else
		{
			alarmDateLabel.setTextColor(Color.RED);

			Toast.makeText(this, "Please fix the items in Red", Toast.LENGTH_LONG).show();
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
					toggleEditable();
					invalidateOptionsMenu();
					return true;
				}
				return false;
			case R.id.action_ok:
				addItem();
				break;
			case R.id.action_edit:
				getIntent().putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.EDIT);
				toggleEditable();
				invalidateOptionsMenu();
				break;
			case R.id.action_delete:
				DatabaseHandler.getInstance().delete(Contracts.AlarmEntry.TABLE_NAME, Integer.parseInt(alarmId.getText().toString()));
				finish();
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		finish();
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