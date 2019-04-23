package com.ahsai.catchthemall;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class alarmreceiver extends BroadcastReceiver {
	NotificationManager notifmanager;

	@Override
	public void onReceive(Context context, Intent intent) {
		String sandi = initapp.getStringIntent(intent, "myname");
		if (sandi.equalsIgnoreCase("ahsai")) {
			// cara 1:show directly alarmpopup
			Intent popupIntent = new Intent(context, alarmpopup.class);
			popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(popupIntent);

			// cara 2:show notification that has pendingIntent to popup
			// alarmpopup
			/*
			 * notifmanager =
			 * (NotificationManager)context.getSystemService(Context
			 * .NOTIFICATION_SERVICE); CharSequence from = "Catch Them All";
			 * CharSequence message = "Please Click This"; PendingIntent
			 * contentIntent = PendingIntent.getActivity(context,0,new
			 * Intent(context
			 * ,alarmpopup.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), 0);
			 * Notification notif = new
			 * Notification(R.drawable.icon,"Please Click This",
			 * System.currentTimeMillis()); notif.flags =
			 * Notification.FLAG_AUTO_CANCEL; notif.setLatestEventInfo(context,
			 * from, message, contentIntent); notifmanager.notify(1, notif);
			 */
		}
	}

}
