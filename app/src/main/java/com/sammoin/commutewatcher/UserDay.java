package com.sammoin.commutewatcher;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDay implements Serializable
{



	/**
	 * 
	 */
	private ArrayList<UserDayItem> dayItemArrayList;
	private Day dayOfTheWeek;


	public Day getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	public void setDayOfTheWeek(Day dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public ArrayList<UserDayItem> getDayItemArrayList() {
		return dayItemArrayList;
	}

	public void setDayItemArrayList(ArrayList<UserDayItem> dayItemArrayList) {
		this.dayItemArrayList = dayItemArrayList;
	}
	
	
	public boolean addItemToDay(UserDayItem item)
	{
		if (item.getWorkDay().equals(dayOfTheWeek))
		{
			dayItemArrayList.add(item);
			return true;
		}
		else return false;
	}

	public void removeItemFromDay(int index)
	{
		dayItemArrayList.remove(index);
	}

	public void removeItemFromDay(UserDayItem userDayItem)
	{
		for (int counter=0; counter < getDayItemArrayList().size(); counter++)
        {
            if (getDayItemArrayList().get(counter) == userDayItem)
            {
                getDayItemArrayList().remove(counter);
            }
        }
	}

}