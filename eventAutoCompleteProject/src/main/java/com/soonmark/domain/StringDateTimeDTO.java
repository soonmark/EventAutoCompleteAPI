package com.soonmark.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringDateTimeDTO {

	private String date;
	private String time;
	
	public StringDateTimeDTO() {
		this.date = "";
		this.time = "";
	}

	public StringDateTimeDTO(String date, String time) {
		this.date = date;
		this.time = time;
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

	private LocalDate toLocalDate() {
		LocalDate localDate = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			localDate = LocalDate.parse(this.date, formatter);
		} catch (DateTimeParseException exc) {
		}
		return localDate;
	}

	private LocalTime toLocalTime() {
		LocalTime localTime = null;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm");
			localTime = LocalTime.parse(this.time, formatter);
		}catch(DateTimeParseException exc) {
			try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:-");
			localTime = LocalTime.parse(this.time, formatter);
			} catch(DateTimeParseException ex) {
				
			}
		}
		return localTime;
	}
	
	public DateTimeDTO toDateTimeDTO() {
		DateTimeDTO dateTimeDTO = new DateTimeDTO(this.toLocalDate(), this.toLocalTime(), this.noMin());

		return dateTimeDTO;
	}
	
	private boolean noMin() {
		try {
			LocalTime.parse(this.time, DateTimeFormatter.ofPattern("a hh:-"));
			return true;
		}catch(DateTimeParseException exc) {
			return false;
		}
	}

	@Override
	public String toString() {
		String jsonString = "";
		jsonString = "{\"date\":\"" + this.date
				+ "\", \"time\":\"" + this.time + "\"}";

		return jsonString;
	}
}
