package com.soonmark.enums;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.regex.Matcher;

import com.soonmark.domain.AppConstants;
import com.soonmark.managers.DateTimeObjManager;

public enum TokenType {
	dates(0){
		public void setDtObjInfo(DateTimeObjManager dtObj, Matcher matcher) {
			try {
				dtObj.setYear(Integer.parseInt(matcher.group("year")));
				int year = dtObj.getYear();
				if(year >= 0 && year < 100) {
					// 받은 년도가 2자리수면 처리해줘야함.
					// 일단 90년 전까지는 19로, 그 전은 20으로 해놓기.
					if(LocalDate.now().getYear()-(year + 2000) < 90
							&& LocalDate.now().getYear()-(year + 2000) > -10) {
						year += 2000;
					}else {
						year += 1900;
					}
					dtObj.setYear(year);
				}
				dtObj.setHasInfo(DateTimeEn.year.ordinal(), true);
			} catch (IllegalArgumentException e) {
				dtObj.setYear(AppConstants.NO_DATA);
			}
			try {
				dtObj.setMonth(Integer.parseInt(matcher.group("month")));
				dtObj.setHasInfo(DateTimeEn.month.ordinal(), true);
			} catch (IllegalArgumentException e) {
				dtObj.setMonth(AppConstants.NO_DATA);
			}
			try {
				dtObj.setDate(Integer.parseInt(matcher.group("date")));
				dtObj.setHasInfo(DateTimeEn.date.ordinal(), true);
			} catch (IllegalArgumentException e) {
				dtObj.setDate(AppConstants.NO_DATA);
			}
		}
	}, days(1){
		public void setDtObjInfo(DateTimeObjManager dtObj, Matcher matcher) {
			// month와 date 에 해당하는 group 만 따로 읽어 저장
			try {
				// getDayOfWeekByLocale
				String engWeekday = dtObj.getDayOfWeekByLocale(matcher.group("day"));
				dtObj.setDay(DayOfWeek.valueOf(engWeekday));
				dtObj.setHasInfo(DateTimeEn.day.ordinal(), true);
			} catch (IllegalArgumentException e) {
				dtObj.setDay(AppConstants.NO_DATA_FOR_DAY);
			}
		}
	}, times(2){
		public void setDtObjInfo(DateTimeObjManager dtObj, Matcher matcher) {
			dtObj.setHour(Integer.parseInt(matcher.group("hour")));
			dtObj.setHasInfo(DateTimeEn.hour.ordinal(), true);
			try {
				// 시간 중에 group 명이 minute 이 없는 경우 0으로 세팅
				dtObj.setMinute(Integer.parseInt(matcher.group("minute")));
				dtObj.setHasInfo(DateTimeEn.minute.ordinal(), true);
			} catch (IllegalArgumentException e) {
				dtObj.setMinute(0);
				dtObj.setHasInfo(DateTimeEn.minute.ordinal(), true);
			}
		}
	}, special(3){
		public void setDtObjInfo(DateTimeObjManager dtObj, Matcher matcher) {
			//enum 
			for(specialDateTypeNeedsDay sdt : specialDateTypeNeedsDay.values()) {
				try {
					dtObj.setSpecialDate(matcher.group(sdt.name()));
					
					// date 로 해두면 이상해질 것 같지만 일단...
					dtObj.setHasInfo(DateTimeEn.date.ordinal(), true);
					dtObj.setFocusOnDay(false);
					
					break;
				} catch (IllegalArgumentException e) {
					//enum 
					for(specialDateTypeWithoutDay sdtwd : specialDateTypeWithoutDay.values()) {
						try {
							dtObj.setSpecialDate(matcher.group(sdtwd.name()));
							
							// date 로 해두면 이상해질 것 같지만 일단...
							dtObj.setHasInfo(DateTimeEn.date.ordinal(), true);
							dtObj.setFocusOnDay(false);
							break;
						} catch (IllegalArgumentException event) {
							dtObj.setSpecialDate(AppConstants.NO_DATA_FOR_SPECIALDATE);
						}
					}
				}
			}
		}
	};
	
	int tokenType;
	TokenType(int type){
		tokenType = type;
	}
	
	public int getInteger() {
		return tokenType;
	}
		
	// 추상 메소드
	abstract public void setDtObjInfo(DateTimeObjManager vo, Matcher matcher);
}
