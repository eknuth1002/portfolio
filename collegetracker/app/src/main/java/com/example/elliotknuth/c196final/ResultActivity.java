package com.example.elliotknuth.c196final;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

public class ResultActivity extends Activity
{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);


		//DatabaseHandler.getInstance().delete(Contracts.AlarmEntry.TABLE_NAME, Integer.parseInt(getActivity().getIntent().getStringExtra("id")));

		finish();

	}

}
