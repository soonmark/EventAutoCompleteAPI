package com.soonmark.core;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

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

	// 패턴 관리 객체
	private PatternManager patternManager;
	
	// 각 날짜, 요일, 시간, 특수 리스트 매니저 셋
	private DateTimeListMgrSet dateTimeListManagerSet;

	String inputText;

	DateTimeDTO startDate;
	DateTimeDTO endDate;
	

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

	public List<EventDTO> getRecommendations(String inputText, DateTimeDTO startDate, DateTimeDTO endDate) throws JsonParseException, JsonMappingException, IOException {
		focusingRecurNum = 2;
		dateTimeListManagerSet = new DateTimeListMgrSet();
		
		this.inputText = inputText;
		this.startDate = startDate;
		this.endDate = endDate;
		
		
		logger.info("입력받은 일정 : " + inputText);

		if (blockInvalidCharacters() == true) {
			InvalidDateTimeObj dtObj = new InvalidDateTimeObj();
			// -2는 잘못된 기호나 문자 입력 시 에러 코드
			dtObj.setYear(AppConstants.INVALID_INPUT_CHARACTER);
			InvalidEventObj evObj = new InvalidEventObj(dtObj, null);
			dateTimeListManagerSet.getResultList().insertDtObj(evObj);
		} else {
			// 패턴 매칭
			patternManager.matchToPatterns(inputText, dateTimeListManagerSet);

			// 기본 날짜 병합
			dateTimeListManagerSet.deduplicateElements(TokenType.dates);
			dateTimeListManagerSet.deduplicateElements(TokenType.days);
			dateTimeListManagerSet.mergeList(TokenType.dates, TokenType.days);
			dateTimeListManagerSet.mergeList(TokenType.dates, TokenType.special);

			// 시간
			dateTimeListManagerSet.deduplicateElements(TokenType.times);
			dateTimeListManagerSet.adjustForAmPmTime();
			
			// 기간, 날짜, 시간 조정
			EventListManager mergedListMgr = dateTimeListManagerSet.mergeList(TokenType.period, TokenType.dates, TokenType.times);

			createRecommendations(mergedListMgr);
		}

		logger.info("JSON 값  : " + dateTimeListManagerSet.getResultList().getEventDTOList());
		
		return dateTimeListManagerSet.getResultList().getEventDTOList();
	}

	// 특수기호 예외처리
	public boolean blockInvalidCharacters() {
		// 한글, 숫자, 영어, 공백만 입력 가능
		if (!(Pattern.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\\s\\:\\-\\.\\/]*$", inputText))) {
			logger.error("Error : 입력 허용 패턴이 아님");
			return true;
		}
		return false;
	}

	// 수정해야하는 메소드
	public void createRecommendations(EventListManager mergedListMgr) {
		// 빈 토큰 채우기
		fillEmptyDatas(mergedListMgr);

		// 우선순위대로 정렬
		sortByPriority();

		// 추천수 이상의 노드는 삭제
		removeAllAfterRecomNum();
	}
	
	private void fillEmptyDatas(EventListManager mergedListMgr) {
		dateTimeListManagerSet.setResultList(
				new DateTimeEstimator(dateTimeListManagerSet.getTimeList(), dateTimeListManagerSet.getDateList())
					.fillEmptyDatas(startDate, endDate));
	}
	
	private void sortByPriority() {
		for (int i = 0; i < dateTimeListManagerSet.getResultList().getEvMgrList().size(); i++) {
			dateTimeListManagerSet.getResultList().sortByPriority();
		}
	}

	private void removeAllAfterRecomNum() {
		// 2개만 남기고 다 지우기
		for (int i = recomNum; i < dateTimeListManagerSet.getResultList().getEventDTOList().size();) {
			dateTimeListManagerSet.getResultList().deleteDtObj(i);
		}
	}
	
	public StringDateTimeDTO getObjectFromJson(String date) throws JsonParseException, JsonMappingException, IOException {

		StringDateTimeDTO dto = null;
		ObjectMapper objectMapper = new ObjectMapper();
		if(date != null) {
			dto = objectMapper.readValue(date, StringDateTimeDTO.class);
		}
		
		return dto;
	}
}
