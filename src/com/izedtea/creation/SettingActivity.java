package com.izedtea.creation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends Activity {

	private SharedPreferences sp;
	
	private EditText nameEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		nameEditText = (EditText)findViewById(R.id.nameEditText);
		
		sp = getSharedPreferences("global", 0);
		
		nameEditText.setText(sp.getString("username", null));
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("username", nameEditText.getText().toString());
		editor.commit();
		
		Toast.makeText(SettingActivity.this, "username has been saved.", Toast.LENGTH_SHORT).show();
	}
}
