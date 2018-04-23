package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.Priority;
import com.soonmark.domain.TokenType;

public class RecommendationManager {

	private Logger logger = LoggerFactory.getLogger(RecommendationManager.class);

	// 패턴 관리 객체
	private PatternManager patternManager;
	
	// 각 날짜, 요일, 시간, 특수 리스트 매니저 셋
	private DateTimeListMgrSet dateTimeListManagerSet;

	String inputText;

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

	public List<DateTimeDTO> getRecommendations(String inputText) {
		focusingRecurNum = 2;
		dateTimeListManagerSet = new DateTimeListMgrSet();
		this.inputText = inputText;

		logger.info("입력받은 일정 : " + inputText);

		if (blockInvalidCharacters() == true) {
			DateTimeLogicalObject dtObj = new DateTimeLogicalObject();
			// -2는 잘못된 기호나 문자 입력 시 에러 코드
			dtObj.setYear(AppConstants.INVALID_INPUT_CHARACTER);
			dateTimeListManagerSet.getResultList().insertDtObj(dtObj);
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

			createRecommendations();
		}

		logger.info("JSON 값  : " + dateTimeListManagerSet.getResultList().getDtDTOList().toString());

		return dateTimeListManagerSet.getResultList().getDtDTOList();
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
	public void createRecommendations() {
		// 빈 토큰 채우기
		fillEmptyDatas();

		// 우선순위대로 정렬
		sortByPriority();

		// 추천수 이상의 노드는 삭제
		removeAllAfterRecomNum();
	}
	
	private void fillEmptyDatas() {
		dateTimeListManagerSet.setResultList(new DateTimeEstimator(dateTimeListManagerSet.getTimeList(), dateTimeListManagerSet.getDateList()).fillEmptyDatas());
	}
	
	private void sortByPriority() {
		for (int i = 0; i < dateTimeListManagerSet.getResultList().getDtMgrList().size(); i++) {
			dateTimeListManagerSet.getResultList().sortByPriority();
		}
	}

	private void removeAllAfterRecomNum() {
		// 2개만 남기고 다 지우기
		for (int i = recomNum; i < dateTimeListManagerSet.getResultList().getDtDTOList().size();) {
			dateTimeListManagerSet.getResultList().deleteDtObj(i);
		}
	}
}
