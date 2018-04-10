package com.soonmark.domain;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import com.soonmark.enums.DateTimeEn;
import com.soonmark.enums.DayOfWeekByLocale;

public class DateTimeObject {
	DateTimeDTO dateTimeDTO;
	private String specialDate;
	private boolean isFocusOnDay;
	private boolean[] hasInfo;
	private DateTimeEn focusToRepeat;
            
	public DateTimeObject() {
		dateTimeDTO = new DateTimeDTO();
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] { false, false, false, false, false, false };
	}

	public DateTimeObject(int year, int month, int date, DayOfWeek day, int hour, int minute) {
		dateTimeDTO = new DateTimeDTO(year, month, date, day, hour, minute, false);
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] { false, false, false, false, false, false };
	}

	public DateTimeObject(int year, int month, int date, DayOfWeek day, int hour, int minute, boolean isFocusOnDay) {
		dateTimeDTO = new DateTimeDTO(year, month, date, day, hour, minute, isFocusOnDay);
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] { false, false, false, false, false, false };
	}

	public DateTimeEn getFocusToRepeat() {
		return focusToRepeat;
	}

	public void setFocusToRepeat(DateTimeEn focusToRepeat) {
		this.focusToRepeat = focusToRepeat;
	}

	public boolean isAllDayEvent() {
		return dateTimeDTO.isAllDayEvent();
	}

	public void setAllDayEvent(boolean isAllDayEvent) {
		this.dateTimeDTO.setAllDayEvent(isAllDayEvent);
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

	public void setAllDate(DateTimeObject dtObj) {
		setDay(dtObj.getDay());
		setYear(dtObj.getYear());
		setMonth(dtObj.getMonth());
		setDate(dtObj.getDate());
		setSpecialDate(dtObj.getSpecialDate());
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

	public int getYear() {
		return dateTimeDTO.getYear();
	}

	public void setYear(int year) {
		dateTimeDTO.setYear(year);
	}

	public int getMonth() {
		return dateTimeDTO.getMonth();
	}

	public void setMonth(int month) {
		dateTimeDTO.setMonth(month);
	}

	public int getDate() {
		return dateTimeDTO.getDate();
	}

	public void setDate(int date) {
		dateTimeDTO.setDate(date);
	}

	public DayOfWeek getDay() {
		return dateTimeDTO.getDay();
	}

	public void setDay(DayOfWeek day) {
		dateTimeDTO.setDay(day);
	}

	public int getHour() {
		return dateTimeDTO.getHour();
	}

	public void setHour(int hour) {
		dateTimeDTO.setHour(hour);
	}

	public int getMinute() {
		return dateTimeDTO.getMinute();
	}

	public void setMinute(int minute) {
		dateTimeDTO.setMinute(minute);
	}

	public boolean isDateInfoFull() {
		boolean tmp = false;
		if (hasInfo[DateTimeEn.year.ordinal()] && hasInfo[DateTimeEn.month.ordinal()]
				&& hasInfo[DateTimeEn.date.ordinal()]) {
			tmp = true;
		}
		return tmp;
	}

	public void adjustDay() {
		if (isDateInfoFull()) {
			MyLocalDateTime cal = new MyLocalDateTime();
			cal.setYear(dateTimeDTO.getYear());
			cal.setMonth(dateTimeDTO.getMonth());
			cal.setDate(dateTimeDTO.getDate());
			dateTimeDTO.setDay(cal.getDay());
		}
	}

	public String getDayOfWeekByLocale(String localeWeekDay) {
		for (DayOfWeekByLocale day : DayOfWeekByLocale.values()) {
			if (day.getLocaleName().equals(localeWeekDay)) {
				return day.name();
			}
		}
		return null;
	}

	public String toString() {
		return dateTimeDTO.toString();
	}
}
