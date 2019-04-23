package com.ahsai.catchthemall;

import java.util.Calendar;
import java.util.Random;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class initapp extends Activity {
	/** Called when the activity is first created. */
	public static int HOURS_X_MINUTES = 60; // menit * detik
	public static int RANDOM_MAX = 60;// random max for nextime in hour
	public static int RANDOM_MIN = 30;// random min for nextime in hour
	public static int RANDOM_INT = 1000;// random for key

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button addTextToReminder = (Button) findViewById(R.id.Button01);
		final Button startStopReminder = (Button) findViewById(R.id.Button02);
		final EditText text = (EditText) findViewById(R.id.EditText01);

		final EditText min = (EditText) findViewById(R.id.EditText02);
		final EditText max = (EditText) findViewById(R.id.EditText03);
		final Button savemaxmin = (Button) findViewById(R.id.Button03);

		savemaxmin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String minimumString = min.getEditableText().toString();
				String maximumString = max.getEditableText().toString();
				if (minimumString.length() > 0
						&& maximumString.length() > 0
						&& Integer.parseInt(minimumString) <= Integer
								.parseInt(maximumString)) {
					SharedPreferences reminderDB = initapp.this
							.getSharedPreferences("reminderDB",
									MODE_WORLD_READABLE);
					SharedPreferences.Editor reminderDBEditor = reminderDB
							.edit();
					reminderDBEditor.putInt("minimum", Integer
							.parseInt(minimumString));
					reminderDBEditor.putInt("maximum", Integer
							.parseInt(maximumString));

					RANDOM_MIN = Integer.parseInt(minimumString);
					RANDOM_MAX = Integer.parseInt(maximumString);

					boolean success = reminderDBEditor.commit();
					if (success) {
						Toast.makeText(initapp.this, "New Setting Saved Min:"+String.valueOf(RANDOM_MIN)+" Max:"+String.valueOf(RANDOM_MAX),
								Toast.LENGTH_SHORT).show();
						min.getEditableText().clear();
						max.getEditableText().clear();
					} else {
						Toast.makeText(initapp.this, "New Setting Fail Saved",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast
							.makeText(
									initapp.this,
									"Max Min must setted and min must smaller than max",
									Toast.LENGTH_SHORT).show();
				}
			}

		});

		addTextToReminder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String textString = text.getEditableText().toString();
				if (textString != null && textString.length() > 0) {
					SharedPreferences reminderDB = initapp.this
							.getSharedPreferences("reminderDB",
									MODE_WORLD_READABLE);
					SharedPreferences.Editor reminderDBEditor = reminderDB
							.edit();

					Random ran = new Random();
					int ranInt = ran.nextInt(RANDOM_INT) + 1;
					long lastIndex = System.currentTimeMillis();
					reminderDBEditor.putString(String.valueOf(lastIndex) + ""
							+ String.valueOf(ranInt), textString);
					boolean success = reminderDBEditor.commit();
					if (success) {
						Toast
								.makeText(initapp.this,
										"Add "+"("+textString+")"+" into Reminder Success",
										Toast.LENGTH_SHORT).show();
						text.getEditableText().clear();
					} else {
						Toast.makeText(initapp.this,
								"Add into Reminder Failed", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});

		startStopReminder.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SharedPreferences reminderDB = initapp.this
						.getSharedPreferences("reminderDB", MODE_WORLD_READABLE);
				SharedPreferences.Editor reminderDBEditor = reminderDB.edit();
				boolean isAlarmActive = false;
				if (reminderDB.contains("isAlarmActive")) {
					isAlarmActive = reminderDB.getBoolean("isAlarmActive",
							false);
				}
				isAlarmActive = !isAlarmActive;
				reminderDBEditor.putBoolean("isAlarmActive", isAlarmActive);

				int nextTime = 0;
				if (isAlarmActive) {
					Random ran = new Random();
					while ((nextTime = ((RANDOM_MAX == RANDOM_MIN) ? 0 : ran
							.nextInt(RANDOM_MAX - RANDOM_MIN))
							+ RANDOM_MIN) == 0) {
					}
					;// 0 - 47
					Log.e("CatchThemAll", "nextTime:"+String.valueOf(nextTime));
					Toast
					.makeText(initapp.this,
							"nextTime:"+String.valueOf(nextTime),
							Toast.LENGTH_SHORT).show();
					reminderDBEditor.putInt("nextTime", nextTime);
				} else {
					if (reminderDB.contains("nextTime")) {
						nextTime = reminderDB.getInt("nextTime", 0);
					}
				}

				reminderDBEditor.commit();
				if (nextTime > 0) {
					setOneTimeAlarm(isAlarmActive, initapp.this,
							alarmreceiver.class, nextTime * HOURS_X_MINUTES);
				}

				if (isAlarmActive) {
					startStopReminder.setText("Stop Alarm");
				} else {
					startStopReminder.setText("Start Alarm");
				}
			}

		});

		SharedPreferences reminderDB = initapp.this.getSharedPreferences(
				"reminderDB", MODE_WORLD_READABLE);
		SharedPreferences.Editor reminderDBEditor = reminderDB.edit();

		// set pref for all

		boolean isAlarmActive = false;
		if (reminderDB.contains("isAlarmActive")) {
			isAlarmActive = reminderDB.getBoolean("isAlarmActive", false);
		} else {
			reminderDBEditor.putBoolean("isAlarmActive", false);
		}
		if (isAlarmActive) {
			startStopReminder.setText("Stop Alarm");
		} else {
			startStopReminder.setText("Start Alarm");
		}

		if (reminderDB.contains("minimum")) {
			RANDOM_MIN = reminderDB.getInt("minimum", RANDOM_MIN);
		} else {
			reminderDBEditor.putInt("minimum", RANDOM_MIN);
		}

		if (reminderDB.contains("maximum")) {
			RANDOM_MAX = reminderDB.getInt("maximum", RANDOM_MAX);
		} else {
			reminderDBEditor.putInt("maximum", RANDOM_MAX);
		}

		reminderDBEditor.putInt("nextTime", 0);

		reminderDBEditor.commit();
	}

	// *************************** MODUL STATIC
	// *******************************************

	// set & unset Alarm for scheduling BroadCastReceiver
	public static void setOneTimeAlarm(boolean isSet, Context context,
			Class<?> cls, int attime) {
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				context, cls).putExtra("myname", "ahsai"),
				PendingIntent.FLAG_ONE_SHOT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		if (isSet) {
			Log.e("CatchThemAll", "start alarm attime:"+attime+" second later");
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (attime * 1000), pi);
		} else {
			Log.e("CatchThemAll", "stop alarm");
			am.cancel(pi);
		}
	}

	public static void setRepeatingAlarm(boolean isSet, Context context,
			Class<?> cls, int atttime, int interval) {
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				context, cls).putExtra("myname", "ahsai"),
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		if (isSet) {
			// am.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+
			// (atttime * 1000),(interval * 1000), pi);
			am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ (atttime * 1000), (interval * 1000), pi);
		} else {
			am.cancel(pi);
		}
	}

	public static void setRepeatingDetailAlarm(boolean isSet, Context context,
			Class<?> cls, int hour, int minute, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, new Intent(
				context, cls).putExtra("myname", "ahsai"),
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		if (isSet) {
			// am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
			// calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
			am.setRepeating(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
		} else {
			am.cancel(pi);
		}
	}

	// get data from intent
	public static String getStringIntent(Intent intent, String name) {
		String retval = null;
		if (intent != null) {
			if (intent.hasExtra(name))
				retval = intent.getStringExtra(name);
		}
		return retval;
	}
}