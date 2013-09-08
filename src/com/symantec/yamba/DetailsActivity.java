package com.symantec.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

public class DetailsActivity extends Activity {
	private DetailsFragment detailsFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (detailsFragment == null)
			detailsFragment = new DetailsFragment();

		getFragmentManager()
				.beginTransaction()
				.replace(android.R.id.content, detailsFragment,
						DetailsFragment.class.getSimpleName()).commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		long id = getIntent().getLongExtra("id", -1);

		detailsFragment.setId(id);
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
