package com.soonmark.managers;

import java.time.DayOfWeek;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.enums.DateTimeEn;
import com.soonmark.enums.DayOfWeekByLocale;

public class DateTimeManager {
	DateTimeDTO dateTimeDTO;
	private String specialDate;
	private boolean isFocusOnDay;
	private boolean[] hasInfo;
	private DateTimeEn focusToRepeat;
            
	public DateTimeManager() {
		dateTimeDTO = new DateTimeDTO();
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] { false, false, false, false, false, false };
	}

	public DateTimeManager(int year, int month, int date, DayOfWeek day, int hour, int minute) {
		dateTimeDTO = new DateTimeDTO(year, month, date, day, hour, minute, false);
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] { false, false, false, false, false, false };
	}

	public DateTimeManager(int year, int month, int date, DayOfWeek day, int hour, int minute, boolean isFocusOnDay) {
		dateTimeDTO = new DateTimeDTO(year, month, date, day, hour, minute, isFocusOnDay);
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		hasInfo = new boolean[] { false, false, false, false, false, false };
	}
	
	
	public DateTimeDTO getDateTimeDTO() {
		return dateTimeDTO;
	}

	public void setDateTimeDTO(DateTimeDTO dateTimeDTO) {
		this.dateTimeDTO = dateTimeDTO;
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

	public void setAllDate(DateTimeAdjuster cal) {
		setDay(cal.getDay());
		setYear(cal.getYear());
		setMonth(cal.getMonth());
		setDate(cal.getDate());
	}

	public void setProperDay() {
		DateTimeAdjuster tmpCal = new DateTimeAdjuster();
		tmpCal.setYear(this.getYear());
		tmpCal.setMonth(this.getMonth());
		tmpCal.setDate(this.getDate());

		setDay(tmpCal.getDay());
	}

	public void setAllDate(DateTimeManager dtObj) {
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
	
	public void setByDateTimeEn(DateTimeEn dt, int val) {
		switch(dt) {
		case year:
			setYear(val);
			break;
		case month:
			setMonth(val);
			break;
		case date:
			setDate(val);
			break;
		case hour:
			setHour(val);
			break;
		case minute:
			setMinute(val);
			break;
		default:
			break;

		}
	}
	public void setByDateTimeEn(DateTimeEn dt, DayOfWeek val) {
		switch(dt) {
		case day:
			setDay(val);
			break;
		default:
			break;
			
		}
	}
	
	public int getByDateTimeEn(DateTimeEn dt) {
		int val = 0;
		switch(dt) {
		case year:
			val = getYear();
			break;
		case month:
			val = getMonth();
			break;
		case date:
			val = getDate();
			break;
		case day:
			if(getDay() == null) {
				val = -1;
			}
			break;
		case hour:
			val = getHour();
			break;
		case minute:
			val = getMinute();
			break;
		default:
			break;

		}
		return val;
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
			DateTimeAdjuster cal = new DateTimeAdjuster();
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
