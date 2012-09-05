package com.example.ildarglasses;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class BitmapLoaded extends Activity {
	private final String LOG_TAG = "Ildar_glasses";
	class RenderView extends View {
		Bitmap firstBmp;
		//Bitmap secondBmp;
		Rect dst = new Rect();
		InputStream is;
		Paint paint = new Paint();
		String conf_1, conf_2;

		RenderView(Context context) {
			super(context);

			try {
				AssetManager aManager = getAssets();
				is = aManager.open("images/LaTour.jpg");
				firstBmp = BitmapFactory.decodeStream(is);
				is.close();
				is = aManager.open("images/LaTour.jpg");
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_4444;
				//secondBmp = BitmapFactory.decodeStream(is, null, options);
				is.close();				
				conf_1="" + firstBmp.getConfig();
				//conf_2 = "" + secondBmp.getConfig();

			} catch (IOException ioe) {
				Log.d(LOG_TAG, "Can't read image");
			}

		}

		protected void onDraw(Canvas canvas) {
			firstBmp = Bitmap.createScaledBitmap(firstBmp, 16, 16, true);
			canvas.drawColor(Color.CYAN);
			dst.set(50, 50, 500, 500);
			canvas.drawBitmap(firstBmp, null, dst, null);
			paint.setARGB(255, 0, 255, 0);
			canvas.drawText(conf_1, 100, 100, paint);
			//canvas.drawText(conf_2, 200, 200, paint);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(new RenderView(this));
	}

}
