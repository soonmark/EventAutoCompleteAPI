package com.soonmark.myapp;

public class DateVO {
	String year;
	String month;
	String date;
	String day;
	String hour;
	String minute;
	boolean isFocusOnDay;

	DateVO() {
		this.year = "-1";
		this.month = "-1";
		this.date = "-1";
		this.day = "-1";
		this.hour = "-1";
		this.minute = "-1";
		this.isFocusOnDay = false;
	}

	DateVO(String year, String month, String date, String day, String hour, String minute) {
		this.year = year;
		this.month = month;
		this.date = date;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.isFocusOnDay = false;
	}
	
	public boolean isFocusOnDay() {
		return isFocusOnDay;
	}
	
	public void setFocusOnDay(boolean isFocusOnDay) {
		this.isFocusOnDay = isFocusOnDay;
	}
	
	public void set(int idx, String val) {
		switch(idx) {
		case 0:
			this.year = val;
			break;
		case 1:
			this.month = val;
			break;
		case 2:
			this.date = val;
			break;
		case 3:
			this.day = val;
			break;
		case 4:
			this.hour = val;
			break;
		case 5:
			this.minute = val;
			break;
		default:
			break;
		}
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDay() {
		return day;
	}

	public String getIntDay() {
		return day;
	}
	
	public void setDay(String day) {
		this.day = day;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getMinute() {
		return minute;
	}

	public void setMinute(String minute) {
		// 0분, 1분 등이면 00분, 01분 으로 세팅
		if(minute.length() == 1) {
			this.minute = "0" +  minute;
		}
		else {
			this.minute = minute;
		}
	}

	public String toString() {
		String jsonString = "{\"year\":\"" + this.year + "\", \"month\":\"" + this.month + "\", \"date\":\"" + this.date
				+ "\", \"day\":\"" + this.day + "\", \"hour\":\"" + this.hour + "\", \"minute\":\"" + this.minute
				+ "\"}";

		return jsonString;
	}
}
