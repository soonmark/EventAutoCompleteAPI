package com.soonmark.domain;

import java.time.DayOfWeek;
import java.util.regex.Matcher;

import com.soonmark.core.During;
import com.soonmark.core.InvalidDateTimeObj;
import com.soonmark.core.RecommendationManager;

public enum TokenType {
	period(0){
		@Override
		public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher) {
		}
	}, dates(1){
		@Override
		public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher) {
			try {
				dtObj.setYear(Integer.parseInt(matcher.group("year")));
				int year = dtObj.getYear();
				if(year >= 0 && year < 100) {
					// 받은 년도가 2자리수면 처리해줘야함.
					// 일단 90년 전까지는 19로, 그 전은 20으로 해놓기.
					if(RecommendationManager.curTime.toLocalDate().getYear()-(year + 2000) < 90
							&& RecommendationManager.curTime.toLocalDate().getYear()-(year + 2000) > -10) {
//					if(LocalDate.now().getYear()-(year + 2000) < 90
//							&& LocalDate.now().getYear()-(year + 2000) > -10) {
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
	}, days(2){
		@Override
		public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher) {
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
	}, times(3){
		@Override
		public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher) {
			try {
				if(matcher.group("am") != null) {
					dtObj.setAmpm(DateTimeEn.am);
					dtObj.setHasInfo(DateTimeEn.am.ordinal(), true);
				}
			} catch(IllegalArgumentException e) {
			}
			try {
				if(matcher.group("pm") != null) {
					dtObj.setAmpm(DateTimeEn.pm);
					dtObj.setHasInfo(DateTimeEn.pm.ordinal(), true);
				}
			} catch(IllegalArgumentException e) {
				
			}
			try {
				dtObj.setHour(Integer.parseInt(matcher.group("hour")));
				dtObj.setHasInfo(DateTimeEn.hour.ordinal(), true);
				
			} catch(IllegalArgumentException e) {
			}
			
			try {
				dtObj.setMinute(Integer.parseInt(matcher.group("minute")));
				dtObj.setHasInfo(DateTimeEn.minute.ordinal(), true);
			} catch (IllegalArgumentException e) {
			}
		}
	}, special(4){
		@Override
		public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher) {
			//enum 
			for(SpecialDateType sdt : SpecialDateType.values()) {
				try {
					dtObj.setSpecialDate(matcher.group(sdt.name()));
					dtObj.setHasInfo(DateTimeEn.specialDate.ordinal(), true);
					break;
				} catch (IllegalArgumentException e) {
					dtObj.setSpecialDate(AppConstants.NO_DATA_FOR_SPECIALDATE);
				}
			}
		}
	}, during(5) {
		@Override
		public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher) {
		}
	}/*, commonDateTime(6) {
		@Override
		public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher) {
			
		}
	}*/;
	
	int tokenType;
	TokenType(int type){
		tokenType = type;
	}
	
	public int getInteger() {
		return tokenType;
	}
		
	// 추상 메소드
	abstract public void setDtObjInfo(InvalidDateTimeObj dtObj, Matcher matcher);
}
