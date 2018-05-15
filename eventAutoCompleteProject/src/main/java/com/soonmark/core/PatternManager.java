package com.soonmark.core;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.TokenType;

public class PatternManager {
	private Logger logger = LoggerFactory.getLogger(PatternManager.class);

	PatternStorage patternStorage;

	public PatternManager() {
		patternStorage = new PatternStorage();
	}

	public void matchToDateTimePatterns(String inputText, DateTimeListMgrSet dateTimeListManager) {
		for (TokenType tokType : TokenType.values()) {
			matchToOnePatternType(inputText, tokType, dateTimeListManager);
		}
	}

	boolean matchToOnePatternType(String inputText, TokenType tokenType, DateTimeListMgrSet dateTimeListManager) {
		boolean hasPattern = false;

		Iterator<String> iter;
		switch (tokenType) {
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
		case during:
			iter = patternStorage.getDuringPatterns().iterator();
			break;
		default:
			iter = null;
			break;
		}

		DateTimeListManager list = dateTimeListManager.getDTListByTokType(tokenType);

		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputText.replace("\n", ""));

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
		// 패턴 찾으면 인덱스 저장하고 list add
		boolean hasPeriod = matchToPeriodPatterns(inputText, periodManager);

		Iterator<PeriodManager> iter = periodManager.iterator();
		while (iter.hasNext()) {
			PeriodManager period = iter.next();
			if (hasPeriod) {
				if (period.getFrom() != null) {
					matchToDateTimePatterns(period.getFrom(), period.getStartDateListMgr());
				}
				if (period.getTo() != null) {
					matchToDateTimePatterns(period.getTo(), period.getEndDateListMgr());
				}
				if (period.getDuring() != null) {
					matchToDuringPatterns(period.getDuring());
				}
			}
		}

		// 기간패턴이 없을 때 시작시간으로 보고 처리.
		if (periodManager.size() == 0) {
			PeriodManager period = new PeriodManager(inputText, null);
			periodManager.add(period);
			matchToDateTimePatterns(period.getFrom(), period.getStartDateListMgr());
		}
	}

	private void matchToDuringPatterns(During during) {
		Iterator<String> iter = patternStorage.getDuringPatterns().iterator();

		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(during.getText());

			while (matcher.find()) {
				logger.info("패턴 : " + pattern);
				logger.info("패턴 만족 : " + matcher.group(0));

				// enum의 추상메소드로 바로 감.
				setDuringObj(during, matcher);
			}
		}
	}

	private void setDuringObj(During during, Matcher matcher) {
		try {
			during.setValue(Integer.parseInt(matcher.group("month")));
			during.setType(DateTimeEn.month);
			return;
		} catch (IllegalArgumentException e) {
		}
		try {
			during.setValue(Integer.parseInt(matcher.group("date")));
			during.setType(DateTimeEn.date);
			return;
		} catch (IllegalArgumentException e) {
		}
		try {
			during.setValue(Integer.parseInt(matcher.group("hour")));
			during.setType(DateTimeEn.hour);
			return;
		} catch (IllegalArgumentException e) {
		}
		try {
			during.setValue(Integer.parseInt(matcher.group("minute")));
			during.setType(DateTimeEn.minute);
			return;
		} catch (IllegalArgumentException e) {
		}

	}

	private boolean matchToPeriodPatterns(String inputText, List<PeriodManager> periodManager) {
		boolean hasPattern = false;

		Iterator<String> iter = patternStorage.getPeriodPatterns().iterator();

		boolean hasFullPeriodPattern = false;
		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputText);

			while (matcher.find()) {
				if (hasFullPeriodPattern == false) {
					logger.info("패턴 : " + pattern);
					logger.info("패턴 만족 : " + matcher.group(0));

					String from = null;
					String to = null;
					String during = null;

					try {
						from = matcher.group("from");
					} catch (IllegalArgumentException ev) {
					}
					try {
						to = matcher.group("to");
					} catch (IllegalArgumentException ev) {
					}
					try {
						during = matcher.group("during");
						if(from == null) {
							from = "";
						}
					} catch (IllegalArgumentException e) {
					}

					if (from != null) {
						if (to != null || during != null) {
							hasFullPeriodPattern = true;
						}
					}

					// during을 to에서 빼기
					if (during != null) {
						if(to != null) {
							to.replace(during + "동안", "");
						}
					}

					if(during == null) {
						periodManager.add(new PeriodManager(from, to));
					}
					else {
						periodManager.add(new PeriodManager(from, to, during));
					}

					hasPattern = true;
				}
			}
		}

		return hasPattern;
	}
}
