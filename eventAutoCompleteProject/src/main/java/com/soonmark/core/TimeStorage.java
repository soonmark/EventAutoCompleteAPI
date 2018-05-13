package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.soonmark.domain.DateTimeEn;

public class TimeStorage {

	List<LocalTime> amTimes;
	List<LocalTime> pmTimes;
	List<LocalTime> minTimes;
	List<LocalTime> defaultTimes;

	public TimeStorage() {
		this.amTimes = new ArrayList<LocalTime>();
		this.pmTimes = new ArrayList<LocalTime>();
		this.minTimes = new ArrayList<LocalTime>();
		this.defaultTimes = new ArrayList<LocalTime>();
		initTimes();
	}

	// 7시부터 14시 30분까지
	private void initTimes() {
		
		defaultTimes.add(LocalTime.of(7, 0));
		defaultTimes.add(LocalTime.of(8, 0));
		defaultTimes.add(LocalTime.of(9, 0));
		defaultTimes.add(LocalTime.of(10, 0));
		defaultTimes.add(LocalTime.of(11, 0));
		defaultTimes.add(LocalTime.of(12, 0));
		defaultTimes.add(LocalTime.of(13, 0));
		defaultTimes.add(LocalTime.of(14, 0));
		defaultTimes.add(LocalTime.of(15, 0));
		defaultTimes.add(LocalTime.of(16, 0));

		amTimes.add(LocalTime.of(0, 0));
		amTimes.add(LocalTime.of(1, 0));
		amTimes.add(LocalTime.of(2, 0));
		amTimes.add(LocalTime.of(3, 0));
		amTimes.add(LocalTime.of(4, 0));
		amTimes.add(LocalTime.of(5, 0));
		amTimes.add(LocalTime.of(6, 0));
		amTimes.add(LocalTime.of(7, 0));
		amTimes.add(LocalTime.of(8, 0));
		amTimes.add(LocalTime.of(9, 0));

		pmTimes.add(LocalTime.of(12, 0));
		pmTimes.add(LocalTime.of(13, 0));
		pmTimes.add(LocalTime.of(14, 0));
		pmTimes.add(LocalTime.of(15, 0));
		pmTimes.add(LocalTime.of(16, 0));
		pmTimes.add(LocalTime.of(17, 0));
		pmTimes.add(LocalTime.of(18, 0));
		pmTimes.add(LocalTime.of(19, 0));
		pmTimes.add(LocalTime.of(20, 0));
		pmTimes.add(LocalTime.of(21, 0));
		
		minTimes.add(LocalTime.of(0, 0));
		minTimes.add(LocalTime.of(0, 10));
		minTimes.add(LocalTime.of(0, 20));
		minTimes.add(LocalTime.of(0, 30));
		minTimes.add(LocalTime.of(0, 40));
		minTimes.add(LocalTime.of(0, 50));
	}

	public List<LocalTime> getTimes() {
		return defaultTimes;
	}

	public List<LocalTime> getTimes(DateTimeEn ampm) {
		if(ampm == DateTimeEn.am) {
			return amTimes;
		}
		else if(ampm == DateTimeEn.pm){
			return pmTimes;
		}
		else {
			return defaultTimes;
		}
	}

	public void setTimes(List<LocalTime> times, DateTimeEn ampm) {
		if(ampm == DateTimeEn.am) {
			this.amTimes = times;
		}
		else if(ampm == DateTimeEn.pm){
			this.pmTimes = times;
		}
		else {
			this.defaultTimes = times;
		}
	}
	
	public List<LocalTime> getMinTimesWith(int hour) {
		List<LocalTime> minList = minTimes;
		
		for(int i = 0 ; i < minList.size() ; i++) {
			minList.set(i, minList.get(i).withHour(hour));
		}
		
		return minTimes;
	}

	public List<LocalTime> getTimesAfter(LocalTime stdTime, DateTimeEn ampm) {
		List<LocalTime> timeList = getTimes(ampm);
		List<LocalDateTime> dtList = new ArrayList<LocalDateTime>();
		
		for(int i = 0 ; i < timeList.size() ; i++) {
			dtList.add(timeList.get(i).atDate(LocalDate.now()));
		}
		
		// 자정 넘어가는 걸 생각하기 위해 DateTime으로 바꿈.
		LocalDateTime stdDT = stdTime.atDate(LocalDate.now());
		
		int plusAmount = 0;
		while(dtList.get(0).plusHours(plusAmount).isBefore(stdDT)){
			plusAmount += 1;
		}
		
		for(int i = 0 ; i < dtList.size() ; i++) {
			dtList.set(i, dtList.get(i).plusHours(plusAmount));
			timeList.set(i, timeList.get(i).plusHours(plusAmount));
		}
		
		for(int i = 0 ; i < dtList.size() ;) {
			// 오늘에서 내일로 넘어가는 12시보다 늦으면 제거
			if(!dtList.get(i).isBefore(LocalTime.of(0, 0).atDate(LocalDate.now().plusDays(1)))) {
				timeList.remove(i);
				dtList.remove(i);
			}
			else {
				i++;
			}
		}
		
		return timeList;
	}
	
}
