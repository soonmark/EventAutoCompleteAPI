package com.soonmark.domain;

public enum SpecialDateType {
	today("오늘"), tomorrow("내일"), dayAfterTomorrow("모레"), thisWeek("이번주"), nextWeek("다음주"), weekAfterNext("다다음주");

	String title;
	SpecialDateType(String title){
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
}
