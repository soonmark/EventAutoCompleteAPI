package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
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
	
	public List<LocalTime> getTimesAfter(LocalTime stdTime, DateTimeEn ampm) {
		List<LocalTime> timeList = getTimes(ampm);
		List<LocalDateTime> dtList = new ArrayList<LocalDateTime>();
		
		for(int i = 0 ; i < timeList.size() ; i++) {
			dtList.add(timeList.get(i).atDate(LocalDate.now()));
		}
		
		// 자정 넘어가는 걸 생각하기 위해 DateTime으로 바꿈.
		LocalDateTime stdDT = stdTime.atDate(LocalDate.now());
		
		int plusAmount = 0;
		while(dtList.get(0).plusMinutes(plusAmount).isBefore(stdDT)){
			plusAmount += 30;
		}
		
		for(int i = 0 ; i < dtList.size() ; i++) {
			dtList.set(i, dtList.get(i).plusMinutes(plusAmount));
			timeList.set(i, timeList.get(i).plusMinutes(plusAmount));
		}
		
		for(int i = 0 ; i < dtList.size() ;) {
			// 오늘에서 내일로 넘어가는 12시보다 늦으면 제거
			if(dtList.get(i).isAfter(LocalTime.of(0, 0).atDate(LocalDate.now().plusDays(1)))) {
//				dtList.remove(i);
				timeList.remove(i);
			}
			else {
				i++;
			}
		}
		
		return timeList;
	}
	
}
