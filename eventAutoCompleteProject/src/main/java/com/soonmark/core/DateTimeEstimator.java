package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.Priority;
import com.soonmark.domain.TokenType;

public class DateTimeEstimator {

	private Logger logger = LoggerFactory.getLogger(DateTimeEstimator.class);

	private DateTimeListManager timeList;
	private DateTimeListManager dateList;
	private EventListManager resultList;
	private int focusingRecurNum;

	boolean inputStartDateExists;
	boolean inputEndDateExists;

	public DateTimeEstimator(DateTimeListManager timeList, DateTimeListManager dateList) {
		this.timeList = timeList;
		this.dateList = dateList;
		resultList = new EventListManager();
		focusingRecurNum = 2;
	}

	private boolean insertObjIfEmpty(TokenType type) {
		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		if (type == TokenType.dates) {
			if (dateList.getDtMgrList().size() == 0) {
				dateList.insertDtObj(new InvalidDateTimeObj());
				return true;
			}
		} else if (type == TokenType.times) {

			if (timeList.getDtMgrList().size() == 0) {
				timeList.insertDtObj(new InvalidDateTimeObj());
				return true;
			}
		}
		return false;
	}

	private boolean isNotNull(InvalidDateTimeObj inputDateTimeObj) {
		if (inputDateTimeObj != null) {
			return true;
		}
		return false;
	}

	public EventListManager fillEmptyDatas(InvalidEventObj inputEventObj, boolean focusStart,
			DateTimeListMgrSet sEstimatedDates, During during) {

		// focusingnRecurNum 세팅
		setFocusingRecurNum();

		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		boolean isDateEmpty = insertObjIfEmpty(TokenType.dates);
		boolean isTimeEmpty = insertObjIfEmpty(TokenType.times);

		// startDate, endDate 존재여부 확인
		setStartEndExists(inputEventObj);

		if (isDateEmpty && isTimeEmpty) {
			timeList.deleteDtObj(0);
			dateList.deleteDtObj(0);

			estimateDateTimeBy(inputEventObj, focusStart, sEstimatedDates, during);

			// 오늘 날짜 + 시간이 있으면 지난 시간은 지우기
			deleteTodayPastTime(focusStart);

			return resultList;
		}

		if (isDateEmpty) {
			// 시간 10개 뽑기
			setTimeToCloseFutureTime(inputEventObj, focusStart, sEstimatedDates);
			setPriorityForTimeWithoutDate(focusStart);
		} else {
			// 월간 일수 차이에 대한 예외처리
			if (isValidDates() == true) {
				addEstimateDateAndTime(isTimeEmpty, inputEventObj, focusStart, sEstimatedDates);
			}
			// 일자가 부분적으로 이탈한 경우 ex. 2/30
			// 가장 가까운 해당 날짜에 맞는 달로 추천하기
			else {
				addEstimateDateAndTimeDiscardMonth(isTimeEmpty, inputEventObj, focusStart, sEstimatedDates);
			}
		}

		// 오늘 날짜 + 시간이 있으면 지난 시간은 지우기

		deleteTodayPastTime(focusStart);

		return resultList;
	}

	private void setStartEndExists(InvalidEventObj inputEventObj) {
		inputStartDateExists = isNotNull(inputEventObj.getStartDate());
		inputEndDateExists = isNotNull(inputEventObj.getEndDate());
	}

	private void estimateDateTimeBy(InvalidEventObj inputEventObj, boolean focusStart,
			DateTimeListMgrSet sEstimatedDates, During during) {
		// 시작날짜가 있으면 시작시간 세팅.
		if (inputStartDateExists) {
			setStartTimes(inputEventObj, sEstimatedDates, during);
		}
		// 종료날짜가 있으면 종료시간 세팅.
		if (inputEndDateExists) {
			if (sEstimatedDates == AppConstants.NO_DATA_TO_CONSIDER) {
				if (inputEventObj.getEndDate().hasNoTime()
						|| inputEventObj.getEndDate().getMinute() == AppConstants.NO_DATA) {
					setEndTimes(inputEventObj, false, sEstimatedDates, during);
				}
			}
		}
	}

	private void setEndTimes(InvalidEventObj inputEventObj, boolean ignoreStartTime,
			DateTimeListMgrSet sEstimatedDates, During during) {

		if (!ignoreStartTime) {
			// endDate에 분이 없으면 분 리스트 추천
			if (!inputEventObj.getEndDate().hasNoTime()) {
				setMinuteList(inputEventObj, false, during);
				return;
			}
		}
		
		int stdsize = getStoredTimeListByCurTime(inputEventObj.getStartDate()).size();
		if(during != null) {
			stdsize = 1;
		}

		// 시간 추가
		for (int i = 0; i < stdsize; i++) {
			LocalTime curPosTime = getStoredTimeListByCurTime(inputEventObj.getStartDate()).get(i);

			InvalidDateTimeObj endDtObj = new InvalidDateTimeObj();
			InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
			InvalidEventObj evObj = new InvalidEventObj();
			startDtObj.setDateTime(inputEventObj.getStartDate());

			if (inputStartDateExists && inputEventObj.getStartDate().hasNoTime()) {
				startDtObj.setHour(curPosTime.getHour());
				startDtObj.setMinute(curPosTime.getMinute());
			}
			evObj.setStartDate(startDtObj);

			if (ignoreStartTime) {
				// 시작 시간 1시간 뒤부터 1시간 간격으로 10개로 추천
				DateTimeAdjuster adjuster = new DateTimeAdjuster();
				adjuster.setTimePoint(LocalDateTime.of(startDtObj.getLocalDate(), startDtObj.getLocalTime()));
				if(during == null || during.getType() == DateTimeEn.hour) {
					adjuster.plusHour(1 + i);
					endDtObj.setHour(adjuster.getHour());
				}

				endDtObj.setAllDate(adjuster);
				
				// endDtObj.setMinute(adjuster.getMinute());
			} else {
				endDtObj.setAllDate(inputEventObj.getEndDate());
				if (inputEventObj.getStartDate().hasNoTime()) {
					endDtObj.setMinute(curPosTime.getMinute());
				}

				if (inputEventObj.getEndDate().getLocalDate().isEqual(inputEventObj.getStartDate().getLocalDate())) {
					// 시작 종료 날짜가 같으면 1시간 뒤부터 1시간 간격으로 추천
					endDtObj.setHour(curPosTime.getHour() + 1);
					stdsize = getStoredTimeListByCurTime(inputEventObj.getStartDate()).size() - 1;
				} else {
					if (inputEventObj.getEndDate().hasNoTime()) {
						endDtObj.setHour(curPosTime.getHour());
					}
				}
			}

			evObj.setEndDate(endDtObj);
			resultList.insertDtObj(evObj);
		}
	}

	private void setStartTimes(InvalidEventObj inputEventObj, DateTimeListMgrSet sEstimatedDates, During during) {
		// 생성된 객체에 시간이 없는 경우
		if (inputEventObj.getStartDate().hasNoTime()) {
			// 1/1~1/2 와 같이 생성 객체가 시간이 없는 기간일 경우
			if (inputEndDateExists && inputEventObj.getEndDate().hasNoTime()) {

				// 1/1~1/2 + "12시"인 경우
				if (sEstimatedDates != AppConstants.NO_DATA_TO_CONSIDER
						&& sEstimatedDates.getResultList().getElement(0).getStartDate() != null
						&& !sEstimatedDates.getResultList().getElement(0).getStartDate().hasNoTime()
						&& sEstimatedDates.getResultList().getElement(0).getEndDate() != null
						&& sEstimatedDates.getResultList().getElement(0).getEndDate().hasNoTime()) {
					sEstimatedDates.getResultList().getEvMgrList().clear();
				}
			}
			// 이미 생성된 시작날짜가 있어서 추정값이 end로 들어간 경우
			// 예를 들어, 1/1 + "2일"가 들어온 경우
			else if (sEstimatedDates != AppConstants.NO_DATA_TO_CONSIDER
					&& sEstimatedDates.getResultList().getElement(0).getEndDate() != null) {
			}
			// 이미 생성된 시작날짜가 있고 시작시간은 없는데 ""가 들어온 경우
			// 1/1 + ""가 들어온 경우
			else if (sEstimatedDates != AppConstants.NO_DATA_TO_CONSIDER
					&& sEstimatedDates.getResultList().getElement(0).getStartDate() != null
					&& !sEstimatedDates.getResultList().getElement(0).getStartDate().hasNoTime()) {
			}
			// 나머지 경우 시간 최대 10개 추가
			else {
				// 시간 추가
				Iterator<LocalTime> iter = getStoredTimeListByCurTime(inputEventObj.getStartDate()).iterator();

				while (iter.hasNext()) {
					LocalTime curPosTime = iter.next();
					InvalidEventObj evObj = new InvalidEventObj();
					InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();

					startDtObj.setAllDate(inputEventObj.getStartDate());

					if(during != null && during.getType().getTypeNum() == TokenType.dates.ordinal()) {
						evObj.setStartDate(startDtObj);

						resultList.insertDtObj(evObj);
						
						break;
					}
					
					startDtObj.setHour(curPosTime.getHour());
					// startDtObj.setMinute(AppConstatn);

					evObj.setStartDate(startDtObj);

					resultList.insertDtObj(evObj);
				}
			}
		}
		// 시작 날짜시간 모두 존재하고, 종료날짜 없으면 종료 시간을 시작날짜의 시간 한시간 뒤부터 차례로 세팅해서 추천.
		// 단, 시작 시간이 0분이면 분 리스트 생성해서 추천
		else if (!inputEndDateExists) {
			// 이미 시작 날짜 시간 기준으로 값이 나왔으면 패스
			if (sEstimatedDates != AppConstants.NO_DATA_TO_CONSIDER
					&& sEstimatedDates.getResultList().getElement(0).getEndDate() != null) {
				//
			} else {
				if (inputTimeWithoutMinute(inputEventObj)) {
					setMinuteList(inputEventObj, true, during);
				} else {
					setEndTimes(inputEventObj, true, sEstimatedDates, during);
				}
			}
		} else if (inputEndDateExists) {
		}

	}

	private void setMinuteList(InvalidEventObj inputEventObj, boolean focusStart, During during) {
		TimeStorage times = new TimeStorage();

		// 시간(분) 추가
		for (int i = 0; i < times.getMinTimesWith(inputEventObj.getStartOrEnd(focusStart).getHour()).size(); i++) {
			LocalTime curPosTime = times.getMinTimesWith(inputEventObj.getStartOrEnd(focusStart).getHour()).get(i);

			InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
			InvalidDateTimeObj endDtObj = null;

			if (focusStart) {
				startDtObj.setDateTime(inputEventObj.getStartOrEnd(focusStart));
				startDtObj.setHour(curPosTime.getHour());
				startDtObj.setMinute(curPosTime.getMinute());
			} else {
				startDtObj.setDateTime(inputEventObj.getStartDate());
				endDtObj = new InvalidDateTimeObj();
				endDtObj.setDateTime(inputEventObj.getStartOrEnd(focusStart));
				endDtObj.setHour(curPosTime.getHour());
				endDtObj.setMinute(curPosTime.getMinute());
			}

			InvalidEventObj evObj = new InvalidEventObj(startDtObj, endDtObj);
			resultList.insertDtObj(evObj);
			
			if(during != null && i == 0) {
				break;
			}
		}
	}

	private boolean inputTimeWithoutMinute(InvalidEventObj inputEventObj) {
		if (inputEventObj.getStartDate().getMinute() == AppConstants.NO_DATA) {
			return true;
		}
		return false;
	}

	private List<LocalTime> getStoredTimeListByCurTime(InvalidDateTimeObj dateTimeObj) {
		TimeStorage times = new TimeStorage();

		// 오늘이면 이후 시간으로 변경
		if (dateTimeObj.getLocalDate().isEqual(RecommendationManager.curTime.toLocalDate())) {
			return times.getTimesAfter(RecommendationManager.curTime.toLocalTime(), null);
		} else {
			return times.getTimes(null);
		}
	}

	private void setFocusingRecurNum() {
		// year+date, date, month+date+24time, month+24time, dayOfWeek, 이번주+12time,
		// 이번주+date+요일+12time : 2
		// month+12time : 4

	}

	private void addEstimateDateAndTimeDiscardMonth(boolean isTimeEmpty, InvalidEventObj inputEventObj,
			boolean focusStart, DateTimeListMgrSet sEstimatedDates) {
		// request 로 들어온 startDate가 존재할 때,
		if ((inputStartDateExists && !inputEndDateExists) || sEstimatedDates != AppConstants.NO_DATA_TO_CONSIDER) {
			for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
					for (int k = 0; k < focusingRecurNum; k++) {
						InvalidEventObj eventObj = new InvalidEventObj(new InvalidDateTimeObj(),
								new InvalidDateTimeObj());
						// InvalidDateTimeObj startDtObj = new InvalidDateTimeObj();
						// InvalidDateTimeObj endDtObj = new InvalidDateTimeObj();
						if ((inputStartDateExists && !inputEndDateExists)) {
							eventObj.getStartDate().copyAllExceptForDayFrom(dateList.getElement(j));
							eventObj.getEndDate().copyAllExceptForDayFrom(dateList.getElement(j));

							// 시작시간과 입력시간정보 없을 땐 종일 로 나타내기
							estimateTime(isTimeEmpty, eventObj.getEndDate(), timeList.getElement(i));

							// 시작 시간있으면 종료시간을 그 한 시간뒤로 변경해야함.
							if (!inputEventObj.getStartDate().hasNoTime()) {
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
							if (sEstimatedDates.getResultList().getEvMgrList().size() != 0) {
								if (!sEstimatedDates.getResultList().getElement(0).getStartDate().hasNoTime()) {
									if (eventObj.getEndDate().isAllDayEvent() == true) {
										eventObj.getEndDate().setHour(
												sEstimatedDates.getResultList().getElement(0).getStartDate().getHour()
														+ 1);
										eventObj.getEndDate().setMinute(sEstimatedDates.getResultList().getElement(0)
												.getStartDate().getMinute());
									}
									setMinuteZeroIfNull(sEstimatedDates.getResultList().getEvMgrList());
								}
							}

							// 년월일 요일 추정
							if (sEstimatedDates.getResultList().getEvMgrList().size() != 0) {
								estimateDates(eventObj, true, k, dateList.getElement(j),
										sEstimatedDates.getResultList().getElement(0).getStartDate().getLocalDate());
							}
						}
					}
				}
			}
		}
		// request 로 들어온 startDate는 없지만, endDate는 존재할 때
		else if (!inputStartDateExists && inputEndDateExists) {

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
						estimateDates(eventObj, false, k, dateList.getElement(j), null);
					}
				}
			}
		}

	}

	private void setPriorityForTimeWithoutDate(boolean focusStart) {
		if (resultList.getEvMgrList().size() == 0) {
			return;
		}

		int closestIdx = 0;
		DateTimeAdjuster closest = new DateTimeAdjuster();

		closest.setDateTime(resultList.getElement(closestIdx).getStartOrEnd(focusStart));

		for (int i = 1; i < resultList.getEvMgrList().size(); i++) {
			DateTimeAdjuster cur = new DateTimeAdjuster();
			cur.setDateTime(resultList.getElement(i).getStartOrEnd(focusStart));

			if (closest.getTimePoint().isAfter(cur.getTimePoint())) {
				closest.setTimePoint(cur.getTimePoint());
				closestIdx = i;
			}
		}
		resultList.getElement(closestIdx).getStartOrEnd(focusStart).setPriority(Priority.timeWithFirstEstimateDate);
	}

	private void setTimeToCloseFutureTime(InvalidEventObj inputEventObj, boolean focusStart,
			DateTimeListMgrSet sEstimatedDates) {

		logger.info("날짜 정보없음");
		int totalNum = 0;
		if (!inputStartDateExists && !inputEndDateExists) {
			focusingRecurNum = 10;
			if (timeList.getDtMgrList().size() > 0 && timeList.getElement(0).isFocusOnAmPm()) {
				focusingRecurNum = 1;
			}
		}
		InvalidDateTimeObj prevObj = new InvalidDateTimeObj();
		for (int k = 0; k < focusingRecurNum; k++) {
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
					if (!inputStartDateExists && !inputEndDateExists) {
						// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
						if (tmpCal.getTimePoint().toLocalTime().isBefore(RecommendationManager.curTime.toLocalTime())
								&& timeList.getElement(i).isFocusOnAmPm() != true) {
							tmpCal.plusDate(1 + k);
						} else {
							if (k > 0) {
								tmpCal.plusDate(k);
							}
						}

						if (focusStart) {
							startDtObj.setAllDate(tmpCal);
							startDtObj.setHour(tmpCal.getHour());
							if (timeList.getElement(i).getMinute() == AppConstants.NO_DATA) {
								startDtObj.setMinute(AppConstants.NO_DATA);
							} else {
								startDtObj.setMinute(tmpCal.getMinute());
							}
							startDtObj.setPriority(timeList.getElement(i).getPriority());
							startDtObj.setFocusOnAmPm(timeList.getElement(i).isFocusOnAmPm());
							prevObj.copyAllExceptForDayFrom(startDtObj);
						} else {
							endDtObj = startDtObj;
							startDtObj = null;
							endDtObj.setAllDate(tmpCal);
							endDtObj.setHour(tmpCal.getHour());
							endDtObj.setMinute(tmpCal.getMinute());
							endDtObj.setPriority(timeList.getElement(i).getPriority());
							endDtObj.setFocusOnAmPm(timeList.getElement(i).isFocusOnAmPm());
							prevObj.copyAllExceptForDayFrom(endDtObj);
							
							if(i > 0) {
								focusingRecurNum = 1;
								continue;
							}
						}

					}
					// 미리 선택된 날짜시간 정보가 있으면
					else {
						if (k > 0)
							continue;
						// 일정 시작 날짜만 있을 때
						if (inputStartDateExists && !inputEndDateExists) {

							// tmpCal의 년월일 세팅.
							tmpCal.setAllDate(inputEventObj.getStartDate());

							// 시간이 없을 때
							if (inputEventObj.getStartDate().hasNoTime() && focusStart) {
								// 선택된 날짜로 세팅.

								startDtObj.setAllDate(tmpCal);
								startDtObj.setHour(tmpCal.getHour());
								startDtObj.setMinute(tmpCal.getMinute());
								if (timeList.getElement(i).getMinute() == AppConstants.NO_DATA) {
									startDtObj.setMinute(AppConstants.NO_DATA);
								}
								startDtObj.setPriority(timeList.getElement(i).getPriority());
							}
							// 일정 시작 날짜와 시간 모두 있을 때
							else {
								boolean canChangeDate = true;
								endDtObj = new InvalidDateTimeObj();
								endDtObj.setAllDate(inputEventObj.getStartDate());
								endDtObj.setHour(tmpCal.getHour());
								endDtObj.setMinute(tmpCal.getMinute());
								if (timeList.getElement(i).getMinute() == AppConstants.NO_DATA) {
									endDtObj.setMinute(AppConstants.NO_DATA);
								} else if (timeList.getElement(i).isFocusOnAmPm()) {
									endDtObj.setMinute(AppConstants.NO_DATA);
									endDtObj.setFocusOnAmPm(true);
									endDtObj.setAmpm(timeList.getElement(i).getAmpm());

									canChangeDate = false;
								}
								endDtObj.setPriority(timeList.getElement(i).getPriority());

								LocalTime stdSTime = RecommendationManager.curTime.toLocalTime();
								if (!focusStart) {
									startDtObj = null;
									if (sEstimatedDates.getResultList().getEvMgrList().size() > 0
											&& sEstimatedDates.getResultList().getElement(0).getEndDate() != null) {
										sEstimatedDates.getResultList().getEvMgrList().clear();
									}
									if (sEstimatedDates.getResultList().getEvMgrList().size() > 0
											&& !sEstimatedDates.getResultList().getElement(0).getStartDate().hasNoTime()
											&& inputEventObj.getStartDate().hasNoTime()) {
										if (sEstimatedDates.getResultList().getElement(0).getStartDate()
												.getMinute() == AppConstants.NO_DATA) {
											for (int a = 0; a < sEstimatedDates.getResultList().getEvMgrList()
													.size(); a++) {
												sEstimatedDates.getResultList().getElement(a).getStartDate()
														.setMinute(0);
											}
										}
										canChangeDate = false;
									}
								} else {
									// if (tmpCal.getHour() == inputEventObj.getStartDate().getHour()) {
									startDtObj.setDateTime(inputEventObj.getStartDate());
									stdSTime = startDtObj.getLocalTime();

									if (!inputEventObj.getStartDate().hasNoTime()
											&& inputEventObj.getStartDate().getMinute() == AppConstants.NO_DATA) {
										startDtObj.setMinute(0);
									}
								}

								// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
								if (canChangeDate) {
									if (endDtObj.getLocalTime().isBefore(stdSTime)) {
										tmpCal.plusDate(1);
										endDtObj.setAllDate(tmpCal);
									}
								}
							}
						} else if (!inputStartDateExists && inputEndDateExists) {

							// 시작시간, 종료시간 존재 시,
						} else if (inputStartDateExists && inputEndDateExists) {
							// 1/1~1/2 + "12시" -> size 0
							// 1/1~1/2 + "10시~12시" -> size not 0
							// 1/1 time ~ 1/2 + "12시" -> size not 0

							// 두 경우 다른 값이 나와야함.
							startDtObj.setAllDate(inputEventObj.getStartDate());
							if (inputEventObj.getStartDate().hasNoTime()) {
								if (focusStart) {
									startDtObj.setHour(tmpCal.getHour());
									startDtObj.setMinute(tmpCal.getMinute());
								}
								endDtObj = new InvalidDateTimeObj();
							} else {
								startDtObj.setHour(inputEventObj.getStartDate().getHour());
								startDtObj.setMinute(inputEventObj.getStartDate().getMinute());
								if (focusStart) {
									endDtObj = new InvalidDateTimeObj();
									endDtObj.setHour(tmpCal.getHour());
									endDtObj.setMinute(tmpCal.getMinute());
									endDtObj.setPriority(timeList.getElement(i).getPriority());
								}
							}
							endDtObj.setAllDate(inputEventObj.getEndDate());

							if (!focusStart) {
								endDtObj.setHour(tmpCal.getHour());
								endDtObj.setMinute(tmpCal.getMinute());
								endDtObj.setPriority(timeList.getElement(i).getPriority());
							}
						}
						// 둘다 존재하지 않을
						else {

						}
					}

					totalNum++;
					if (startDtObj == null && endDtObj == null) {
						continue;
					}
					if (totalNum > 10) {
						continue;
					}

					InvalidEventObj evObj = new InvalidEventObj(startDtObj, endDtObj);
					resultList.insertDtObj(evObj);
				}
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
			DateTimeListMgrSet sEstimatedDates) {
		// request 로 들어온 startDate가 존재할 때,
		if ((inputStartDateExists && !inputEndDateExists) || sEstimatedDates != AppConstants.NO_DATA_TO_CONSIDER) {
			for (int i = 0; i < timeList.getDtMgrList().size(); i++) {
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {
					for (int k = 0; k < focusingRecurNum; k++) {
						InvalidEventObj eventObj = new InvalidEventObj(new InvalidDateTimeObj(),
								new InvalidDateTimeObj());

						if ((inputStartDateExists && !inputEndDateExists)) {
							eventObj.getStartDate().copyAllExceptForDayFrom(dateList.getElement(j));
							eventObj.getEndDate().copyAllExceptForDayFrom(dateList.getElement(j));

							// 시작시간과 입력시간정보 없을 땐 종일 로 나타내기
							estimateTime(isTimeEmpty, eventObj.getEndDate(), timeList.getElement(i));

							// 시작 시간있으면 종료시간을 그 한 시간뒤로 변경해야함.
							if (!inputEventObj.getStartDate().hasNoTime()) {
								if (inputEventObj.getStartDate().getMinute() == AppConstants.NO_DATA) {
									inputEventObj.getStartDate().setMinute(0);
								} else {
									if (eventObj.getEndDate().isAllDayEvent() == true) {
										eventObj.getEndDate().setHour(inputEventObj.getStartDate().getHour() + 1);
										eventObj.getEndDate().setMinute(inputEventObj.getStartDate().getMinute());
									}
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
							if (sEstimatedDates.getResultList().getEvMgrList().size() != 0) {
								if (!sEstimatedDates.getResultList().getElement(0).getStartDate().hasNoTime()) {
									if (eventObj.getEndDate().isAllDayEvent() == true) {
										eventObj.getEndDate().setHour(
												sEstimatedDates.getResultList().getElement(0).getStartDate().getHour()
														+ 1);
										eventObj.getEndDate().setMinute(sEstimatedDates.getResultList().getElement(0)
												.getStartDate().getMinute());
									}
									setMinuteZeroIfNull(sEstimatedDates.getResultList().getEvMgrList());
								}
								// 시작날짜만 있고 시간이 없으면 시작 추천리스트를 1개만 남기기
								else {
									while (sEstimatedDates.getResultList().getEvMgrList().size() > 1) {
										sEstimatedDates.getResultList().getEvMgrList().remove(1);
									}
								}
								focusingRecurNum = 1;
							}

							// 년월일 요일 추정
							if (sEstimatedDates.getResultList().getEvMgrList().size() != 0) {
								estimateDates(eventObj, true, k, dateList.getElement(j),
										sEstimatedDates.getResultList().getElement(0).getStartDate().getLocalDate());
							}
						}
					}
				}
			}
		}
		// request 로 들어온 startDate는 없지만, endDate는 존재할 때
		else if (!inputStartDateExists && inputEndDateExists) {

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
						estimateDates(eventObj, false, k, dateList.getElement(j), null);
					}
				}
			}

			// // 오늘 날짜 + 시간이 있으면 지난 시간은 지우기
			// deleteTodayPastTime(focusStart);
		}

	}

	private void setMinuteZeroIfNull(List<InvalidEventObj> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getStartDate().getMinute() == AppConstants.NO_DATA) {
				list.get(i).getStartDate().setMinute(0);
			}
		}

	}

	private void deleteTodayPastTime(boolean focusStart) {

		int amPmTimesInRange = resultList.getEvMgrList().size();
		Iterator<InvalidEventObj> iter = resultList.getEvMgrList().iterator();
		if (focusStart) {
			while (iter.hasNext()) {
				InvalidEventObj curObj = iter.next();

				// 종료날짜 있을 때
				if (curObj.getEndDate() != null) {
					if (!curObj.getEndDate().hasNoTime()) {
						LocalDateTime objDateTime;
						if (curObj.getEndDate().getMinute() != AppConstants.NO_DATA) {
							objDateTime = LocalDateTime.of(curObj.getEndDate().getLocalDate(),
									curObj.getEndDate().getLocalTime());
						} else {
							objDateTime = LocalDateTime.of(curObj.getEndDate().getLocalDate(),
									LocalTime.of(curObj.getEndDate().getHour(), 0));
						}
						if (objDateTime.toLocalDate().isEqual(curObj.getStartDate().getLocalDate())
								&& !objDateTime.isAfter(LocalDateTime.of(curObj.getStartDate().getLocalDate(),
										curObj.getStartDate().getLocalTime()))) {
							if (curObj.getEndDate().getAmpm() == DateTimeEn.pm
									&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
											.getHour() < 24
									&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
											.getHour() >= 12
									&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
											.isAfter(curObj.getStartDate().getLocalTime())) {

								curObj.getEndDate().setHour(curObj.getEndDate().getLocalTime()
										.plusHours(AppConstants.DEFAULT_RECOM_TIMES).getHour());
							} else if (curObj.getEndDate().getAmpm() == DateTimeEn.am
									&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
											.getHour() < 12
									&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
											.getHour() >= 0
									&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
											.isAfter(curObj.getStartDate().getLocalTime())) {

							} else {
								amPmTimesInRange--;
							}
						}
					}
				} else {
					if (!curObj.getStartDate().hasNoTime()) {
						LocalDateTime objDateTime;
						if (curObj.getStartDate().getMinute() != AppConstants.NO_DATA) {
							objDateTime = LocalDateTime.of(curObj.getStartDate().getLocalDate(),
									curObj.getStartDate().getLocalTime());
						} else {
							objDateTime = LocalDateTime.of(curObj.getStartDate().getLocalDate(),
									LocalTime.of(curObj.getStartDate().getHour(), 0));
						}
						if (objDateTime.toLocalDate().isEqual(RecommendationManager.curTime.toLocalDate())
								&& !objDateTime.isAfter(RecommendationManager.curTime)) {
							amPmTimesInRange--;
						}
					}
				}
			}

			iter = resultList.getEvMgrList().iterator();
			while (iter.hasNext()) {
				InvalidEventObj curObj = iter.next();
				for (int j = 0; j < dateList.getDtMgrList().size(); j++) {

					// 종료날짜 있을 때
					if (curObj.getEndDate() != null) {
						// 종료날짜 추정값이 시작날짜랑 같으면
						if (curObj.getEndDate().getLocalDate().isEqual(curObj.getStartDate().getLocalDate())) {
							// 추정값과 입력값이 같으면
							if (amPmTimesInRange > 0) {
								if (!curObj.getStartDate().hasNoTime()) {
									LocalTime t;
									if (curObj.getEndDate().getMinute() == AppConstants.NO_DATA) {
										t = LocalTime.of(curObj.getEndDate().getHour(), 0);
									} else {
										t = curObj.getEndDate().getLocalTime();
									}
									if (!t.isAfter(curObj.getStartDate().getLocalTime())) {
										if (curObj.getEndDate().getAmpm() == DateTimeEn.am
												&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
												.getHour() < 12
										&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
												.getHour() >= 0
										&& curObj.getEndDate().getLocalTime().plusHours(AppConstants.DEFAULT_RECOM_TIMES)
												.isAfter(curObj.getStartDate().getLocalTime())) {
											curObj.getEndDate().setHour(curObj.getEndDate().getLocalTime()
													.plusHours(AppConstants.DEFAULT_RECOM_TIMES).getHour());
										} else {
											iter.remove();
											continue;
										}
									}
								}
							}
							// 추정값만 오늘이면 하루 더하기
							// but 오전, 오후는 하나라도 있으면 지우기만 하기
							else {
								DateTimeAdjuster localDateTime = new DateTimeAdjuster();
								if (!curObj.getEndDate().hasNoTime()) {
									if (curObj.getEndDate().getMinute() == AppConstants.NO_DATA) {
										localDateTime.setTimePoint(LocalDateTime.of(curObj.getEndDate().getLocalDate(),
												LocalTime.of(curObj.getEndDate().getHour(), 0)));
									} else {
										localDateTime.setTimePoint(LocalDateTime.of(curObj.getEndDate().getLocalDate(),
												curObj.getEndDate().getLocalTime()));
									}
									localDateTime.plusDate(1);
								}

								if (curObj.getEndDate().isFocusOnAmPm()) {
									// 오전 오후일 때 하나라도 있으면 지우기만 하기
									if (amPmTimesInRange > 0) {
										if (curObj.getEndDate().getLocalTime()
												.isBefore(curObj.getStartDate().getLocalTime())) {

											if (curObj.getEndDate().getLocalTime()
													.plusHours(AppConstants.DEFAULT_RECOM_TIMES)
													.isAfter(curObj.getStartDate().getLocalTime())) {
												curObj.getEndDate().setHour(curObj.getEndDate().getLocalTime()
														.plusHours(AppConstants.DEFAULT_RECOM_TIMES).getHour());
											} else {
												iter.remove();
												continue;
											}

										}
									}
									// 하나도 없으면 다음날로 ..
									else {
										curObj.getEndDate().setAllDate(localDateTime);
									}
								} else if (!curObj.getEndDate().hasNoTime() && curObj.getEndDate().getLocalTime()
										.isBefore(curObj.getStartDate().getLocalTime())) {
									curObj.getEndDate().setAllDate(localDateTime);
								}
							}
						}
					} else {

						// 추정값이 오늘이면
						if (curObj.getStartDate().getLocalDate().isEqual(RecommendationManager.curTime.toLocalDate())) {
							// 추정값과 입력값이 같으면
							if (dateList.getElement(j).getDate() == curObj.getStartDate().getDate()) {
								if (amPmTimesInRange > 0) {
									if (!curObj.getStartDate().hasNoTime()) {
										LocalTime t;
										if (curObj.getStartDate().getMinute() == AppConstants.NO_DATA) {
											t = LocalTime.of(curObj.getStartDate().getHour(), 0);
										} else {
											t = curObj.getStartDate().getLocalTime();
										}
										if (t.isBefore(RecommendationManager.curTime.toLocalTime())) {
											iter.remove();
										}
									}
								}
								// 이전 시간인 거 다 지우기
								// if (curObj.getStartDate().isFocusOnAmPm()) {
								// // 오전 오후일 때 하나라도 있으면 지우기만 하기
								// if (amPmTimesInRange > 0) {
								// if (curObj.getStartDate().getLocalTime()
								// .isBefore(RecommendationManager.curTime.toLocalTime())) {
								// iter.remove();
								// }
								// }
								// } else if (curObj.getStartDate().getLocalTime() != null &&
								// curObj.getStartDate()
								// .getLocalTime().isBefore(RecommendationManager.curTime.toLocalTime())) {
								// iter.remove();
								// }
							}
							// 추정값만 오늘이면 하루 더하기
							// but 오전, 오후는 하나라도 있으면 지우기만 하기
							else {
								DateTimeAdjuster localDateTime = new DateTimeAdjuster();
								if (!curObj.getStartDate().hasNoTime()) {
									if (curObj.getStartDate().getMinute() == AppConstants.NO_DATA) {
										localDateTime
												.setTimePoint(LocalDateTime.of(curObj.getStartDate().getLocalDate(),
														LocalTime.of(curObj.getStartDate().getHour(), 0)));
									} else {
										localDateTime
												.setTimePoint(LocalDateTime.of(curObj.getStartDate().getLocalDate(),
														curObj.getStartDate().getLocalTime()));
									}
									localDateTime.plusDate(1);
								}

								if (curObj.getStartDate().isFocusOnAmPm()) {
									// 오전 오후일 때 하나라도 있으면 지우기만 하기
									if (amPmTimesInRange > 0) {
										if (curObj.getStartDate().getLocalTime()
												.isBefore(RecommendationManager.curTime.toLocalTime())) {

											if (curObj.getStartDate().getLocalTime()
													.plusHours(AppConstants.DEFAULT_RECOM_TIMES)
													.isAfter(RecommendationManager.curTime.toLocalTime())) {
												curObj.getStartDate().setHour(curObj.getStartDate().getLocalTime()
														.plusHours(AppConstants.DEFAULT_RECOM_TIMES).getHour());
											} else {
												iter.remove();
												continue;
											}

										}
									}
									// 하나도 없으면 다음날로 ..
									else {
										curObj.getStartDate().setAllDate(localDateTime);
									}
								} else if (!curObj.getStartDate().hasNoTime() && curObj.getStartDate().getLocalTime()
										.isBefore(RecommendationManager.curTime.toLocalTime())) {
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
			LocalDate sDate) {

		boolean isYearEstimated = estimateYear(eventObj, fillEnd, sDate);

		if (eventObj.getStartOrEnd(!fillEnd).getFocusToRepeat() == null) {
			// 반복없이 해당 값만 insert
			estimateOneDate(eventObj, fillEnd, isYearEstimated, sDate);
		} else {
			// focus할 게 있으면 그 정보를 기준으로 for문 돌며 여러값 insert
			estimateMultipleDates(eventObj, fillEnd, k, origin, isYearEstimated, sDate);
		}
	}

	private void estimateMultipleDates(InvalidEventObj eventObj, boolean fillEnd, int k, InvalidDateTimeObj origin,
			boolean isYearEstimated, LocalDate sDate) {

		// 데이터 없으면 기본값 세팅
		setDefaultIfNoData(eventObj, fillEnd, false);

		if (k == 0 && eventObj.getStartOrEnd(!fillEnd).isAllDayEvent() != true) {
			eventObj.getStartOrEnd(!fillEnd).setPriority(Priority.timeWithFirstEstimateDate);
		}

		if (eventObj.getStartOrEnd(!fillEnd).isFocusOnDay() == true) {
			// 매주 해당 요일에 맞는 날짜만 뽑도록 구하는 로직
			setDatesByEveryWeek(eventObj.getStartOrEnd(!fillEnd), k, origin);
		} else {
			setDatesByToken(eventObj.getStartOrEnd(!fillEnd), k, sDate);
		}

		if (isYearEstimated) {
			eventObj.getStartOrEnd(!fillEnd).getLocalDate();
			DateTimeAdjuster now = new DateTimeAdjuster();
			DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
			if (!eventObj.getStartOrEnd(!fillEnd).hasNoTime()) {
				dateTimeAdjuster.setTimePoint(LocalDateTime.of(eventObj.getStartOrEnd(!fillEnd).getLocalDate(),
						eventObj.getStartOrEnd(!fillEnd).getLocalTime()));
			} else {
				dateTimeAdjuster.setYear(eventObj.getStartOrEnd(!fillEnd).getYear());
				dateTimeAdjuster.setMonth(eventObj.getStartOrEnd(!fillEnd).getMonth());
				dateTimeAdjuster.setDate(eventObj.getStartOrEnd(!fillEnd).getDate());
			}
			dateTimeAdjuster.setCloseDate(now, DateTimeEn.year, 0, false, null);

			eventObj.getStartOrEnd(!fillEnd).setAllDate(dateTimeAdjuster);
		}

		InvalidEventObj evObj = new InvalidEventObj(eventObj.getStartDate(), eventObj.getEndDate());
		resultList.insertDtObj(evObj);
	}

	private void setDefaultIfNoData(InvalidEventObj eventObj, boolean fillEnd, boolean setByCurTime) {
		if (eventObj.getStartOrEnd(!fillEnd).getMonth() == AppConstants.NO_DATA) {
			eventObj.getStartOrEnd(!fillEnd).setMonth(1);
			if (setByCurTime) {
				eventObj.getStartOrEnd(!fillEnd).setMonth(RecommendationManager.curTime.toLocalDate().getMonthValue());
			}
		}
		if (eventObj.getStartOrEnd(!fillEnd).getDate() == AppConstants.NO_DATA) {
			eventObj.getStartOrEnd(!fillEnd).setDate(1);
			if (setByCurTime) {
				eventObj.getStartOrEnd(!fillEnd).setDate(RecommendationManager.curTime.toLocalDate().getDayOfMonth());
			}
		}
		if (eventObj.getStartOrEnd(!fillEnd).getDay() == AppConstants.NO_DATA_FOR_DAY) {
			// 날짜에 맞는 요일 구하는 메소드
			eventObj.getStartOrEnd(!fillEnd).setProperDay();
		}
	}

	private void setDatesByToken(InvalidDateTimeObj dtObj, int k, LocalDate sDate) {
		DateTimeAdjuster tmpCal2 = new DateTimeAdjuster();
		tmpCal2.setYear(dtObj.getYear());
		tmpCal2.setMonth(dtObj.getMonth());
		tmpCal2.setDate(dtObj.getDate());

		boolean checkTime = false;
		if (dtObj.getHour() != AppConstants.NO_DATA) {
			checkTime = true;
			tmpCal2.setHour(dtObj.getHour(), false);
			// tmpCal2.setMinute(dtObj.getMinute());
		}
		// focus 할 해당 정보를 기준으로 더해주기.
		tmpCal2.setCloseDate(tmpCal2, dtObj.getFocusToRepeat(), k, checkTime, sDate);

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

	private void estimateOneDate(InvalidEventObj eventObj, boolean fillEnd, boolean isYearEstimated, LocalDate sDate) {

		boolean shouldSkip = false;

		focusingRecurNum = 1;

		setDefaultIfNoData(eventObj, fillEnd, true);

		if (isYearEstimated) {
			eventObj.getStartOrEnd(!fillEnd).getLocalDate();
			DateTimeAdjuster stdDT = new DateTimeAdjuster();
			DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
			if (!eventObj.getStartOrEnd(!fillEnd).hasNoTime()) {
				dateTimeAdjuster.setTimePoint(LocalDateTime.of(eventObj.getStartOrEnd(!fillEnd).getLocalDate(),
						eventObj.getStartOrEnd(!fillEnd).getLocalTime()));
			} else {
				dateTimeAdjuster.setYear(eventObj.getStartOrEnd(!fillEnd).getYear());
				dateTimeAdjuster.setMonth(eventObj.getStartOrEnd(!fillEnd).getMonth());
				dateTimeAdjuster.setDate(eventObj.getStartOrEnd(!fillEnd).getDate());
			}
			if (sDate != null) {
				stdDT.setTimePoint(LocalDateTime.of(sDate, LocalTime.of(0, 0)));
			}
			dateTimeAdjuster.setCloseDate(stdDT, DateTimeEn.year, 0, false, null);

			eventObj.getStartOrEnd(!fillEnd).setAllDate(dateTimeAdjuster);
		}

		// if(eventObj.getStartDate().isFocusOnAmPm()) {
		// // 오전, 오후일 때 현재 시간보다 빠르면 스킵하기
		// if(LocalDateTime.of(eventObj.getStartDate().getLocalDate(),eventObj.getStartDate().getLocalTime()).isBefore(RecommendationManager.curTime))
		// {
		// shouldSkip = true;
		// }
		// }

		if (!shouldSkip) {
			// resultList.insertDtObj(dtObj);
			InvalidEventObj evObj = new InvalidEventObj(eventObj.getStartDate(), eventObj.getEndDate());
			resultList.insertDtObj(evObj);
		}

	}

	private boolean estimateYear(InvalidEventObj eventObj, boolean fillEnd, LocalDate sDate) {
		boolean estimated = false;
		if (eventObj.getStartOrEnd(!fillEnd).getYear() == AppConstants.NO_DATA) {
			if (sDate != null) {
				eventObj.getStartOrEnd(!fillEnd).setYear(sDate.getYear());
			} else {
				eventObj.getStartOrEnd(!fillEnd).setYear(RecommendationManager.curTime.toLocalDate().getYear());
				// eventObj.getEndDate().setYear(LocalDate.now().getYear());
			}
			estimated = true;
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
