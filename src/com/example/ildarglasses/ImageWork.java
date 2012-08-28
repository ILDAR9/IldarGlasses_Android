package com.example.ildarglasses;

import android.graphics.Bitmap;
import android.util.Log;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class ImageWork {
	private static final String LOG_TAG = "Ildar_glasses";
	private int hashCode[] = new int[3], maxDist = 7;
	private Bitmap img;
	private boolean isException;

	// construcot FILE
	/*
	 * public ImageWork(File file){ loadImage(file); }
	 */
	public ImageWork() {

	}

	public ImageWork(Bitmap img) {
		this.img = img;
	}

	public void setImg(Bitmap img) {
		this.img = img;
	}

	public Bitmap getImg() {
		return img;
	}

	public static File readPathToImg() {
		Scanner scanner = new Scanner(System.in);
		return new File(scanner.nextLine());
	}

	// for constructor FILE
	/*
	 * public Bitmap loadImage(File file) {
	 * 
	 * Bitmap img = null; if (file.exists()) { try { img = ImageIO.read(file);
	 * System.out.printf("Image '%s' is loaded.\n", file.getCanonicalPath()); }
	 * catch (IOException e) { Log.d(LOG_TAG, new
	 * Formatter().format("Can't load the image '%s'\n",
	 * file.getAbsolutePath()).toString()); } } else { Log.d(LOG_TAG,
	 * "Can't find image in asset's folder"); }
	 * 
	 * return img; }
	 */

	private Bitmap deleteRGB(Bitmap img) { // taking
											// several
											// common parts
											// of R, G and B
		int temp;
		Log.d(LOG_TAG, "Deleting RGB canal...");
		for (int i = 0; i < img.getHeight(); i++) {			
			for (int j = 0; j < img.getWidth(); j++) {
				temp = img.getPixel(j, i);
				int r = (int) ((temp & 0xff) * 0.3);				
				int g = temp & 0xff00;
				g >>>= 8;
				g = (int) (0.59 * g);		
				int b = temp & 0xff0000;
				b >>>= 16;
				b = (int) (0.11 * b);
				temp = 0;
				temp = r + g + b;
				img.setPixel(j, i, temp);
			}			
		}
		Log.d(LOG_TAG, "RGB canal is replaced by one gray canal.");
		return img;
	}

	private static Bitmap scale(Bitmap image, int width, int height) {
		Log.d(LOG_TAG, "Scaliing...");
		Bitmap scaledImg = Bitmap.createScaledBitmap(image, width, height,
				image.hasAlpha());
		Log.d(LOG_TAG, String.format("Scaled to: %d x %d", 8, 8));
		return scaledImg;
	}

	private int averageSum() {
		if (img == null) {
			return -1;
		}
		img = scale(img, 8, 8);
		img = deleteRGB(img);

		int sum = 0;
		for (int i = 0; i < img.getHeight(); i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				sum += img.getPixel(j, i);
			}
		}
		return sum / (img.getHeight() * img.getWidth());
	}

	private int toBite(Bitmap img, int averageSum, int start, int end) {
		int temp = 0;
		for (int i = start; i < end; i++) {
			for (int j = 0; j < img.getWidth(); j++) {
				if (img.getPixel(j, i) < averageSum) {
					temp += 1;
				}
				temp <<= 1;
			}
		}
		Log.d(LOG_TAG, "Creating image hash...");
		return temp;
	}

	public int checkingHemingDistance(int firstPart0, int secondPart0,
			int lastPart0, int max) {
		int sum = 0;
		sum += xorCounter(hashCode[0], firstPart0);
		if (sum > max) {
			return -1;
		}
		sum += xorCounter(hashCode[1], secondPart0);
		if (sum > max) {
			return -1;
		}
		sum += xorCounter(hashCode[2], lastPart0);
		if (sum > max) {
			return -1;
		}
		return sum;
	}

	public int fullHemingDistance(int firstPart0, int secondPart0, int lastPart0) {
		int sum = 0;
		sum += xorCounter(hashCode[0], firstPart0);
		sum += xorCounter(hashCode[1], secondPart0);
		sum += xorCounter(hashCode[2], lastPart0);
		return sum;
	}

	public int[] getHemingDistance() {
		getHardHash();
		return hashCode;

	}

	private static int xorCounter(int temp, int temp0) {
		temp = temp ^ temp0;
		temp0 = 0;
		do {
			temp0 += temp % 2;
		} while ((temp /= 2) != 0);
		return temp0;
	}

	private void getHardHash() {
		int averageSum = averageSum();
		hashCode[0] = toBite(img, averageSum, 0, 3);
		hashCode[1] = toBite(img, averageSum, 3, 6);
		hashCode[2] = toBite(img, averageSum, 6, 8);
	}

	private void getSimpleHash(int averageSum) {
		hashCode[0] = toBite(img, averageSum, 0, 3);
		hashCode[1] = toBite(img, averageSum, 3, 6);
		hashCode[2] = toBite(img, averageSum, 6, 8);
	}
	/*
	 * 2 public void compareImagesTest(File file) { List<String> ans; int max =
	 * 6; int averageSum; do { averageSum = averageSum(file); for (int i = 0; i
	 * < 4; i++) { hashCode[0] = toBite(img, averageSum, 0, 3); hashCode[1] =
	 * toBite(img, averageSum, 3, 6); hashCode[2] = toBite(img, averageSum, 6,
	 * 8);
	 * 
	 * } getSimpleHash(averageSum(file)); ans = HashBase.startSearch(max); max
	 * += 2; } while (ans.isEmpty()); System.out.println("max = " + max);
	 * Iterator<String> iter = ans.iterator(); while (iter.hasNext()) { String
	 * temp = iter.next(); System.out.println(temp); ShowImage.showImg(new
	 * File(temp)); } }
	 */
	/*
	 * public static void startSearch(File file) { int averageSum =
	 * averageSum(file); if (averageSum == -1) { return; }
	 * getSimpleHash(averageSum); List<String> ans; // набор ответов
	 * int max = 3; // начнем с max = 4
	 * 
	 * do { max += 2; ans = HashBase.startSearch(max); // Возвращется
	 * // обратно в этот // класс для // проверки //
	 * дистанции // Хеминга } while (!isException && max <
	 * maxDist && ans.isEmpty()); HashBase.end(); if (isException) {
	 * System.err.println("There is a database's Exception"); return; } if
	 * (ans.isEmpty()) { System.out
	 * .println("--------------------There is no same image )-;"); return; }
	 * System.out.println("max = " + max); Iterator<String> iter =
	 * ans.iterator(); String imagePath; while (iter.hasNext()) { imagePath =
	 * iter.next(); System.out.println(imagePath); ShowImage.showImg(new
	 * File(imagePath)); } }
	 */
	/*
	 * public static Iterator<String> testStartSearch(File file) { int
	 * averageSum = averageSum(file); if (averageSum == -1) { return null; }
	 * getSimpleHash(averageSum); List<String> ans; // set of founded images int
	 * max = 3; // start from max = 3 to maxDist do { max += 2; ans =
	 * HashBase.startSearch(max); // Возвращется // обратно в
	 * этот // класс для // проверки // дистанции
	 * // Хеминга } while (!isException && max <= maxDist &&
	 * ans.isEmpty()); // maximal // constraint // can be // setted in //
	 * maxDist HashBase.end(); if (isException) {
	 * System.err.println("There is a database's Exception"); return null; } if
	 * (ans.isEmpty()) { System.out
	 * .println("--------------------There is no same image )-;"); return null;
	 * } System.out.println("max = " + max);
	 * 
	 * return ans.iterator(); }
	 */
	/*
	 * static void imagesToSQL(File file) { getSimpleHash(averageSum(file));
	 * HashBase.addValues(hashCode, file, null); }
	 */

}
