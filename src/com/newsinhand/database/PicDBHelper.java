package com.newsinhand.database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PicDBHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME="nav.db";
	private static int DATABASE_VERSION=1;

	public PicDBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE IF NOT EXISTS nav"
				+ "(title TEXT PRIMARY KEY,image TEXT,link TEXT,date TEXT,store TEXT)");
	}

	// 如果DATABASE_VERSION值被改为2,系统发现现有数据库版本不同,即会调用onUpgrade
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS nav");
		onCreate(db);
	}

}

