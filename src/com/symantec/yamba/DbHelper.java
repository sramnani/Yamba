package com.symantec.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context) {
		super(context, StatusContract.DB_NAME, null, StatusContract.DB_VERSION);
	}

	// Happens only once, first time when the database doesn't even exist
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = String
				.format("create table %s ( %s int primary key,"
						+ " %s text, %s text, %s int )", StatusContract.TABLE,
						StatusContract.Column.ID, StatusContract.Column.USER,
						StatusContract.Column.MESSAGE,
						StatusContract.Column.CREATED_AT);

		Log.d("DbHelper", "onCreate sql: " + sql);
		db.execSQL(sql);
	}

	// Happens whenever currentVersion != newVersion
	// Typically runs alter table ()
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// For development purposes
		db.execSQL("drop table is exists "+StatusContract.TABLE);
		this.onCreate(db);
	}

}
