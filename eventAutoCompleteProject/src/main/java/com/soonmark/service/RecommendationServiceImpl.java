package com.soonmark.service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.soonmark.domain.DateTimeListManager;
import com.soonmark.domain.DateTimeObject;
import com.soonmark.domain.PatternManager;
import com.soonmark.enums.TokenType;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	private Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
	
	// 패턴 관리 객체
	private PatternManager patternManager;
	private DateTimeListManager dateTimeListManager;


	@Override
	public String getRecommendation(String inputText) throws Exception {
		
		dateTimeListManager = new DateTimeListManager();

		logger.info("입력받은 일정 : " + inputText);

		if (blockInvalidCharacters(inputText) == true) {
			DateTimeObject dtObj = new DateTimeObject();
			// -2는 잘못된 기호나 문자 입력 시 에러 코드
			dtObj.setYear(-2);
			dateTimeListManager.getResultList().insertDtObj(dtObj);
		}
		else {
			// 패턴 생성
			patternManager = new PatternManager();
			patternManager.matchToPatterns(inputText, dateTimeListManager);
			
			// 기본 날짜 병합
			dateTimeListManager.innerMerge(TokenType.dates);
			dateTimeListManager.mergeBetween(TokenType.dates, TokenType.days);
			dateTimeListManager.mergeBetween(TokenType.dates, TokenType.special);
			
			dateTimeListManager.createRecommendation();
		}
		
		logger.info("JSON 값  : " + dateTimeListManager.getResultList().toJsonString());
		
		return dateTimeListManager.getResultList().toJsonString();
	}

	
	// 특수기호 예외처리
	@Override
	public boolean blockInvalidCharacters(String inputText) {
		// 한글, 숫자, 영어, 공백만 입력 가능
		if (!(Pattern.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\\s\\:\\-\\.\\/]*$", inputText))) {
			logger.error("Error : 입력 허용 패턴이 아님");
			return true;
		}
		return false;
	}
}
