package com.symantec.yamba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class StatusActivity extends Activity {
	private static final String TAG = "StatusActivity";
	private Button buttonTweet;
	private EditText editStatus;
	private TextView textCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.activity_status);

		buttonTweet = (Button) findViewById(R.id.button_tweet);
		editStatus = (EditText) findViewById(R.id.edit_status);
		textCount = (TextView) findViewById(R.id.text_count);

		editStatus.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				int count = 140 - editStatus.length();
				textCount.setText(Integer.toString(count));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		buttonTweet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String status = editStatus.getText().toString();
				new PostTask().execute(status);

				Log.d(TAG, "onClicked with status: " + status);
			}
		});
	}

	private final class PostTask extends AsyncTask<String, Void, String> {
		ProgressDialog dialog;

		// Runs on the UI thread prior to doInBackground()
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(StatusActivity.this, "Posting...",
					"Please wait");
		}

		// Executes on a non-UI thread
		@Override
		protected String doInBackground(String... params) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(StatusActivity.this);
			String username = prefs.getString("username", "");
			String password = prefs.getString("password", "");

			// Check if username/password is set
			if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
				startActivity(new Intent(StatusActivity.this,
						SettingsActivity.class));
				return "Please set your login info";
			}

			YambaClient yamba = new YambaClient(username, password);
			String message;
			try {
				yamba.postStatus(params[0]);
				message = "Successfully posted!";
			} catch (YambaClientException e) {
				message = "Failed to post";
				Log.e(TAG, message);
				e.printStackTrace();
			}
			return message;
		}

		// Executes on UI thread, after doInBackground()
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();

			Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class).addFlags(
					Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(
					Intent.FLAG_ACTIVITY_NEW_TASK));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
