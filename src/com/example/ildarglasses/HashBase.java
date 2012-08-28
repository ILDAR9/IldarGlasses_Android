package com.example.ildarglasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created with IntelliJ IDEA. User: NurgalievI2 Date: 01.08.12 Time: 16:02 To
 * change this template use File | Settings | File Templates.
 */
public class HashBase {
	static Connection con;
	static java.sql.Statement st;
	static ResultSet rs;
	static int imgID;

	class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			// конструктор суперкласса
			super(context, "Ildar_glasses", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// создаем таблицу с полями
			db.execSQL("create table imagehash ("
					+ "ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
					+ "firstPart INTEGER NOT NULL,"
					+ "secondPart INTEGER NOT NULL,"
					+ "lastPart INTEGER NOT NULL," + "imagePath TEXT NOT NULL)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {		
			
		}
	}
  /*
	private static void createConnetion() {

		try {
			Class.forName(driver);
			System.out.println("The driver is loaded");
			con = DriverManager.getConnection(url + db, user, pass);
			System.out.println("The connection established");
		} catch (ClassNotFoundException e) {
			System.out.println("The driver isn't loaded!");
		} catch (SQLException e) {
			System.out.println("Connetion ERROR!");
		}

	}*/

	static void end() {
		try {
			if (con != null) {
				con.close();
			}
			if (rs != null) {
				rs.close();
			}
			if (st != null) {
				st.close();
			}
		} catch (SQLException e) {
			System.err.println("Can't close connection to database");
		}

	}

	private static String replacement(File file) {
		String filePath = file.getAbsolutePath();
		String imageExt = filePath.substring(filePath.lastIndexOf('.')); // get
																			// image's
																			// extension
		String rootDir = (new File("")).getAbsolutePath();
		rootDir = rootDir.replace("\\", "\\\\"); // we need double (\\) instead
													// single (\)
		String imagePath = String.format("%s\\\\res\\\\inImageBase\\\\%s%s",
				rootDir, imgID, imageExt);
		System.out.println(imagePath);

		System.out.println(imgID);
		imgID++;
		file.renameTo(new File(imagePath));
		return imagePath;
	}

	static void readLastImgId() {
		File file = new File("res\\lastImgID");
		try {
			FileReader reader = new FileReader(file);
			BufferedReader bf = new BufferedReader(reader);
			imgID = Integer.parseInt(bf.readLine());
			reader.close();
		} catch (FileNotFoundException e) {
			System.err.println("Can't find file-'lastImgID'");
		} catch (IOException e) {
			System.err.println("Can't open file-'lastImgID'");
		} catch (java.lang.NumberFormatException ex) {
			System.err.println("in file-'lastImgID' no number");
		}
	}

	static void saveLastImgID() {
		File file = new File("res\\lastImgID");
		try {
			PrintWriter writer = new PrintWriter(file);
			writer.print(imgID);
			writer.close();
		} catch (IOException e) {
			System.err.println("Can't open file-'lastImgID'");
		}

	}
/*
	public static void addValues(int[] hashCode, File file, String description) {
		try {
			if (con == null || con.isClosed()) {
				createConnetion();
			}
		} catch (SQLException e) {
			System.out.println("Checking Failed, connection's exception");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO imagehash VALUES ( null,");
		final String divide = ", ";
		final char quote = '\'';

		for (int i = 0; i < 3; i++) {
			sb.append(hashCode[i]);
			sb.append(divide);
		}
		sb.append(quote);
		sb.append(replacement(file)); // replace to ..\inImageBase\ and gets new
										// image's path
		sb.append(quote);
		sb.append(')');

		String sql = sb.toString();
		try {
			st = con.createStatement();
			// System.out.println(sql);
			st.execute(sql);
			System.out.println("--------------The data is loaded ^_^");
		} catch (SQLException e) {
			e.printStackTrace();
			// System.err.println("This path of image is busy");
		}

	}

	*/ /*
	
	public static void addImagesToBase() {
		File imageBase = new File("res\\toImageBase");
		File[] imageFiles = imageBase.listFiles();
		readLastImgId();
		for (File imageFile : imageFiles) {
			ImageWork.imagesToSQL(imageFile);
		}
		end();
		saveLastImgID(); // saving last image id, not such in database
	}*/
/*
	public static List<String> startSearch(int max) {
		List<String> response = new LinkedList<String>();
		try {
			if (con == null || con.isClosed()) {
				createConnetion();
			}
			// int i = 0;
			st = con.createStatement();
			rs = st.executeQuery("SELECT firstPart, secondPart, lastPart,  imagePath FROM imagehash");
			int distance;
			while (rs.next()) {
				distance = ImageWork.checkingHemingDistance(rs.getInt(1),
						rs.getInt(2), rs.getInt(3), max);
				if (distance != -1) {
					response.add(rs.getString(4));
				}
			}
		} catch (SQLException e) {
			System.err.println("Can't create query from imagehash!");
			ImageWork.isException = true;
		}
		return response;
	}

	public static void clean() {
		createConnetion();
		try {
			st = con.createStatement();
			System.out.println("Trying to truncate database...");
			st.execute("TRUNCATE imagehash");
			System.out.println("The database is truncated");
			imgID = 0;
			saveLastImgID();
		} catch (SQLException e) {
			System.out.println("Can't truncate database!!!");
		}
		end();
	}

	public static void correctBase() {
		createConnetion();
		try {
			st = con.createStatement();
			rs = st.executeQuery("SELECT id, imagepath FROM imagehash");
			st = con.createStatement(); // statement for deleting unexisting
										// image's path
			String temp;
			boolean deleted = false;
			while (rs.next()) {
				temp = rs.getString(2);
				if (!(new File(temp).exists())) {
					deleted = true;
					st.execute("DELETE FROM imagehash WHERE (id = "
							+ rs.getInt(1) + ')');
					System.out.println(temp);
				}
			}
			if (deleted) {
				System.out.println("are deleted from database.");
			}
		} catch (SQLException e) {
			System.err.println("Can't get image's path from database.");
		}
		end();
	}
*/
}
