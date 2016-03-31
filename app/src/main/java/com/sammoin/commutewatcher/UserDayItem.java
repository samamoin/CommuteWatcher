package com.sammoin.commutewatcher;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class UserDayItem implements Serializable
{

	/**
	 * 
	 */
	private String startAddress = "";
	private String endAddress = "";
	private GregorianCalendar startCommuteTime = new GregorianCalendar();
	private GregorianCalendar driveToHomeTime = new GregorianCalendar();
	private Day workDay = Day.SUNDAY;
	private boolean active = false;
	
	//NEW DESIGN PLAN - STATIC ARRAY OF DAYS, CONTAINS COMMUTE IN EACH DAY. SHOULD BE EASIER TO USE WITH ALARMS AND SAVE.
	public Day getWorkDay()
	
	{
		
		return this.workDay;
	}

	public boolean isActive()
	{
		return active;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public void setWorkDay(Day workDay)
	{
		this.workDay = workDay;
	}




	@Override 
	public String toString()
	{
		
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa ZZZZ",
				Locale.getDefault());
		
		String toWorkTimeString = sdf.format(startCommuteTime.getTime());
		String toHomeTimeString = sdf.format(driveToHomeTime.getTime());
		
		return workDay.toString() + " from " + startAddress + " at " + toWorkTimeString + "to " + endAddress +  " at " + toHomeTimeString;
	}

	public void clear()
	{
		setHomeAddress("");
		setWorkAddress("");
		setDriveToHomeTime(new GregorianCalendar());
		setStartCommuteTime(new GregorianCalendar());
		setActive(false);
	}

	

	public GregorianCalendar getStartCommuteTime()
	{
		return startCommuteTime;
	}

	public void setStartCommuteTime(GregorianCalendar in)
	{
		startCommuteTime = in;
	}

	public GregorianCalendar getDriveToHomeTime()
	{
		return driveToHomeTime;
	}

	public void setDriveToHomeTime(GregorianCalendar in)
	{
		driveToHomeTime = in;
	}

	public String getHomeAddress()
	{
		return startAddress;
	}

	public void setHomeAddress(String in)
	{
		startAddress = in;
	}

	public String getWorkAddress()
	{
		return endAddress;
	}


	public void setWorkAddress(String in)
	{
		endAddress = in;
	}

	@SuppressWarnings("unchecked")
	public void copyUserData(UserDayItem inboundUserDayItem)
	{
		driveToHomeTime.setTime(inboundUserDayItem.getDriveToHomeTime().getTime());
		startCommuteTime.setTime(inboundUserDayItem.getStartCommuteTime().getTime());
		startAddress = inboundUserDayItem.getHomeAddress();
		endAddress = inboundUserDayItem.getWorkAddress();
		workDay = inboundUserDayItem.getWorkDay();
		active = inboundUserDayItem.isActive();
		
		
	}

}