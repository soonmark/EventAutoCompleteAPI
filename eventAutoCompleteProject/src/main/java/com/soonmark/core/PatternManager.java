package com.soonmark.core;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.domain.TokenType;

public class PatternManager {
	private Logger logger = LoggerFactory.getLogger(PatternManager.class);

	PatternStorage patternStorage;

	public PatternManager() {
		patternStorage = new PatternStorage();
	}

	public void matchToPatterns(String inputText, DateTimeListMgrSet dateTimeListManager) {
		for(TokenType tokType : TokenType.values()) {
			matchToOnePatternType(inputText, tokType, dateTimeListManager);
		}
	}

	boolean matchToOnePatternType(String inputText, TokenType tokenType, DateTimeListMgrSet dateTimeListManager) {
		boolean hasPattern = false;
		
		Iterator<String> iter;
		switch(tokenType) {
		case period:
			iter = patternStorage.getPeriodPatterns().iterator();
		break;
		case dates:
			iter = patternStorage.getDatePatterns().iterator();
		break;
		case days:
			iter = patternStorage.getDayPatterns().iterator();
		break;
		case times:
			iter = patternStorage.getTimePatterns().iterator();
		break;
		case special:
			iter = patternStorage.getSpecialDatePatterns().iterator();
		break;
		default:
			iter = null;
		break;
		}
		
		DateTimeListManager list = dateTimeListManager.getDTListByTokType(tokenType);

		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputText);

			while (matcher.find()) {
				logger.info("패턴 : " + pattern);
				logger.info("패턴 만족 : " + matcher.group(0));

				InvalidDateTimeObj dtObj = new InvalidDateTimeObj();

				// enum의 추상메소드로 바로 감.
				tokenType.setDtObjInfo(dtObj, matcher);

				list.insertDtObj(dtObj);
				
				hasPattern = true;
			}
		}
		
		return hasPattern;
	}

	public void matchToPatterns(String inputText, List<PeriodManager> periodManager) {
		//패턴 찾으면 인덱스 저장하고 list add
		boolean hasPeriod = matchToPeriodPatterns(inputText, periodManager);
		
		
		Iterator<PeriodManager> iter = periodManager.iterator();
		while(iter.hasNext()) {
			PeriodManager period = iter.next();
			if(hasPeriod) {
				matchToPatterns(period.getFrom(), period.getStartDateListMgr());
				matchToPatterns(period.getTo(), period.getEndDateListMgr());
			}
		}
	}

	private boolean matchToPeriodPatterns(String inputText, List<PeriodManager> periodManager) {
		boolean hasPattern = false;
		
		Iterator<String> iter = patternStorage.getPeriodPatterns().iterator();
		
		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputText);

			while (matcher.find()) {
				logger.info("패턴 : " + pattern);
				logger.info("패턴 만족 : " + matcher.group(0));
				
				String from = "";
				String to = "";
//				String during = "";
				try {
					from = matcher.group("from");
				}
				catch(IllegalArgumentException e){
				}
				try {
					to = matcher.group("to");
				}
				catch(IllegalArgumentException e){
				}
//				try {
//					during = matcher.group("during");
//				}
//				catch(IllegalArgumentException e){
//				}
				
				periodManager.add(new PeriodManager(from, to));
				
				hasPattern = true;
			}
		}
		
		return hasPattern;
	}
}
