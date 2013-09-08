package com.symantec.yamba;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

public class YambaWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {

		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		int[] appWidgetIds = appWidgetManager
				.getAppWidgetIds(new ComponentName(context, YambaWidget.class));

		this.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		// Get the last tweet
		Cursor cursor = context.getContentResolver().query(
				StatusContract.CONTENT_URI, null, null, null,
				StatusContract.DEFAULT_SORT);

		if (cursor == null || !cursor.moveToFirst())
			return;

		// Cursor -> Strings
		String textUser = cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.USER));
		String textMessage = cursor.getString(cursor
				.getColumnIndex(StatusContract.Column.MESSAGE));
		long createdAt = cursor.getLong(cursor
				.getColumnIndex(StatusContract.Column.CREATED_AT));
		CharSequence relTime = DateUtils.getRelativeTimeSpanString(createdAt);

		// Create the view
		PendingIntent operation = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.list_item);
		views.setTextViewText(R.id.text_user, textUser);
		views.setTextViewText(R.id.text_message, textMessage);
		views.setTextViewText(R.id.text_createdAt, relTime);
		views.setOnClickPendingIntent(R.id.list_item, operation);

		// Update all instances of the widget
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
}
