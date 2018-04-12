package com.soonmark.managers;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.enums.TokenType;

public class PatternManager {
	private Logger logger = LoggerFactory.getLogger(PatternManager.class);

	PatternStorage patternMaker;

	public PatternManager() {
		patternMaker = new PatternStorage();
	}

	public void matchToPatterns(String inputText, DateTimeListManagerSet dateTimeListManager) {
		for(TokenType tokType : TokenType.values()) {
			matchToOnePatternType(inputText, tokType, dateTimeListManager);
		}
	}
	
	void matchToOnePatternType(String inputText, TokenType tokenType, DateTimeListManagerSet dateTimeListManager) {
		
		Iterator<String> iter;
		switch(tokenType) {
		case dates:
			iter = patternMaker.getDatePatterns().iterator();
		break;
		case days:
			iter = patternMaker.getDayPatterns().iterator();
		break;
		case times:
			iter = patternMaker.getTimePatterns().iterator();
		break;
		case special:
			iter = patternMaker.getSpecialDatePatterns().iterator();
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

			if (matcher.matches()) {
				logger.info("패턴 : " + pattern);
				logger.info("패턴 만족 : " + matcher.group(0));

				DateTimeManager dtObj = new DateTimeManager();

				// enum의 추상메소드로 바로 감.
				tokenType.setDtObjInfo(dtObj, matcher);

				list.insertDtObj(dtObj);
			}
		}
	}
}
