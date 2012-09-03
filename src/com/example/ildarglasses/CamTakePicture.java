package com.example.ildarglasses;

import java.io.IOException;
import java.nio.CharBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CamTakePicture extends Activity implements SurfaceHolder.Callback {
	private static final String LOG_TAG = "Ildar_glasses";
	//Tools
	private ImageWork imgWork;
	private HashBase hashBase;	
	
	//From addon layout	
	private ImageButton bStart;
	private ImageButton bStop;
	private ImageButton bTake;
	
	//From main layout
	private TextView textInfo;
	private SurfaceView surView;
	private SurfaceHolder surHolder;

	private Camera camera;
	private boolean isCameraPreview = false;

	//addon for showing any image
	class RenderView extends View {
		Bitmap image;
		Rect dst = new Rect();
		Paint paint = new Paint();

		RenderView(Context context, Bitmap image) {
			super(context);
			this.image = image;

		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.WHITE);
			dst.set(50, 50, 550, 750);
			canvas.drawBitmap(image, null, dst, null);
			paint.setARGB(255, 0, 255, 0);
		}
	}

	private ShutterCallback shutter = new ShutterCallback() {
		@Override
		public void onShutter() {
		}
	};

	private PictureCallback raw = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
		}
	};

	PictureCallback jpg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			Bitmap camImg = BitmapFactory.decodeByteArray(arg0, 0,
					arg0.length);
			imgWork = new ImageWork(camImg, 8);
			int hashCode[] = imgWork.getImageHash();
			//Write to Text_View 1's and 0's
			hashWrite(hashCode);			
			String description = null;
			hashBase.addValues(hashCode, camImg,description);
			camera.startPreview();
		}
	};
	
	private void hashWrite(int[] hashCode){
		StringBuffer sb = new StringBuffer();
		textInfo.setText("");
		for (int x : hashCode) {
			sb.setLength(0);
			while(x != 0){
				if (x%2 == 1){
					sb.append('1');
				}else{
					sb.append('0');
				}
				x/= 2;
			}
			textInfo.append(sb.reverse().toString());
		}
		textInfo.append("\n");
	} 

	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			// TODO Auto-generated method stub
			bTake.setEnabled(true);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);
		//database helper
		hashBase = new HashBase(this);
		
		textInfo = (TextView) findViewById(R.id.text_info);
		
		surView = (SurfaceView) findViewById(R.id.surfaceview);
		surHolder = surView.getHolder();
		surHolder.addCallback(this);

		// insert second layout (3 buttons on surfaceView)
		LayoutInflater inflater = LayoutInflater.from(getBaseContext());
		View overlay = inflater.inflate(R.layout.overlay, null);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		addContentView(overlay, params);

		bStart = (ImageButton) overlay.findViewById(R.id.bStart);
		bStop = (ImageButton) overlay.findViewById(R.id.bStop);
		bTake = (ImageButton) overlay.findViewById(R.id.bTake);

		bStop.setEnabled(isCameraPreview);
		bTake.setEnabled(false);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (isCameraPreview) {
			camera.stopPreview();
			camera.release();
			isCameraPreview = false;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bStart:
			try {
				camera = Camera.open();
				camera.setPreviewDisplay(surHolder);
				camera.startPreview();
				isCameraPreview = true;
				camera.autoFocus(myAutoFocusCallback);

				bStart.setEnabled(!isCameraPreview);
				bStop.setEnabled(isCameraPreview);
			} catch (IOException e) {
				Log.e(LOG_TAG,
						"Can't set preview display from camera to SurfaceView\n"
								+ e.toString());
			}
			break;
		case R.id.bTake:
			camera.takePicture(shutter, raw, jpg);
			break;
		case R.id.bStop:
			if (isCameraPreview) {
				camera.stopPreview();
				isCameraPreview = false;
			}
			camera.release();			
			bStart.setEnabled(!isCameraPreview);
			bTake.setEnabled(false);
			bStop.setEnabled(isCameraPreview);
			break;
		}
	}

}
