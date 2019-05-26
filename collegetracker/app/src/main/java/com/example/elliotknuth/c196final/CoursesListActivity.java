package com.example.elliotknuth.c196final;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class CoursesListActivity extends AppCompatActivity
{
	int num = 0;
	//TODO Add fields for input fields

	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager recyclerLayoutManager;
	private SQLiteDatabase database;
	private CourseRecyclerViewAdapter adapter;
	private FloatingActionButton fab;

	final DatabaseHandler handler = DatabaseHandler.getInstance(this);
	//final DBOpenHelper helper = new DBOpenHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_list);

		getSupportActionBar().setTitle("Courses");

		//	database = helper.getReadableDatabase();

		recyclerView = findViewById(R.id.recyclerCourses);
		recyclerLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(recyclerLayoutManager);
		adapter = new CourseRecyclerViewAdapter(this, DatabaseHandler.getInstance().getAllCourses());
		recyclerView.setAdapter(adapter);

		fab = findViewById(R.id.buttonAddCourse);

		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(CoursesListActivity.this, CourseActivity.class);
				intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.ADD);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		adapter.swapCursor(DatabaseHandler.getInstance().getAllCourses());
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

		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed()
	{
		finish();
	}

}