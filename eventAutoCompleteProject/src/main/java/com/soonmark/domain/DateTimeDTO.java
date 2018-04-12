package com.soonmark.domain;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateTimeDTO {
	private int year;
	private int month;
	private int date;
	private DayOfWeek day;
	private String displayDay;
	private int hour;
	private int minute;
	private boolean isAllDayEvent;
	
	public DateTimeDTO() {
		this.year = AppConstants.NO_DATA;
		this.month = AppConstants.NO_DATA;
		this.date = AppConstants.NO_DATA;
		this.day = AppConstants.NO_DATA_FOR_DAY;
		this.setDisplayDay(AppConstants.NO_DATA_FOR_DISPLAYDAY);
		this.hour = AppConstants.NO_DATA;
		this.minute = AppConstants.NO_DATA;
		this.isAllDayEvent = false;
	}
	
	public DateTimeDTO(int year, int month, int date, DayOfWeek day, int hour, int minute, boolean isAllDayEvent) {
		this.year = year;
		this.month = month;
		this.date = date;
		this.day = day;
		if(day != AppConstants.NO_DATA_FOR_DAY) {
			this.setDisplayDay(day.getDisplayName(TextStyle.FULL, Locale.KOREA));
		}else {
			this.setDisplayDay(AppConstants.NO_DATA_FOR_DISPLAYDAY);
		}
		this.hour = hour;
		this.minute = minute;
		this.isAllDayEvent = isAllDayEvent;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getDate() {
		return date;
	}
	public void setDate(int date) {
		this.date = date;
	}
	public DayOfWeek getDay() {
		return day;
	}
	public void setDay(DayOfWeek day) {
		this.day = day;
		if(day != AppConstants.NO_DATA_FOR_DAY) {
			this.setDisplayDay(day.getDisplayName(TextStyle.FULL, Locale.KOREA));
		}else {
			this.setDisplayDay(AppConstants.NO_DATA_FOR_DISPLAYDAY);
		}
	}
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMinute() {
		return minute;
	}
	public void setMinute(int minute) {
		this.minute = minute;
	}
	public boolean isAllDayEvent() {
		return isAllDayEvent;
	}
	public void setAllDayEvent(boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	public String getDisplayDay() {
		return displayDay;
	}

	public void setDisplayDay(String displayDay) {
		this.displayDay = displayDay;
	}
	
	@Override
	public String toString() {
		String jsonString = "";
		if (this.day != AppConstants.NO_DATA_FOR_DAY) {
			jsonString = "{\"year\":\"" + String.format("%04d", this.year)
			+ "\", \"month\":\"" + String.format("%02d", this.month)
			+ "\", \"date\":\"" + String.format("%02d", this.date)
			+ "\", \"day\":\"" + this.displayDay
			+ "\", \"isAllDayEvent\":\"" + this.isAllDayEvent
			+ "\", \"hour\":\"" + String.format("%02d", this.hour)
			+ "\", \"minute\":\"" + String.format("%02d", this.minute)
			+ "\"}";
		} else {
			jsonString = "{\"year\":\"" + String.format("%04d", this.year)
			+ "\", \"month\":\"" + String.format("%02d", this.month)
			+ "\", \"date\":\"" + String.format("%02d", this.date)
			+ "\", \"day\":\""
			+ "\", \"isAllDayEvent\":\"" + this.isAllDayEvent
			+ "\", \"hour\":\"" + String.format("%02d", this.hour)
			+ "\", \"minute\":\"" + String.format("%02d", this.minute)
			+ "\"}";
		}

		return jsonString;
	}
}
