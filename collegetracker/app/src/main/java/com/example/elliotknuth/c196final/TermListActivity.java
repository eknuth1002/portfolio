package com.example.elliotknuth.c196final;

import android.content.ContentValues;
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

public class TermListActivity extends AppCompatActivity
{
	int num = 0;
	//TODO Add fields for input fields

	private RecyclerView recyclerView;
	private RecyclerView.LayoutManager recyclerLayoutManager;
	private SQLiteDatabase database;
	private TermRecyclerViewAdapter adapter;
	private FloatingActionButton fab;

	final DatabaseHandler handler = DatabaseHandler.getInstance(this);
	//final DBOpenHelper helper = new DBOpenHelper(this);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_term_list);

		getSupportActionBar().setTitle("Terms");

		//database = helper.getReadableDatabase();

		recyclerView = findViewById(R.id.recyclerTerms);
		fab = findViewById(R.id.buttonAddTerm);

		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TermListActivity.this, TermActivity.class);
				intent.putExtra(Contracts.ACTIONS.action, Contracts.ACTIONS.ADD);
				startActivity(intent);
			}
		});

		recyclerLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(recyclerLayoutManager);
		adapter = new TermRecyclerViewAdapter(this, handler.getAllTerms());
		recyclerView.setAdapter(adapter);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		adapter.swapCursor(handler.getAllTerms());
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

	public void addItem(ContentValues cv)
	{

		//database.insert(cv.get("tableName").toString(), null, cv);
		adapter.swapCursor(handler.getAllTerms());

	}

	public void notifyOfDataChange()
	{
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed()
	{
		finish();
	}

}
