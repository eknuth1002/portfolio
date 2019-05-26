package com.example.elliotknuth.c196final;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import static android.content.Context.NOTIFICATION_SERVICE;

public class Receiver extends BroadcastReceiver
{
	static int notificationID = 0;
	final String channel_id = "c196finalNotificationChannel";
	final String group = "C196_Notifications";

	@Override
	public void onReceive(Context context, Intent intent)
	{

		createNotificationChannel(context, channel_id);

		Intent pushIntent = new Intent(context, MainActivity.class);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addNextIntentWithParentStack(pushIntent);
		pushIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		//Create notification
		NotificationCompat.Builder n = new NotificationCompat.Builder(context, channel_id).setSmallIcon(R.drawable.ic_notification)
																						  .setContentText(intent.getStringExtra("text"))
																						  .setContentTitle(intent.getStringExtra("title"))
																						  .setAutoCancel(true).setGroup(group);

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

		//Push notification into channel
		notificationManager.notify(intent.getIntExtra("id", -1), n.build());

		//gets notifications in channel notificationManager.getActiveNotifications()[0].getId();

	}

	//Creates an Oreo Notification Channel, requires API 26
	private void createNotificationChannel(Context context, String CHANNEL_ID)
	{

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			CharSequence name = context.getResources().getString(R.string.channel_name);
			String description = context.getString(R.string.channel_description);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;

			//Set channel
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);

			NotificationManager notificationManager =
					context.getSystemService(NotificationManager.class);

			//Register channel
			notificationManager.createNotificationChannel(channel);
		}
	}
}
