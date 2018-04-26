package com.soonmark.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeDTO {
	LocalDate date;
	LocalTime time;

	public DateTimeDTO() {
		this.date = null;
		this.time = null;
	}
	
	public DateTimeDTO(LocalDate date, LocalTime time) {
		this.date = date;
		this.time = time;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}
}
