package com.example.ildarglasses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;

public class CamTakePicture extends Activity implements SurfaceHolder.Callback {
	private static final String LOG_TAG = "Ildar_glasses";
	// Tools
	private ImageWork imgWork;
	private HashBase hashBase;

	// From addon layout
	private ImageButton bStart;
	private ImageButton bStop;
	private ImageButton bTake;
	private ImageButton bSave;

	// From main layout
	private TextView textInfo;
	private SurfaceView surView;
	private SurfaceHolder surHolder;

	private Camera camera;
	private boolean isCameraPreview = false;

	private boolean toSave = false;
	private String description;
	private int imgID;
	private String dir;
	private static final String FILE_EXT = ".jpg";
	private static final String DIRECTORY_DOCUMENTS = "/IldarGlasses";
	private File imgFile = null;

	// addon for showing any image
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
			Bitmap camImg = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
			Matrix mat = new Matrix();
			mat.postRotate(90);
			camImg = Bitmap.createBitmap(camImg, 0, 0, camImg.getWidth(),
					camImg.getHeight(), mat, true);
			imgWork = new ImageWork(camImg, 8);
			int hashCode[] = imgWork.getImageHash();
			// Write to Text_View 1's and 0's
			hashWrite(imgWork.getBinaryView());

			if (toSave) {
				try {
					Log.d(LOG_TAG, "Saving photo...");
					imgFile = new File(dir, imgID++ + FILE_EXT);
					imgFile.createNewFile();
					FileOutputStream out = new FileOutputStream(imgFile);
					// 90 is gradus
					camImg.compress(Bitmap.CompressFormat.JPEG, 90, out);
					hashBase.addValues(hashCode, imgFile.getAbsolutePath(),
							description);
					out.close();
					Log.d(LOG_TAG, "Image is saved");
				} catch (FileNotFoundException e) {
					Log.e(LOG_TAG, "File didn't create itself");
					e.printStackTrace();
				} catch (IOException e) {
					Log.e(LOG_TAG, "Can't save image!!!");
					e.printStackTrace();
				}
			}
			camera.startPreview();
		}
	};

	private void hashWrite(char[] hashCode) {
		textInfo.setText("");
		int size = 22;
		char[] temp = new char[22];
		for (int i = 0, start = size * i; i < 2; i++) {
			for (int index = 0, j = start; j < start + size; j++, index++) {
				temp[index] = hashCode[j];
			}
			textInfo.append(new String(temp));
			textInfo.append("\n");
		}
		for (int index = 0, j = 44; j < 64; j++, index++) {
			temp[index] = hashCode[j];
		}
		textInfo.append(new String(temp));
	}

	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		@Override
		public void onAutoFocus(boolean arg0, Camera arg1) {
			// TODO Auto-generated method stub
			bTake.setEnabled(true);
			bSave.setEnabled(true);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_preview);
		// database helper
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
		bSave = (ImageButton) overlay.findViewById(R.id.bSave);

		bStop.setEnabled(isCameraPreview);
		bTake.setEnabled(false);
		bSave.setEnabled(false);
		
		// creating directory
		dir = Environment.getExternalStorageDirectory().toString()
				+ DIRECTORY_DOCUMENTS;
		File folder = new File(dir);

		if (!folder.exists()) {
			folder.mkdir();
		}
		imgID = readLastImgId();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveLastImgID(imgID);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (camera != null) {
			Camera.Parameters parameters = camera.getParameters();
			parameters.set("orientation", "portrait");
			camera.setParameters(parameters);
		}
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
		case R.id.bStart: {
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
				e.printStackTrace();
			}
			break;
		}
		case R.id.bTake: {
			toSave = false;
			camera.takePicture(shutter, raw, jpg);
			break;
		}
		case R.id.bStop: {
			if (isCameraPreview) {
				camera.stopPreview();
				isCameraPreview = false;
			}
			camera.release();
			bStart.setEnabled(!isCameraPreview);
			bTake.setEnabled(false);
			bSave.setEnabled(false);
			bStop.setEnabled(isCameraPreview);
			break;
		}
		case R.id.bSave: {
			toSave = true;
			camera.takePicture(shutter, raw, jpg);
			break;
		}
		}
	}

	private int readLastImgId() {
		Log.d(LOG_TAG, "Reading lastImgID.txt...");
		File imgIdFile = new File(dir, "lastImgID.txt");
		int imgID = 0;
		try {
			if (!imgIdFile.exists()) {
				imgIdFile.createNewFile();
				saving(imgIdFile, imgID);
				return imgID;
			}
			FileReader reader = new FileReader(imgIdFile);
			BufferedReader bf = new BufferedReader(reader);
			imgID = Integer.parseInt(bf.readLine());
			reader.close();
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "Can't find file-'lastImgID.txt' :\n"+imgFile.getAbsolutePath().toString());
			e.printStackTrace();
		} catch (IOException e) {
			String temp ;
			if (imgIdFile == null){
				temp = "null";
			} else{
				temp = imgIdFile.getAbsolutePath();
			}
		 
			Log.e(LOG_TAG, "Can't open file-'lastImgID.txt' :\n"+temp);
			e.printStackTrace();
		} catch (java.lang.NumberFormatException e) {
			Log.e(LOG_TAG, "in file-'lastImgID' no number");
			e.printStackTrace();
		}
		Log.d(LOG_TAG, "Image ID is copyed.");
		return imgID;
	}

	private void saveLastImgID(int imgID) {
		Log.d(LOG_TAG, "Writing last image ID...");
		File imgIdFile = new File(dir, "lastImgID.txt");
		if (!imgIdFile.exists()) {
			imgIdFile.mkdir();
		}
		saving(imgIdFile, imgID);
		Log.d(LOG_TAG, "Last image ID is wrote");
	}

	private void saving(File imgIdFile, int imgID) {
		try {
			PrintWriter writer = new PrintWriter(imgIdFile);
			writer.print(imgID);
			writer.close();
		} catch (IOException e) {
			Log.d(LOG_TAG, "Can't open file-'lastImgID.txt'");
			e.printStackTrace();
		}
	}

}
