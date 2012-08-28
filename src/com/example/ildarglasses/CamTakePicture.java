package com.example.ildarglasses;

import java.io.IOException;

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
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class CamTakePicture extends Activity implements SurfaceHolder.Callback {
	private ImageWork imgWork;
	private TextView textInfo;
	private ImageButton bStart;
	private ImageButton bStop;	
	private ImageButton bTake;	
	
	private SurfaceView surView;
	private SurfaceHolder surHolder;
	
	private Camera camera;
	private boolean isCameraPreview = false;
	class RenderView extends View {
		Bitmap image;
		Rect dst = new Rect();
		Paint paint = new Paint();

		RenderView(Context context,Bitmap image) {
			super(context);
			this.image = image;
			

		}
		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(Color.BLACK);
			dst.set(50, 50, 550, 750);
			canvas.drawBitmap(image, null, dst, null);
			paint.setARGB(255, 0, 255, 0);
		}
	}
	
    private ShutterCallback shutter = new ShutterCallback(){
		@Override
		public void onShutter() { }
	};

	private PictureCallback raw = new PictureCallback(){
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) { }
	};
	
	PictureCallback jpg = new PictureCallback(){
		@Override
		public void onPictureTaken(byte[] arg0, Camera arg1) {
			Bitmap bitmapPicture
				= BitmapFactory.decodeByteArray(arg0, 0, arg0.length);			
			setContentView(new RenderView(getBaseContext(), bitmapPicture));	
			imgWork.setImg(bitmapPicture);
			int hash[] = imgWork.getHemingDistance();
			textInfo.setText("");
			for (int x : hash) {
				textInfo.append(Integer.toString(x) + "\n");
			}
			bitmapPicture = imgWork.getImg();
			camera.startPreview();
		}
	};
		
	AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback(){

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
	    imgWork = new ImageWork();
	    textInfo = (TextView) findViewById(R.id.text_info);
	    surView = (SurfaceView)findViewById(R.id.surfaceview);
	    surHolder = surView.getHolder();
	    surHolder.addCallback(this);
	    surHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	  
	    LayoutInflater inflater = LayoutInflater.from(getBaseContext());
	    View overlay = inflater.inflate(R.layout.overlay, null);
	    LayoutParams params = new LayoutParams(
	    		LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    addContentView(overlay, params);
	    
		bStart = (ImageButton)overlay.findViewById(R.id.bStart);
		bStop = (ImageButton)overlay.findViewById(R.id.bStop);
		bTake = (ImageButton)overlay.findViewById(R.id.bTake);
		
		bStop.setEnabled(isCameraPreview);		
		bTake.setEnabled(false);
	}
	
	@Override
	public void surfaceChanged(
			SurfaceHolder holder, int format, int width, int height) { }

	@Override
	public void surfaceCreated(SurfaceHolder holder) { }

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
			} 
			catch (IOException e) {
			   Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.bTake:
			camera.takePicture(shutter, raw, jpg);
			break;	
		case R.id.bStop:
			camera.stopPreview();
			camera.release(); 
			isCameraPreview = false;
				
			bStart.setEnabled(!isCameraPreview);
			bTake.setEnabled(false);
			bStop.setEnabled(isCameraPreview);
			break;
		}		
	}
	
}
