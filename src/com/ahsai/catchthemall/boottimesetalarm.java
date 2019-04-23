package com.ahsai.catchthemall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class boottimesetalarm extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences reminderDB = context.getSharedPreferences(
				"reminderDB", Context.MODE_WORLD_READABLE);

		boolean isAlarmActive = false;
		if (reminderDB.contains("isAlarmActive")) {
			isAlarmActive = reminderDB.getBoolean("isAlarmActive", false);
		}
		if (isAlarmActive) {
			if (reminderDB.contains("nextTime")) {
				int nextTime = 0;
				nextTime = reminderDB.getInt("nextTime", 0);
				if (nextTime > 0)
					initapp.setOneTimeAlarm(isAlarmActive, context,
							alarmreceiver.class, nextTime * initapp.HOURS_X_MINUTES);
			}
		}

	}
}
