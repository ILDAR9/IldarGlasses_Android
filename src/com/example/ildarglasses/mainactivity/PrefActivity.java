package com.example.ildarglasses.mainactivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.example.ildarglasses.R;

public class PrefActivity extends PreferenceActivity {
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);
		sp = PreferenceManager.getDefaultSharedPreferences(this);

	}
}
