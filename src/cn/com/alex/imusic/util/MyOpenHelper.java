package cn.com.alex.imusic.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.content.ContentValues;
import android.widget.Toast;
import android.database.Cursor;

import java.util.ArrayList;
import java.io.File;

public class MyOpenHelper extends SQLiteOpenHelper {
	public static final String DB_NAME = "musicplayer";
	public static final String TABLE_NAME = "playlist";
	public static final String ID = "_id";
	public static final String FILE_PATH = "file_path";
	public static final String SONG_NAME = "song_name";
	public static final String ARTIST = "artist";
	public static final String LYRICS = "lyrics";
	public static final String ALBUM = "album";
	public static final String COVER = "cover";
	private Context ctx;

	public MyOpenHelper(Context ctx, String name, CursorFactory factory,
			int version) {
		super(ctx, name, factory, version);
		this.ctx = ctx;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists " + TABLE_NAME + "(" + ID
				+ "integer primary key," + FILE_PATH + " varchar," + SONG_NAME
				+ " varchar," + ARTIST + " varchar," + LYRICS + " varchar,"
				+ ALBUM + " varchar," + COVER + " varchar)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists " + TABLE_NAME);
		onCreate(db);
	}

	// 插入�?��数据
	public void insert(String[] strArray) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(SONG_NAME, strArray[0]);
		values.put(ARTIST, strArray[1]);
		values.put(FILE_PATH, strArray[2]);
		// 
		values.put(LYRICS, "");
		values.put(ALBUM, "");
		values.put(COVER, "");
		// values.put(LYRICS, strArray[3]);
		// values.put(ALBUM, strArray[4]);
		// values.put(COVER, strArray[5]);
		long count = db.insert(TABLE_NAME, ID, values);
		db.close();
		if (count == -1) {
			Toast.makeText(ctx, "", Toast.LENGTH_LONG).show();
		}
	}

	// 
	public void delete(String file_path) {
		String[] tmp = { file_path };
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_NAME, FILE_PATH+"=?", tmp);
		db.close();
	}

	// 
	public ArrayList<String[]> getAll() {
		ArrayList<String[]> fs = new ArrayList<String[]>();
		String[] f ;//= new String[6];
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
		cursor.moveToFirst();
		String filePath;
		while (cursor.moveToNext()) {
			if (isExists(filePath = cursor.getString(1))) {
				f = new String[6];
				// song_name
				f[0] = cursor.getString(2);
				// artist
				f[1] = cursor.getString(3);
				// file_path
				f[2] = cursor.getString(1);
				// Lyrics
				f[3] = cursor.getString(4);

				f[4] = cursor.getString(5);
				f[5] = cursor.getString(6);
				fs.add(f);
			} else {// 
				delete(filePath);
			}
		}
		db.close();
		return fs;
	}

	// 判断文件是否还在
	public boolean isExists(String file_path) {
		return new File(file_path).exists();
	}
}
