package com.symantec.yamba;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
	public static final int NOTIFICATION_ID = 42;

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		int count = intent.getIntExtra("count", 0);

		// --- Notification ---
		String message = String.format("You have %d new tweet", count);
		message = (count > 0) ? message + "s" : message;

		PendingIntent operation = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		// Create a new Notification object using Notification.Builder
		Notification.Builder builder = new Notification.Builder(context)
				.setContentTitle("New status")
				.setContentText(message)
				.setSmallIcon(android.R.drawable.stat_notify_chat)
				.setContentIntent(operation).setAutoCancel(true);

		Notification notification;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			notification = builder.build();
		} else {
			notification = builder.getNotification();
		}

		// Get NotificationManager
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Notify!
		notificationManager.notify(NOTIFICATION_ID, notification);
		
		// --- Vibrate ---
		Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		if(vibrator.hasVibrator()) {
			vibrator.vibrate(1000);
		}

		Log.d("NotificationReceiver", "onReceived with count: " + count);
	}
}
