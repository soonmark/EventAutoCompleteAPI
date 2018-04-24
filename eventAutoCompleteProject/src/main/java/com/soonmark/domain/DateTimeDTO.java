package com.soonmark.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeDTO {

	private String date;
	private String time;
	private boolean allDayEvent;
	
	public DateTimeDTO() {
		this.date = "";
		this.time = "";
		this.allDayEvent = true;
	}

	public DateTimeDTO(String date, String time, boolean allDayEvent) {
		this.date = date;
		this.time = time;
		this.allDayEvent = allDayEvent;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isAllDayEvent() {
		return allDayEvent;
	}

	public void setAllDayEvent(boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}

	public LocalDate toLocalDate() {
		LocalDate localDate = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			localDate = LocalDate.parse(this.date, formatter);
		} catch (DateTimeParseException exc) {
		}
		return localDate;
	}

	public LocalTime toLocalTime() {
		LocalTime localTime = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm");
			localTime = LocalTime.parse(this.time, formatter);
		}catch(DateTimeParseException exc) {
		}
		return localTime;
	}
	
	@Override
	public String toString() {
		String jsonString = "";
		jsonString = "{\"date\":\"" + this.date
				+ "\", \"time\":\"" + this.time
				+ "\", \"allDayEvent\":\"" + this.allDayEvent + "\"}";

		return jsonString;
	}
}
