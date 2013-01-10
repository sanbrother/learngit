package com.neosoft.demo.rtp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PriceHistoryDbHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Text.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME_PRICE_HISTORY = "price_history";
    private static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRICE_RECORD = "name";
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME_PRICE_HISTORY
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PRICE_RECORD + " TEXT)";
    
	public PriceHistoryDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
