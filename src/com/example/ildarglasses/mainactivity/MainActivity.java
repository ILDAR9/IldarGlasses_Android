package com.example.ildarglasses.mainactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.ildarglasses.BDViewer;
import com.example.ildarglasses.CamTakePicture;
import com.example.ildarglasses.R;

public class MainActivity extends Activity {
	private static final String LOG_TAG = "Ildar_glasses";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_btn: {
			Log.d(LOG_TAG, "Camera is adapted");
			Intent bitmap = new Intent(this, CamTakePicture.class);
			startActivity(bitmap);
			break;
		}
		case R.id.add_btn: {
			Intent reader = new Intent(this, BDViewer.class);
			startActivity(reader);
			break;
		}
		case R.id.settings_btn: {
			Intent settings = new Intent(this, Settings.class);
			startActivity(settings);
			break;
		}
		default:
			break;
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, "Settings");
		mi.setIntent(new Intent(this, Settings.class));
		return super.onCreateOptionsMenu(menu);
	}

}
