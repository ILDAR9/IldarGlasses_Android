package com.example.ildarglasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

public class HashBase {
	private static final String LOG_TAG = "Ildar_glasses";
	DBHelper dbHelper;

	class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			// конструктор суперкласса
			super(context, "Ildar_glasses", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// создаем таблицу с полями
			String image_hash = "create table image_hash ("
					+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
					+ "firstPart INTEGER NOT NULL,"
					+ "secondPart INTEGER NOT NULL,"
					+ "lastPart INTEGER NOT NULL," + "imagePath TEXT NOT NULL)";
			String image_description = "create table image_description("
					+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
					+ "image_id INTEGER NOT NULL,"
					+ "description TEXT NOT NULL,"
					+ "constraint description_hash foreign key (image_id) references imagehash(id)"
					+ "on update cascade on delete cascade);";
			db.execSQL(image_hash);
			db.execSQL(image_description);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	public HashBase(Context context) {
		dbHelper = new DBHelper(context);
	}

	// private static String replacement(File file) {
	// String filePath = file.getAbsolutePath();
	// String imageExt = filePath.substring(filePath.lastIndexOf('.')); // get
	// // image's
	// // extension
	// String rootDir = (new File("")).getAbsolutePath();
	// rootDir = rootDir.replace("\\", "\\\\"); // we need double (\\) instead
	// // single (\)
	// String imagePath = String.format("%s\\\\res\\\\inImageBase\\\\%s%s",
	// rootDir, imgID, imageExt);
	// System.out.println(imagePath);
	//
	// System.out.println(imgID);
	// imgID++;
	// file.renameTo(new File(imagePath));
	// return imagePath;
	// }

	public void addValues(int[] hashCode, String imgPath, String description) {
		Log.d(LOG_TAG, "Creating connetion to database and preparing data...");
		// data container
		ContentValues cv = new ContentValues();
		// preparing data for inserting
		// key - value
		cv.put("firstPart", hashCode[0]);
		cv.put("secondPart", hashCode[1]);
		cv.put("lastPart", hashCode[2]);
		cv.put("imagePath", imgPath);
		// connetion to database
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		// Inserts data and gets insert's id
		Log.d(LOG_TAG, "Loading data into database...");
		long hashID = db.insert("image_hash", null, cv);
		cv.clear();
		if (description != null) {
			cv.put("image_id", hashID);
			cv.put("description", description);
			db.insert("image_description", null, cv);
		}
		Log.d(LOG_TAG, imgPath + "\n--------------The data is loaded ^_^");
		Log.d(LOG_TAG, "Disconnect from database.");
		db.close();
	}

	public Cursor getInfo() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return db.query("image_hash", null, null, null, null, null, null);
	}

	public int truncate(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = db.delete("image_hash", null, null);
		db.delete("image_description", null, null);
		return	count;
	}
	// public static void addImagesToBase() {
	// File imageBase = new File("res\\toImageBase");
	// File[] imageFiles = imageBase.listFiles();
	// readLastImgId();
	// for (File imageFile : imageFiles) {
	// ImageWork.imagesToSQL(imageFile);
	// }
	// saveLastImgID(); // saving last image id, not such in database
	// }

	// public List<String> startSearch(int max) {
	// List<String> response = new LinkedList<String>();
	// try {
	// if (con == null || con.isClosed()) {
	// createConnetion();
	// }
	// // int i = 0;
	// st = con.createStatement();
	// rs =
	// st.executeQuery("SELECT firstPart, secondPart, lastPart,  imagePath FROM imagehash");
	// int distance;
	// while (rs.next()) {
	// distance = ImageWork.checkingHemingDistance(rs.getInt(1),
	// rs.getInt(2), rs.getInt(3), max);
	// if (distance != -1) {
	// response.add(rs.getString(4));
	// }
	// }
	// } catch (SQLException e) {
	// System.err.println("Can't create query from imagehash!");
	// ImageWork.isException = true;
	// }
	// return response;
	// }
	//
	// public static void clean() {
	// createConnetion();
	// try {
	// st = con.createStatement();
	// System.out.println("Trying to truncate database...");
	// st.execute("TRUNCATE imagehash");
	// System.out.println("The database is truncated");
	// imgID = 0;
	// saveLastImgID();
	// } catch (SQLException e) {
	// System.out.println("Can't truncate database!!!");
	// }
	// end();
	// }
	//
	// public static void correctBase() {
	// createConnetion();
	// try {
	// st = con.createStatement();
	// rs = st.executeQuery("SELECT id, imagepath FROM imagehash");
	// st = con.createStatement(); // statement for deleting unexisting
	// // image's path
	// String temp;
	// boolean deleted = false;
	// while (rs.next()) {
	// temp = rs.getString(2);
	// if (!(new File(temp).exists())) {
	// deleted = true;
	// st.execute("DELETE FROM imagehash WHERE (id = "
	// + rs.getInt(1) + ')');
	// System.out.println(temp);
	// }
	// }
	// if (deleted) {
	// System.out.println("are deleted from database.");
	// }
	// } catch (SQLException e) {
	// System.err.println("Can't get image's path from database.");
	// }
	// end();
	// }
}
