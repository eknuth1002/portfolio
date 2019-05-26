package com.example.elliotknuth.c196final;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

public class NotificationAlarmManager extends AppCompatActivity
{
	/* Use to create alarms
	 NotificationAlarmManager.createAlarm(this, "Title ", "Text ", IntegerIDNumber,
	 IntegerTimeUntilAlarm);
	 */

	//make class non-instantiable
	private NotificationAlarmManager(Context caller)
	{
	}

	public static void createAlarm(Context context, String title, String text, int id,
								   long millisecondsUntilAlarm)
	{
		Intent intent = new Intent(context, Receiver.class);
		intent.putExtra("text", text);
		intent.putExtra("title", title);
		intent.putExtra("id", id);

		//Add notification to channel and get PendingIntent returned
		PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		//Add alarm
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

		alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ((millisecondsUntilAlarm / 1000) * 1000), sender);

	}

	public static void deleteAlarm(int id)
	{

		NotificationManager notificationManager =
				(NotificationManager) C196Final.getContext().getSystemService
						(NOTIFICATION_SERVICE);
		notificationManager.cancel(id);

	}
}
