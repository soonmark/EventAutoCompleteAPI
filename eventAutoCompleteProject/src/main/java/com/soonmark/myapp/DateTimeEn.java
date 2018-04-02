package com.soonmark.myapp;

public enum DateTimeEn {
	year(0), month(1), date(2), day(3), hour(4), minute(5);
	
	int dateTime;
	DateTimeEn(int dt) {
		dateTime = dt;
	}
	int getInteger() {
		return dateTime;
	}
	
	// Enum 클래스는 name 메소드 제공함.
}
