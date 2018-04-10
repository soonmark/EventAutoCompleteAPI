package com.soonmark.domain;

public enum specialDateTypeNeedsDay {
	thisWeek("이번주"), nextWeek("다음주"), weekAfterNext("다다음주");

	String title;
	specialDateTypeNeedsDay(String title){
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
}
