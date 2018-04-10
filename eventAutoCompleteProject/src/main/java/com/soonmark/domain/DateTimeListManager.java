package com.soonmark.domain;

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

public class DateTimeListManager {
	private Logger logger = LoggerFactory.getLogger(DateTimeListManager.class);
	
	// 앞으로 추천할 날짜 리스트
	private DateTimeListDTO dateList;

	// 앞으로 추천할 예외 날짜 리스트
	private DateTimeListDTO specialDateList;

	// 앞으로 추천할 요일 리스트
	private DateTimeListDTO dayList;

	// 앞으로 추천할 시간 리스트
	private DateTimeListDTO timeList;

	// 최종 리스트
	private DateTimeListDTO resultList;

	public DateTimeListManager() {
		dateList = new DateTimeListDTO(TokenType.dates);
		specialDateList = new DateTimeListDTO(TokenType.special);
		dayList = new DateTimeListDTO(TokenType.days);
		timeList = new DateTimeListDTO(TokenType.times);
		resultList = new DateTimeListDTO();
	}

	public DateTimeListDTO getDateList() {
		return dateList;
	}

	public void setDateList(DateTimeListDTO dateList) {
		this.dateList = dateList;
	}

	public DateTimeListDTO getSpecialDateList() {
		return specialDateList;
	}

	public void setSpecialDateList(DateTimeListDTO specialDateList) {
		this.specialDateList = specialDateList;
	}

	public DateTimeListDTO getDayList() {
		return dayList;
	}

	public void setDayList(DateTimeListDTO dayList) {
		this.dayList = dayList;
	}

	public DateTimeListDTO getTimeList() {
		return timeList;
	}

	public void setTimeList(DateTimeListDTO timeList) {
		this.timeList = timeList;
	}

	public DateTimeListDTO getResultList() {
		return resultList;
	}

	public void setResultList(DateTimeListDTO resultList) {
		this.resultList = resultList;
	}

	public void innerMerge(TokenType tokenType) {
		getDTListByTokType(tokenType).mergeItself();
	}

	public DateTimeListDTO getDTListByTokType(TokenType tokenType) {
		DateTimeListDTO list;
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
			list = new DateTimeListDTO();
			break;
		}
		return list;
	}

	public void mergeBetween(TokenType target, TokenType nonTarget) {
		DateTimeListDTO targetList = getDTListByTokType(target);
		DateTimeListDTO nonTargetList = getDTListByTokType(nonTarget);

		// mergeBy 완성하자
		// targetList.mergeBy(nonTarget, nonTargetList);

		if (target == TokenType.dates && nonTarget == TokenType.days) {

			// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
			dateList.insertDtObj(new DateTimeObject());

			// 우선, 요일과 날짜 크로스
			// 날짜가 있고 요일이 없는 경우나 - clear
			// 요일이 있는데 날짜가 없는 경우
			// 요일과 날짜가 있지만 서로 안 맞는 경우
			// 요일과 날짜가 있고 둘이 맞는 경우
			// 위 4가지 경우에 대해 코딩.

			for (int i = 0; i < dateList.getList().size(); i++) {
				// 요일 정보 없으면 그냥 나가기
				if (dayList.getList().size() == 0) {
					dateList.deleteDtObj(dateList.getList().size() - 1);
					break;
				}
				for (int j = 0; j < dayList.getList().size(); j++) {
					// 날짜 없고 요일있는건 처리해야하니까 if문 처리 안 함.
					MyLocalDateTime tmpCal = new MyLocalDateTime();
					DateTimeObject dtObj = new DateTimeObject();

					// 날짜 없고 요일만 있을 때
					if (dateList.getList().size() == 1) {
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
			targetList.insertDtObj(new DateTimeObject());

			boolean out = false;
			for (int i = 0; i < targetList.getList().size(); i++) {
				for (int j = 0; j < nonTargetList.getList().size(); j++) {

					MyLocalDateTime cal = new MyLocalDateTime();
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
						targetList.insertDtObj(new DateTimeObject());
						break;
					}
				}
				if (out) {
					break;
				}
			}

			if (!out) {
				for (int i = 0; i < targetList.getList().size(); i++) {
					if (targetList.getList().size() > 1 && i == targetList.getList().size() - 1) {
						continue;
					}
					for (int j = 0; j < nonTargetList.getList().size(); j++) {
						DateTimeObject dtObj = new DateTimeObject();

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
							targetList.insertDtObj(new DateTimeObject());
							out = true;
							break;
						}
					}
					if (out) {
						break;
					}
				}
			}
			targetList.deleteDtObj(targetList.getList().size() - 1);
		}
	}

	public void createRecommendation() {
		
		int recomNum = 2; // 추천할 개수를 2개로 한정

		// 현재 시스템 날짜 // 여기서 수정하자.
		MyLocalDateTime now = new MyLocalDateTime();
		// now.plusHour(3);
		
		
		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		timeList.insertDtObj(new DateTimeObject());
		
		// dateList에 추가했던 element를 삭제했으므로 다시 하나 만들어줌.
		dateList.insertDtObj(new DateTimeObject());

		for (int i = 0; i < timeList.getList().size(); i++) {
			for (int j = 0; j < dateList.getList().size(); j++) {

				// 둘다 정보가 들어왔으면 빈값 매칭 안 해줘도 됨.
				// 시간만 있을 때는 -> 날짜 빈거랑 매칭하고 시간 여분 빼기
				// 날짜만 있을 때는 -> 시간 빈거랑 매칭하고 날짜 여분 빼기
				// 둘 다 비어있을 때도 안 해줘도 됨.
				if (i == timeList.getList().size() - 1 && j == dateList.getList().size() - 1) {
					continue;
				}

//				logger.info("시간 정보 존재");

				int y = dateList.getElement(j).getYear();
				int m = dateList.getElement(j).getMonth();
				int dt = dateList.getElement(j).getDate();
				DayOfWeek day = dateList.getElement(j).getDay();
				boolean isFocusOnDay = dateList.getElement(j).isFocusOnDay();

				int h = timeList.getElement(i).getHour();
				int min = timeList.getElement(i).getMinute();

				// 월간 일수 차이에 대한 예외처리
				if ((m == 2 && dt > 28) || (m < 8 && m % 2 == 0 && dt > 30) || (m > 7 && m % 2 == 1 && dt > 30)) {

					break;
				}

				// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
				if (dateList.getList().size() == 1) {
					logger.info("날짜 정보없음");

					// 현재 시각과 비교해서 이미 지난 시간일 경우 + 12;
					MyLocalDateTime tmpCal = new MyLocalDateTime();

					// 메소드의 객체가 now 캘린더가 아니면 true 입력
					tmpCal.setHour(h, true);
					if (min == -1) {
						tmpCal.setMinute(0);
					} else {
						tmpCal.setMinute(min);
					}

					// for 문 돌면서 비교할 기준 시 설정
					MyLocalDateTime comparedCal = new MyLocalDateTime();
					comparedCal.setTimePoint(now.getTimePoint());

					for (int k = 0; k < recomNum; k++) {
						DateTimeObject dtObj = new DateTimeObject();

						tmpCal.setCloseDateOfTime(comparedCal);
						comparedCal.setTimePoint(tmpCal.getTimePoint());

						// 현재 시스템 날짜
						dtObj.setAllDate(tmpCal);
						dtObj.setHour(tmpCal.getHour());
						dtObj.setMinute(tmpCal.getMinute());

						resultList.insertDtObj(dtObj);
					}
				}

				else { // 날짜 정보 있으면 (시간은 있든 말든 상관없음.)
					for (int k = 0; k < recomNum; k++) {
						DateTimeObject dtObj = new DateTimeObject();
						DateTimeObject secDtObj = new DateTimeObject();

						dtObj.setAllDate(dateList.getElement(j));
						dtObj.setFocusOnDay(isFocusOnDay);
						dtObj.setFocusToRepeat(dateList.getElement(j).getFocusToRepeat());

						// 무슨 정보가 있는지 담겨있음
						dtObj.setHasInfo(DateTimeEn.year.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.year.ordinal()));
						dtObj.setHasInfo(DateTimeEn.month.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.month.ordinal()));
						dtObj.setHasInfo(DateTimeEn.date.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.date.ordinal()));
						dtObj.setHasInfo(DateTimeEn.day.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.day.ordinal()));

						// 시간정보 없을 땐, 종일 로 나타내기
						if (timeList.getList().size() == 1) {
							dtObj.setAllDayEvent(true);

						} else { // 날짜와 시간 정보 있을 때
							dtObj.setHour(h);
							dtObj.setMinute(min);
						}

						if (y == -1) {
							dtObj.setYear(now.getYear());
						}

						if (dtObj.getFocusToRepeat() == null) { // 반복없이 해당 값만 insert 하게 하기
							recomNum = 1; // 반복 안 하도록
							if (m == -1) {
								dtObj.setMonth(now.getMonth());
							}
							if (dt == -1) {
								dtObj.setDate(now.getDate());
							}
							if (day == null) {
								// 날짜에 맞는 요일 구하는 메소드
								dtObj.setProperDay();
							}

						} else { // focus할 게 있으면 그 정보를 기준으로 for문 돌게끔...
							if (m == -1) {
								dtObj.setMonth(1);
							}
							if (dt == -1) {
								dtObj.setDate(1);
							}
							if (day == null) {
								// 날짜에 맞는 요일 구하는 메소드
								dtObj.setProperDay();
							}

							// 이전에는 요일 정보를 안 받았기 때문에 이렇게 짰는데 다시 짜자.
							if (dtObj.isFocusOnDay() == true) {
								// 요일에 맞는 날짜만 뽑도록 구하는 로직
								LocalDate tmpDate = LocalDate.of(dtObj.getYear(), dtObj.getMonth(), dtObj.getDate());

								tmpDate = tmpDate.plusWeeks(k);
								dtObj.setDate(tmpDate.getDayOfMonth());
								dtObj.setYear(tmpDate.getYear());
								dtObj.setMonth(tmpDate.getMonthValue());
							} else {
								MyLocalDateTime tmpCal2 = new MyLocalDateTime();
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
						}

						resultList.insertDtObj(dtObj);

						// 시간정보와 날짜 모두 있을 땐, halfTime 일 경우, 오후 시간도 저장
						if (timeList.getList().size() > 1 && dtObj.getHour() <= 12) {
							secDtObj.setHour((dtObj.getHour() + 12) % 24);
							secDtObj.setMinute(dtObj.getMinute());
							secDtObj.setAllDate(dtObj);
							resultList.insertDtObj(secDtObj);
						}
					}
				}

			}
		}
	}

}
