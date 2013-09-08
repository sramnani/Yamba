package com.symantec.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getFragmentManager()
					.beginTransaction()
					.add(android.R.id.content, new SettingsFragment(),
							SettingsFragment.class.getSimpleName()).commit();
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
