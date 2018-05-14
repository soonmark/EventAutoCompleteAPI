package com.soonmark.domain;

import java.time.LocalDate;
import java.time.LocalTime;

import com.soonmark.core.InvalidDateTimeObj;

// 이 클래스는 LocalDate와 LocalTime의 값을 null 허용으로 사용하기 위함.
// 기존 LocalDateTime 클래스는 LocalDate와 LocalTime 이 not null 임.
public class DateTimeDTO {
	private LocalDate date;
	private LocalTime time;
	private boolean noMin;

	public DateTimeDTO() {
		this.date = null;
		this.time = null;
		noMin = false;
	}
	
	public DateTimeDTO(LocalDate date, LocalTime time) {
		this.date = date;
		this.time = time;
		noMin = false;
	}
	
	public DateTimeDTO(LocalDate date, LocalTime time, boolean noMin) {
		this.date = date;
		this.time = time;
		this.noMin = noMin;
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
	
	public boolean isNoMin() {
		return noMin;
	}
	
	public InvalidDateTimeObj toInvalidDateTimeObj() {
		InvalidDateTimeObj obj = new InvalidDateTimeObj();
		boolean noData = true;
		if(date != null) {
			obj.setDate(date.getDayOfMonth());
			obj.setMonth(date.getMonthValue());
			obj.setYear(date.getYear());
			noData = false;
		}
		if(time != null) {
			obj.setHour(time.getHour());
			if(noMin) {
				obj.setMinute(-1);
			}
			else {
				obj.setMinute(time.getMinute());
			}
			noData = false;
		}
		if(noData) {
			return null;
		}
		return obj;
	}
}
