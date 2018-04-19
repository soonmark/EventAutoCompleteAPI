package com.soonmark.domain;

public enum DateTimeEn {
	year(0, TokenType.dates.ordinal()), month(1, TokenType.dates.ordinal()), date(2, TokenType.dates.ordinal()),
	day(3, TokenType.days.ordinal()),
	am(4, TokenType.times.ordinal()), pm(5, TokenType.times.ordinal()),
	hour(6, TokenType.times.ordinal()), minute(7, TokenType.times.ordinal()),
	specialDate(8, TokenType.special.ordinal());
//	hour(4, TokenType.times.ordinal()), minute(5, TokenType.times.ordinal()),
//	specialDate(6, TokenType.special.ordinal());
	
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
