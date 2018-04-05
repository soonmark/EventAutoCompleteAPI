package com.soonmark.myapp;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateVO {
	int year;
	int month;
	int date;
	String specialDate;
	DayOfWeek day;
	int hour;
	int minute;
	boolean isFocusOnDay;
	boolean ignoreDay;
	boolean[] hasInfo;
	boolean isAllDayEvent;
	DateTimeEn focusToRepeat;

	DateVO() {
		this.year = -1;
		this.month = -1;
		this.date = -1;
		this.day = null;
		this.hour = -1;
		this.minute = -1;
		this.isFocusOnDay = false;
		this.ignoreDay = false;
		this.isAllDayEvent = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] {false, false, false, false, false, false};
	}

	DateVO(int year, int month, int date, DayOfWeek day, int hour, int minute) {
		this.year = year;
		this.month = month;
		this.date = date;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.isFocusOnDay = false;
		this.ignoreDay = false;
		this.isAllDayEvent = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] {false, false, false, false, false, false};
	}
	
	public DateTimeEn getFocusToRepeat() {
		return focusToRepeat;
	}

	public void setFocusToRepeat(DateTimeEn focusToRepeat) {
		this.focusToRepeat = focusToRepeat;
	}

	public boolean isAllDayEvent() {
		return isAllDayEvent;
	}

	public void setAllDayEvent(boolean isAllDayEvent) {
		this.isAllDayEvent = isAllDayEvent;
	}

	public void setAllDate(MyLocalDateTime cal) {
		setDay(cal.getDay());
		setYear(cal.getYear());
		setMonth(cal.getMonth());
		setDate(cal.getDate());
	}
	
	public void setProperDay() {
		MyLocalDateTime tmpCal = new MyLocalDateTime();
		tmpCal.setYear(this.getYear());
		tmpCal.setMonth(this.getMonth());
		tmpCal.setDate(this.getDate());

		setDay(tmpCal.getDay());
	}

	public void setAllDate(DateVO vo) {
		setDay(vo.getDay());
		setYear(vo.getYear());
		setMonth(vo.getMonth());
		setDate(vo.getDate());
		setSpecialDate(vo.getSpecialDate());
	}
	
	public String getSpecialDate() {
		return specialDate;
	}
	
	public void setSpecialDate(String specialDate) {
		this.specialDate = specialDate;
	}

	public boolean hasInfo(int idx) {
		return hasInfo[idx];
	}
	
	public void setHasInfo(int idx, boolean flag) {
		hasInfo[idx] = flag;
	}
	
	public boolean isFocusOnDay() {
		return isFocusOnDay;
	}
	
	public void setFocusOnDay(boolean isFocusOnDay) {
		this.isFocusOnDay = isFocusOnDay;
	}
	
	public void set(int idx, int val) {
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
			this.day = DayOfWeek.of(val);
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
		// 0분, 1분 등이면 00분, 01분 으로 세팅
//		if(minute.length() == 1) {
//			this.minute = "0" +  minute;
//		}
//		else {
			this.minute = minute;
//		}
	}
	
	public boolean isDateInfoFull() {
		boolean tmp = false;
		if(hasInfo[DateTimeEn.year.ordinal()]
			&& hasInfo[DateTimeEn.month.ordinal()]
			&& hasInfo[DateTimeEn.date.ordinal()]) {
			tmp = true;
		}
		return tmp;
	}
	
	public void adjustDay() {
		if(isDateInfoFull()) {
			MyLocalDateTime cal = new MyLocalDateTime();
			cal.setYear(year);
			cal.setMonth(month);
			cal.setDate(date);
			day = cal.getDay();
		}
	}
	
	public String getDayOfWeekByLocale(String localeWeekDay){
        for (DayOfWeekByLocale day : DayOfWeekByLocale.values()) {
            if (day.getLocaleName().equals(localeWeekDay)) {
                return day.name();
            }
        }
        return null;
    }

	public String toString() {
		String jsonString = "{\"year\":\"" + String.format("%04d", this.year)
				+ "\", \"month\":\"" + String.format("%02d", this.month)
				+ "\", \"date\":\"" + String.format("%02d", this.date)
				+ "\", \"day\":\"" + this.day.getDisplayName(TextStyle.FULL, Locale.KOREA)
				+ "\", \"isAllDayEvent\":\"" + this.isAllDayEvent
				+ "\", \"hour\":\"" + String.format("%02d", this.hour)
				+ "\", \"minute\":\"" + String.format("%02d", this.minute)
				+ "\"}";

		return jsonString;
	}
}
