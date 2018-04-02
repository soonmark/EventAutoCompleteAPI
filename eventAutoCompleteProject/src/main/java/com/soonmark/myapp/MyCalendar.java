package com.soonmark.myapp;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MyCalendar {
	LocalDateTime timePoint;
	boolean isHalfTime;

	MyCalendar() {
		timePoint = LocalDateTime.now();
	}

	public LocalDateTime getTimePoint() {
		return timePoint;
	}

	public void setTimePoint(LocalDateTime timePoint) {
		this.timePoint = timePoint;
	}

	public String getYear() {
		return timePoint.getYear() + "";
	}

	public String getMonth() {
		return timePoint.getMonthValue() + "";
	}

	public String getDate() {
		return timePoint.getDayOfMonth() + "";
	}

	public String getDay() {
		String tDay;
		switch (timePoint.getDayOfWeek()) {
		case SUNDAY:
			tDay = "일";
			break;
		case MONDAY:
			tDay = "월";
			break;
		case TUESDAY:
			tDay = "화";
			break;
		case WEDNESDAY:
			tDay = "수";
			break;
		case THURSDAY:
			tDay = "목";
			break;
		case FRIDAY:
			tDay = "금";
			break;
		case SATURDAY:
			tDay = "토";
			break;
		default:
			tDay = "";
			break;
		}
		return tDay;
	}

	public String getHour() {
		return timePoint.getHour() + "";
	}

	public String getMinute() {
		return timePoint.getMinute() + "";
	}

	public boolean isAfter(MyCalendar cal) {
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

	public void setCloseDateOfTheDay(String val) {
		DayOfWeek d = DayOfWeek.SUNDAY;

		if (val.equals("일")) {
			d = DayOfWeek.SUNDAY; // 6
		} else if (val.equals("월")) {
			d = DayOfWeek.MONDAY; // 0
		} else if (val.equals("화")) {
			d = DayOfWeek.TUESDAY; // 1
		} else if (val.equals("수")) {
			d = DayOfWeek.WEDNESDAY; // 2
		} else if (val.equals("목")) {
			d = DayOfWeek.THURSDAY; // 3
		} else if (val.equals("금")) {
			d = DayOfWeek.FRIDAY; // 4
		} else if (val.equals("토")) {
			d = DayOfWeek.SATURDAY; // 5
		}

		LocalDate tmpDate = LocalDate.now();

		// 일
		int diff = d.getValue() - tmpDate.getDayOfWeek().getValue();
		if (diff < 0) {
			diff = 7 - diff;
		} else {
		}

		// 현재 시간의 요일과 비교해서 그 차이를 현재에 더해준 날짜로 세팅.
		timePoint = timePoint.plusDays(diff);
	}

	public void setCloseDateOfTime(MyCalendar cal) {
		if (isHalfTime) {
			LocalDateTime tmpTime = timePoint;
			tmpTime = tmpTime.plusHours(12);

			// halfTime 이고, 09:30 가 들어왔을 때
			// 09:30 < 기준 시간
			if (!(timePoint.isAfter(cal.getTimePoint()))) {
				// 09:30 < 기준 시간 < 21:30
				if (tmpTime.isAfter(cal.getTimePoint())) {
					// 다음날 오전으로 해야함. 하루를 더하면 됨.
					plusHour(12);
				} else { // 09:30 < 21:30 < 기준 시간
					// 오늘 오후로 해야함.
					plusDate(1);
				}
			}
		}
	}

	public void setCloseDate(MyCalendar cal, String type) {
		// 세팅된 날짜가 기준 날짜 전의 날짜면
		if (!(timePoint.isAfter(cal.getTimePoint()))) {
			if (type.equals("year")) {
				long diff = cal.getTimePoint().getYear() - timePoint.getYear();
				timePoint = timePoint.plusYears(diff);
				// 차이만큼 더했는데도 이전이면, 월이나 일을 계산했을 때 이전인 것이므로 한번더 1년을 더해줌.
				if(!(timePoint.isAfter(cal.getTimePoint()))) {
					timePoint = timePoint.plusYears(1);
				}
			} else if (type.equals("month")) {
				long diff = cal.getTimePoint().getMonthValue() - timePoint.getMonthValue();
				timePoint = timePoint.plusMonths(diff);
				// 차이만큼 더했는데도 이전이면, 일을 계산했을 때 이전인 것이므로 한번더 1월을 더해줌.
				if(!(timePoint.isAfter(cal.getTimePoint()))) {
					timePoint = timePoint.plusMonths(1);
				}
			} else if (type.equals("date")) {
				long diff = cal.getTimePoint().getDayOfMonth() - timePoint.getDayOfMonth();
				timePoint = timePoint.plusMonths(diff);
			}
		}

	}
}
