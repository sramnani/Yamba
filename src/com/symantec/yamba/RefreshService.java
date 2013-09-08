package com.symantec.yamba;

import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class RefreshService extends IntentService {
	private static final String TAG = "RefreshService";

	public RefreshService() {
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	// Executes in a worker thread
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onStarted");

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String username = prefs.getString("username", "");
		String password = prefs.getString("password", "");

		// Check if username/password is set
		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
			Toast.makeText(this, "Please set your login info",
					Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, SettingsActivity.class)
					.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			return;
		}

		YambaClient yamba = new YambaClient(username, password);
		try {
			ContentValues values = new ContentValues();
			List<Status> timeline = yamba.getTimeline(20);
			int count = 0;
			for (Status status : timeline) {
				values.clear();
				values.put(StatusContract.Column.ID, status.getId());
				values.put(StatusContract.Column.USER, status.getUser());
				values.put(StatusContract.Column.MESSAGE, status.getMessage());
				values.put(StatusContract.Column.CREATED_AT, status
						.getCreatedAt().getTime());

				Uri uri = getContentResolver().insert(
						StatusContract.CONTENT_URI, values);

				if (uri != null) {
					count++;
				}

				Log.d(TAG,
						String.format("%s: %s", status.getUser(),
								status.getMessage()));
			}

			if (count > 0) {
				sendBroadcast(new Intent(StatusContract.ACTION_NEW_STATUS)
						.putExtra("count", count));
			}
		} catch (YambaClientException e) {
			Log.e(TAG, "Failed to fetch timeline", e);
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}

}
