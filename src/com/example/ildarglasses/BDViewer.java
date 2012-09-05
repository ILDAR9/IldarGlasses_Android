package com.example.ildarglasses;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class BDViewer extends Activity{
	TextView textView;
	@Override
	public void onCreate(Bundle instance){
		super.onCreate(instance);
		textView = new TextView(this);
		// getts Cursor from database
		Cursor cursor = new HashBase(this).getInfo();
		if (cursor.moveToFirst()) {
			StringBuilder sb = new StringBuilder();
			int idColIndex = cursor.getColumnIndex("ID");
			int firstColIndex = cursor.getColumnIndex("firstPart");
			int secondColIndex = cursor.getColumnIndex("secondPart");
			int lastColIndex = cursor.getColumnIndex("lastPart");
			
			sb.setLength(0);
			do {
										sb.append("ID = " + cursor.getInt(idColIndex)
						+ ",\nfirst = " + cursor.getInt(firstColIndex)
						+ ",\nsecond = " + cursor.getInt(secondColIndex)
						+"\nlast = " + cursor.getInt(lastColIndex)
						+ "\n------------------------------------------\n");							
			} while (cursor.moveToNext());
			textView.setText(sb.toString());
		} else {
			textView.setText("0 rows");
		}
					
//					LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
//							LayoutParams.FILL_PARENT);
		textView.setMovementMethod(new ScrollingMovementMethod());
		setContentView(textView);		
	}

}
