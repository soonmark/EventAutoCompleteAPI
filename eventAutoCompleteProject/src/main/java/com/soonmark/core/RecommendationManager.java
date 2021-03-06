package com.soonmark.core;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.TokenType;

public class RecommendationManager {

	private Logger logger = LoggerFactory.getLogger(RecommendationManager.class);
	static public LocalDateTime curTime;

	// 패턴 관리 객체
	private PatternManager patternManager;

	// 각 날짜, 요일, 시간, 특수 리스트 매니저 셋
	private DateTimeListMgrSet dateTimeListManagerSet;
	private List<PeriodManager> periodManagerList;

	String inputText;

	InvalidEventObj inputEventObj;

	// 리스트에 보여줄 추천 날짜 개수
	int recomNum;
	// focus 두고 반복시킬 횟수
	int focusingRecurNum;

	public RecommendationManager() {
		// 패턴 생성
		patternManager = new PatternManager();
	}

	public PatternManager getPatternManager() {
		return patternManager;
	}

	public void setPatternManager(PatternManager patternManager) {
		this.patternManager = patternManager;
	}

	public DateTimeListMgrSet getDateTimeListManagerSet() {
		return dateTimeListManagerSet;
	}

	public void setDateTimeListManagerSet(DateTimeListMgrSet dateTimeListManagerSet) {
		this.dateTimeListManagerSet = dateTimeListManagerSet;
	}

	public List<PeriodManager> getPeriodManagerList() {
		return periodManagerList;
	}

	public void setPeriodManagerList(List<PeriodManager> periodManagerList) {
		this.periodManagerList = periodManagerList;
	}

	public List<EventDTO> getRecommendations(String inputText, DateTimeDTO startDate, DateTimeDTO endDate)
			throws JsonParseException, JsonMappingException, IOException {
		recomNum = 2;

		// 현재 시간 저장.
		curTime = LocalDateTime.of(2018, 5, 16, 16, 0);
//		 curTime = LocalDateTime.now();

		focusingRecurNum = 2;
		dateTimeListManagerSet = new DateTimeListMgrSet();
		periodManagerList = new ArrayList<PeriodManager>();

		this.inputText = inputText;
		// 객체로부터 미리 period 세팅 해두기.
		initInputEventObjBy(startDate, endDate);

		logger.info("입력받은 일정 : " + inputText);

		// 최종 추천할 기간일정리스트
		List<InvalidEventObj> evObjList = new ArrayList<InvalidEventObj>();

		// 패턴 매칭
		// 기간 패턴 매칭은 부터, 까지가 한 세트 있으면 바로 끝.
		patternManager.matchToPatterns(inputText, periodManagerList);
		
		Iterator<PeriodManager> iter = periodManagerList.iterator();
		while (iter.hasNext()) {
			PeriodManager periodManager = iter.next();
			
			// 이미 객체로 생성되어있는 자리의 값을 채우려고 하는 경우 각 date, time, 등등의 리스트 초기화
			deleteDatasIfAlreadyCreated(periodManager);

			if (periodManager.getFrom() != null && periodManager.getTo() != null) {
				if (periodManager.getEndDateListMgr().allListEmpty()
						&& periodManager.getStartDateListMgr().allListEmpty()) {
					recomNum = 1;
				}
			}
			periodManager.getStartDateListMgr().getResultList().setFocusStart(true);
			periodManager.getEndDateListMgr().getResultList().setFocusStart(false);

			// 시작날짜 추천리스트 생성
			RecommendProcess(periodManager.getStartDateListMgr(), AppConstants.NO_DATA_TO_CONSIDER, periodManager.getDuring());
			
			// '동안'이 있으면 시작 날짜 추천리스트에 더해서 result만들기
			if(periodManager.duringExists() ) {
				periodManager.getStartDateListMgr().addDuringDatas(periodManager.getDuring());
			}
			else {
				// 종료날짜 추천리스트 생성 - 동안패턴도 고려
				RecommendProcess(periodManager.getEndDateListMgr(), periodManager.getStartDateListMgr(), null);
			}

			// 시작날짜에 대한 추천리스트 + 종료날짜에 대한 추천리스트 합치기
			evObjList = MergeRecomListBy(periodManager.getStartDateListMgr().getResultList().getEvMgrList(),
					periodManager.getEndDateListMgr().getResultList().getEvMgrList());
		}

		// 최종 리스트에서 뽑을 개수 제외하고 다 지움.
		removeAllAfterRecomNum(evObjList);

		// 시간 순으로 정렬.
		sortByTime(evObjList);

		dateTimeListManagerSet.getResultList().setEvObjList(evObjList);
		logger.info("JSON 값  : " + dateTimeListManagerSet.getResultList().getEventDTOList());

		return dateTimeListManagerSet.getResultList().getEventDTOList();
	}

	private void deleteDatasIfAlreadyCreated(PeriodManager periodManager) {
		boolean endExists = false;
		boolean endTimeExists = false;
		if (inputEventObj.getEndDate() != null) {
			endExists = true;
			if (!inputEventObj.getEndDate().hasNoTime()) {
				endTimeExists = true;
			}
		}
		// 이미 선택된 시작날짜가 있는데
		if (inputEventObj.getStartDate() != null) {
			if (inputEventObj.getStartDate().hasNoTime()) {
				if(endTimeExists) {
					// 입력 받은 것이 ..
					if (!periodManager.getStartDateListMgr().relatedToDateListEmpty() && !periodManager.getEndDateListMgr().allListEmpty()) {
						allClear(periodManager);
					}
					else {
						
					}
				}
				else if(endExists) {
					// 입력 받은 것이 ..
					if (!periodManager.getStartDateListMgr().relatedToDateListEmpty() && !periodManager.getEndDateListMgr().relatedToDateListEmpty()) {
						allClear(periodManager);
					}
					else {
						
					}
				}
			}
			else {
				if(endTimeExists) {
					// 입력 받은 것이 ..
					if (!periodManager.getStartDateListMgr().allListEmpty() && !periodManager.getEndDateListMgr().allListEmpty()) {
						allClear(periodManager);
					}
					else {
						
					}
				}
				else if(endExists) {
					// 입력 받은 것이 ..
					if (!periodManager.getStartDateListMgr().relatedToDateListEmpty() && !periodManager.getEndDateListMgr().allListEmpty()) {
						allClear(periodManager);
					}
					else {
						
					}
				}
				
				
				// 입력 받은 것이 ..
				if (!periodManager.getStartDateListMgr().allListEmpty() && !periodManager.getEndDateListMgr().allListEmpty()) {
					allClear(periodManager);
				}
//				if(inputEventObj.getStartDate().getMinute() == AppConstants.NO_DATA) {
//					// 입력 받은 것이 ..
//					if (!periodManager.getStartDateListMgr().getTimeList().getDtMgrList().isEmpty() && !periodManager.getEndDateListMgr().allListEmpty()) {
//						allClear(periodManager);
//					}
//					else {
//						
//					}
//				}
//				// 생성된 객체의 분이 존재할 때
//				else {
//
//				}
			}
		}
		// 선택된 시작날짜 없을 때
		else {
			
		}

	}

	private void allClear(PeriodManager periodManager) {
		periodManager.getStartDateListMgr().getDateList().clearList();
		periodManager.getStartDateListMgr().getDayList().clearList();
		periodManager.getStartDateListMgr().getSpecialDateList().clearList();
		periodManager.getStartDateListMgr().getTimeList().clearList();
		
		periodManager.getEndDateListMgr().getDateList().clearList();
		periodManager.getEndDateListMgr().getDayList().clearList();
		periodManager.getEndDateListMgr().getSpecialDateList().clearList();
		periodManager.getEndDateListMgr().getTimeList().clearList();
		
	}

	private void sortByTime(List<InvalidEventObj> evObjList) {
		AscendingDateTimeEvents ascending = new AscendingDateTimeEvents();
		Collections.sort(evObjList, ascending);
	}

	private void removeAllAfterRecomNum(List<InvalidEventObj> evObjList) {
//		 // 2개만 남기고 다 지우기
//		 for (int i = recomNum; i < evObjList.size();) {
//		 evObjList.remove(i);
//		 }
	}

	private void initInputEventObjBy(DateTimeDTO startDate, DateTimeDTO endDate) {
		boolean isInputStartFull = false;
		boolean isInputEndFull = false;

		inputEventObj = new InvalidEventObj();
		if (startDate != null) {
			inputEventObj.setStartDate(startDate.toInvalidDateTimeObj());
			if (startDate.getTime() != null && startDate.isNoMin()) {
				isInputStartFull = true;
			}
		}
		if (endDate != null) {
			inputEventObj.setEndDate(endDate.toInvalidDateTimeObj());
			if (endDate.getTime() != null && endDate.isNoMin()) {
				isInputEndFull = true;
			}
		}

		if (isInputStartFull && isInputEndFull) {
			clearInputEvent(inputEventObj);
		}
	}

	private void clearInputEvent(InvalidEventObj inputEventObj) {
		inputEventObj.setStartDate(null);
		inputEventObj.setEndDate(null);
	}

	private List<InvalidEventObj> MergeRecomListBy(List<InvalidEventObj> evMgrList, List<InvalidEventObj> evMgrList2) {
		List<InvalidEventObj> evObjList = new ArrayList<InvalidEventObj>();

		Iterator<InvalidEventObj> iter3 = evMgrList2.iterator();
		while (iter3.hasNext()) {
			InvalidEventObj beingMerged = iter3.next();
			Iterator<InvalidEventObj> iter2 = evMgrList.iterator();
			while (iter2.hasNext()) {
				InvalidEventObj first = iter2.next();

				// endDate랑 하도록 변경.
				if (first.getStartDate() != null
						&& /*
							 * first.getEndDate() == null && beingMerged.getStartDate() == null &&
							 */beingMerged.getEndDate() != null) {
					if (!first.getStartDate().hasNoTime() || !beingMerged.getEndDate().hasNoTime()) {
						LocalDateTime r;
						LocalDateTime b;
						if (first.getStartDate().getMinute() == AppConstants.NO_DATA) {
							r = LocalDateTime.of(first.getStartDate().getLocalDate(),
									LocalTime.of(first.getStartDate().getHour(), 0));
						} else {
							r = LocalDateTime.of(first.getStartDate().getLocalDate(),
									first.getStartDate().getLocalTime());
						}
						if (beingMerged.getEndDate().getMinute() == AppConstants.NO_DATA) {
							b = LocalDateTime.of(beingMerged.getEndDate().getLocalDate(),
									LocalTime.of(beingMerged.getEndDate().getHour(), 0));
						} else {
							b = LocalDateTime.of(beingMerged.getEndDate().getLocalDate(),
									beingMerged.getEndDate().getLocalTime());
							if (first.getStartDate().getMinute() == AppConstants.NO_DATA) {
								first.getStartDate().setMinute(0);
							}
						}

						if (!r.isAfter(b)) {
							if (inputEventObj.getStartDate() != null && !inputEventObj.getStartDate().hasNoTime()
									&& (inputEventObj.getStartDate().getHour() != first.getStartDate().getHour()
											|| inputEventObj.getStartDate().getMinute() != first.getStartDate()
													.getMinute())) {
							} else {
								evObjList.add(new InvalidEventObj(first.getStartDate(), beingMerged.getEndDate()));
							}
						}
					} else {
						LocalDate r = first.getStartDate().getLocalDate();
						LocalDate b = beingMerged.getEndDate().getLocalDate();

						if (!r.isAfter(b)) {
							evObjList.add(new InvalidEventObj(first.getStartDate(), beingMerged.getEndDate()));
						}
					}
				} else if (((first.getStartDate() == null && beingMerged.getStartDate() == null)
						|| (first.getStartDate() != null && beingMerged.getStartDate() != null) && (first.getStartDate()
								.getLocalDate().isEqual(beingMerged.getStartDate().getLocalDate())
								&& first.getStartDate().getHour() == beingMerged.getStartDate().getHour()
								&& first.getStartDate().getMinute() == beingMerged.getStartDate().getMinute()))
						&& ((first.getEndDate() == null && beingMerged.getEndDate() == null)
								|| (first.getEndDate() != null && beingMerged.getEndDate() != null) && (first
										.getEndDate().getLocalDate().isEqual(beingMerged.getEndDate().getLocalDate())
										&& first.getEndDate().getHour() == beingMerged.getEndDate().getHour()
										&& first.getEndDate().getMinute() == beingMerged.getEndDate().getMinute()))) {
					evObjList.add(new InvalidEventObj(first.getStartDate(), beingMerged.getEndDate()));
				}
			}

			if (evMgrList.size() == 0) {
				recomNum = evObjList.size();
				if (beingMerged.getStartDate() != null) {
					evObjList.add(new InvalidEventObj(beingMerged.getStartDate(), beingMerged.getEndDate()));
				}
			}
		}
		if (evMgrList2.size() == 0) {
			Iterator<InvalidEventObj> iter2 = evMgrList.iterator();
			while (iter2.hasNext()) {
				InvalidEventObj first = iter2.next();
				// endDate가 startDate 보다 빠르면
				if (first.getStartDate() != null && first.getEndDate() != null) {
					if (!first.getStartDate().hasNoTime() && !first.getEndDate().hasNoTime()) {

						LocalDateTime sdt;
						LocalDateTime edt;

						if (first.getStartDate().getMinute() != AppConstants.NO_DATA) {
							sdt = LocalDateTime.of(first.getStartDate().getLocalDate(),
									first.getStartDate().getLocalTime());
						} else {
							sdt = LocalDateTime.of(first.getStartDate().getLocalDate(),
									LocalTime.of(first.getStartDate().getHour(), 0));
						}
						if (first.getEndDate().getMinute() != AppConstants.NO_DATA) {
							edt = LocalDateTime.of(first.getEndDate().getLocalDate(),
									first.getEndDate().getLocalTime());
						} else {
							edt = LocalDateTime.of(first.getEndDate().getLocalDate(),
									LocalTime.of(first.getEndDate().getHour(), 0));
						}
						// 시작시간이 종료시간보다 빠르면 skip
						if (!sdt.isAfter(edt)) {
							evObjList.add(new InvalidEventObj(first.getStartDate(), first.getEndDate()));
						}
					} else {
						// 시작시간이 종료시간보다 빠르면 skip
						if (!first.getStartDate().getLocalDate().isAfter(first.getEndDate().getLocalDate())) {
							evObjList.add(new InvalidEventObj(first.getStartDate(), first.getEndDate()));
						}
					}
				} else {
					evObjList.add(new InvalidEventObj(first.getStartDate(), first.getEndDate()));
				}
			}
		}

		return evObjList;
	}

	private void RecommendProcess(DateTimeListMgrSet dateTimeListMgr, DateTimeListMgrSet sEstimatedDates, During during) {
		// 기본 날짜 병합
		dateTimeListMgr.deduplicateElements(TokenType.dates);
		dateTimeListMgr.deduplicateElements(TokenType.days);
		dateTimeListMgr.mergeList(TokenType.dates, TokenType.days);
		dateTimeListMgr.mergeList(TokenType.dates, TokenType.special);

		// 시간
		dateTimeListMgr.deduplicateElements(TokenType.times);
		dateTimeListMgr.adjustForAmPmTime();

		// 기간, 날짜, 시간 조정
//		EventListManager mergedListMgr = dateTimeListMgr.mergeList(TokenType.period, TokenType.dates, TokenType.times);

		createRecommendations(dateTimeListMgr, /*mergedListMgr, */sEstimatedDates, during);
	}

	private void createRecommendations(DateTimeListMgrSet dateListMgr,/* EventListManager mergedListMgr,*/
			DateTimeListMgrSet sEstimatedDates, During during) {
		// 빈 토큰 채우기
		fillEmptyDatas(dateListMgr, /*mergedListMgr, */sEstimatedDates, during);

		// 우선순위대로 정렬
		sortByPriority(dateListMgr);

		// // 추천수 이상의 노드는 삭제
		// removeAllAfterRecomNum(dateListMgr);
	}

	// private void removeAllAfterRecomNum(DateTimeListMgrSet startDateListMgr) {
	// // 2개만 남기고 다 지우기
	// for (int i = recomNum; i <
	// startDateListMgr.getResultList().getEventDTOList().size();) {
	// startDateListMgr.getResultList().deleteDtObj(i);
	// }
	// }

	private void sortByPriority(DateTimeListMgrSet startDateListMgr) {
		for (int i = 0; i < startDateListMgr.getResultList().getEvMgrList().size(); i++) {
			startDateListMgr.getResultList().sortByPriority();
		}
	}

	private void fillEmptyDatas(DateTimeListMgrSet dateListMgr, DateTimeListMgrSet sEstimatedDates, During during) {
		dateListMgr.setResultList(new DateTimeEstimator(dateListMgr.getTimeList(), dateListMgr.getDateList())
				.fillEmptyDatas(inputEventObj, dateListMgr.getResultList().isFocusStart(), sEstimatedDates, during));

		if (dateListMgr.getResultList().getEvMgrList().size() > 2) {
			recomNum = dateListMgr.getResultList().getEvMgrList().size();
		}
	}

	public StringDateTimeDTO getObjectFromJson(String date)
			throws JsonParseException, JsonMappingException, IOException {

		StringDateTimeDTO dto = null;
		ObjectMapper objectMapper = new ObjectMapper();
		if (date != null) {
			dto = objectMapper.readValue(date, StringDateTimeDTO.class);
		}

		return dto;
	}
}
