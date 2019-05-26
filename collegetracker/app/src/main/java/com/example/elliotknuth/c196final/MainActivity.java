/*
Name: Elliot Knuth
Project Name: C196 Final - School Tracker
Android Minimum Version: 24
Android Compile Version: 26
 */

package com.example.elliotknuth.c196final;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

	Button termsButton;
	Button coursesButton;
	Button assessmentsButton;
	Button alarmsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		DatabaseHandler.getInstance(this);


		new checkForDeletedAlarms().execute();


		termsButton = findViewById(R.id.termsButton);
		coursesButton = findViewById(R.id.coursesButton);
		assessmentsButton = findViewById(R.id.assessmentsButton);
		alarmsButton = findViewById(R.id.alarmsButton);

		termsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainActivity.this, TermListActivity.class);
				startActivity(intent);
			}
		});

		coursesButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainActivity.this, CoursesListActivity.class);
				startActivity(intent);
			}
		});

		assessmentsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainActivity.this, AssessmentsListActivity.class);
				startActivity(intent);
			}
		});

		alarmsButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
				startActivity(intent);
			}
		});

		//TODO Create RecyclerViews for each course and make a list.  OnClick, remove all other
		// views and add view to that term

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId())
		{
            case R.id.action_settings:
				Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
		}

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
    }

	private class checkForDeletedAlarms extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... voids)
		{
			ArrayList<DatabaseHandler.AlarmObject> alarms = DatabaseHandler.getInstance().getAllAlarms();
			if (alarms != null && alarms.size() > 0)
			{
				SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy h:mm a");

				for (DatabaseHandler.AlarmObject alarm : alarms)
				{
					try
					{
						Date alarmDateTime = dateFormatter.parse(alarm.getDate().concat(" ").concat(alarm.getTime()));
						if (alarmDateTime.before(Calendar.getInstance().getTime()))
						{
							DatabaseHandler.getInstance().delete(Contracts.AlarmEntry.TABLE_NAME, Integer.parseInt(alarm.getId()));
						}
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}


				}
			}

			return null;
		}
	}

}
