package com.newsinhand.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.newsinhand.definition.News;

public class DB {
	private Context context;
	SQLiteDatabase db;
	DBHelper dbhelper;

	public DB(Context context) {
		this.context = context;
	}

	public void insert(List<News> news) {
		dbhelper = new DBHelper(context);
		db = dbhelper.getReadableDatabase();
		for (int i = 0; i < news.size(); i++) {
			final News ns = (News) news.get(i);
			db.execSQL(
					"insert into news(title,id,desc,image,link,date,sort,refresh,store,content) values(?,?,?,?,?,?,?,?,?,?)",
					new Object[] { ns.getTitle(), ns.getId(), ns.getDesc(),
							ns.getImage(), ns.getLink(), ns.getDate(),
							ns.getSort(), ns.getRefresh(), ns.getStore(),"" });
		}
		db.close();
	}

	public List<News> check(String sort) {
		List<News> news = new ArrayList<News>();
		dbhelper = new DBHelper(context);
		db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select *from news where sort='" + sort
				+ "' order by id asc", null);
		while (cursor.moveToNext()) {
			News ns = new News();
			ns.setId(cursor.getInt(0));
			ns.setTitle(cursor.getString(1));
			ns.setDesc(cursor.getString(2));
			ns.setImage(cursor.getString(3));
			ns.setLink(cursor.getString(4));
			ns.setDate(cursor.getString(5));
			ns.setSort(cursor.getString(6));
			ns.setRefresh(cursor.getString(7));
			ns.setStore(cursor.getString(8));
			ns.set_content("");
			news.add(ns);
		}
		cursor.close();
		db.close();
		return news;
	}
	public News checkNews(String title,String sort) {
		
		dbhelper = new DBHelper(context);
		db = dbhelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select *from news where sort='" + sort
				+ "' order by id asc", null);
		while (cursor.moveToNext()) {
			if(cursor.getString(1).equals(title)){
				News ns = new News();
				ns.setId(cursor.getInt(0));
				ns.setTitle(cursor.getString(1));
				ns.setDesc(cursor.getString(2));
				ns.setImage(cursor.getString(3));
				ns.setLink(cursor.getString(4));
				ns.setDate(cursor.getString(5));
				ns.setSort(cursor.getString(6));
				ns.setRefresh(cursor.getString(7));
				ns.setStore(cursor.getString(8));
				ns.set_content(cursor.getString(9));
				return ns;
			}
		}
		cursor.close();
		db.close();
		return null;
	}
	public boolean deleteimage(String title) {
		dbhelper = new DBHelper(context);
		db = dbhelper.getReadableDatabase();
		db.execSQL("delete from news where title='" + title + "'");
		db.close();
		return true;
	}
	public boolean delete(String sort) {
		dbhelper = new DBHelper(context);
		db = dbhelper.getReadableDatabase();
		db.execSQL("delete from news where sort='" + sort + "'");
		db.close();
		return true;
	}
	public void update(String content,String title) {
		dbhelper = new DBHelper(context);
		db = dbhelper.getReadableDatabase();
		db.execSQL("update news set content='" + content + "' where title='" + title + "'");
		db.close();
	}
	

}
