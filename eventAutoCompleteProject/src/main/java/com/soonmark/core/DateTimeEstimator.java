package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.Priority;

public class DateTimeEstimator {

	private Logger logger = LoggerFactory.getLogger(DateTimeEstimator.class);

	private DateTimeListManager timeList;
	private DateTimeListManager dateList;
	private EventListManager resultList;
	private int focusingRecurNum;

	boolean startDateExists;
	boolean endDateExists;

	public DateTimeEstimator(DateTimeListManager timeList, DateTimeListManager dateList) {
		this.timeList = timeList;
		this.dateList = dateList;
		resultList = new EventListManager();
		focusingRecurNum = 2;
	}

	public EventListManager fillEmptyDatas(InvalidEventObj inputEventObj, boolean focusStart,
			DateTimeListMgrSet... sEstimatedDates) {
		startDateExists = false;
		endDateExists = false;

		boolean isDateEmpty = false;
		boolean isTimeEmpty = false;

		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		if (timeList.getDtMgrList().size() == 0) {
			timeList.insertDtObj(new InvalidDateTimeObj());
			isTimeEmpty = true;
		}

		if (dateList.getDtMgrList().size() == 0) {
			dateList.insertDtObj(new InvalidDateTimeObj());
			isDateEmpty = true;
		}

		// startDate, endDate 존재여부 확인
		if (inputEventObj.getStartDate() != null) {
			startDateExists = true;
		}

		if (inputEventObj.getEndDate() != null) {
			endDateExists = true;
		}

		if (isDateEmpty && isTimeEmpty) {
			timeList.deleteDtObj(0);
			dateList.deleteDtObj(0);
			// 시작날짜가 있으면 시작시간 세팅.
			if (startDateExists) {
				TimeStorage times = new TimeStorage();
				if (inputEventObj.getStartDate().getLocalTime() == null) {
					if (endDateExists && inputEventObj.getEndDate().getLocalTime() == null) {

					} else if (sEstimatedDates.length > 0
							&& sEstimatedDates[0].getResultList().getEvMgrList().get(0).getEndDate() != null) {
						//
					} else {
						// 시간 추가
						Iterator<LocalTime> iter;
						// 오늘이면 이후 시간으로 변경
						if (inputEventObj.getStartDate().getLocalDate()
								.isEqual(RecommendationManager.curTime.toLocalDate())) {
							iter = times.getTimesAfter(RecommendationManager.curTime.toLocalTime(), DateTimeEn.am)
									.iterator();
						} else {
							iter = times.getTimes(DateTimeEn.am).iterator();
						}

						while (iter.hasNext()) {
							LocalTime thisTime = iter.next();
							// InvalidDateTimeObj endDtObj = new InvalidDateTimeObj();
							InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
							InvalidEventObj evObj = new InvalidEventObj();
							startDtObj.setAllDate(inputEventObj.getStartDate());

							startDtObj.setHour(thisTime.getHour());
							startDtObj.setMinute(thisTime.getMinute());
							evObj.setStartDate(startDtObj);
							resultList.insertDtObj(evObj);
						}
					}
				}
				// 시작 날짜시간 모두 존재하고, 종료날짜 없으면 종료 시간을 시작날짜의 시간으로 세팅해서 추천.
				else if (!endDateExists) {
					// 이미 시작 날짜 시간 기준으로 값이 나왔으면 패스
					if (sEstimatedDates.length > 0
							&& sEstimatedDates[0].getResultList().getEvMgrList().get(0).getEndDate() != null) {
						//
					} else {

						// 종료 시간 추가
						int repeatNum = times.getTimes(DateTimeEn.am).size();
						for (int i = 0; i < repeatNum; i++) {
							InvalidDateTimeObj endDtObj = new InvalidDateTimeObj();
							InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
							InvalidEventObj evObj = new InvalidEventObj();
							startDtObj.setDateTime(inputEventObj.getStartDate());
							evObj.setStartDate(startDtObj);

							// 시작 시간 1시간 뒤부터 14개로 추천
							DateTimeAdjuster adjuster = new DateTimeAdjuster();
							adjuster.setTimePoint(
									LocalDateTime.of(startDtObj.getLocalDate(), startDtObj.getLocalTime()));
							adjuster.plusMinute(60 + i * 30);
							endDtObj.setAllDate(adjuster);
							endDtObj.setHour(adjuster.getHour());
							endDtObj.setMinute(adjuster.getMinute());
							evObj.setEndDate(endDtObj);

							resultList.insertDtObj(evObj);
						}
					}
				}
			}
			// 종료날짜가 있으면 종료시간 세팅.
			if (endDateExists) {
				TimeStorage times = new TimeStorage();
				if (inputEventObj.getEndDate().getLocalTime() == null) {
					// 시간 추가
					Iterator<LocalTime> iter = times.getTimes(DateTimeEn.am).iterator();
					while (iter.hasNext()) {
						LocalTime thisTime = iter.next();
						InvalidDateTimeObj endDtObj = new InvalidDateTimeObj();
						InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
						InvalidEventObj evObj = new InvalidEventObj();
						startDtObj.setDateTime(inputEventObj.getStartDate());
						if (startDateExists && inputEventObj.getStartDate().getLocalTime() == null) {
							startDtObj.setHour(thisTime.getHour());
							startDtObj.setMinute(thisTime.getMinute());
						}
						evObj.setStartDate(startDtObj);

						endDtObj.setAllDate(inputEventObj.getEndDate());
						endDtObj.setHour(thisTime.getHour());
						endDtObj.setMinute(thisTime.getMinute());

						evObj.setEndDate(endDtObj);
						resultList.insertDtObj(evObj);
					}
				}
			}

			// 오늘 날짜 + 시간이 있으면 지난 시간은 지우기
			deleteTodayPastTime(focusStart);

			return resultList;
		}

		if (isDateEmpty) {
			setTimeToCloseFutureTime(inputEventObj, focusStart);
			setPriorityForTimeWithoutDate(focusStart);
		} else {
			// 월간 일수 차이에 대한 예외처리
			if (isValidDates() == true) {
				if (sEstimatedDates.length > 0) {
					addEstimateDateAndTime(isTimeEmpty, inputEventObj, focusStart, sEstimatedDates[0]);
				} else {
					addEstimateDateAndTime(isTimeEmpty, inputEventObj, focusStart);
				}
			}
			// 일자가 부분적으로 이탈한 경우 ex. 2/30
			// 가장 가까운 해당 날짜에 맞는 달로 추천하기
			else {
				if (sEstimatedDates.length > 0) {
					addEstimateDateAndTimeDiscardMonth(isTimeEmpty, inputEventObj, focusStart, sEstimatedDates[0]);
				} else {
					addEstimateDateAndTimeDiscardMonth(isTimeEmpty, inputEventObj, focusStart);
				}
			}
		}

		// 오늘 날짜 + 시간이 있으면 지난 시간은 지우기
		deleteTodayPastTime(focusStart);

		return resultList;
	}

	// private void addTodayAmPmTime() {
	// if(timeObj.getAmpm() == DateTimeEn.am || timeObj.getAmpm() == DateTimeEn.pm)
	// {
	// // 시간 추가
	// resultList.getEvMgrList().clear();
	// TimeStorage times = new TimeStorage();
	// Iterator<LocalTime> iter = times.getTimes(timeObj.getAmpm()).iterator();
	// while (iter.hasNext()) {
	// LocalTime thisTime = iter.next();
	// InvalidEventObj evObj = new InvalidEventObj();
	// InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
	// startDtObj.setHour(thisTime.getHour());
	// startDtObj.setMinute(thisTime.getMinute());
	// evObj.setStartDate(startDtObj);
	// resultList.insertDtObj(evObj);
	// }
	// }
	// }

	private void addEstimateDateAndTimeDiscardMonth(boolean isTimeEmpty, InvalidEventObj inputEventObj,
			boolean focusStart, DateTimeListMgrSet... sEstimatedDates) {
		// request 로 들어온 startDate가 존재할 때,
		if ((startDateExists && !endDateExists) || sEstimatedDates.length > 0) {
			for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
					for (int k = 0; k < focusingRecurNum; k++) {
						InvalidEventObj eventObj = new InvalidEventObj(new InvalidDateTimeObj(),
								new InvalidDateTimeObj());
						// InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
						// InvalidDateTimeObj endDtObj = new InvalidDateTimeObj();
						if ((startDateExists && !endDateExists)) {
							eventObj.getStartDate().copyAllExceptForDayFrom(dateList.getElement(j));
							eventObj.getEndDate().copyAllExceptForDayFrom(dateList.getElement(j));

							// 시작시간과 입력시간정보 없을 땐 종일 로 나타내기
							estimateTime(isTimeEmpty, eventObj.getEndDate(), timeList.getElement(i));

							// 시작 시간있으면 종료시간을 그 한 시간뒤로 변경해야함.
							if (inputEventObj.getStartDate().getLocalTime() != null) {
								if (eventObj.getEndDate().isAllDayEvent() == true) {
									eventObj.getEndDate().setHour(inputEventObj.getStartDate().getHour() + 1);
									eventObj.getEndDate().setMinute(inputEventObj.getStartDate().getMinute());
								}
								eventObj.getStartDate().setAllTime(inputEventObj.getStartDate());
							} /*
								 * else { // 시작시간과 입력시간정보 없을 땐 종일 로 나타내기 estimateTime(isTimeEmpty,
								 * eventObj.getEndDate(), timeList.getElement(i)); }
								 */

							eventObj.getStartDate().setAllDate(inputEventObj.getStartDate());
							// 년월일 요일 추정
							estimateDates(eventObj, true, k, dateList.getElement(j),
									inputEventObj.getStartDate().getLocalDate());
						}
						// 부터, 까지 패턴일 때
						else {
							eventObj.setStartDate(null);
							eventObj.getEndDate().copyAllExceptForDayFrom(dateList.getElement(j));
							eventObj.getEndDate().setMonth(AppConstants.NO_DATA);

							// 시작시간과 입력시간정보 없을 땐 종일 로 나타내기
							estimateTime(isTimeEmpty, eventObj.getEndDate(), timeList.getElement(i));

							// 시작 시간있으면 종료시간을 그 한 시간뒤로 변경해야함.
							if (sEstimatedDates[0].getResultList().getEvMgrList().size() != 0) {
								if (sEstimatedDates[0].getResultList().getEvMgrList().get(0).getStartDate()
										.getLocalTime() != null) {
									if (eventObj.getEndDate().isAllDayEvent() == true) {
										eventObj.getEndDate().setHour(sEstimatedDates[0].getResultList().getEvMgrList()
												.get(0).getStartDate().getHour() + 1);
										eventObj.getEndDate().setMinute(sEstimatedDates[0].getResultList()
												.getEvMgrList().get(0).getStartDate().getMinute());
									}
								}
							}

							// 년월일 요일 추정
							if (sEstimatedDates[0].getResultList().getEvMgrList().size() != 0) {
								estimateDates(eventObj, true, k, dateList.getElement(j), sEstimatedDates[0]
										.getResultList().getEvMgrList().get(0).getStartDate().getLocalDate());
							}
						}
					}
				}
			}
		}
		// request 로 들어온 startDate는 없지만, endDate는 존재할 때
		else if (!startDateExists && endDateExists) {

		}
		// startDate, endDate 둘 다 있거나
		// request 로 들어온 startDate, endDate 존재하지 않을 때
		else {
			for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
					for (int k = 0; k < focusingRecurNum; k++) {
						InvalidEventObj eventObj = new InvalidEventObj();
						// InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
						eventObj.setStartDate(new InvalidDateTimeObj());
						eventObj.getStartDate().copyAllExceptForDayFrom(dateList.getElement(j));
						eventObj.getStartDate().setMonth(AppConstants.NO_DATA);

						// 시간정보 없을 땐 종일 로 나타내기
						estimateTime(isTimeEmpty, eventObj.getStartDate(), timeList.getElement(i));

						// 년월일 요일 추정
						estimateDates(eventObj, false, k, dateList.getElement(j));
					}
				}
			}
		}

	}

	private void setPriorityForTimeWithoutDate(boolean focusStart) {
		
		if(focusStart) {
			int closestIdx = 0;
			DateTimeAdjuster closest = new DateTimeAdjuster();
			closest.setDate(resultList.getElement(closestIdx).getStartDate().getDate());
			closest.setMonth(resultList.getElement(closestIdx).getStartDate().getMonth());
			closest.setYear(resultList.getElement(closestIdx).getStartDate().getYear());
			closest.setHour(resultList.getElement(closestIdx).getStartDate().getHour(), false);
			closest.setMinute(resultList.getElement(closestIdx).getStartDate().getMinute());
			
			for (int i = 1; i < resultList.getEvMgrList().size(); i++) {
				DateTimeAdjuster cur = new DateTimeAdjuster();
				cur.setDate(resultList.getElement(i).getStartDate().getDate());
				cur.setMonth(resultList.getElement(i).getStartDate().getMonth());
				cur.setYear(resultList.getElement(i).getStartDate().getYear());
				cur.setHour(resultList.getElement(i).getStartDate().getHour(), false);
				cur.setMinute(resultList.getElement(i).getStartDate().getMinute());
				
				if (closest.getTimePoint().isAfter(cur.getTimePoint())) {
					closest.setTimePoint(cur.getTimePoint());
					closestIdx = i;
				}
			}
			resultList.getElement(closestIdx).getStartDate().setPriority(Priority.timeWithFirstEstimateDate);
		}
		else {
			int closestIdx = 0;
			DateTimeAdjuster closest = new DateTimeAdjuster();
			closest.setDate(resultList.getElement(closestIdx).getEndDate().getDate());
			closest.setMonth(resultList.getElement(closestIdx).getEndDate().getMonth());
			closest.setYear(resultList.getElement(closestIdx).getEndDate().getYear());
			closest.setHour(resultList.getElement(closestIdx).getEndDate().getHour(), false);
			closest.setMinute(resultList.getElement(closestIdx).getEndDate().getMinute());
			
			for (int i = 1; i < resultList.getEvMgrList().size(); i++) {
				DateTimeAdjuster cur = new DateTimeAdjuster();
				cur.setDate(resultList.getElement(i).getEndDate().getDate());
				cur.setMonth(resultList.getElement(i).getEndDate().getMonth());
				cur.setYear(resultList.getElement(i).getEndDate().getYear());
				cur.setHour(resultList.getElement(i).getEndDate().getHour(), false);
				cur.setMinute(resultList.getElement(i).getEndDate().getMinute());
				
				if (closest.getTimePoint().isAfter(cur.getTimePoint())) {
					closest.setTimePoint(cur.getTimePoint());
					closestIdx = i;
				}
			}
			resultList.getElement(closestIdx).getEndDate().setPriority(Priority.timeWithFirstEstimateDate);
		}

	}

	private void setTimeToCloseFutureTime(InvalidEventObj inputEventObj, boolean focusStart) {
		logger.info("날짜 정보없음");
		for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
			for (int j = 0; j < dateList.getDtMgrList().size(); j++) {

				InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
				InvalidDateTimeObj endDtObj = null;
				DateTimeAdjuster tmpCal = new DateTimeAdjuster();

				// boolean isAmPm = false;

				// 시간이 없다는 건, "오전", "오후"란 의미.
				if (timeList.getElement(i).getHour() == AppConstants.NO_DATA) {
					// isAmPm = true;
				} else {
					tmpCal.setHour(timeList.getElement(i).getHour(), false);
					// 메소드의 객체가 now 캘린더가 아니면 true 입력
					if (timeList.getElement(i).getMinute() == AppConstants.NO_DATA) {
						tmpCal.setMinute(0);
					} else {
						tmpCal.setMinute(timeList.getElement(i).getMinute());
					}
				}

				// 미리 선택된 날짜가 전혀 없을 때
				if (!startDateExists && !endDateExists) {
					// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
					if (tmpCal.getTimePoint().toLocalTime().isBefore(RecommendationManager.curTime.toLocalTime())
							&& timeList.getElement(i).isFocusOnAmPm() != true) {
						// if (tmpCal.getTimePoint().toLocalTime().isBefore(LocalTime.now())) {
						tmpCal.plusDate(1);
					}
					startDtObj.setAllDate(tmpCal);
					// if(isAmPm) {
					// TimeStorage times = new TimeStorage();
					// // 시간 추가
					// Iterator<LocalTime> iter =
					// times.getTimes(timeList.getElement(i).getAmpm()).iterator();
					// while (iter.hasNext()) {
					// LocalTime thisTime = iter.next();
					// InvalidEventObj evObj = new InvalidEventObj();
					// // startrDtObj를 가까운 미래로...
					// startDtObj.setHour(thisTime.getHour());
					// startDtObj.setMinute(thisTime.getMinute());
					// evObj.setStartDate(startDtObj);
					// resultList.insertDtObj(evObj);
					// startDtObj = new InvalidDateTimeObj();
					// startDtObj.setAllDate(tmpCal);
					// }
					// }else {
					startDtObj.setHour(tmpCal.getHour());
					startDtObj.setMinute(tmpCal.getMinute());
					// }
					startDtObj.setPriority(timeList.getElement(i).getPriority());
					startDtObj.setFocusOnAmPm(timeList.getElement(i).isFocusOnAmPm());
				}
				// 미리 선택된 날짜시간 정보가 있으면
				else {
					// 일정 시작 날짜만 있을 때
					if (startDateExists && !endDateExists) {

							// tmpCal의 년월일 세팅.
							tmpCal.setAllDate(inputEventObj.getStartDate());

							// 시간이 없을 때
							if (inputEventObj.getStartDate().getLocalTime() == null && focusStart) {
								// 선택된 날짜로 세팅.

								startDtObj.setAllDate(tmpCal);
								startDtObj.setHour(tmpCal.getHour());
								startDtObj.setMinute(tmpCal.getMinute());
								startDtObj.setPriority(timeList.getElement(i).getPriority());
							}
							// 일정 시작 날짜와 시간 모두 있을 때
							else {
								if(!focusStart) {
									startDtObj = null;
								}else {
									startDtObj.setDateTime(inputEventObj.getStartDate());
								}
								endDtObj = new InvalidDateTimeObj();
								endDtObj.setAllDate(inputEventObj.getStartDate());
								endDtObj.setHour(tmpCal.getHour());
								endDtObj.setMinute(tmpCal.getMinute());
								endDtObj.setPriority(timeList.getElement(i).getPriority());
							}
					} else if (!startDateExists && endDateExists) {

					}
					// 시작시간, 종료시간 존재 시,
					else if (startDateExists && endDateExists) {
						startDtObj.setAllDate(inputEventObj.getStartDate());
						startDtObj.setHour(tmpCal.getHour());
						startDtObj.setMinute(tmpCal.getMinute());

						endDtObj = new InvalidDateTimeObj();
						// endDtObj.setAllDate(tmpCal);
						endDtObj.setAllDate(inputEventObj.getEndDate());
						endDtObj.setHour(tmpCal.getHour());
						endDtObj.setMinute(tmpCal.getMinute());
						endDtObj.setPriority(timeList.getElement(i).getPriority());
					}
					// 둘다 존재하지 않을
					else {

					}
				}

				// resultList.insertDtObj(dtObj);
				// if(!isAmPm) {
				InvalidEventObj evObj = new InvalidEventObj(startDtObj, endDtObj);
				resultList.insertDtObj(evObj);
				// }
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

	private void addEstimateDateAndTime(boolean isTimeEmpty, InvalidEventObj inputEventObj, boolean focusStart,
			DateTimeListMgrSet... sEstimatedDates) {
		// request 로 들어온 startDate가 존재할 때,
		if ((startDateExists && !endDateExists) || sEstimatedDates.length > 0) {
			for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
					for (int k = 0; k < focusingRecurNum; k++) {
						InvalidEventObj eventObj = new InvalidEventObj(new InvalidDateTimeObj(),
								new InvalidDateTimeObj());
						// InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
						// InvalidDateTimeObj endDtObj = new InvalidDateTimeObj();
						if ((startDateExists && !endDateExists)) {
							eventObj.getStartDate().copyAllExceptForDayFrom(dateList.getElement(j));
							eventObj.getEndDate().copyAllExceptForDayFrom(dateList.getElement(j));

							// 시작시간과 입력시간정보 없을 땐 종일 로 나타내기
							estimateTime(isTimeEmpty, eventObj.getEndDate(), timeList.getElement(i));

							// 시작 시간있으면 종료시간을 그 한 시간뒤로 변경해야함.
							if (inputEventObj.getStartDate().getLocalTime() != null) {
								if (eventObj.getEndDate().isAllDayEvent() == true) {
									eventObj.getEndDate().setHour(inputEventObj.getStartDate().getHour() + 1);
									eventObj.getEndDate().setMinute(inputEventObj.getStartDate().getMinute());
								}
								eventObj.getStartDate().setAllTime(inputEventObj.getStartDate());
							} /*
								 * else { // 시작시간과 입력시간정보 없을 땐 종일 로 나타내기 estimateTime(isTimeEmpty,
								 * eventObj.getEndDate(), timeList.getElement(i)); }
								 */

							eventObj.getStartDate().setAllDate(inputEventObj.getStartDate());
							// 년월일 요일 추정
							estimateDates(eventObj, true, k, dateList.getElement(j),
									inputEventObj.getStartDate().getLocalDate());
						}
						// 부터, 까지 패턴일 때
						else {
							eventObj.setStartDate(null);
							eventObj.getEndDate().copyAllExceptForDayFrom(dateList.getElement(j));

							// 시작시간과 입력시간정보 없을 땐 종일 로 나타내기
							estimateTime(isTimeEmpty, eventObj.getEndDate(), timeList.getElement(i));

							// 시작 시간있으면 종료시간을 그 한 시간뒤로 변경해야함.
							if (sEstimatedDates[0].getResultList().getEvMgrList().size() != 0) {
								if (sEstimatedDates[0].getResultList().getEvMgrList().get(0).getStartDate()
										.getLocalTime() != null) {
									if (eventObj.getEndDate().isAllDayEvent() == true) {
										eventObj.getEndDate().setHour(sEstimatedDates[0].getResultList().getEvMgrList()
												.get(0).getStartDate().getHour() + 1);
										eventObj.getEndDate().setMinute(sEstimatedDates[0].getResultList()
												.getEvMgrList().get(0).getStartDate().getMinute());
									}
								}
							}

							// 년월일 요일 추정
							if (sEstimatedDates[0].getResultList().getEvMgrList().size() != 0) {
								estimateDates(eventObj, true, k, dateList.getElement(j), sEstimatedDates[0]
										.getResultList().getEvMgrList().get(0).getStartDate().getLocalDate());
							}
						}
					}
				}
			}
		}
		// request 로 들어온 startDate는 없지만, endDate는 존재할 때
		else if (!startDateExists && endDateExists) {

		}
		// startDate, endDate 둘 다 있거나
		// request 로 들어온 startDate, endDate 존재하지 않을 때
		else {
			for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
					for (int k = 0; k < focusingRecurNum; k++) {
						InvalidEventObj eventObj = new InvalidEventObj();
						// InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
						eventObj.setStartDate(new InvalidDateTimeObj());
						eventObj.getStartDate().copyAllExceptForDayFrom(dateList.getElement(j));

						// 시간정보 없을 땐 종일 로 나타내기
						estimateTime(isTimeEmpty, eventObj.getStartDate(), timeList.getElement(i));

						// 년월일 요일 추정
						estimateDates(eventObj, false, k, dateList.getElement(j));
					}
				}
			}

			// // 오늘 날짜 + 시간이 있으면 지난 시간은 지우기
			// deleteTodayPastTime(focusStart);
		}

	}

	private void deleteTodayPastTime(boolean focusStart) {
		int amPmTimesInRange = resultList.getEvMgrList().size();
		Iterator<InvalidEventObj> iter = resultList.getEvMgrList().iterator();
		if (focusStart) {
			while (iter.hasNext()) {
				InvalidEventObj curObj = iter.next();
				if (curObj.getStartDate().isFocusOnAmPm()) {
					LocalDateTime objDateTime = LocalDateTime.of(curObj.getStartDate().getLocalDate(),
							curObj.getStartDate().getLocalTime());
					if (objDateTime.toLocalDate().isEqual(RecommendationManager.curTime.toLocalDate())
							&& objDateTime.isBefore(RecommendationManager.curTime)) {
						amPmTimesInRange--;
					}
				}
			}

			iter = resultList.getEvMgrList().iterator();
			while (iter.hasNext()) {
				InvalidEventObj curObj = iter.next();
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
					// 추정값이 오늘이면
					if (curObj.getStartDate().getLocalDate().isEqual(RecommendationManager.curTime.toLocalDate())) {
						// 추정값과 입력값이 같으면
						if (dateList.getElement(j).getDate() == curObj.getStartDate().getDate()) {
							// 이전 시간인 거 다 지우기
							if (curObj.getStartDate().isFocusOnAmPm()) {
								// 오전 오후일 때 하나라도 있으면 지우기만 하기
								if (amPmTimesInRange > 0) {
									if (curObj.getStartDate().getLocalTime()
											.isBefore(RecommendationManager.curTime.toLocalTime())) {
										iter.remove();
									}
								}
							} else {
								if (curObj.getStartDate().getLocalTime() != null && curObj.getStartDate().getLocalTime()
										.isBefore(RecommendationManager.curTime.toLocalTime())) {
									iter.remove();
								}
							}
						}
						// 추정값만 오늘이면 하루 더하기
						// but 오전, 오후는 하나라도 있으면 지우기만 하기
						else {
							if (curObj.getStartDate().isFocusOnAmPm()) {
								// 오전 오후일 때 하나라도 있으면 지우기만 하기
								if (amPmTimesInRange > 0) {
									if (curObj.getStartDate().getLocalTime()
											.isBefore(RecommendationManager.curTime.toLocalTime())) {
										iter.remove();
									}
								}
								// 하나도 없으면 다음날로 ..
								else {
									DateTimeAdjuster localDateTime = new DateTimeAdjuster();
									localDateTime.setTimePoint(LocalDateTime.of(curObj.getStartDate().getLocalDate(),
											curObj.getStartDate().getLocalTime()));
									localDateTime.plusDate(1);

									curObj.getStartDate().setAllDate(localDateTime);
								}
							} else {
								if (curObj.getStartDate().getLocalTime() != null && curObj.getStartDate().getLocalTime()
										.isBefore(RecommendationManager.curTime.toLocalTime())) {
									DateTimeAdjuster localDateTime = new DateTimeAdjuster();
									localDateTime.setTimePoint(LocalDateTime.of(curObj.getStartDate().getLocalDate(),
											curObj.getStartDate().getLocalTime()));
									localDateTime.plusDate(1);

									curObj.getStartDate().setAllDate(localDateTime);
								}
							}
						}
					}
				}

			}
		}
	}

	private void estimateDates(InvalidEventObj eventObj, boolean fillEnd, int k, InvalidDateTimeObj origin,
			LocalDate... sDate) {

		boolean isYearEstimated = estimateYear(eventObj, fillEnd, sDate);

		if (fillEnd) {
			if (eventObj.getEndDate().getFocusToRepeat() == null) {
				// 반복없이 해당 값만 insert
				estimateOneDate(eventObj, fillEnd, isYearEstimated, sDate);
			} else {
				// focus할 게 있으면 그 정보를 기준으로 for문 돌며 여러값 insert
				estimateMultipleDates(eventObj, fillEnd, k, origin, isYearEstimated, sDate);
			}
		} else {
			if (eventObj.getStartDate().getFocusToRepeat() == null) {
				// 반복없이 해당 값만 insert
				estimateOneDate(eventObj, fillEnd, isYearEstimated, sDate);
			} else {
				// focus할 게 있으면 그 정보를 기준으로 for문 돌며 여러값 insert
				estimateMultipleDates(eventObj, fillEnd, k, origin, isYearEstimated, sDate);
			}
		}
		//
		// // 오늘인데 이전 시간있으면 삭제하기
		// deleteTodayPastTime();
	}

	private void estimateMultipleDates(InvalidEventObj eventObj, boolean fillEnd, int k, InvalidDateTimeObj origin,
			boolean isYearEstimated, LocalDate... sDate) {
		if (fillEnd) {
			if (eventObj.getEndDate().getMonth() == AppConstants.NO_DATA) {
				eventObj.getEndDate().setMonth(1);
			}
			if (eventObj.getEndDate().getDate() == AppConstants.NO_DATA) {
				eventObj.getEndDate().setDate(1);
			}
			if (eventObj.getEndDate().getDay() == AppConstants.NO_DATA_FOR_DAY) {
				// 날짜에 맞는 요일 구하는 메소드
				eventObj.getEndDate().setProperDay();
			}

			if (k == 0 && eventObj.getEndDate().isAllDayEvent() != true) {
				eventObj.getEndDate().setPriority(Priority.timeWithFirstEstimateDate);
			}

			if (eventObj.getEndDate().isFocusOnDay() == true) {
				// 매주 해당 요일에 맞는 날짜만 뽑도록 구하는 로직
				setDatesByEveryWeek(eventObj.getEndDate(), k, origin);
			} else {
				setDatesByToken(eventObj.getEndDate(), k, sDate);
			}

			if (isYearEstimated) {
				eventObj.getEndDate().getLocalDate();
				DateTimeAdjuster now = new DateTimeAdjuster();
				DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
				if (eventObj.getEndDate().getLocalTime() != null) {
					dateTimeAdjuster.setTimePoint(LocalDateTime.of(eventObj.getEndDate().getLocalDate(),
							eventObj.getEndDate().getLocalTime()));
				} else {
					dateTimeAdjuster.setYear(eventObj.getEndDate().getYear());
					dateTimeAdjuster.setMonth(eventObj.getEndDate().getMonth());
					dateTimeAdjuster.setDate(eventObj.getEndDate().getDate());
				}
				dateTimeAdjuster.setCloseDate(now, DateTimeEn.year, 0, false);

				eventObj.getEndDate().setAllDate(dateTimeAdjuster);
			}
		} else {
			if (eventObj.getStartDate().getMonth() == AppConstants.NO_DATA) {
				eventObj.getStartDate().setMonth(1);
			}
			if (eventObj.getStartDate().getDate() == AppConstants.NO_DATA) {
				eventObj.getStartDate().setDate(1);
			}
			if (eventObj.getStartDate().getDay() == AppConstants.NO_DATA_FOR_DAY) {
				// 날짜에 맞는 요일 구하는 메소드
				eventObj.getStartDate().setProperDay();
			}

			if (k == 0 && eventObj.getStartDate().isAllDayEvent() != true) {
				eventObj.getStartDate().setPriority(Priority.timeWithFirstEstimateDate);
			}

			if (eventObj.getStartDate().isFocusOnDay() == true) {
				// 매주 해당 요일에 맞는 날짜만 뽑도록 구하는 로직
				setDatesByEveryWeek(eventObj.getStartDate(), k, origin);
			} else {
				setDatesByToken(eventObj.getStartDate(), k);
			}

			if (isYearEstimated) {
				eventObj.getStartDate().getLocalDate();
				DateTimeAdjuster now = new DateTimeAdjuster();
				DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
				if (eventObj.getStartDate().getLocalTime() != null) {
					dateTimeAdjuster.setTimePoint(LocalDateTime.of(eventObj.getStartDate().getLocalDate(),
							eventObj.getStartDate().getLocalTime()));
				} else {
					dateTimeAdjuster.setYear(eventObj.getStartDate().getYear());
					dateTimeAdjuster.setMonth(eventObj.getStartDate().getMonth());
					dateTimeAdjuster.setDate(eventObj.getStartDate().getDate());
				}
				dateTimeAdjuster.setCloseDate(now, DateTimeEn.year, 0, false);

				eventObj.getStartDate().setAllDate(dateTimeAdjuster);
			}
		}
		// resultList.insertDtObj(dtObj);
		InvalidEventObj evObj = new InvalidEventObj(eventObj.getStartDate(), eventObj.getEndDate());
		resultList.insertDtObj(evObj);
	}

	private void setDatesByToken(InvalidDateTimeObj dtObj, int k, LocalDate... sDate) {
		DateTimeAdjuster tmpCal2 = new DateTimeAdjuster();
		tmpCal2.setYear(dtObj.getYear());
		tmpCal2.setMonth(dtObj.getMonth());
		tmpCal2.setDate(dtObj.getDate());

		boolean checkTime = false;
		if (dtObj.getHour() != AppConstants.NO_DATA) {
			checkTime = true;
			tmpCal2.setHour(dtObj.getHour(), false);
			tmpCal2.setMinute(dtObj.getMinute());
		}
		// focus 할 해당 정보를 기준으로 더해주기.
		if (sDate.length > 0) {
			tmpCal2.setCloseDate(tmpCal2, dtObj.getFocusToRepeat(), k, checkTime, sDate[0]);
		} else {
			tmpCal2.setCloseDate(tmpCal2, dtObj.getFocusToRepeat(), k, checkTime);
		}

		dtObj.setDate(tmpCal2.getDate());
		dtObj.setYear(tmpCal2.getYear());
		dtObj.setMonth(tmpCal2.getMonth());

		// 날짜에 맞는 요일 구하는 로직
		dtObj.setProperDay();
	}

	private void setDatesByEveryWeek(InvalidDateTimeObj dtObj, int k, InvalidDateTimeObj origin) {
		// 날짜 정보 없이 요일만 있을 때
		if (!dtObj.hasInfo(DateTimeEn.year.ordinal()) && !dtObj.hasInfo(DateTimeEn.month.ordinal())
				&& !dtObj.hasInfo(DateTimeEn.date.ordinal())) {
			LocalDate tmpDate = RecommendationManager.curTime.toLocalDate();
			// LocalDate tmpDate = LocalDate.now();
			tmpDate = tmpDate.with(TemporalAdjusters.nextOrSame(origin.getDay()));
			tmpDate = tmpDate.plusWeeks(k);
			dtObj.setDate(tmpDate.getDayOfMonth());
			dtObj.setYear(tmpDate.getYear());
			dtObj.setMonth(tmpDate.getMonthValue());
			dtObj.setDay(tmpDate.getDayOfWeek());
			if (k == 0) {
				dtObj.setPriority(Priority.dayOrigin);
			} else {
				dtObj.setPriority(Priority.dayClones);
			}
		} else { // 날짜도 있는데 요일에 맞춰야할 때
			LocalDate tmpDate = LocalDate.of(dtObj.getYear(), dtObj.getMonth(), dtObj.getDate());
			tmpDate = tmpDate.with(TemporalAdjusters.nextOrSame(origin.getDay()));
			tmpDate = tmpDate.plusWeeks(k);
			dtObj.setDate(tmpDate.getDayOfMonth());
			dtObj.setYear(tmpDate.getYear());
			dtObj.setMonth(tmpDate.getMonthValue());
			dtObj.setDay(tmpDate.getDayOfWeek());
			if (k == 0) {
				dtObj.setPriority(Priority.dayOrigin);
			} else {
				dtObj.setPriority(Priority.dayClones);
			}
		}
	}

	private void estimateOneDate(InvalidEventObj eventObj, boolean fillEnd, boolean isYearEstimated,
			LocalDate... sDate) {

		boolean shouldSkip = false;

		focusingRecurNum = 1;

		if (fillEnd) {
			if (eventObj.getEndDate().getMonth() == AppConstants.NO_DATA) {
				eventObj.getEndDate().setMonth(RecommendationManager.curTime.toLocalDate().getMonthValue());
				// eventObj.getEndDate().setMonth(LocalDate.now().getMonthValue());
			}
			if (eventObj.getEndDate().getDate() == AppConstants.NO_DATA) {
				eventObj.getEndDate().setDate(RecommendationManager.curTime.toLocalDate().getDayOfMonth());
				// eventObj.getEndDate().setDate(LocalDate.now().getDayOfMonth());
			}
			if (eventObj.getEndDate().getDay() == AppConstants.NO_DATA_FOR_DAY) {
				// 날짜에 맞는 요일 구하는 메소드
				eventObj.getEndDate().setProperDay();
			}

			if (isYearEstimated) {
				eventObj.getEndDate().getLocalDate();
				DateTimeAdjuster now = new DateTimeAdjuster();
				DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
				if (eventObj.getEndDate().getLocalTime() != null) {
					dateTimeAdjuster.setTimePoint(LocalDateTime.of(eventObj.getEndDate().getLocalDate(),
							eventObj.getEndDate().getLocalTime()));
				} else {
					dateTimeAdjuster.setYear(eventObj.getEndDate().getYear());
					dateTimeAdjuster.setMonth(eventObj.getEndDate().getMonth());
					dateTimeAdjuster.setDate(eventObj.getEndDate().getDate());
				}
				dateTimeAdjuster.setCloseDate(now, DateTimeEn.year, 0, false);

				eventObj.getEndDate().setAllDate(dateTimeAdjuster);
			}

		} else {
			if (eventObj.getStartDate().getMonth() == AppConstants.NO_DATA) {
				eventObj.getStartDate().setMonth(RecommendationManager.curTime.toLocalDate().getMonthValue());
			}
			if (eventObj.getStartDate().getDate() == AppConstants.NO_DATA) {
				eventObj.getStartDate().setDate(RecommendationManager.curTime.toLocalDate().getDayOfMonth());
			}
			if (eventObj.getStartDate().getDay() == AppConstants.NO_DATA_FOR_DAY) {
				// 날짜에 맞는 요일 구하는 메소드
				eventObj.getStartDate().setProperDay();
			}

			if (isYearEstimated) {
				eventObj.getStartDate().getLocalDate();
				DateTimeAdjuster now = new DateTimeAdjuster();
				now.setTimePoint(RecommendationManager.curTime);
				DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
				if (eventObj.getStartDate().getLocalTime() != null) {
					dateTimeAdjuster.setTimePoint(LocalDateTime.of(eventObj.getStartDate().getLocalDate(),
							eventObj.getStartDate().getLocalTime()));
				} else {
					dateTimeAdjuster.setYear(eventObj.getStartDate().getYear());
					dateTimeAdjuster.setMonth(eventObj.getStartDate().getMonth());
					dateTimeAdjuster.setDate(eventObj.getStartDate().getDate());
				}
				dateTimeAdjuster.setCloseDate(now, DateTimeEn.year, 0, false);

				eventObj.getStartDate().setAllDate(dateTimeAdjuster);
			}

			// if(eventObj.getStartDate().isFocusOnAmPm()) {
			// // 오전, 오후일 때 현재 시간보다 빠르면 스킵하기
			// if(LocalDateTime.of(eventObj.getStartDate().getLocalDate(),eventObj.getStartDate().getLocalTime()).isBefore(RecommendationManager.curTime))
			// {
			// shouldSkip = true;
			// }
			// }
		}

		if (!shouldSkip) {
			// resultList.insertDtObj(dtObj);
			InvalidEventObj evObj = new InvalidEventObj(eventObj.getStartDate(), eventObj.getEndDate());
			resultList.insertDtObj(evObj);
		}

	}

	private boolean estimateYear(InvalidEventObj eventObj, boolean fillEnd, LocalDate... sDate) {
		boolean estimated = false;
		if (fillEnd) {
			if (eventObj.getEndDate().getYear() == AppConstants.NO_DATA) {
				if (sDate.length > 0 && sDate[0] != null) {
					eventObj.getEndDate().setYear(sDate[0].getYear());
				} else {
					eventObj.getEndDate().setYear(RecommendationManager.curTime.toLocalDate().getYear());
					// eventObj.getEndDate().setYear(LocalDate.now().getYear());
					estimated = true;
				}
			}
		} else {
			if (eventObj.getStartDate().getYear() == AppConstants.NO_DATA) {
				if (sDate.length > 0 && sDate[0] != null) {
					eventObj.getStartDate().setYear(sDate[0].getYear());
				} else {
					eventObj.getStartDate().setYear(RecommendationManager.curTime.toLocalDate().getYear());
					// eventObj.getStartDate().setYear(LocalDate.now().getYear());
					estimated = true;
				}
			}
		}

		return estimated;
	}

	private void estimateTime(boolean isTimeEmpty, InvalidDateTimeObj dtObj, InvalidDateTimeObj timeObj) {
		if (isTimeEmpty == true) {
			dtObj.setAllDayEvent(true);
		} else { // 날짜와 시간 정보 있을 때
			// if(timeObj.getAmpm() == DateTimeEn.am || timeObj.getAmpm() == DateTimeEn.pm)
			// {
			// // 시간 추가
			// resultList.getEvMgrList().clear();
			// TimeStorage times = new TimeStorage();
			// Iterator<LocalTime> iter = times.getTimes(timeObj.getAmpm()).iterator();
			// while (iter.hasNext()) {
			// LocalTime thisTime = iter.next();
			// InvalidEventObj evObj = new InvalidEventObj();
			// InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
			// startDtObj.setHour(thisTime.getHour());
			// startDtObj.setMinute(thisTime.getMinute());
			// evObj.setStartDate(startDtObj);
			// resultList.insertDtObj(evObj);
			// }
			// }else {
			dtObj.setPriority(timeObj.getPriority());
			dtObj.setHour(timeObj.getHour());
			dtObj.setMinute(timeObj.getMinute());
			dtObj.setFocusOnAmPm(timeObj.isFocusOnAmPm());
			// }
		}
	}
}
