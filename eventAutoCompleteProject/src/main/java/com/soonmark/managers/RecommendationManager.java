package com.soonmark.managers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.enums.DateTimeEn;
import com.soonmark.enums.TokenType;

public class RecommendationManager {

	private Logger logger = LoggerFactory.getLogger(RecommendationManager.class);
	
	// 패턴 관리 객체
	private PatternManager patternManager;
	
	// 각 날짜, 요일, 시간, 특수 리스트 매니저 셋
	private DateTimeListManagerSet dateTimeListManagerSet;
	
	String inputText;
	

	public RecommendationManager(String inputText) {
		
		dateTimeListManagerSet = new DateTimeListManagerSet();
		
		// 패턴 생성
		patternManager = new PatternManager();
		
		this.inputText = inputText;
	}
	
	public String getRecommendations(){
		
		logger.info("입력받은 일정 : " + inputText);

		if (blockInvalidCharacters() == true) {
			DateTimeManager dtObj = new DateTimeManager();
			// -2는 잘못된 기호나 문자 입력 시 에러 코드
			dtObj.setYear(-2);
			dateTimeListManagerSet.getResultList().insertDtObj(dtObj);
		}
		else {
			// 패턴 매칭
			patternManager.matchToPatterns(inputText, dateTimeListManagerSet);
			
			// 기본 날짜 병합
			dateTimeListManagerSet.innerMerge(TokenType.dates);
			dateTimeListManagerSet.mergeBetween(TokenType.dates, TokenType.days);
			dateTimeListManagerSet.mergeBetween(TokenType.dates, TokenType.special);
			
			createRecommendations();
		}
		
		logger.info("JSON 값  : " + dateTimeListManagerSet.getResultList().getDtDTOList().toString());
		
		return dateTimeListManagerSet.getResultList().getDtDTOList().toString();
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
	
	public void createRecommendations() {
		int recomNum = 2; // 추천할 개수를 2개로 한정

		// 현재 시스템 날짜 // 여기서 수정하자.
		LocalDateTime now = LocalDateTime.now();
		// now.plusHour(3);
		
		
		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		dateTimeListManagerSet.getTimeList().insertDtObj(new DateTimeManager());
		
		// dateList에 추가했던 element를 삭제했으므로 다시 하나 만들어줌.
		dateTimeListManagerSet.getDateList().insertDtObj(new DateTimeManager());

		for (int i = 0; i < dateTimeListManagerSet.getTimeList().getDtMgrList().size(); i++) {
			for (int j = 0; j < dateTimeListManagerSet.getDateList().getDtMgrList().size(); j++) {

				// 둘다 정보가 들어왔으면 빈값 매칭 안 해줘도 됨.
				// 시간만 있을 때는 -> 날짜 빈거랑 매칭하고 시간 여분 빼기
				// 날짜만 있을 때는 -> 시간 빈거랑 매칭하고 날짜 여분 빼기
				// 둘 다 비어있을 때도 안 해줘도 됨.
				if (i == dateTimeListManagerSet.getTimeList().getDtMgrList().size() - 1 && j == dateTimeListManagerSet.getDateList().getDtMgrList().size() - 1) {
					continue;
				}

//				logger.info("시간 정보 존재");

				int y = dateTimeListManagerSet.getDateList().getElement(j).getYear();
				int m = dateTimeListManagerSet.getDateList().getElement(j).getMonth();
				int dt = dateTimeListManagerSet.getDateList().getElement(j).getDate();
				DayOfWeek day = dateTimeListManagerSet.getDateList().getElement(j).getDay();
				boolean isFocusOnDay = dateTimeListManagerSet.getDateList().getElement(j).isFocusOnDay();

				int h = dateTimeListManagerSet.getTimeList().getElement(i).getHour();
				int min = dateTimeListManagerSet.getTimeList().getElement(i).getMinute();

				// 월간 일수 차이에 대한 예외처리
				if ((m == 2 && dt > 28) || (m < 8 && m % 2 == 0 && dt > 30) || (m > 7 && m % 2 == 1 && dt > 30)) {

					break;
				}

				// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
				if (dateTimeListManagerSet.getDateList().getDtMgrList().size() == 1) {
					logger.info("날짜 정보없음");

					// 현재 시각과 비교해서 이미 지난 시간일 경우 + 12;
					DateTimeAdjuster tmpCal = new DateTimeAdjuster();

					// 메소드의 객체가 now 캘린더가 아니면 true 입력
					tmpCal.setHour(h, true);
					if (min == -1) {
						tmpCal.setMinute(0);
					} else {
						tmpCal.setMinute(min);
					}

					// for 문 돌면서 비교할 기준 시 설정
					LocalDateTime comparedCal = LocalDateTime.now();

					for (int k = 0; k < recomNum; k++) {
						DateTimeManager dtObj = new DateTimeManager();

						tmpCal.setCloseDateOfTime(comparedCal);
						comparedCal = comparedCal.with(tmpCal.getTimePoint());

						// 현재 시스템 날짜
						dtObj.setAllDate(tmpCal);
						dtObj.setHour(tmpCal.getHour());
						dtObj.setMinute(tmpCal.getMinute());

						dateTimeListManagerSet.getResultList().insertDtObj(dtObj);
					}
				}

				else { // 날짜 정보 있으면 (시간은 있든 말든 상관없음.)
					for (int k = 0; k < recomNum; k++) {
						DateTimeManager dtObj = new DateTimeManager();
						DateTimeManager secDtObj = new DateTimeManager();

						dtObj.setAllDate(dateTimeListManagerSet.getDateList().getElement(j));
						dtObj.setFocusOnDay(isFocusOnDay);
						dtObj.setFocusToRepeat(dateTimeListManagerSet.getDateList().getElement(j).getFocusToRepeat());

						// 무슨 정보가 있는지 담겨있음
						dtObj.setHasInfo(DateTimeEn.year.ordinal(),
								dateTimeListManagerSet.getDateList().getElement(j).hasInfo(DateTimeEn.year.ordinal()));
						dtObj.setHasInfo(DateTimeEn.month.ordinal(),
								dateTimeListManagerSet.getDateList().getElement(j).hasInfo(DateTimeEn.month.ordinal()));
						dtObj.setHasInfo(DateTimeEn.date.ordinal(),
								dateTimeListManagerSet.getDateList().getElement(j).hasInfo(DateTimeEn.date.ordinal()));
						dtObj.setHasInfo(DateTimeEn.day.ordinal(),
								dateTimeListManagerSet.getDateList().getElement(j).hasInfo(DateTimeEn.day.ordinal()));

						// 시간정보 없을 땐, 종일 로 나타내기
						if (dateTimeListManagerSet.getTimeList().getDtMgrList().size() == 1) {
							dtObj.setAllDayEvent(true);

						} else { // 날짜와 시간 정보 있을 때
							dtObj.setHour(h);
							dtObj.setMinute(min);
						}

						if (y == -1) {
							dtObj.setYear(now.getYear());
						}

						if (dtObj.getFocusToRepeat() == null) { // 반복없이 해당 값만 insert 하게 하기
							recomNum = 1; // 반복 안 하도록
							if (m == -1) {
								dtObj.setMonth(now.getMonthValue());
							}
							if (dt == -1) {
								dtObj.setDate(now.getDayOfMonth());
							}
							if (day == null) {
								// 날짜에 맞는 요일 구하는 메소드
								dtObj.setProperDay();
							}

						} else { // focus할 게 있으면 그 정보를 기준으로 for문 돌게끔...
							if (m == -1) {
								dtObj.setMonth(1);
							}
							if (dt == -1) {
								dtObj.setDate(1);
							}
							if (day == null) {
								// 날짜에 맞는 요일 구하는 메소드
								dtObj.setProperDay();
							}

							// 이전에는 요일 정보를 안 받았기 때문에 이렇게 짰는데 다시 짜자.
							if (dtObj.isFocusOnDay() == true) {
								// 요일에 맞는 날짜만 뽑도록 구하는 로직
								LocalDate tmpDate = LocalDate.of(dtObj.getYear(), dtObj.getMonth(), dtObj.getDate());

								tmpDate = tmpDate.plusWeeks(k);
								dtObj.setDate(tmpDate.getDayOfMonth());
								dtObj.setYear(tmpDate.getYear());
								dtObj.setMonth(tmpDate.getMonthValue());
							} else {
								DateTimeAdjuster tmpCal2 = new DateTimeAdjuster();
								tmpCal2.setYear(dtObj.getYear());
								tmpCal2.setMonth(dtObj.getMonth());
								tmpCal2.setDate(dtObj.getDate());
								// focus 할 해당 정보를 기준으로 더해주기.
								tmpCal2.setCloseDate(tmpCal2, dtObj.getFocusToRepeat(), k);

								dtObj.setDate(tmpCal2.getDate());
								dtObj.setYear(tmpCal2.getYear());
								dtObj.setMonth(tmpCal2.getMonth());

								// 날짜에 맞는 요일 구하는 로직
								dtObj.setProperDay();
							}
						}

						dateTimeListManagerSet.getResultList().insertDtObj(dtObj);

						// 시간정보와 날짜 모두 있을 땐, halfTime 일 경우, 오후 시간도 저장
						if (dateTimeListManagerSet.getTimeList().getDtMgrList().size() > 1 && dtObj.getHour() <= 12) {
							secDtObj.setHour((dtObj.getHour() + 12) % 24);
							secDtObj.setMinute(dtObj.getMinute());
							secDtObj.setAllDate(dtObj);
							dateTimeListManagerSet.getResultList().insertDtObj(secDtObj);
						}
					}
				}

			}
		}
	}
	
	public PatternManager getPatternManager() {
		return patternManager;
	}
	
	public void setPatternManager(PatternManager patternManager) {
		this.patternManager = patternManager;
	}
	
	public DateTimeListManagerSet getDateTimeListManagerSet() {
		return dateTimeListManagerSet;
	}
	
	public void setDateTimeListManagerSet(DateTimeListManagerSet dateTimeListManagerSet) {
		this.dateTimeListManagerSet = dateTimeListManagerSet;
	}
}
