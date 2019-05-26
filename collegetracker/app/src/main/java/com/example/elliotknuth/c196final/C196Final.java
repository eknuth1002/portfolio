package com.example.elliotknuth.c196final;

import android.app.Application;
import android.content.Context;

public class C196Final extends Application
{
	private static Application sApplication;

	public static Application getApplication()
	{
		return sApplication;
	}

	public static Context getContext()
	{
		return getApplication().getApplicationContext();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		sApplication = this;
	}
}
