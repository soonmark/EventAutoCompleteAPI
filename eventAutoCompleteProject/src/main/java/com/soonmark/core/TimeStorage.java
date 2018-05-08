package com.soonmark.core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.soonmark.domain.DateTimeEn;

public class TimeStorage {

	List<LocalTime> amTimes;
	List<LocalTime> pmTimes;

	public TimeStorage() {
		this.amTimes = new ArrayList<LocalTime>();
		this.pmTimes = new ArrayList<LocalTime>();
		initTimes();
	}

	// 7시부터 14시 30분까지
	private void initTimes() {
		amTimes.add(LocalTime.of(7, 0));
		amTimes.add(LocalTime.of(7, 30));
		amTimes.add(LocalTime.of(8, 0));
		amTimes.add(LocalTime.of(8, 30));
		amTimes.add(LocalTime.of(9, 0));
		amTimes.add(LocalTime.of(9, 30));
		amTimes.add(LocalTime.of(10, 0));
		amTimes.add(LocalTime.of(10, 30));
		amTimes.add(LocalTime.of(11, 0));
		amTimes.add(LocalTime.of(11, 30));

		pmTimes.add(LocalTime.of(12, 0));
		pmTimes.add(LocalTime.of(12, 30));
		pmTimes.add(LocalTime.of(13, 0));
		pmTimes.add(LocalTime.of(13, 30));
		pmTimes.add(LocalTime.of(14, 0));
		pmTimes.add(LocalTime.of(14, 30));
		pmTimes.add(LocalTime.of(15, 0));
		pmTimes.add(LocalTime.of(15, 30));
		pmTimes.add(LocalTime.of(16, 0));
		pmTimes.add(LocalTime.of(16, 30));
	}

	public List<LocalTime> getTimes(DateTimeEn ampm) {
		if(ampm == DateTimeEn.am) {
			return amTimes;
		}
		else if(ampm == DateTimeEn.pm){
			return pmTimes;
		}
		else {
			return null;
		}
	}

	public void setTimes(List<LocalTime> times, DateTimeEn ampm) {
		if(ampm == DateTimeEn.am) {
			this.amTimes = times;
		}
		else if(ampm == DateTimeEn.pm){
			this.pmTimes = times;
		}
	}
}
