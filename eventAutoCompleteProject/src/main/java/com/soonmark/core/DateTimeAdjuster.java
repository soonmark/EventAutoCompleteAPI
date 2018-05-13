package com.soonmark.core;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.Priority;

public class DateTimeAdjuster {
	private LocalDateTime timePoint;
	private boolean isHalfTime;

	public DateTimeAdjuster() {
		timePoint = RecommendationManager.curTime;
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
		for (int i = 0; i < 4; i++) {
			if (timePoint.plusYears(i).toLocalDate().isLeapYear()) {
				year = timePoint.plusYears(i).getYear();
				break;
			}
		}
		return year;
	}
	
	public void setDateTime(InvalidDateTimeObj obj) {
		setAllDate(obj);
		setTime(obj);
	}
	
	public void setTime(InvalidDateTimeObj obj) {
		setHour(obj.getHour(), false);
		setMinute(obj.getMinute());
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
		if(val == AppConstants.NO_DATA) {
			timePoint = timePoint.withMinute(0);
		}
		else {
			timePoint = timePoint.withMinute(val);
		}
	}

	public void setAllDate(InvalidDateTimeObj dtObj) {
		timePoint = timePoint.withYear(dtObj.getYear());
		timePoint = timePoint.withMonth(dtObj.getMonth());
		timePoint = timePoint.withDayOfMonth(dtObj.getDate());
	}
	
	public void setCloseDateOfTheDay(DayOfWeek val) {
		if (val != AppConstants.NO_DATA_FOR_DAY) {

			LocalDate tmpDate = RecommendationManager.curTime.toLocalDate();
//			LocalDate tmpDate = LocalDate.now();

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
	
	// 31일이 맞을 때까지 월을 더해서 찾아줌.
	public LocalDate plusMonthsWithDate(LocalDate dateTime, int plus) {
		LocalDate tmp = dateTime.plusMonths(plus);
		while(dateTime.getDayOfMonth() != tmp.getDayOfMonth()) {
			try {
				tmp = tmp.plusMonths(1).withDayOfMonth(dateTime.getDayOfMonth());
			}
			catch (DateTimeException ex) {
				tmp = tmp.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
			}
		}
		return tmp;
	}

	public LocalDate plusFromMonthsWithDate(LocalDate dateTime, LocalDate from) {
		int stdDate = dateTime.getDayOfMonth();
		LocalDate tmp = from;
		if(tmp.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth() < stdDate) {
			tmp = tmp.with(from).with(TemporalAdjusters.lastDayOfMonth());
		}
		else {
			tmp = tmp.withDayOfMonth(stdDate);
		}

		if(tmp.isBefore(from)) {
			tmp = tmp.plusMonths(1);
		}
		
		while(dateTime.getDayOfMonth() != tmp.getDayOfMonth()) {
			try {
				tmp = tmp.plusMonths(1).withDayOfMonth(stdDate);
			}
			catch (DateTimeException ex) {
				tmp = tmp.plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
			}
		}
		return tmp;
	}
	
	public void plusYearsWithDate(LocalDate std, int plus) {
		while(std.isAfter(timePoint.toLocalDate())) {
			if(timePoint.plusYears(1).getDayOfMonth() == timePoint.getDayOfMonth()) {
				timePoint = timePoint.plusYears(1);
			}
			else {
				timePoint = timePoint.plusYears(2);
			}
		}
		
		if(timePoint.plusYears(plus).getDayOfMonth() == timePoint.getDayOfMonth()) {
			timePoint = timePoint.plusYears(plus);
		}
		else {
			timePoint = timePoint.plusYears(plus + 1);
		}
	}
	
	public void setCloseDate(DateTimeAdjuster cal, DateTimeEn focus, int plus, boolean checkTime, LocalDate sDate) {
		if (focus == DateTimeEn.year) {
			plusYearsWithDate(cal.getTimePoint().toLocalDate(), plus);
		} else if (focus == DateTimeEn.month) {
			// 올해와 년이 같으면
			if (timePoint.getYear() == RecommendationManager.curTime.getYear()) {
				// 오늘보다 이전이라면
				if(timePoint.toLocalDate().isBefore(RecommendationManager.curTime.toLocalDate())) {
					if(sDate != null) {
						timePoint = timePoint.with(
								plusFromMonthsWithDate(timePoint.toLocalDate(), sDate));
					}
					else {
						// timPoint의 월을 오늘 이후의 월로 세팅.
						timePoint = timePoint.with(
								plusFromMonthsWithDate(timePoint.toLocalDate(), RecommendationManager.curTime.toLocalDate()));
					}
				}
			}
			else if(sDate != null && timePoint.getYear() == sDate.getYear()) {
				if(timePoint.toLocalDate().isBefore(sDate)) {
					timePoint = timePoint.with(
							plusFromMonthsWithDate(timePoint.toLocalDate(), sDate));
				}
			}
			// 년도가 다른 경우에도 수행
			timePoint = timePoint.with(plusMonthsWithDate(timePoint.toLocalDate(), plus));
		} else if (focus == DateTimeEn.date) {
			long diff = cal.getTimePoint().getDayOfMonth() - timePoint.getDayOfMonth();
			if (timePoint.getYear() == RecommendationManager.curTime.getYear()
					&& timePoint.getMonthValue() == RecommendationManager.curTime.getMonthValue()) {
				timePoint = timePoint.withDayOfMonth(RecommendationManager.curTime.getDayOfMonth());
				if(checkTime) {
					this.isHalfTime = true;
					setCloseDateByTime(cal.getTimePoint());
				}
			}
			timePoint = timePoint.plusDays(diff + plus);
		}
	}

	void addPmTime(InvalidDateTimeObj dateTimeLogicalObject) {
		if(dateTimeLogicalObject.getAmpm() == DateTimeEn.am) {
			if(dateTimeLogicalObject.getHour() >= 12) {
				dateTimeLogicalObject.setHour(dateTimeLogicalObject.getHour() - 12);
			}
		}
		else if(dateTimeLogicalObject.getAmpm() == DateTimeEn.pm) {
			if(dateTimeLogicalObject.getHour() < 12 && dateTimeLogicalObject.getHour() != AppConstants.NO_DATA) {
				dateTimeLogicalObject.setHour(dateTimeLogicalObject.getHour() + 12);
			}
		}
	}

	public void adjustForAmPmTime(DateTimeListManager targetList) {
		DateTimeListManager beforeList = new DateTimeListManager();
		for (int i = 0; i < targetList.getDtMgrList().size(); i++) {
			beforeList.insertDtObj(targetList.getElement(i));
		}

		for (int i = 0; i < beforeList.getDtMgrList().size(); i++) {
			InvalidDateTimeObj dtObj = new InvalidDateTimeObj();
			dtObj.setAllDate(beforeList.getElement(i));
			dtObj.setMinute(beforeList.getElement(i).getMinute());
			dtObj.setHour((beforeList.getElement(i).getHour() + 12) % 24);
			if (beforeList.getElement(i).getAmpm() == AppConstants.NO_DATA_FOR_AMPM) {
				if (beforeList.getElement(i).getHour() < 12) {
					targetList.getElement(i).setPriority(Priority.am);
					dtObj.setPriority(Priority.pm);
					targetList.insertDtObj(dtObj);
				} else if (beforeList.getElement(i).getHour() == 12) {
					targetList.getElement(i).setPriority(Priority.pm);
					dtObj.setPriority(Priority.am);
					targetList.insertDtObj(dtObj);
				}
			}
			else {
				addPmTime(targetList.getElement(i));
				
				// 오전, 오후만 입력되면 오전 리스트, 오후리스트 추가하기.
				if(beforeList.getElement(i).getHour() == AppConstants.NO_DATA) {
					addWholeAmPmList(i, targetList);
				}
			}
		}
	}

	private void addWholeAmPmList(int i, DateTimeListManager targetList) {
		int num = 0;
		TimeStorage times = new TimeStorage();
		Iterator<LocalTime> iter = times.getTimes(targetList.getElement(i).getAmpm()).iterator();
		while (iter.hasNext()) {
			LocalTime thisTime = iter.next();
			InvalidDateTimeObj dtObj = new InvalidDateTimeObj();
			dtObj.setAmpm(targetList.getElement(i).getAmpm());
			dtObj.setHour(thisTime.getHour());
			dtObj.setMinute(thisTime.getMinute());
			dtObj.setFocusOnAmPm(true);
			if(num == 0) {
				targetList.getElement(i).setHour(dtObj.getHour());
				targetList.getElement(i).setMinute(dtObj.getMinute());
				targetList.getElement(i).setFocusOnAmPm(dtObj.isFocusOnAmPm());
			}
			else {
				targetList.insertDtObj(dtObj);
			}
			num++;
		}
	}

	public void plusMinute(int i) {
		timePoint = timePoint.plusMinutes(i);
	}

}
