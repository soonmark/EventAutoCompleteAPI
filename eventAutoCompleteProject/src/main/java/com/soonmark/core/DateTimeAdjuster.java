package com.soonmark.core;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;

public class DateTimeAdjuster {
	private LocalDateTime timePoint;
	private boolean isHalfTime;

	public DateTimeAdjuster() {
		timePoint = LocalDateTime.now();
	}

	public LocalDateTime getTimePoint() {
		return timePoint;
	}

	public void setTimePoint(LocalDateTime timePoint) {
		this.timePoint = timePoint;
	}

	public int getYear() {
		return timePoint.getYear();
	}

	public int getMonth() {
		return timePoint.getMonthValue();
	}

	public int getDate() {
		return timePoint.getDayOfMonth();
	}
	
	public int getNextOrSameLeapYear() {
		int year = 0;
		for (int i = 0 ; i < 4 ; i++) {
			if (timePoint.plusYears(i).toLocalDate().isLeapYear()) {
				year = timePoint.plusYears(i).getYear();
				break;
			}
		}
		return year;
	}

	public void setByDateTimeEn(DateTimeEn dateTimeEn, int val) {
		switch (dateTimeEn) {
		case year:
			timePoint = timePoint.withYear(val);
			break;
		case month:
			timePoint = timePoint.withMonth(val);
			break;
		case date:
			timePoint = timePoint.withDayOfMonth(val);
			break;
		case hour:
			timePoint = timePoint.withHour(val);
			break;
		case minute:
			timePoint = timePoint.withMinute(val);
			break;
		default:
			break;
		}
	}

	public DayOfWeek getDay() {
		return timePoint.getDayOfWeek();
	}

	public int getHour() {
		return timePoint.getHour();
	}

	public int getMinute() {
		return timePoint.getMinute();
	}

	public boolean isAfter(DateTimeAdjuster cal) {
		return timePoint.isAfter(cal.getTimePoint());
	}

	public boolean isHalfTime() {
		return isHalfTime;
	}

	public void plusHour(int val) {
		timePoint = timePoint.plusHours(val);
	}

	public void plusDate(int val) {
		timePoint = timePoint.plusDays(val);
	}

	public void setYear(int val) {
		timePoint = timePoint.withYear(val);
	}

	public void setMonth(int val) {
		timePoint = timePoint.withMonth(val);
	}

	public void setDate(int val) {
		timePoint = timePoint.withDayOfMonth(val);
	}

	public void setHour(int val, boolean controlHalfT) {
		timePoint = timePoint.withHour(val);

		if (controlHalfT) {
			if (val <= 12) {
				isHalfTime = true;
			} else {
				isHalfTime = false;
			}
		}
	}

	public void setMinute(int val) {
		timePoint = timePoint.withMinute(val);
	}

	public void setCloseDateOfTheDay(DayOfWeek val) {
		if (val != AppConstants.NO_DATA_FOR_DAY) {

			LocalDate tmpDate = LocalDate.now();

			// 일
			int diff = val.getValue() - tmpDate.getDayOfWeek().getValue();
			if (diff < 0) {
				diff = 7 + diff;
			}

			// 현재 시간의 요일과 비교해서 그 차이를 현재에 더해준 날짜로 세팅.
			timePoint = timePoint.plusDays(diff);
		}
	}

	public void setCloseDateByTime(LocalDateTime cal) {
		if (isHalfTime) {
			LocalDateTime tmpTime = timePoint;
			tmpTime = tmpTime.plusHours(12);

			// halfTime 이고, 09:30 가 들어왔을 때
			// 09:30 < 기준 시간
			if (!(timePoint.isAfter(cal))) {
				// 09:30 < 기준 시간 < 21:30
				if (tmpTime.isAfter(cal)) {
					// 다음날 오전으로 해야함. 하루를 더하면 됨.
					plusHour(12);
				} else { // 09:30 < 21:30 < 기준 시간
					// 오늘 오후로 해야함.
					plusDate(1);
				}
			}
		} else {
			// halfTime 이 아니고, 13:30 가 들어왔을 때
			// 13:30 < 기준 시간
			if (!(timePoint.toLocalTime().isAfter(cal.toLocalTime()))) {
				plusDate(1);
			}
		}
	}

	public void setCloseDate(DateTimeAdjuster cal, DateTimeEn focus, int plus) {
		if (focus == DateTimeEn.year) {
			long diff = cal.getTimePoint().getYear() - timePoint.getYear();
			timePoint = timePoint.plusYears(diff + plus);
			// 차이만큼 더했는데도 이전이면, 월이나 일을 계산했을 때 이전인 것이므로 한번더 1년을 더해줌.
		}
		else if (focus == DateTimeEn.month) {
			long diff = cal.getTimePoint().getMonthValue() - timePoint.getMonthValue();
			if(timePoint.getYear() == LocalDateTime.now().getYear()) {
					timePoint = timePoint.withMonth(LocalDateTime.now().getMonthValue());
				}
			timePoint = timePoint.plusMonths(diff + plus);
		}
		else if (focus == DateTimeEn.date) {
			long diff = cal.getTimePoint().getDayOfMonth() - timePoint.getDayOfMonth();
			if(timePoint.getYear() == LocalDateTime.now().getYear()
				&& timePoint.getMonthValue() == LocalDateTime.now().getMonthValue()) {
				timePoint = timePoint.withDayOfMonth(LocalDateTime.now().getDayOfMonth());
			}
			timePoint = timePoint.plusDays(diff + plus);
		}
	}
	
	void addPmTime(DateTimeListManager targetList){
		DateTimeListManager beforeList = new DateTimeListManager();
		for(int i = 0 ; i < targetList.getDtMgrList().size() ; i++) {
			beforeList.insertDtObj(targetList.getElement(i));
		}
		
		for(int i = 0 ; i < beforeList.getDtMgrList().size() ; i++) {
			if(beforeList.getElement(i).getHour() <= 12) {
				DateTimeLogicalObject dtObj = new DateTimeLogicalObject();
				dtObj.setAllDate(beforeList.getElement(i));
				dtObj.setMinute(beforeList.getElement(i).getMinute());
				dtObj.setHour((beforeList.getElement(i).getHour() + 12) % 24);
				targetList.insertDtObj(dtObj);
			}
		}
	}
}
