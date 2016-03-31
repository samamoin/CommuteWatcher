package com.sammoin.commutewatcher;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class CommuteCheckAlarmService extends IntentService
{
	public static final String TAG = "MapQueryService";
	public static final String PREF_IS_ALARM_ON = "isServiceAlarmOn";
	public static final String START_ADDRESS = "work address";
	public static final String END_ADDRESS = "home address";
	public static final String COMMUTE_DAY = "user info object";
	UserWeek tempSavedUserInfo;
	static GregorianCalendar today;

	public CommuteCheckAlarmService(String name)
	{
		super(TAG);

	}
	/*
	@Override
	public void onCreate()
	{
		registerReceiver(mNewDayReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
	}
	
	@Override
	public void onDestroy()
	{
		try
		{
		unregisterReceiver(mNewDayReceiver);
		}
		
		catch (IllegalArgumentException ex)
		{
			if (ex.getMessage().contains("Receiver not registered"))
			{
				//apparently this is a known bug, but our goal of unregistering the receiver still happens
				
			}
			else
			{
				throw ex;
			}
		}
	}
	*/
	
	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.i(TAG, "MapQueryService is handing intent: " + intent);

		String startAddress = intent.getStringExtra(START_ADDRESS);
		String endAddress = intent.getStringExtra(END_ADDRESS);

		Geocoder startGeocoder = new Geocoder(this, Locale.getDefault());
		Geocoder endGeocoder = new Geocoder(this, Locale.getDefault());

		double startLatitude = 0.0;
		double startLongitude = 0.0;
		double endLatitude = 0.0;
		double endLongitude = 0.0;

		try
		{
			List<Address> startGeocoderAddress = startGeocoder
					.getFromLocationName(startAddress, 1);
			startLatitude = startGeocoderAddress.get(0).getLatitude();
			startLongitude = startGeocoderAddress.get(0).getLongitude();

			List<Address> endGeocoderAddress = endGeocoder.getFromLocationName(
					endAddress, 1);
			endLatitude = endGeocoderAddress.get(0).getLatitude();
			endLongitude = endGeocoderAddress.get(0).getLongitude();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Uri startAndEndUri = Uri.parse("http://maps.google.com/maps?saddr="
				+ startLatitude + "," + startLongitude + "&daddr="
				+ endLatitude + "," + endLongitude);

		Intent mapIntent = new Intent(android.content.Intent.ACTION_VIEW,
				startAndEndUri);
		mapIntent.setPackage("com.google.android.apps.maps");

		PendingIntent notificationIntent = PendingIntent.getActivity(this, 0,
				mapIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(this)
				.setTicker("Time to check your commute!")
				.setSmallIcon(android.R.drawable.ic_dialog_alert)
				.setContentTitle("CommuteWatcher")
				.setContentText(
						"It's time to check your commute! Tap this notification to start.")
				.setContentIntent(notificationIntent).setAutoCancel(true)
				.build();

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		notificationManager.notify(0, notification);

	}

	public CommuteCheckAlarmService()
	{
		super(TAG);
	}
	
	//TODO: add boolean to each userday for active/inactive
	

	@SuppressWarnings("unchecked")
	public static void setServiceAlarm(Context context, boolean isOn)
			throws StreamCorruptedException, IOException,
			ClassNotFoundException
	{
		// this overloaded method is since a BroadcastReceiver's lifecycle isn't
		// really meant for file I/O, so it can just call this method, which
		// will in turn
		// call the same method the mainfragment would normally call.
		ArrayList<UserDayItem> tempSavedUserInfo = WorkWeek.get(context);
		ObjectInputStream file;

		try
		{

			file = new ObjectInputStream((new FileInputStream(new File(
					new File(context.getFilesDir(), "") + File.separator
							+ TimeAndTravelFragment.USER_INFO_FILE))));
			tempSavedUserInfo = (ArrayList<UserDayItem>) ((ArrayList<UserDayItem>) file.readObject()).clone();
			
			file.close();

		} 
		catch (FileNotFoundException e)
		{

			e.printStackTrace();
		}
		setServiceAlarm(context, isOn, tempSavedUserInfo);

	}

	@SuppressWarnings("static-access")
	public static void setServiceAlarm(Context context, boolean isOn,
			ArrayList<UserDayItem> userInfo)
	{
		ArrayList<PendingIntent> pendingIntentList = new ArrayList<PendingIntent>();
		ArrayList<Intent> intentList = new ArrayList<Intent>();
		
		today = (GregorianCalendar) Calendar
				.getInstance();
		
		
		for (int i =0; i < userInfo.size(); i++)
		{
			if (userInfo.get(i).isActive())
			{
			Intent commuteIntent = new Intent(context,
					CommuteCheckAlarmService.class);
			commuteIntent.putExtra(COMMUTE_DAY, userInfo.get(i));
			intentList.add(new Intent(context, CommuteCheckAlarmService.class));
			pendingIntentList.add(PendingIntent.getService(context, i, commuteIntent, PendingIntent.FLAG_UPDATE_CURRENT));
			}
		}
		
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);

		if (isOn)
		{
			today = (GregorianCalendar) Calendar
					.getInstance();
			/*
			if ((userInfo.get(today.DAY_OF_WEEK-1).isActive()))
			{
					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
							workTime, AlarmManager.INTERVAL_DAY,
							pendingWorkCommuteIntent);

					alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
							homeTime, AlarmManager.INTERVAL_DAY,
							pendingHomeCommuteIntent);

			}
			*/
			
			for (int i=0; i < intentList.size(); i++)
			{
				long commuteTime= (((UserDayItem)intentList.get(i).getSerializableExtra(COMMUTE_DAY)).getStartCommuteTime().getTimeInMillis());
				alarmManager.setRepeating(AlarmManager.RTC, commuteTime, AlarmManager.INTERVAL_DAY, pendingIntentList.get(i));
			}

		}

		else
		{
			for (PendingIntent pi : pendingIntentList)
			{
			alarmManager.cancel(pi);
			pi.cancel();
			}

		}

		PreferenceManager.getDefaultSharedPreferences(context).edit()
				.putBoolean(PREF_IS_ALARM_ON, isOn).commit();

	}

	public static boolean isServiceAlarmOn(Context context)
	{
		Intent i = new Intent(context.getApplicationContext(),
				CommuteCheckAlarmService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, i,
				PendingIntent.FLAG_NO_CREATE);
		return pi != null;
	}
	/*
	private final NewDayReceiver mNewDayReceiver = new NewDayReceiver();

	public class NewDayReceiver extends BroadcastReceiver{
		
		
        
	    @Override
	    public void onReceive(final Context context, Intent intent) {

	    	String intentAction = intent.getAction();
	    	
			if (intentAction.equals(Intent.ACTION_TIME_TICK))
			{
				if (today.DAY_OF_WEEK !=((GregorianCalendar)Calendar.getInstance()).DAY_OF_WEEK )
				{
					
					try
					{

						setServiceAlarm(context, isServiceAlarmOn(context));

					} 
					catch (FileNotFoundException e)
					{

						e.printStackTrace();
					} 
					catch (StreamCorruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					catch (ClassNotFoundException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
	    }
	}
	 */
}