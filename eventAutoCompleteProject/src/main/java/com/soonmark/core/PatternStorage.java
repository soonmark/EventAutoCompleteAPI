package com.soonmark.core;

import java.util.ArrayList;
import java.util.List;

public class PatternStorage {
	// 기간 패턴
	List<String> periodPatterns = new ArrayList<String>();
	// during 패턴
	List<String> duringPatterns = new ArrayList<String>();
	// 년월일 패턴
	List<String> datePatterns = new ArrayList<String>();
	// 요일 패턴
	List<String> dayPatterns = new ArrayList<String>();
	// 그 외 특이 패턴
	List<String> specialDatePatterns = new ArrayList<String>();

	// 시간 패턴
	List<String> timePatterns = new ArrayList<String>();
	
	List<String> dateTimeCommonPatterns = new ArrayList<String>();
	
	PatternStorage(){
		initPatterns();
	}

	public void initPatterns() {
		initPeriodPatterns();
		initDuringPatterns();
		initDatePatterns();
		initTimePatterns();
		initDayPatterns();
		initSpecialDatePatterns();
		initDateTimeCommonPatterns();
	}

	private void initDateTimeCommonPatterns() {
//		dateTimeCommonPatterns.add("^(.*)(?<date>[0-9][0-9])(.*)$"); // 19 (일)
//		dateTimeCommonPatterns.add("^(.*)(?<month>[0-2][0-9])(?<date>[0-9][0-9])(.*)$"); // 1201 (월일)
//		dateTimeCommonPatterns.add("^(.*)(?<year>[0-2][0-9])(?<month>[0-2][0-9])(?<date>[0-9][0-9])(.*)$"); // 181201 (년월일)
//		dateTimeCommonPatterns.add("^(.*)(?<year>[0-2][0-9][0-9][0-9])(?<month>[0-2][0-9])(?<date>[0-9][0-9])(.*)$"); // 20181201 (년월일)
		
//		dateTimeCommonPatterns.add("^(.*)(?<hour>[0-2][0-9])(?<minute>[0-9][0-9])(.*)$"); // 1210 (시분)
//		dateTimeCommonPatterns.add("^(.*)(?<hour>[0-2][0-9])(.*)$"); // 12 (시)
//		dateTimeCommonPatterns.add("^(.*)(?<minute>[0-2][0-9])(.*)$"); // 10 (분)
	}

	private void initDuringPatterns() {
		// 월
		duringPatterns.add("^(.*)(?<month>[1-2][0-9]|3[0-1])개월(.*)$"); // 19개월
		duringPatterns.add("^(|.*[^1-3])(?<month>[1-9])개월(.*)$"); // 1개월

		duringPatterns.add("^(.*)(?<month>[1-2][0-9]|3[0-1])달(.*)$"); // 19달
		duringPatterns.add("^(|.*[^1-3])(?<month>[1-9])달(.*)$"); // 1달

		// 일
		duringPatterns.add("^(.*)(?<date>[1-2][0-9]|3[0-1])일(.*)$"); // 19일
		duringPatterns.add("^(|.*[^1-3])(?<date>[1-9])일(.*)$"); // 1일

		// 시간
		duringPatterns.add("^(.*)(?<hour>[0-9][0-9])시간(.*)$"); // 12시간
		duringPatterns.add("^(|.*[^0-9])(?<hour>[0-9])시간(.*)$"); // 7시간
	}

	private void initPeriodPatterns() {
		periodPatterns.add("(?<period>(?<from>.*?)부터(?<to>.*?)까지)");	// 부터 까지
		periodPatterns.add("(?<period>(?<from>.*?)부터(?<during>.*?)동안)");	// 부터 동안
		periodPatterns.add("^(?<from>.*?)~(?<to>.*?)$");	// 날짜시간 ~ 날짜시간
//		periodPatterns.add("(?<from>)-(<?to>)");	// 날짜시간 - 날짜시간

		periodPatterns.add("^(?<from>.*?)부터(?<to>.*?)$");	// 부터
		periodPatterns.add("(?<to>.*?)까지");	// 까지

		periodPatterns.add("(?<period>(?<during>.*?)동안)");	// 3시간동안
	}

	public void initDatePatterns() {
//		datePatterns.add("^(.*)(?<date>[0-9][0-9])(.*)$"); // 19 (일)
//		datePatterns.add("^(.*)(?<month>[0-2][0-9])(?<date>[0-9][0-9])(.*)$"); // 1201 (월일)
//		datePatterns.add("^(.*)(?<year>[0-2][0-9])(?<month>[0-2][0-9])(?<date>[0-9][0-9])(.*)$"); // 181201 (년월일)
//		datePatterns.add("^(.*)(?<year>[0-2][0-9][0-9][0-9])(?<month>[0-2][0-9])(?<date>[0-9][0-9])(.*)$"); // 20181201 (년월일)
		
		
		
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
		datePatterns.add("^(|.*[^1-9])(?<month>[1-9])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3-09
		datePatterns.add("^(.*)(?<month>1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11-9
		datePatterns.add("^(|.*[^1-9])(?<month>[1-9])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3-9
		datePatterns.add("^(.*)(?<month>1[0-2])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11.09
		datePatterns.add("^(|.*[^1-9])(?<month>[1-9])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3.09
		datePatterns.add("^(.*)(?<month>1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11.9
		datePatterns.add("^(|.*[^1-9])(?<month>[1-9])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3.9
		datePatterns.add("^(.*)(?<month>1[0-2])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11/09
		datePatterns.add("^(|.*[^1-9])(?<month>[1-9])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3/09
		datePatterns.add("^(.*)(?<month>1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11/9
		datePatterns.add("^(|.*[^1-9])(?<month>[1-9])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3/9

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
		// 오전 오후
		timePatterns.add("(?<am>오전)"); // 오전
		timePatterns.add("(?<pm>오후)"); // 오후
		
		timePatterns.add("(?<am>(?i)am)"); // am
		timePatterns.add("(?<pm>(?i)pm)"); // pm
		
		timePatterns.add("(?<am>(?i)a.m)"); // am
		timePatterns.add("(?<pm>(?i)p.m)"); // pm
		
		timePatterns.add("(?<am>(?i)AM)"); // am
		timePatterns.add("(?<pm>(?i)PM)"); // pm

		// 시간 패턴
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3]):(?<minute>[0-5][0-9])(.*)$"); // 12:01 // 12:1은 안 됨
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9]):(?<minute>[0-5][0-9])(.*)$"); // 2:01 // 2:1은 안 됨
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시 (?<minute>[0-5][0-9])분(.*)$"); // 12시 30분
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시 (?<minute>[0-9])분(.*)$"); // 12시 3분
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시 (?<minute>[0-5][0-9])분(.*)$"); // 7시 30분
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시 (?<minute>[0-9])분(.*)$"); // 7시 3분
		// 분 정보 없는 시간
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시(.*)$"); // 12시
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시(.*)$"); // 7시
		
		
//		timePatterns.add("^(.*)(?<hour>[0-2][0-9])(?<minute>[0-9][0-9])(.*)$"); // 1210 (시분)
//		timePatterns.add("^(.*)(?<hour>[0-2][0-9])(.*)$"); // 12 (시)
//		timePatterns.add("^(.*)(?<minute>[0-2][0-9])(.*)$"); // 10 (분)
		
		// 분 정보만 있는 시간
//		timePatterns.add("^(.*)(?<minute>1[0-9]|2[0-3])분(.*)$"); // 12분
//		timePatterns.add("^(|.*[^1-2])(?<minute>[1-9])분(.*)$"); // 7분
	}

	public void initDayPatterns() {
		// 요일 패턴
		dayPatterns.add("(?<day>월|화|수|목|금|토|일)요일"); // 월요일
		dayPatterns.add("(?<day>월|화|수|목|금|토|일)요"); // 월요
	}

	public void initSpecialDatePatterns() {
		// 그 외 특이 패턴
		specialDatePatterns.add("(?<today>오늘)"); // 오늘
		specialDatePatterns.add("(?<tomorrow>내일)"); // 내일
		specialDatePatterns.add("(?<dayAfterTomorrow>모레)"); // 모레

		specialDatePatterns.add("(?<thisWeek>이번주)"); // 이번주
		specialDatePatterns.add("^(|.*[^다])(?<nextWeek>다음주)(.*)$"); // 다음주
		specialDatePatterns.add("(?<weekAfterNext>다다음주)"); // 다다음주
	}

	public List<String> getPeriodPatterns() {
		return periodPatterns;
	}
	
	public void setPeriodPatterns(List<String> periodPatterns) {
		this.periodPatterns = periodPatterns;
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

	public List<String> getDuringPatterns() {
		return duringPatterns;
	}

	public List<String> getDateTimeCommonPatterns() {
		return dateTimeCommonPatterns;
	}

	public void setDateTimeCommonPatterns(List<String> dateTimeCommonPatterns) {
		this.dateTimeCommonPatterns = dateTimeCommonPatterns;
	}
}
