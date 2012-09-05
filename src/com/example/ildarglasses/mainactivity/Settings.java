package com.example.ildarglasses.mainactivity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Settings extends Activity {
	TextView tvInfo;
	SharedPreferences sp;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		tvInfo = new TextView(this);
		setContentView(tvInfo);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
	}

	protected void onResume() {
		String listValue = sp.getString("list", "�� �������");
		tvInfo.setText("�������� ������ - " + listValue);
		super.onResume();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem mi = menu.add(0, 1, 0, "Preferences");
		mi.setIntent(new Intent(this, PrefActivity.class));
		return super.onCreateOptionsMenu(menu);
	}
}