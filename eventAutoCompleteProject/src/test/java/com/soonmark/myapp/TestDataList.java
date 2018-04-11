package com.soonmark.myapp;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import com.soonmark.managers.DateTimeManager;

public class TestDataList {
	List<TestData> list;
	
	TestDataList(){
		list = new ArrayList<TestData>();

		// 테스트 케이스 입력
		addData("4월 9일 월요일 19시",
				new DateTimeManager(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false),
				new DateTimeManager(2019, 4, 9, DayOfWeek.TUESDAY, 19, 0, false));
		addData("4/9 화요일 7:00",
				new DateTimeManager(2018, 4, 9, DayOfWeek.MONDAY, 7, 0, false),
				new DateTimeManager(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false));
		addData("15년 4월 9일",
				new DateTimeManager(2015, 4, 9, DayOfWeek.THURSDAY, -1, -1, true),
				null);
		addData("2018-03-19",
				new DateTimeManager(2018, 3, 19, DayOfWeek.MONDAY, -1, -1, true),
				null);
		addData("4-9",
				new DateTimeManager(2018, 4, 9, DayOfWeek.MONDAY, -1, -1, true),
				new DateTimeManager(2019, 4, 9, DayOfWeek.TUESDAY, -1, -1, true));
		addData("11월 1시",
				new DateTimeManager(2018, 11, 1, DayOfWeek.THURSDAY, 1, 0, false),
				new DateTimeManager(2018, 11, 1, DayOfWeek.THURSDAY, 13, 0, false));
		
		
		// 자연어 케이스
		addData("4월 3일에 친구 모임 1시 서현",
				new DateTimeManager(2018, 4, 3, DayOfWeek.TUESDAY, 1, 0, false),
				new DateTimeManager(2018, 4, 3, DayOfWeek.TUESDAY, 13, 0, false));
		addData("19일날 엄마랑 외식",
				new DateTimeManager(2018, 4, 19, DayOfWeek.THURSDAY, -1, -1, true),
				new DateTimeManager(2018, 5, 19, DayOfWeek.SATURDAY, -1, -1, true));
//		addData("이번주 13일",
//				new DateTimeHandler(2018, 4, 5, DayOfWeek.THURSDAY, -1, -1, true),
//				new DateTimeHandler(2018, 4, 13, DayOfWeek.FRIDAY, -1, -1, true));
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
	
	public void addData(String input, DateTimeManager firstOutput, DateTimeManager secondOutput) {
		list.add(new TestData(input, firstOutput, secondOutput));
	}
}
