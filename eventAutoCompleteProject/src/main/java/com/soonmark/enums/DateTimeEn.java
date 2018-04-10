package com.soonmark.enums;

public enum DateTimeEn {
	year(0, TokenType.dates.ordinal()), month(1, TokenType.dates.ordinal()), date(2, TokenType.dates.ordinal()),
	day(3, TokenType.days.ordinal()),
	hour(4, TokenType.times.ordinal()), minute(5, TokenType.times.ordinal());
	
	int dateTime;
	int typeNum;
	DateTimeEn(int dt, int typeNum) {
		dateTime = dt;
		this.typeNum = typeNum;
	}
	public int getInteger() {
		return dateTime;
	}
	public int getTypeNum() {
		return typeNum;
	}
	
	// Enum 클래스는 name 메소드 제공함.
}
