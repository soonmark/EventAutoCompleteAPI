package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.Priority;
import com.soonmark.domain.TokenType;

public class RecommendationManager {

	private Logger logger = LoggerFactory.getLogger(RecommendationManager.class);

	// 패턴 관리 객체
	private PatternManager patternManager;

	// 각 날짜, 요일, 시간, 특수 리스트 매니저 셋
	private DateTimeListMgrSet dateTimeListManagerSet;

	String inputText;

	int recomNum;

	public RecommendationManager() {
		// 패턴 생성
		patternManager = new PatternManager();
	}

	public List<DateTimeDTO> getRecommendations(String inputText) {
		recomNum = 2;
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
			dateTimeListManagerSet.mergeList(TokenType.dates, TokenType.days);
			dateTimeListManagerSet.mergeList(TokenType.dates, TokenType.special);

			// 시간
			// dateTimeListManagerSet.deduplicateElements(TokenType.times);
			dateTimeListManagerSet.addPmTime();

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

	private void removeAllAfterRecomNum() {
		// 2개만 남기고 다 지우기
		for (int i = recomNum; i < dateTimeListManagerSet.getResultList().getDtDTOList().size();) {
			dateTimeListManagerSet.getResultList().deleteDtObj(i);
		}
	}

	private void fillEmptyDatas() {
		boolean isDateEmpty = false;
		boolean isTimeEmpty = false;

		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		if (dateTimeListManagerSet.getTimeList().getDtMgrList().size() == 0) {
			dateTimeListManagerSet.getTimeList().insertDtObj(new DateTimeLogicalObject());
			isTimeEmpty = true;
		}

		if (dateTimeListManagerSet.getDateList().getDtMgrList().size() == 0) {
			dateTimeListManagerSet.getDateList().insertDtObj(new DateTimeLogicalObject());
			isDateEmpty = true;
		}

		if (isDateEmpty && isTimeEmpty) {
			dateTimeListManagerSet.getTimeList().deleteDtObj(0);
			dateTimeListManagerSet.getDateList().deleteDtObj(0);
			return;
		}

		if (isDateEmpty) {
			setTimeToCloseFutureTime();
		} else {
			// 월간 일수 차이에 대한 예외처리
			if (isValidDates() == true) {
				addEstimateDateAndTime(isTimeEmpty);
			}
		}

	}

	private boolean isValidDates() {
		boolean result = true;
		for (int j = 0; j < dateTimeListManagerSet.getDateList().getDtMgrList().size(); j++) {
			int m = dateTimeListManagerSet.getDateList().getElement(j).getMonth();
			int dt = dateTimeListManagerSet.getDateList().getElement(j).getDate();

			if ((m == 2 && dt > 29) || (m < 8 && m % 2 == 0 && dt > 30) || (m > 7 && m % 2 == 1 && dt > 30)) {
				result = false;
			}
		}
		return result;
	}

	private void addEstimateDateAndTime(boolean isTimeEmpty) {
		for (int i = 0; i < dateTimeListManagerSet.getTimeList().getDtMgrList().size(); i++) {
			for (int j = 0; j < dateTimeListManagerSet.getDateList().getDtMgrList().size(); j++) {
				for (int k = 0; k < recomNum; k++) {
					DateTimeLogicalObject dtObj = new DateTimeLogicalObject();
					dtObj.copyEveryPropertyExceptForDayFrom(dateTimeListManagerSet.getDateList().getElement(j));

					// 시간정보 없을 땐 종일 로 나타내기
					estimateTime(isTimeEmpty, dtObj, dateTimeListManagerSet.getTimeList().getElement(i));

					// 년월일 요일 추정
					estimateDates(dtObj, k);
				}
			}
		}

	}

	private void estimateDates(DateTimeLogicalObject dtObj, int k) {

		estimateYear(dtObj);

		if (dtObj.getFocusToRepeat() == null) {
			// 반복없이 해당 값만 insert
			estimateOneDate(dtObj);
		} else {
			// focus할 게 있으면 그 정보를 기준으로 for문 돌며 여러값 insert
			estimateMultipleDates(dtObj, k);
		}
	}

	private void estimateMultipleDates(DateTimeLogicalObject dtObj, int k) {
		if (dtObj.getMonth() == AppConstants.NO_DATA) {
			dtObj.setMonth(1);
		}
		if (dtObj.getDate() == AppConstants.NO_DATA) {
			dtObj.setDate(1);
		}
		if (dtObj.getDay() == AppConstants.NO_DATA_FOR_DAY) {
			// 날짜에 맞는 요일 구하는 메소드
			dtObj.setProperDay();
		}

		if (k == 0 && dtObj.isAllDayEvent() != true) {
			dtObj.setPriority(Priority.timeWithFirstEstimateDate);
		}
		if (dtObj.isFocusOnDay() == true) {
			// 매주 해당 요일에 맞는 날짜만 뽑도록 구하는 로직
			setDatesByEveryWeek(dtObj, k);
		} else {
			setDatesByToken(dtObj, k);
		}
		dateTimeListManagerSet.getResultList().insertDtObj(dtObj);
	}

	private void setDatesByToken(DateTimeLogicalObject dtObj, int k) {
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

	private void setDatesByEveryWeek(DateTimeLogicalObject dtObj, int k) {
		LocalDate tmpDate = LocalDate.of(dtObj.getYear(), dtObj.getMonth(), dtObj.getDate());
		tmpDate = tmpDate.plusWeeks(k);
		dtObj.setDate(tmpDate.getDayOfMonth());
		dtObj.setYear(tmpDate.getYear());
		dtObj.setMonth(tmpDate.getMonthValue());
	}

	private void estimateOneDate(DateTimeLogicalObject dtObj) {
		recomNum = 1;
		if (dtObj.getMonth() == AppConstants.NO_DATA) {
			dtObj.setMonth(LocalDate.now().getMonthValue());
		}
		if (dtObj.getDate() == AppConstants.NO_DATA) {
			dtObj.setDate(LocalDate.now().getDayOfMonth());
		}
		if (dtObj.getDay() == AppConstants.NO_DATA_FOR_DAY) {
			// 날짜에 맞는 요일 구하는 메소드
			dtObj.setProperDay();
		}

		dateTimeListManagerSet.getResultList().insertDtObj(dtObj);
	}

	private void estimateYear(DateTimeLogicalObject dtObj) {
		if (dtObj.getYear() == AppConstants.NO_DATA) {
			dtObj.setYear(LocalDate.now().getYear());
		}
	}

	private void estimateTime(boolean isTimeEmpty, DateTimeLogicalObject dtObj, DateTimeLogicalObject timeObj) {
		if (isTimeEmpty == true) {
			dtObj.setAllDayEvent(true);
		} else { // 날짜와 시간 정보 있을 때
			dtObj.setPriority(timeObj.getPriority());
			dtObj.setHour(timeObj.getHour());
			dtObj.setMinute(timeObj.getMinute());
		}
	}

	private void setTimeToCloseFutureTime() {
		for (int i = 0; i < dateTimeListManagerSet.getTimeList().getDtMgrList().size(); i++) {
			for (int j = 0; j < dateTimeListManagerSet.getDateList().getDtMgrList().size(); j++) {
				// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
				DateTimeLogicalObject dtObj = new DateTimeLogicalObject();
				logger.info("날짜 정보없음");

				// 현재 날짜
				DateTimeAdjuster tmpCal = new DateTimeAdjuster();

				// 메소드의 객체가 now 캘린더가 아니면 true 입력
				if (dateTimeListManagerSet.getTimeList().getElement(i).getMinute() == AppConstants.NO_DATA) {
					tmpCal.setMinute(0);
				} else {
					tmpCal.setMinute(dateTimeListManagerSet.getTimeList().getElement(i).getMinute());
				}

				if (tmpCal.getTimePoint().toLocalTime().isBefore(LocalTime.now())) {
					tmpCal.plusDate(1);
				}
				dtObj.setAllDate(tmpCal);
				dtObj.setHour(tmpCal.getHour());
				dtObj.setMinute(tmpCal.getMinute());

				dateTimeListManagerSet.getResultList().insertDtObj(dtObj);
			}
		}

	}

	private void sortByPriority() {
		for (int i = 0; i < dateTimeListManagerSet.getResultList().getDtMgrList().size(); i++) {
			dateTimeListManagerSet.getResultList().sortByPriority();
		}
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
}
