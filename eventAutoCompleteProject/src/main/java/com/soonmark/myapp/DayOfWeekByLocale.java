package com.soonmark.myapp;

public enum DayOfWeekByLocale {
	MONDAY("월"), TUESDAY("화"), WEDNESDAY("수"), THURSDAY("목"), FRIDAY("금"), SATURDAY("토"), SUNDAY("일");
	
	String dayOfWeekByLocale;
	DayOfWeekByLocale(String dayOfWeekByLocale){
		this.dayOfWeekByLocale = dayOfWeekByLocale;
	}
	
	public String getLocaleName() {
		return dayOfWeekByLocale;
	}
}
