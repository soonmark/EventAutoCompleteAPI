package com.soonmark.domain;

public enum specialDateTypeWithoutDay {
	today("오늘"), tomorrow("내일"), dayAfterTomorrow("모레");

	String title;
	specialDateTypeWithoutDay(String title){
		this.title = title;
	}
	String getTitle() {
		return title;
	}
}
