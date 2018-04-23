package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.Priority;

public class DateTimeEstimator {
	
	private Logger logger = LoggerFactory.getLogger(DateTimeEstimator.class);

	private DateTimeListManager timeList;
	private DateTimeListManager dateList;
	private DateTimeListManager resultList;
	private int focusingRecurNum;
	
	public DateTimeEstimator(DateTimeListManager timeList, DateTimeListManager dateList) {
		this.timeList = timeList;
		this.dateList = dateList;
		resultList = new DateTimeListManager();
		focusingRecurNum = 2;
	}

	public DateTimeListManager fillEmptyDatas() {
		boolean isDateEmpty = false;
		boolean isTimeEmpty = false;

		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		if (timeList.getDtMgrList().size() == 0) {
			timeList.insertDtObj(new DateTimeLogicalObject());
			isTimeEmpty = true;
		}

		if (dateList.getDtMgrList().size() == 0) {
			dateList.insertDtObj(new DateTimeLogicalObject());
			isDateEmpty = true;
		}

		if (isDateEmpty && isTimeEmpty) {
			timeList.deleteDtObj(0);
			dateList.deleteDtObj(0);
			return resultList;
		}

		if (isDateEmpty) {
			setTimeToCloseFutureTime();
			setPriorityForTimeWithoutDate();
		} else {
			// 월간 일수 차이에 대한 예외처리
			if (isValidDates() == true) {
				addEstimateDateAndTime(isTimeEmpty);
			}
		}

		return resultList;
	}
	
	

	private void setPriorityForTimeWithoutDate() {
		int closestIdx = 0;
		DateTimeAdjuster closest = new DateTimeAdjuster();
		closest.setDate(resultList.getElement(closestIdx).getDate());
		closest.setMonth(resultList.getElement(closestIdx).getMonth());
		closest.setYear(resultList.getElement(closestIdx).getYear());
		closest.setHour(resultList.getElement(closestIdx).getHour(), false);
		closest.setMinute(resultList.getElement(closestIdx).getMinute());
		
		for (int i = 1; i < resultList.getDtMgrList().size(); i++) {
			DateTimeAdjuster cur = new DateTimeAdjuster();
			cur.setDate(resultList.getElement(i).getDate());
			cur.setMonth(resultList.getElement(i).getMonth());
			cur.setYear(resultList.getElement(i).getYear());
			cur.setHour(resultList.getElement(i).getHour(), false);
			cur.setMinute(resultList.getElement(i).getMinute());
			
			if(closest.getTimePoint().isAfter(cur.getTimePoint())) {
				closest.setTimePoint(cur.getTimePoint());
				closestIdx = i;
			}
		}

		resultList.getElement(closestIdx).setPriority(Priority.timeWithFirstEstimateDate);
	}
	
	private void setTimeToCloseFutureTime() {
		logger.info("날짜 정보없음");
		for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
			for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
				// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
				DateTimeLogicalObject dtObj = new DateTimeLogicalObject();

				// 현재 날짜
				DateTimeAdjuster tmpCal = new DateTimeAdjuster();
				tmpCal.setHour(timeList.getElement(i).getHour(), false);

				// 메소드의 객체가 now 캘린더가 아니면 true 입력
				if (timeList.getElement(i).getMinute() == AppConstants.NO_DATA) {
					tmpCal.setMinute(0);
				} else {
					tmpCal.setMinute(timeList.getElement(i).getMinute());
				}

				if (tmpCal.getTimePoint().toLocalTime().isBefore(LocalTime.now())) {
					tmpCal.plusDate(1);
				}
				dtObj.setAllDate(tmpCal);
				dtObj.setHour(tmpCal.getHour());
				dtObj.setMinute(tmpCal.getMinute());
				dtObj.setPriority(timeList.getElement(i).getPriority());

				resultList.insertDtObj(dtObj);
			}
		}

	}
	
	private boolean isValidDates() {
		boolean result = true;
		for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
			int m = dateList.getElement(j).getMonth();
			int dt = dateList.getElement(j).getDate();

			if ((m == 2 && dt > 29) || (m < 8 && m % 2 == 0 && dt > 30) || (m > 7 && m % 2 == 1 && dt > 30)) {
				result = false;
			}
		}
		return result;
	}
	

	private void addEstimateDateAndTime(boolean isTimeEmpty) {
		for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
			for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
				for (int k = 0; k < focusingRecurNum; k++) {
					DateTimeLogicalObject dtObj = new DateTimeLogicalObject();
					dtObj.copyAllExceptForDayFrom(dateList.getElement(j));

					// 시간정보 없을 땐 종일 로 나타내기
					estimateTime(isTimeEmpty, dtObj, timeList.getElement(i));

					// 년월일 요일 추정
					estimateDates(dtObj, k, dateList.getElement(j));
				}
			}
		}
	}
	

	private void estimateDates(DateTimeLogicalObject dtObj, int k, DateTimeLogicalObject origin) {

		estimateYear(dtObj);

		if (dtObj.getFocusToRepeat() == null) {
			// 반복없이 해당 값만 insert
			estimateOneDate(dtObj);
		} else {
			// focus할 게 있으면 그 정보를 기준으로 for문 돌며 여러값 insert
			estimateMultipleDates(dtObj, k, origin);
		}
	}

	private void estimateMultipleDates(DateTimeLogicalObject dtObj, int k, DateTimeLogicalObject origin) {
		if (dtObj.getMonth() == AppConstants.NO_DATA) {
			dtObj.setMonth(1);
		}
		if (dtObj.getDate() == AppConstants.NO_DATA) {
			dtObj.setDate(1);
		}
		if (dtObj.getDay() == AppConstants.NO_DATA_FOR_DAY) {
			// 날짜에 맞는 요일 구하는 메소드
			dtObj.setProperDay();
		}

		if (k == 0 && dtObj.isAllDayEvent() != true) {
			dtObj.setPriority(Priority.timeWithFirstEstimateDate);
		}
		if (dtObj.isFocusOnDay() == true) {
			// 매주 해당 요일에 맞는 날짜만 뽑도록 구하는 로직
			setDatesByEveryWeek(dtObj, k, origin);
		} else {
			setDatesByToken(dtObj, k);
		}
		resultList.insertDtObj(dtObj);
	}
	
	
	private void setDatesByToken(DateTimeLogicalObject dtObj, int k) {
		DateTimeAdjuster tmpCal2 = new DateTimeAdjuster();
		tmpCal2.setYear(dtObj.getYear());
		tmpCal2.setMonth(dtObj.getMonth());
		tmpCal2.setDate(dtObj.getDate());
		// focus 할 해당 정보를 기준으로 더해주기.
		tmpCal2.setCloseDate(tmpCal2, dtObj.getFocusToRepeat(), k);

		dtObj.setDate(tmpCal2.getDate());
		dtObj.setYear(tmpCal2.getYear());
		dtObj.setMonth(tmpCal2.getMonth());

		// 날짜에 맞는 요일 구하는 로직
		dtObj.setProperDay();
	}

	private void setDatesByEveryWeek(DateTimeLogicalObject dtObj, int k, DateTimeLogicalObject origin) {
		// 날짜 정보 없이 요일만 있을 때
		if(!dtObj.hasInfo(DateTimeEn.year.ordinal())
				&& !dtObj.hasInfo(DateTimeEn.month.ordinal())
				&& !dtObj.hasInfo(DateTimeEn.date.ordinal())) {
			LocalDate tmpDate = LocalDate.now();
			tmpDate = tmpDate.with(TemporalAdjusters.nextOrSame(origin.getDay()));
			tmpDate = tmpDate.plusWeeks(k);
			dtObj.setDate(tmpDate.getDayOfMonth());
			dtObj.setYear(tmpDate.getYear());
			dtObj.setMonth(tmpDate.getMonthValue());
			dtObj.setDay(tmpDate.getDayOfWeek());
			if(k == 0) {
				dtObj.setPriority(Priority.dayOrigin);
			}
			else {
				dtObj.setPriority(Priority.dayClones);
			}
		}
		else { // 날짜도 있는데 요일에 맞춰야할 때
			LocalDate tmpDate = LocalDate.of(dtObj.getYear(), dtObj.getMonth(), dtObj.getDate());
			tmpDate = tmpDate.with(TemporalAdjusters.nextOrSame(origin.getDay()));
			tmpDate = tmpDate.plusWeeks(k);
			dtObj.setDate(tmpDate.getDayOfMonth());
			dtObj.setYear(tmpDate.getYear());
			dtObj.setMonth(tmpDate.getMonthValue());
			dtObj.setDay(tmpDate.getDayOfWeek());
			if(k == 0) {
				dtObj.setPriority(Priority.dayOrigin);
			}
			else {
				dtObj.setPriority(Priority.dayClones);
			}
		}
	}

	private void estimateOneDate(DateTimeLogicalObject dtObj) {
		focusingRecurNum = 1;
		if (dtObj.getMonth() == AppConstants.NO_DATA) {
			dtObj.setMonth(LocalDate.now().getMonthValue());
		}
		if (dtObj.getDate() == AppConstants.NO_DATA) {
			dtObj.setDate(LocalDate.now().getDayOfMonth());
		}
		if (dtObj.getDay() == AppConstants.NO_DATA_FOR_DAY) {
			// 날짜에 맞는 요일 구하는 메소드
			dtObj.setProperDay();
		}

		resultList.insertDtObj(dtObj);
	}

	private void estimateYear(DateTimeLogicalObject dtObj) {
		if (dtObj.getYear() == AppConstants.NO_DATA) {
			dtObj.setYear(LocalDate.now().getYear());
		}
	}

	private void estimateTime(boolean isTimeEmpty, DateTimeLogicalObject dtObj, DateTimeLogicalObject timeObj) {
		if (isTimeEmpty == true) {
			dtObj.setAllDayEvent(true);
		} else { // 날짜와 시간 정보 있을 때
			dtObj.setPriority(timeObj.getPriority());
			dtObj.setHour(timeObj.getHour());
			dtObj.setMinute(timeObj.getMinute());
		}
	}
}
