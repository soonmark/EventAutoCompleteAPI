package com.soonmark.myapp;

import java.util.regex.Matcher;

public enum TokenType {
	dates(0){
		void setVoInfo(DateVO vo, Matcher matcher) {
			try {
				vo.setMonth(matcher.group("month"));
				vo.setHasInfo(DateTimeEn.month.ordinal(), true);
			} catch (IllegalArgumentException e) {
				vo.setMonth("-1");
			}
			try {
				vo.setDate(matcher.group("date"));
				vo.setHasInfo(DateTimeEn.date.ordinal(), true);
			} catch (IllegalArgumentException e) {
				vo.setDate("-1");
			}
		}
	}, days(1){
		void setVoInfo(DateVO vo, Matcher matcher) {
			// month와 date 에 해당하는 group 만 따로 읽어 저장
			try {
				vo.setDay(matcher.group("day"));
				vo.setHasInfo(DateTimeEn.day.ordinal(), true);
			} catch (IllegalArgumentException e) {
				vo.setDay("-1");
			}
		}
	}, times(2){
		void setVoInfo(DateVO vo, Matcher matcher) {
			vo.setHour(matcher.group("hour"));
			vo.setHasInfo(DateTimeEn.hour.ordinal(), true);
			try {
				// 시간 중에 group 명이 minute 이 없는 경우 0으로 세팅
				vo.setMinute(matcher.group("minute"));
				vo.setHasInfo(DateTimeEn.minute.ordinal(), true);
			} catch (IllegalArgumentException e) {
				vo.setMinute("0");
				vo.setHasInfo(DateTimeEn.minute.ordinal(), true);
			}
		}
	}, special(3){
		void setVoInfo(DateVO vo, Matcher matcher) {
			//enum 
			for(specialDateType sdt : specialDateType.values()) {
				try {
					vo.setSpecialDate(matcher.group(sdt.name()));
					vo.setHasInfo(DateTimeEn.date.ordinal(), true);
					vo.isFocusOnDay = false;
					break;
				} catch (IllegalArgumentException e) {
					vo.setSpecialDate("-1");
				}
			}
//			try {
//				vo.setSpecialDate(matcher.group("dateWithoutDays"));
//				vo.setHasInfo(DateTimeEn.date.ordinal(), true);
//				vo.isFocusOnDay = false;
//			} catch (IllegalArgumentException e) {
//				try {
//					vo.setSpecialDate(matcher.group("dateWithDays"));
//					vo.setHasInfo(DateTimeEn.date.ordinal(), true);
//					vo.isFocusOnDay = true;
//				} catch (IllegalArgumentException ex) {
//					vo.setSpecialDate("-1");
//				}
//			}
		}
	};
	
	int tokenType;
	TokenType(int type){
		tokenType = type;
	}
	
	int getInteger() {
		return tokenType;
	}
	
	
	// 추상 메소드
	abstract void setVoInfo(DateVO vo, Matcher matcher);
}
