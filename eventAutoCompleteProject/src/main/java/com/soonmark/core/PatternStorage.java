package com.soonmark.core;

import java.util.ArrayList;
import java.util.List;

public class PatternStorage {
	// 년월일 패턴
	List<String> datePatterns = new ArrayList<String>();
	// 요일 패턴
	List<String> dayPatterns = new ArrayList<String>();
	// 그 외 특이 패턴
	List<String> specialDatePatterns = new ArrayList<String>();

	// 시간 패턴
	List<String> timePatterns = new ArrayList<String>();
	
	PatternStorage(){
		initPatterns();
	}

	public void initPatterns() {
		initDatePatterns();
		initTimePatterns();
		initDayPatterns();
		initSpecialDatePatterns();
	}

	public void initDatePatterns() {
		datePatterns
				.add("^(.*)(?<year>[0-9]{4})-(?<month>0?[1-9]|1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 2018-(0)3-9
		datePatterns
				.add("^(.*)(?<year>[0-9]{4})/(?<month>0?[1-9]|1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 2018/(0)3/9
		datePatterns.add(
				"^(.*)(?<year>[0-9]{4})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 2018.(0)3.9
		datePatterns
				.add("^(.*)(?<year>[0-9]{4})년 (?<month>0?[1-9]|1[0-2])월 (?<date>[1-9]|[1-2][0-9]|3[0-1])일(|[^0-9].*)$"); // 2018년(0)3월9일

		datePatterns.add("^(.*)(?<year>[0-9]{4})-(?<month>0?[1-9]|1[0-2])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 2018-(0)3-09
		datePatterns.add("^(.*)(?<year>[0-9]{4})/(?<month>0?[1-9]|1[0-2])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 2018/(0)3/09
		datePatterns.add("^(.*)(?<year>[0-9]{4})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 2018.(0)3.09
		datePatterns.add("^(.*)(?<year>[0-9]{4})년 (?<month>0?[1-9]|1[0-2])월 (?<date>0[1-9]|[1-2][0-9]|3[0-1])일(.*)$"); // 2018년(0)3월09일

		datePatterns
				.add("^(.*)(?<year>[0-9]{2})-(?<month>0?[1-9]|1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 18-(0)3-9
		datePatterns
				.add("^(.*)(?<year>[0-9]{2})/(?<month>0?[1-9]|1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 18/(0)3/9
		datePatterns.add(
				"^(.*)(?<year>[0-9]{2})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 18.(0)3.9
		datePatterns
				.add("^(.*)(?<year>[0-9]{2})년 (?<month>0?[1-9]|1[0-2])월 (?<date>[1-9]|[1-2][0-9]|3[0-1])일(|[^0-9].*)$"); // 18년(0)3월9일

		datePatterns.add("^(.*)(?<year>[0-9]{2})-(?<month>0?[1-9]|1[0-2])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 18-(0)3-09
		datePatterns.add("^(.*)(?<year>[0-9]{2})/(?<month>0?[1-9]|1[0-2])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 18/(0)3/09
		datePatterns.add("^(.*)(?<year>[0-9]{2})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 18.(0)3.09
		datePatterns.add("^(.*)(?<year>[0-9]{2})년 (?<month>0?[1-9]|1[0-2])월 (?<date>0[1-9]|[1-2][0-9]|3[0-1])일(.*)$"); // 18년(0)3월09일

		// 20180319

		datePatterns.add("^(.*)(?<month>1[0-2])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11-09
		datePatterns.add("^(|.*[^1])(?<month>[1-9])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3-09
		datePatterns.add("^(.*)(?<month>1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11-9
		datePatterns.add("^(|.*[^1])(?<month>[1-9])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3-9
		datePatterns.add("^(.*)(?<month>1[0-2])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11.09
		datePatterns.add("^(|.*[^1])(?<month>[1-9])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3.09
		datePatterns.add("^(.*)(?<month>1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11.9
		datePatterns.add("^(|.*[^1])(?<month>[1-9])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3.9
		datePatterns.add("^(.*)(?<month>1[0-2])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11/09
		datePatterns.add("^(|.*[^1])(?<month>[1-9])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3/09
		datePatterns.add("^(.*)(?<month>1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11/9
		datePatterns.add("^(|.*[^1])(?<month>[1-9])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3/9

		datePatterns.add("^(.*)(?<year>[1-2][0-9][0-9][0-9])년(.*)$"); // 2018년
		datePatterns.add("^(.*)(?<year>[0-9][0-9])년(.*)$"); // 94년, 18년
		// 일만 입력받기
		datePatterns.add("^(.*)(?<date>[1-2][0-9]|3[0-1])일(.*)$"); // 19일
		datePatterns.add("^(|.*[^1-3])(?<date>[1-9])일(.*)$"); // 1일
		// 월만 입력받기
		datePatterns.add("^(.*)(?<month>1[0-2])월(.*)$"); // 12월
		datePatterns.add("^(|.*[^1])(?<month>[1-9])월(.*)$"); // 1월
	}

	public void initTimePatterns() {
		// 시간 패턴
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3]):(?<minute>[0-5][0-9])(.*)$"); // 12:01 // 12:1은 안 됨
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9]):(?<minute>[0-5][0-9])(.*)$"); // 2:01 // 2:1은 안 됨
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시 (?<minute>[0-5][0-9])분(.*)$"); // 12시 30분
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시 (?<minute>[0-9])분(.*)$"); // 12시 3분
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시 (?<minute>[0-5][0-9])분(.*)$"); // 7시 30분
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시 (?<minute>[0-9])분(.*)$"); // 7시 3분
		// 분 정보 없는 시간
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시([^분]*)$"); // 12시
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시([^분]*)$"); // 7시
	}

	public void initDayPatterns() {
		// 요일 패턴
		dayPatterns.add("(?<day>월|화|수|목|금|토|일)요일"); // 월요일
	}

	public void initSpecialDatePatterns() {
		// 그 외 특이 패턴
		specialDatePatterns.add("(?<today>오늘)"); // 오늘
		specialDatePatterns.add("(?<tomorrow>내일)"); // 내일
		specialDatePatterns.add("(?<dayAfterTomorrow>모레)$"); // 모레

		specialDatePatterns.add("(?<thisWeek>이번주)"); // 이번주
		specialDatePatterns.add("^(|.*[^다])(?<nextWeek>다음주)(.*)$"); // 다음주
		specialDatePatterns.add("(?<weekAfterNext>다다음주)"); // 다다음주
	}

	public List<String> getDatePatterns() {
		return datePatterns;
	}
	
	public void setDatePatterns(List<String> datePatterns) {
		this.datePatterns = datePatterns;
	}
	
	public List<String> getDayPatterns() {
		return dayPatterns;
	}
	
	public void setDayPatterns(List<String> daysPatterns) {
		this.dayPatterns = daysPatterns;
	}
	
	public List<String> getSpecialDatePatterns() {
		return specialDatePatterns;
	}
	
	public void setSpecialDatePatterns(List<String> specialDatePatterns) {
		this.specialDatePatterns = specialDatePatterns;
	}
	
	public List<String> getTimePatterns() {
		return timePatterns;
	}
	
	public void setTimePatterns(List<String> timePatterns) {
		this.timePatterns = timePatterns;
	}
}
