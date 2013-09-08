package com.symantec.yamba;

import android.app.ListFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineFragment extends ListFragment {
	private static final String[] FROM = { StatusContract.Column.USER,
			StatusContract.Column.MESSAGE, StatusContract.Column.CREATED_AT };
	private static final int[] TO = { R.id.text_user, R.id.text_message,
			R.id.text_createdAt };
	private static final int LOADER_ID = 42;
	private SimpleCursorAdapter adapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item,
				null, FROM, TO, 0);
		adapter.setViewBinder(VIEW_BINDER);

		setListAdapter(adapter);

		getLoaderManager().initLoader(LOADER_ID, null, loaderCallbacks);
	}

	private final LoaderCallbacks<Cursor> loaderCallbacks = new LoaderCallbacks<Cursor>() {

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			if (id != LOADER_ID)
				return null;

			return new CursorLoader(getActivity(), StatusContract.CONTENT_URI,
					null, null, null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			adapter.swapCursor(cursor);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			adapter.swapCursor(null);
		}

	};

	private static final ViewBinder VIEW_BINDER = new ViewBinder() {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (view.getId() != R.id.text_createdAt)
				return false;

			// Custom binding for timestamp
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils
					.getRelativeTimeSpanString(timestamp);
			((TextView) view).setText(relTime);

			return true;
		}

	};

	public void onListItemClick(ListView l, View v, int position, long id) {
		DetailsFragment detailsFragment = (DetailsFragment) getFragmentManager()
				.findFragmentById(R.id.fragment_details);
		if (detailsFragment != null && detailsFragment.isVisible()) {
			detailsFragment.setId(id);
		} else {
			startActivity(new Intent(getActivity(), DetailsActivity.class)
					.putExtra("id", id));
		}
	};
}
