package com.symantec.yamba;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class StatusProvider extends ContentProvider {
	private static final String TAG = "StatusProvider";
	private static final UriMatcher MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		MATCHER.addURI(StatusContract.AUTHORITY, StatusContract.TABLE,
				StatusContract.STATUS_DIR);
		MATCHER.addURI(StatusContract.AUTHORITY, StatusContract.TABLE + "/#",
				StatusContract.STATUS_ITEM);
	}

	private DbHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = new DbHelper(getContext());
		return (dbHelper == null) ? false : true;
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case StatusContract.STATUS_DIR:
			return StatusContract.STATUS_TYPE_DIR;
		case StatusContract.STATUS_ITEM:
			return StatusContract.STATUS_TYPE_ITEM;
		default:
			throw new IllegalArgumentException("Invalid uri: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// Assert valid uri
		if (MATCHER.match(uri) != StatusContract.STATUS_DIR) {
			throw new IllegalArgumentException("Invalid uri: " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		long rowId = db.insertWithOnConflict(StatusContract.TABLE, null,
				values, SQLiteDatabase.CONFLICT_IGNORE);
		if (rowId > 0) {
			Uri ret = ContentUris.withAppendedId(uri,
					values.getAsLong(StatusContract.Column.ID));
			getContext().getContentResolver().notifyChange(ret, null);
			Log.d(TAG, "inserted uri: " + ret + " for rowId: " + rowId);
			return ret;
		} else {
			// Duplicate
			return null;
		}

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	// uri: /status/47 selection: " user='?' " selectionArgs: "bob"
	// where: WHERE id=47 AND user='bob'
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		String where;
		switch (MATCHER.match(uri)) {
		case StatusContract.STATUS_ITEM:
			long id = ContentUris.parseId(uri);
			where = String.format(" %s=%d ", StatusContract.Column.ID, id);
			if (!TextUtils.isEmpty(selection)) {
				where = where + " AND " + selection;
			}
			break;
		case StatusContract.STATUS_DIR:
			where = selection;
			break;
		default:
			throw new IllegalArgumentException("Invalid uri: " + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int rows = db.delete(StatusContract.TABLE, where, selectionArgs);
		getContext().getContentResolver().notifyChange(uri, null);
		Log.d(TAG, "deleted rows: " + rows);
		return rows;
	}

	// SELECT user, message FROM status WHERE id=47 AND user='bob' ORDER BY
	// created_at DESC;535
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(StatusContract.TABLE);

		switch (MATCHER.match(uri)) {
		case StatusContract.STATUS_ITEM:
			queryBuilder.appendWhere(StatusContract.Column.ID + "="
					+ ContentUris.parseId(uri));
			break;
		case StatusContract.STATUS_DIR:
			break;
		default:
			throw new IllegalArgumentException("Invalid uri: " + uri);
		}

		if (TextUtils.isEmpty(sortOrder)) {
			sortOrder = StatusContract.DEFAULT_SORT;
		}

		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		Log.d(TAG, "queried rows: " + cursor.getCount());
		return cursor;
	}

}
