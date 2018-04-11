package com.soonmark.managers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.enums.DateTimeEn;
import com.soonmark.enums.TokenType;
import com.soonmark.enums.specialDateTypeNeedsDay;

public class DateTimeListManagerSet {
	private Logger logger = LoggerFactory.getLogger(DateTimeListManagerSet.class);
	
	// 앞으로 추천할 날짜 리스트
	private DateTimeListManager dateList;

	// 앞으로 추천할 특수 날짜 리스트
	private DateTimeListManager specialDateList;

	// 앞으로 추천할 요일 리스트
	private DateTimeListManager dayList;

	// 앞으로 추천할 시간 리스트
	private DateTimeListManager timeList;

	// 최종 리스트
	private DateTimeListManager resultList;

	public DateTimeListManagerSet() {
		dateList = new DateTimeListManager(TokenType.dates);
		specialDateList = new DateTimeListManager(TokenType.special);
		dayList = new DateTimeListManager(TokenType.days);
		timeList = new DateTimeListManager(TokenType.times);
		resultList = new DateTimeListManager();
	}

	public DateTimeListManager getDateList() {
		return dateList;
	}

	public void setDateList(DateTimeListManager dateList) {
		this.dateList = dateList;
	}

	public DateTimeListManager getSpecialDateList() {
		return specialDateList;
	}

	public void setSpecialDateList(DateTimeListManager specialDateList) {
		this.specialDateList = specialDateList;
	}

	public DateTimeListManager getDayList() {
		return dayList;
	}

	public void setDayList(DateTimeListManager dayList) {
		this.dayList = dayList;
	}

	public DateTimeListManager getTimeList() {
		return timeList;
	}

	public void setTimeList(DateTimeListManager timeList) {
		this.timeList = timeList;
	}

	public DateTimeListManager getResultList() {
		return resultList;
	}

	public void setResultList(DateTimeListManager resultList) {
		this.resultList = resultList;
	}

	public void innerMerge(TokenType tokenType) {
		getDTListByTokType(tokenType).mergeItself();
	}

	public DateTimeListManager getDTListByTokType(TokenType tokenType) {
		DateTimeListManager list;
		switch (tokenType) {
		case dates:
			list = dateList;
			break;
		case days:
			list = dayList;
			break;
		case times:
			list = timeList;
			break;
		case special:
			list = specialDateList;
			break;
		default:
			list = new DateTimeListManager();
			break;
		}
		return list;
	}

	public void mergeBetween(TokenType target, TokenType nonTarget) {
		DateTimeListManager targetList = getDTListByTokType(target);
		DateTimeListManager nonTargetList = getDTListByTokType(nonTarget);

		// mergeBy 완성하자
		// targetList.mergeBy(nonTarget, nonTargetList);

		if (target == TokenType.dates && nonTarget == TokenType.days) {

			// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
			dateList.insertDtObj(new DateTimeManager());

			// 우선, 요일과 날짜 크로스
			// 날짜가 있고 요일이 없는 경우나 - clear
			// 요일이 있는데 날짜가 없는 경우
			// 요일과 날짜가 있지만 서로 안 맞는 경우
			// 요일과 날짜가 있고 둘이 맞는 경우
			// 위 4가지 경우에 대해 코딩.

			for (int i = 0; i < dateList.getDtMgrList().size(); i++) {
				// 요일 정보 없으면 그냥 나가기
				if (dayList.getDtMgrList().size() == 0) {
					dateList.deleteDtObj(dateList.getDtMgrList().size() - 1);
					break;
				}
				for (int j = 0; j < dayList.getDtMgrList().size(); j++) {
					// 날짜 없고 요일있는건 처리해야하니까 if문 처리 안 함.
					DateTimeAdjuster tmpCal = new DateTimeAdjuster();
					DateTimeManager dtObj = new DateTimeManager();

					// 날짜 없고 요일만 있을 때
					if (dateList.getDtMgrList().size() == 1) {
						// 가까운 미래시 날짜 찾아 tmpCal에 세팅.
						tmpCal.setCloseDateOfTheDay(dayList.getElement(j).getDay());
						dtObj.setFocusOnDay(true);

					} else { // 요일 정보와 날짜 정보가 있을 때는 요일 정보를 무시
						if (dateList.getElement(i).hasInfo(DateTimeEn.year.ordinal())) {
							tmpCal.setYear(dateList.getElement(i).getYear());
						}
						if (dateList.getElement(i).hasInfo(DateTimeEn.month.ordinal())) {
							tmpCal.setMonth(dateList.getElement(i).getMonth());
						}
						if (dateList.getElement(i).hasInfo(DateTimeEn.date.ordinal())) {
							tmpCal.setDate(dateList.getElement(i).getDate());
						}
						dtObj.setFocusOnDay(false);
					}

					dtObj.setAllDate(tmpCal);

					dateList.getElement(i).setAllDate(dtObj);

					dateList.getElement(i).setFocusOnDay(dtObj.isFocusOnDay());
					dateList.getElement(i).setHasInfo(DateTimeEn.day.ordinal(), true);
				}
			}

		} else if (target == TokenType.dates && nonTarget == TokenType.special) {
			// 빈 객체 하나 넣어주기
			targetList.insertDtObj(new DateTimeManager());

			boolean out = false;
			for (int i = 0; i < targetList.getDtMgrList().size(); i++) {
				for (int j = 0; j < nonTargetList.getDtMgrList().size(); j++) {

					DateTimeAdjuster cal = new DateTimeAdjuster();
					cal.setTimePoint(LocalDateTime.now());
					if (nonTargetList.getElement(j).getSpecialDate().equals("오늘")) {
						targetList.getElement(i).setAllDate(cal);
						out = true;
					} else if (nonTargetList.getElement(j).getSpecialDate().equals("내일")) {
						cal.plusDate(1);
						targetList.getElement(i).setAllDate(cal);
						out = true;
					} else if (nonTargetList.getElement(j).getSpecialDate().equals("모레")) {
						cal.plusDate(2);
						targetList.getElement(i).setAllDate(cal);
						out = true;
					}
					if (out) {
						targetList.insertDtObj(new DateTimeManager());
						break;
					}
				}
				if (out) {
					break;
				}
			}

			if (!out) {
				for (int i = 0; i < targetList.getDtMgrList().size(); i++) {
					if (targetList.getDtMgrList().size() > 1 && i == targetList.getDtMgrList().size() - 1) {
						continue;
					}
					for (int j = 0; j < nonTargetList.getDtMgrList().size(); j++) {
						DateTimeManager dtObj = new DateTimeManager();

						// dtObj 초기화 : secList로 세팅
						dtObj.setAllDate(nonTargetList.getElement(j));
						if (!dtObj.getSpecialDate().equals("-1")) {
							for (specialDateTypeNeedsDay specialDT : specialDateTypeNeedsDay.values()) {
								if (!dtObj.getSpecialDate().equals(specialDT.getTitle())) {
									continue;
								}
								// 이번주 : 0, 다음주 : 1, 다다음주 : 2
								// 이번주라고 무조건 1번째주인건 아님.
								LocalDate td = LocalDate.now();

								// LocalDateTime 에는 Week of Month 가 없어서...
								Calendar calendar = new GregorianCalendar();
								Date trialTime = new Date();
								calendar.setTime(trialTime);
								int wom = calendar.get(Calendar.WEEK_OF_MONTH);

								// 오늘이 수요일인데 이번주 화요일 입력하면 지났지만 나와야함. 그러니까 무턱대고 1주를 더하면 안 됨.
								if (targetList.getElement(i).getDay() != null) {
									td = td.with(TemporalAdjusters.dayOfWeekInMonth(specialDT.ordinal() + wom,
											targetList.getElement(i).getDay()));
								} else { // 요일 정보가 없는데 이번주, 다음주 등의 정보가 있을 때
									td = td.with(TemporalAdjusters.dayOfWeekInMonth(specialDT.ordinal() + wom,
											td.getDayOfWeek()));
								}
								targetList.getElement(i).setDate(td.getDayOfMonth());
								targetList.getElement(i).setMonth(td.getMonthValue());
								targetList.getElement(i).setYear(td.getYear());
								break;
							}
						}
						if (targetList.getElement(i).getDay() == null) {
							targetList.insertDtObj(new DateTimeManager());
							out = true;
							break;
						}
					}
					if (out) {
						break;
					}
				}
			}
			targetList.deleteDtObj(targetList.getDtMgrList().size() - 1);
		}
	}

}
