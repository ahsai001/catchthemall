package com.ahsai.catchthemall;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class alarmpopup extends Activity {
	String textToShown;
	String textKey;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences reminderDB = this.getSharedPreferences("reminderDB",
				MODE_WORLD_READABLE);
		SharedPreferences.Editor reminderDBEditor = reminderDB.edit();
		
		Random ran = new Random();
		int selectedValue = 0;
		textToShown = "Error";

		Map<String, ?> map = reminderDB.getAll();

		Set<String> keySet = map.keySet();
		Object[] keyArray = keySet.toArray();
		keySet = null;
		
		boolean CloseThisPopup = false;

		Collection<?> collectionMap = map.values();
		map = null;
		Object[] arrayMap = collectionMap.toArray();
		collectionMap = null;
		int aMapSize = arrayMap.length;
		if (aMapSize > 4) {//3  int dan 1 boolean, sisanya string data
			while (true) {
				selectedValue = ran.nextInt(aMapSize);
				Object data = arrayMap[selectedValue];
				if ((data instanceof String)) {
					textToShown = (String) data;
					textKey = (String) keyArray[selectedValue];
					keyArray = null;
					break;
				} else {
					continue;
				}
			};
			showDialog(1);
		}else{
			reminderDBEditor.putBoolean("isAlarmActive", false);
			CloseThisPopup = true;
		}
		
		if(reminderDB.contains("minimum")){
			initapp.RANDOM_MIN = reminderDB.getInt("minimum", initapp.RANDOM_MIN);
		}
		if(reminderDB.contains("maximum")){
			initapp.RANDOM_MAX = reminderDB.getInt("maximum", initapp.RANDOM_MAX);
		}
		
		reminderDBEditor.commit();
		
		if(CloseThisPopup){
			finish();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Please Attention!").setMessage(textToShown)
				.setCancelable(false);
		alert.setNegativeButton("Continue",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// set next reminder
						SharedPreferences reminderDB = alarmpopup.this
								.getSharedPreferences("reminderDB",
										MODE_WORLD_READABLE);
						SharedPreferences.Editor reminderDBEditor = reminderDB
								.edit();

						int nextTime = 0;
						Random ran = new Random();
						while ((nextTime = ((initapp.RANDOM_MAX == initapp.RANDOM_MIN)? 0:ran.nextInt(initapp.RANDOM_MAX - initapp.RANDOM_MIN)) + initapp.RANDOM_MIN) == 0) {
						}
						;// 0 - 47
						reminderDBEditor.putInt("nextTime", nextTime);

						if (nextTime > 0){
							initapp.setOneTimeAlarm(true, alarmpopup.this,
									alarmreceiver.class, nextTime
											* initapp.HOURS_X_MINUTES);
							Toast
							.makeText(alarmpopup.this,
									"nextTime:"+String.valueOf(nextTime),
									Toast.LENGTH_SHORT).show();
						}
						reminderDBEditor.commit();
						// close this popup
						alarmpopup.this.finish();
					}
				}).setPositiveButton("Delete This Reminder & Continue",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						SharedPreferences reminderDB = alarmpopup.this
								.getSharedPreferences("reminderDB",
										MODE_WORLD_READABLE);
						SharedPreferences.Editor reminderDBEditor = reminderDB
								.edit();

						// delete reminder
						String textToRemoved = reminderDB
								.getString(textKey, "");
						if (textToRemoved.equalsIgnoreCase(textToShown)) {
							Log.w("CatchThemAll", "Delete Text From DataBase");
							reminderDBEditor.remove(textKey);
						}
						// set new time
						int nextTime = 0;
						Random ran = new Random();
						while ((nextTime = ((initapp.RANDOM_MAX == initapp.RANDOM_MIN)? 0:ran.nextInt(initapp.RANDOM_MAX - initapp.RANDOM_MIN)) + initapp.RANDOM_MIN) == 0) {
						}
						;// 0 - 47
						reminderDBEditor.putInt("nextTime", nextTime);

						if (nextTime > 0){
							initapp.setOneTimeAlarm(true, alarmpopup.this,
									alarmreceiver.class, nextTime
											* initapp.HOURS_X_MINUTES);
							Toast
							.makeText(alarmpopup.this,
									"nextTime:"+String.valueOf(nextTime),
									Toast.LENGTH_SHORT).show();
						}
						reminderDBEditor.commit();
						// close this popup
						alarmpopup.this.finish();
					}

				});
		AlertDialog dlg = alert.create();
		return dlg;
	}

}
