package com.example.elliotknuth.c196final;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

public class AlarmListActivity extends AppCompatActivity
{
	int num = 0;
	//TODO Add fields for input fields

	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager recyclerLayoutManager;
	private AlarmRecyclerViewAdapter adapter;
	private FloatingActionButton fab;

	AlarmUpdateThread alarmThread = new AlarmUpdateThread();

	final DatabaseHandler handler = DatabaseHandler.getInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_list);

		getSupportActionBar().setTitle("Alarms");

		//database = helper.getReadableDatabase();

		recyclerView = findViewById(R.id.recyclerAlarm);
		fab = findViewById(R.id.buttonAddAlarm);

		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(AlarmListActivity.this, AlarmActivity.class);
				intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.ADD);
				startActivity(intent);
			}
		});

		recyclerLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(recyclerLayoutManager);
		adapter = new AlarmRecyclerViewAdapter(this, handler.getAllAlarms());
		recyclerView.setAdapter(adapter);

		alarmThread.execute();

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		//		if (!database.isOpen())
		//		{
		//			//database = helper.getReadableDatabase();
		//		}

		notifyOfDataChange();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		alarmThread.cancel(true);
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		//noinspection SimplifiableIfStatement
		switch (item.getItemId())
		{
			case android.R.id.home:
				return false;
		}

		return super.onOptionsItemSelected(item);
	}

	public void notifyOfDataChange()
	{
		adapter.updateCursor(handler.getAllAlarms());
	}

	@Override
	public void onBackPressed()
	{
		finish();
	}


	private class AlarmUpdateThread extends AsyncTask<Void, Void, Void>
	{
		boolean running = true;

		@Override
		protected Void doInBackground(Void... voids)
		{


			while (running)
			{
				CopyOnWriteArrayList<DatabaseHandler.AlarmObject> alarms = new CopyOnWriteArrayList<>();
				alarms.addAll(DatabaseHandler.getInstance().getAllAlarms());
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

				if (adapter.getItemCount() != DatabaseHandler.getInstance().getAllAlarms().size())
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							notifyOfDataChange();
						}
					});

				}

				try
				{
					Thread.sleep(30000);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onCancelled()
		{
			super.onCancelled();

			running = false;
		}
	}

}
