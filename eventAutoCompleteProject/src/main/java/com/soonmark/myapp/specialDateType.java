package com.soonmark.myapp;

public enum specialDateType {
	thisWeek("이번주"), nextWeek("다음주"), weekAfterNext("다다음주"), everyWeek("매주");

	String title;
	specialDateType(String title){
		this.title = title;
	}
	String getTitle() {
		return title;
	}
}
