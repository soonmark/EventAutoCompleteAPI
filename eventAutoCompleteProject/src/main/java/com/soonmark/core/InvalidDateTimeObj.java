package com.soonmark.core;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.DayOfWeekByLocale;
import com.soonmark.domain.Priority;

public class InvalidDateTimeObj {
	private int year;
	private int month;
	private int date;
	private DayOfWeek day;
	private DateTimeEn ampm;
	private int hour;
	private int minute;
	private boolean isAllDayEvent;
	
	private String specialDate;
	private boolean isFocusOnDay;
	private boolean[] hasInfo;
	private DateTimeEn focusToRepeat;
	private boolean isLeapYear;
	private Priority priority;

	public InvalidDateTimeObj() {
		this.year = AppConstants.NO_DATA;
		this.month = AppConstants.NO_DATA;
		this.date = AppConstants.NO_DATA;
		this.day = AppConstants.NO_DATA_FOR_DAY;
		this.hour = AppConstants.NO_DATA;
		this.minute = AppConstants.NO_DATA;
		this.isAllDayEvent = false;
		this.ampm = AppConstants.NO_DATA_FOR_AMPM;
		
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		specialDate = AppConstants.NO_DATA_FOR_SPECIALDATE;
		hasInfo = new boolean[] { false, false, false, false, false, false, false, false, false };
		isLeapYear = false;
		priority = Priority.none;
	}

	public InvalidDateTimeObj(int year, int month, int date, DayOfWeek day, int hour, int minute) {
		this.year = year;
		this.month = month;
		this.date = date;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.isAllDayEvent = false;
		this.ampm = AppConstants.NO_DATA_FOR_AMPM;
		
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		specialDate = AppConstants.NO_DATA_FOR_SPECIALDATE;
		hasInfo = new boolean[] { false, false, false, false, false, false, false, false, false };
		isLeapYear = false;
		priority = Priority.none;
	}

	public InvalidDateTimeObj(int year, int month, int date, DayOfWeek day, int hour, int minute, boolean isAllDayEvent) {
		this.year =year;
		this.month =month;
		this.date = date;
		this.day = day;
		this.hour = hour;
		this.minute = minute;
		this.isAllDayEvent = isAllDayEvent;
		this.ampm = AppConstants.NO_DATA_FOR_AMPM;
		
		this.isFocusOnDay = false;
		this.focusToRepeat = null;
		specialDate = AppConstants.NO_DATA_FOR_SPECIALDATE;
		hasInfo = new boolean[] { false, false, false, false, false, false, false, false, false };
		isLeapYear = false;
		priority = Priority.none;
	}
	
	public DateTimeEn getAmpm() {
		return ampm;
	}

	public void setAmpm(DateTimeEn ampm) {
		this.ampm = ampm;
	}

	public Priority getPriority() {
		return priority;
	}
	
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	public boolean[] getHasInfo() {
		return hasInfo;
	}

	public void setHasInfo(boolean[] hasInfo) {
		this.hasInfo = hasInfo;
	}

	public void copyAllExceptForDayFrom(InvalidDateTimeObj origin) {
		for(DateTimeEn dtEn : DateTimeEn.values()) {
			if(dtEn == DateTimeEn.day) {
				this.setDay(null);
			}else {
				this.setByDateTimeEn(dtEn, origin.getByDateTimeEn(dtEn));
			}
		}
		this.setSpecialDate(origin.getSpecialDate());
		this.setFocusOnDay(origin.isFocusOnDay());
		this.setHasInfo(origin.getHasInfo());
		this.setFocusToRepeat(origin.getFocusToRepeat());
		this.setLeapYear(origin.isLeapYear());
		this.setPriority(origin.getPriority());
	}
	
	public boolean isLeapYear() {
		return isLeapYear;
	}

	public void setLeapYear(boolean isLeapYear) {
		this.isLeapYear = isLeapYear;
	}

	public StringDateTimeDTO tostrDtDTO() {
		
		String formattedDate = year + "";
		if(year != AppConstants.INVALID_INPUT_CHARACTER) {
			LocalDate localDate = LocalDate.of(year,  month,  date);
		
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formattedDate = localDate.format(formatter);
		}
		
		String formattedTime = "";
		if(isAllDayEvent == false && hour != -1) {
			LocalTime localTime = LocalTime.of(hour,  minute);
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm");
			formattedTime = localTime.format(formatter);
		}
		
		StringDateTimeDTO dateTimeDTO = new StringDateTimeDTO(formattedDate, formattedTime);
		
		return dateTimeDTO;
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

	public void setAllDate(InvalidDateTimeObj dtObj) {
		setDay(dtObj.getDay());
		setYear(dtObj.getYear());
		setMonth(dtObj.getMonth());
		setDate(dtObj.getDate());
		setSpecialDate(dtObj.getSpecialDate());
	}
	
	public void setAllTime(InvalidDateTimeObj dtObj) {
		setHour(dtObj.getHour());
		setMinute(dtObj.getMinute());
	}
	
	public void setDateTime(InvalidDateTimeObj dtObj) {
		setYear(dtObj.getYear());
		setMonth(dtObj.getMonth());
		setDate(dtObj.getDate());
		setHour(dtObj.getHour());
		setMinute(dtObj.getMinute());
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
		case day:
			setDay(DayOfWeek.values()[val]);
			break;
		case am:
		case pm:
			setAmpm(dt);
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
			if(getDay() == AppConstants.NO_DATA_FOR_DAY) {
				val = AppConstants.NO_DATA;
			}
			else {
				val = getDay().ordinal();
			}
			break;
		case am:
		case pm:
			if(getAmpm() == AppConstants.NO_DATA_FOR_AMPM) {
				val = AppConstants.NO_DATA;
			}
			else {
				val = getAmpm().ordinal();
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
		return this.year;
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
		this.minute = minute;
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
			DateTimeAdjuster tmpDTAdjuster = new DateTimeAdjuster();
			tmpDTAdjuster.setYear(year);
			tmpDTAdjuster.setMonth(month);
			tmpDTAdjuster.setDate(date);
			day = tmpDTAdjuster.getDay();
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

	public DateTimeDTO toDtDTO() {
		LocalDate localDate = null;
		LocalTime localTime = null;
		try {
			localDate = LocalDate.of(year, month, date);
		}
		catch(DateTimeException ex) {
		}
		
		try {
			localTime = LocalTime.of(hour, minute);
		}
		catch(DateTimeException ex) {
		}
		
		DateTimeDTO dtDTO = new DateTimeDTO(localDate, localTime);

		return dtDTO;
	}

	public LocalTime getLocalTime() {
		return this.toDtDTO().getTime();
	}
	public LocalDate getLocalDate() {
		return this.toDtDTO().getDate();
	}
}
