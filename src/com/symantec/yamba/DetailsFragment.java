package com.symantec.yamba;

import android.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
	private TextView textUser, textMessage, textCreatedAt;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.list_item, null);

		textUser = (TextView) view.findViewById(R.id.text_user);
		textMessage = (TextView) view.findViewById(R.id.text_message);
		textCreatedAt = (TextView) view.findViewById(R.id.text_createdAt);

		setId(-1); // To clear defaults
		return view;
	}

	public void setId(long id) {
		if (id == -1) {
			textUser.setText("");
			textMessage.setText("");
			textCreatedAt.setText("");
			return;
		}
		
		// Get the data
		Uri uri = ContentUris.withAppendedId(StatusContract.CONTENT_URI, id);
		Cursor cursor = getActivity().getContentResolver().query(uri, null,
				null, null, null);

		// Update the view
		if (cursor.moveToFirst()) {
			textUser.setText(cursor.getString(cursor
					.getColumnIndex(StatusContract.Column.USER)));
			textMessage.setText(cursor.getString(cursor
					.getColumnIndex(StatusContract.Column.MESSAGE)));
			long createdAt = cursor.getLong(cursor
					.getColumnIndex(StatusContract.Column.CREATED_AT));
			textCreatedAt.setText(DateUtils
					.getRelativeTimeSpanString(createdAt));
		}
		Log.d("DetailsFragment", "setId: " + id);
	}
}
