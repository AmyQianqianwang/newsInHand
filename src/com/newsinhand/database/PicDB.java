package com.newsinhand.database;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.newsinhand.database.PicDBHelper;
import com.newsinhand.definition.Pic;

public class PicDB {
	private Context context;
	SQLiteDatabase db;
	PicDBHelper dbhelper;

	public PicDB(Context context) {
		this.context = context;
	}

	public void insert(List<Pic> pic) {
		dbhelper = new PicDBHelper(context);
		db = dbhelper.getReadableDatabase();
		for(int i = 0; i <pic.size(); i++) {
			final Pic ns =pic.get(i);
			db.execSQL(
					"insert into nav(title,image,link,date,store) values(?,?,?,?,?)",
					new Object[] { ns.getTitle(), ns.getImage(), ns.getLink(), ns.getDate(),
							ns.getStore()});
		}
		db.close();
	}
	public List<Pic> check() {
		List<Pic> pic = new ArrayList<Pic>();
		dbhelper = new PicDBHelper(context);
		db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select *from nav order by date desc", null);
		while (cursor.moveToNext()) {
			Pic ns = new Pic();
			ns.setTitle(cursor.getString(0));
			ns.setImage(cursor.getString(1));
			ns.setLink(cursor.getString(2));
			ns.setDate(cursor.getString(3));
			ns.setStore(cursor.getString(4));
			pic.add(ns);
		}
		cursor.close();
		db.close();
		return pic;
	}
	public boolean delete() {
		dbhelper = new PicDBHelper(context);
		db = dbhelper.getReadableDatabase();
		db.execSQL("delete from nav");
		db.close();
		return true;
	}
}

	
