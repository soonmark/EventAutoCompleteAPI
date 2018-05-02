package com.soonmark.core;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeStorage {

	List<LocalTime> times;

	public TimeStorage() {
		this.times = new ArrayList<LocalTime>();
		initTimes();
	}

	// 7시부터 14시 30분까지
	private void initTimes() {
		times.add(LocalTime.of(7, 0));
		times.add(LocalTime.of(7, 30));
		times.add(LocalTime.of(8, 0));
		times.add(LocalTime.of(8, 30));
		times.add(LocalTime.of(9, 0));
		times.add(LocalTime.of(9, 30));
		times.add(LocalTime.of(10, 0));
		times.add(LocalTime.of(10, 30));
		times.add(LocalTime.of(11, 0));
		times.add(LocalTime.of(11, 30));
	}

	public List<LocalTime> getTimes() {
		return times;
	}

	public void setTimes(List<LocalTime> times) {
		this.times = times;
	}
}
