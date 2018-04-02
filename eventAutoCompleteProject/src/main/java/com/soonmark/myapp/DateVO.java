package com.soonmark.myapp;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateVO {
	String year;
	String month;
	String date;
	String day;
	String hour;
	String minute;
	boolean isFocusOnDay;
	String binaryDTInfo;

	DateVO() {
		this.year = "-1";
		this.month = "-1";
		this.date = "-1";
		this.day = "-1";
		this.hour = "-1";
		this.minute = "-1";
		this.isFocusOnDay = false;
		binaryDTInfo = "000000";
	}

	DateVO(String year, String month, String date, String day, String hour, String minute) {
		this.year = year;
		this.month = month;
		this.date = date;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.isFocusOnDay = false;
		binaryDTInfo = "000000";
	}
	
	public String getBinaryDTInfo() {
		return binaryDTInfo;
	}
	
	public void setBinaryDTInfo(DateTimeEn dateTimeEn) {
		int numericDateTime = dateTimeEn.getInteger();
		char tmpBit = this.binaryDTInfo.charAt(numericDateTime);
		int newInfo = Character.getNumericValue(tmpBit) | 1;
		binaryDTInfo = binaryDTInfo.substring(0,numericDateTime)
					+ newInfo + binaryDTInfo.substring(numericDateTime + 1);
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
	
	public String compareBinaryInfo(TokenType tokenType) {
		String result = "";
		switch(tokenType) {
		case dates:
			for(int i = DateTimeEn.year.getInteger() ; i < DateTimeEn.day.getInteger() ; i++) {
				result += binaryDTInfo.charAt(i);
			}
			break;
		case days:
			for(int i = DateTimeEn.day.getInteger() ; i < DateTimeEn.hour.getInteger() ; i++) {
				result += binaryDTInfo.charAt(i);
			}
			break;
		case times:
			for(int i = DateTimeEn.hour.getInteger() ; i < DateTimeEn.minute.getInteger() ; i++) {
				result += binaryDTInfo.charAt(i);
			}
			break;
		}
		return result;
	}
	
	public boolean isDateInfoFull() {
		boolean tmp = false;
		if(compareBinaryInfo(TokenType.dates).equals("111")) {
			tmp = true;
		}
		else {
			tmp = false;
		}
		return tmp;
	}
	
	public void adjustDay() {
		if(isDateInfoFull()) {
			MyCalendar cal = new MyCalendar();
			cal.setYear(Integer.parseInt(year));
			cal.setMonth(Integer.parseInt(month));
			cal.setDate(Integer.parseInt(date));
			day = cal.getDay();
		}
	}

	public String toString() {
		String jsonString = "{\"year\":\"" + this.year + "\", \"month\":\"" + this.month + "\", \"date\":\"" + this.date
				+ "\", \"day\":\"" + this.day + "\", \"hour\":\"" + this.hour + "\", \"minute\":\"" + this.minute
				+ "\"}";

		return jsonString;
	}
}
