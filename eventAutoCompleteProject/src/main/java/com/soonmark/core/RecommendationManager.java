package com.soonmark.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.TokenType;

public class RecommendationManager {

	private Logger logger = LoggerFactory.getLogger(RecommendationManager.class);

	// 패턴 관리 객체
	private PatternManager patternManager;

	// 각 날짜, 요일, 시간, 특수 리스트 매니저 셋
	private DateTimeListMgrSet dateTimeListManagerSet;
	private List<PeriodManager> periodManagerList;

	String inputText;

	InvalidEventObj inputEventObj ;

	// 리스트에 보여줄 추천 날짜 개수
	int recomNum;
	// focus 두고 반복시킬 횟수
	int focusingRecurNum;

	public RecommendationManager() {
		// 패턴 생성
		patternManager = new PatternManager();
		recomNum = 2;
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
		focusingRecurNum = 2;
		dateTimeListManagerSet = new DateTimeListMgrSet();
		periodManagerList = new ArrayList<PeriodManager>();

		this.inputText = inputText;
		// 객체로부터 미리 period 세팅 해두기.
		initInputEvent(startDate, endDate);

		logger.info("입력받은 일정 : " + inputText);

		// 최종 추천할 기간일정리스트
		List<InvalidEventObj> evObjList = new ArrayList<InvalidEventObj>();

		// 패턴 매칭
		patternManager.matchToPatterns(inputText, periodManagerList);

		Iterator<PeriodManager> iter = periodManagerList.iterator();
		while (iter.hasNext()) {
			PeriodManager periodManager = iter.next();
			
			// 시작날짜 추천리스트 생성
			RecommendProcess(periodManager.getStartDateListMgr());
			// 종료날짜 추천리스트 생성
			RecommendProcess(periodManager.getEndDateListMgr());

			
			// 시작날짜에 대한 추천리스트 + 종료날짜에 대한 추천리스트 합치기
			evObjList = MergeRecomListBy(periodManager.getStartDateListMgr().getResultList().getEvMgrList(),
										periodManager.getEndDateListMgr().getResultList().getEvMgrList());
		}

		dateTimeListManagerSet.getResultList().setEvObjList(evObjList);
		logger.info("JSON 값  : " + dateTimeListManagerSet.getResultList().getEventDTOList());

		return dateTimeListManagerSet.getResultList().getEventDTOList();
	}

	private void initInputEvent(DateTimeDTO startDate, DateTimeDTO endDate) {
		inputEventObj = new InvalidEventObj();
		if(startDate != null) {
			inputEventObj.setStartDate(startDate.toInvalidDateTimeObj());
		}
		if(endDate != null) {
			inputEventObj.setEndDate(endDate.toInvalidDateTimeObj());
		}
	}

	private List<InvalidEventObj> MergeRecomListBy(List<InvalidEventObj> evMgrList, List<InvalidEventObj> evMgrList2) {
		List<InvalidEventObj> evObjList = new ArrayList<InvalidEventObj>();
		
		Iterator<InvalidEventObj> iter2 = evMgrList.iterator();
		while (iter2.hasNext()) {
			evObjList.add(iter2.next());
		}
		Iterator<InvalidEventObj> iter3 = evMgrList2.iterator();
		while (iter2.hasNext()) {
			evObjList.add(iter3.next());
		}
		
		return evObjList;
	}

	private void RecommendProcess(DateTimeListMgrSet dateTimeListMgr) {
		// 기본 날짜 병합
		dateTimeListMgr.deduplicateElements(TokenType.dates);
		dateTimeListMgr.deduplicateElements(TokenType.days);
		dateTimeListMgr.mergeList(TokenType.dates, TokenType.days);
		dateTimeListMgr.mergeList(TokenType.dates, TokenType.special);

		// 시간
		dateTimeListMgr.deduplicateElements(TokenType.times);
		dateTimeListMgr.adjustForAmPmTime();

		// 기간, 날짜, 시간 조정
		EventListManager mergedListMgr = dateTimeListMgr.mergeList(TokenType.period,
				TokenType.dates, TokenType.times);

		createRecommendations(dateTimeListMgr, mergedListMgr);
	}

	private void createRecommendations(DateTimeListMgrSet dateListMgr, EventListManager mergedListMgr) {
		// 빈 토큰 채우기
		fillEmptyDatas(dateListMgr, mergedListMgr);

		// 우선순위대로 정렬
		sortByPriority(dateListMgr);

		// 추천수 이상의 노드는 삭제
		removeAllAfterRecomNum(dateListMgr);
	}

	private void removeAllAfterRecomNum(DateTimeListMgrSet startDateListMgr) {
		// 2개만 남기고 다 지우기
		for (int i = recomNum; i < startDateListMgr.getResultList().getEventDTOList().size();) {
			startDateListMgr.getResultList().deleteDtObj(i);
		}
	}

	private void sortByPriority(DateTimeListMgrSet startDateListMgr) {
		for (int i = 0; i < startDateListMgr.getResultList().getEvMgrList().size(); i++) {
			startDateListMgr.getResultList().sortByPriority();
		}
	}

	private void fillEmptyDatas(DateTimeListMgrSet dateListMgr, EventListManager mergedListMgr) {
		dateListMgr
				.setResultList(new DateTimeEstimator(dateListMgr.getTimeList(), dateListMgr.getDateList())
						.fillEmptyDatas(inputEventObj));

		if(dateListMgr.getResultList().getEvMgrList().size() > 2) {
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
