package com.soonmark.myapp;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import com.soonmark.domain.DateTimeObject;

public class TestDataList {
	List<TestData> list;
	
	TestDataList(){
		list = new ArrayList<TestData>();

		// 테스트 케이스 입력
		addData("4월 9일 월요일 19시", new DateTimeObject(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false),
								new DateTimeObject(2019, 4, 9, DayOfWeek.TUESDAY, 19, 0, false));
		addData("4/9 화요일 7:00", new DateTimeObject(2018, 4, 9, DayOfWeek.MONDAY, 7, 0, false),
								new DateTimeObject(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false));
		addData("15년 4월 9일", new DateTimeObject(2015, 4, 9, DayOfWeek.THURSDAY, -1, -1, true),
								null);
	}
	
	public List<TestData> getList() {
		return list;
	}

	public void setList(List<TestData> list) {
		this.list = list;
	}

	public TestData getData(int i) {
		return list.get(i);
	}
	
	public void addData(String input, DateTimeObject firstOutput, DateTimeObject secondOutput) {
		list.add(new TestData(input, firstOutput, secondOutput));
	}
}
