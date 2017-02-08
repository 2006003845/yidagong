package com.zzl.zl_app.city;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.zrlh.llkc.corporate.City;

public class CityDB extends SQLiteOpenHelper {
	// public static final String CITY_DB_NAME = "city.db";
	private static final String CITY_TABLE_NAME = "city_p";
	private SQLiteDatabase db;

	public CityDB(Context context) {
		super(context, "city", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + CITY_TABLE_NAME);
		db.execSQL("CREATE table IF NOT EXISTS "
				+ CITY_TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, id TEXT, city TEXT, countys TEXT, allpy TEXT, allfirstpy TEXT, firstpy TEXT)");
	}

	public boolean isOpen() {
		return db != null && db.isOpen();
	}

	public void close() {
		if (db != null && db.isOpen())
			db.close();
	}

	public void saveCity(List<City> list) {
		if (list == null)
			return;
		SQLiteDatabase sqldb = getWritableDatabase();
		for (City city : list) {
			if (!TextUtils.isEmpty(city.getName()))
				sqldb.execSQL(
						"insert into "
								+ CITY_TABLE_NAME
								+ " (id,city,countys,allpy,allfirstpy,firstpy) values(?,?,?,?,?,?)",
						new Object[] { city.getId(), city.getName(),
								city.getCountyStr(), city.getAllPY(),
								city.getAllFristPY(), city.getFirstPY() });
		}
		sqldb.close();
	}

	public List<City> getAllCity() {
		SQLiteDatabase sqldb = getWritableDatabase();
		List<City> list = new ArrayList<City>();
		Cursor c = sqldb.rawQuery("SELECT * from " + CITY_TABLE_NAME, null);
		while (c.moveToNext()) {
			String id = c.getString(c.getColumnIndex("id"));
			String city = c.getString(c.getColumnIndex("city"));
			String countyStr = c.getString(c.getColumnIndex("countys"));
			String[] countys = null;
			if (countyStr != null)
				countys = countyStr.split(",");
			else
				countys  = new String[]{};
			String allPY = c.getString(c.getColumnIndex("allpy"));
			String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
			String firstPY = c.getString(c.getColumnIndex("firstpy"));
			City item = new City(id, city, countys, firstPY, allPY, allFirstPY);
			item.setCountyStr(countyStr);
			list.add(item);
		}
		c.close();
		sqldb.close();
		return list;
	}

	public City getCity(String city) {
		if (TextUtils.isEmpty(city))
			return null;
		City item = getCityInfo(city);
		return item;
	}

	private City getCityInfo(String city) {
		SQLiteDatabase sqldb = getWritableDatabase();
		City item = null;
		Cursor c = sqldb.rawQuery("SELECT * from " + CITY_TABLE_NAME
				+ " where city=?", new String[] { city });
		if (c.moveToFirst()) {
			String id = c.getString(c.getColumnIndex("id"));
			String name = c.getString(c.getColumnIndex("city"));
			String countyStr = c.getString(c.getColumnIndex("countys"));
			String[] countys = countyStr.split(",");
			String allPY = c.getString(c.getColumnIndex("allpy"));
			String allFirstPY = c.getString(c.getColumnIndex("allfirstpy"));
			String firstPY = c.getString(c.getColumnIndex("firstpy"));
			item = new City(id, name, countys, firstPY, allPY, allFirstPY);
			item.setCountyStr(countyStr);
		}
		c.close();
		sqldb.close();
		return item;
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}
}
