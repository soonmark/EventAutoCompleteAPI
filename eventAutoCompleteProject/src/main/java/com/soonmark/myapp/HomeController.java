package com.soonmark.myapp;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		return "home";
	}

	// Ajax request 받아서 json 형태로 response.
	// 시간, 날짜 추천 리스트 보냄
	@RequestMapping(value = "refresh", method = RequestMethod.POST, produces = "application/json; charset=utf8")
	public @ResponseBody String inputProcess(HttpServletRequest httpServletRequest) {

		@Autowired RecommendationService recommendationService;
		
		int recomNum = 2; // 추천할 개수를 10개로 한정

		// 현재 시스템 날짜 // 여기서 수정하자.
		MyLocalDateTime now = new MyLocalDateTime();
		// now.plusHour(3);

		// 숫자만 저장, 날짜 전체 저장
		logger.info("시간값 : " + now);

		// 입력값 불러오기
		String inputEvent = httpServletRequest.getParameter("inputEventsss");

		logger.info("현재 시간값 : " + now);

		// 앞으로 추천할 날짜 리스트
		DateTimeListDTO dateList = new DateTimeListDTO();

		// 앞으로 추천할 예외 날짜 리스트
		DateTimeListDTO specialDateList = new DateTimeListDTO();

		// 앞으로 추천할 요일 리스트
		DateTimeListDTO dayList = new DateTimeListDTO();

		// 앞으로 추천할 시간 리스트
		DateTimeListDTO timeList = new DateTimeListDTO();

		// 최종 리스트
		DateTimeListDTO resultList = new DateTimeListDTO();

		// 한글, 숫자, 영어, 공백만 입력 가능
		if (!(Pattern.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\\s\\:\\-\\.\\/]*$", inputEvent))) {

			logger.error("Error : 입력 허용 패턴이 아님");

			DateTimeObject dtObj = new DateTimeObject();
			// -2는 잘못된 기호나 문자 입력 시 에러 코드
			dtObj.setYear(-2);
			dateList.insertDtObj(dtObj);

			logger.info("JSON 값  : " + dateList.toJsonString());
			return dateList.toJsonString();
		}

		logger.info("입력받은 일정 : " + inputEvent);

		// 년월일 패턴
		List<String> datePatterns = new ArrayList<String>();
		// 요일 패턴
		List<String> daysPatterns = new ArrayList<String>();
		// 그 외 특이 패턴
		List<String> specialDatePatterns = new ArrayList<String>();
		// 시간 패턴
		List<String> timePatterns = new ArrayList<String>();

		// 패턴 초기 세팅
		initPatterns(datePatterns, daysPatterns, specialDatePatterns, timePatterns);

		// 날짜 매칭 / 요일 매칭 / 시간 매칭
		matchingProcess(inputEvent, datePatterns, TokenType.dates, dateList);
		matchingProcess(inputEvent, specialDatePatterns, TokenType.special, specialDateList);
		matchingProcess(inputEvent, daysPatterns, TokenType.days, dayList);
		matchingProcess(inputEvent, timePatterns, TokenType.times, timeList);

		// 기본 날짜 병합
		mergeItself(dateList);

		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		timeList.insertDtObj(new DateTimeObject());
		dateList.insertDtObj(new DateTimeObject());

		// 우선, 요일과 날짜 크로스
		// 날짜가 있고 요일이 없는 경우나 - clear
		// 요일이 있는데 날짜가 없는 경우
		// 요일과 날짜가 있지만 서로 안 맞는 경우
		// 요일과 날짜가 있고 둘이 맞는 경우
		// 위 4가지 경우에 대해 코딩.
		
		for (int i = 0; i < dateList.getList().size(); i++) {
			// 요일 정보 없으면 그냥 나가기
			if(dayList.getList().size() == 0) {
				dateList.deleteList(dateList.getList().size() - 1);
				break;
			}
			for (int j = 0; j < dayList.getList().size(); j++) {
				// 날짜 없고 요일있는건 처리해야하니까 if문 처리 안 함.
				MyLocalDateTime tmpCal = new MyLocalDateTime();
				DateTimeObject dtObj = new DateTimeObject();

				// 날짜 없고 요일만 있을 때
				if (dateList.getList().size() == 1) {
					// 가까운 미래시 날짜 찾아 tmpCal에 세팅.
					tmpCal.setCloseDateOfTheDay(dayList.getElement(j).getDay());
					dtObj.setFocusOnDay(true);

				} else { // 요일 정보와 날짜 정보가 있을 때는 요일 정보를 무시
					if (dateList.getElement(i).hasInfo(DateTimeEn.year.ordinal())) {
						tmpCal.setYear(dateList.getElement(i).getYear());
					}
					if (dateList.getElement(i).hasInfo(DateTimeEn.month.ordinal())) {
						tmpCal.setMonth(dateList.getElement(i).getMonth());
					}
					if (dateList.getElement(i).hasInfo(DateTimeEn.date.ordinal())) {
						tmpCal.setDate(dateList.getElement(i).getDate());
					}
					dtObj.setFocusOnDay(false);
				}

				dtObj.setAllDate(tmpCal);

				dateList.getElement(i).setAllDate(dtObj);

				dateList.getElement(i).setFocusOnDay(dtObj.isFocusOnDay());
				dateList.getElement(i).setHasInfo(DateTimeEn.day.ordinal(), true);
			}
		}

		// special 날짜와 요일까지 세팅된 기본 날짜 병합
		merge(dateList, specialDateList);

		// dateList에 추가했던 element를 삭제했으므로 다시 하나 만들어줌.
		dateList.insertDtObj(new DateTimeObject());

		for (int i = 0; i < timeList.getList().size(); i++) {
			for (int j = 0; j < dateList.getList().size(); j++) {

				// 둘다 정보가 들어왔으면 빈값 매칭 안 해줘도 됨.
				// 시간만 있을 때는 -> 날짜 빈거랑 매칭하고 시간 여분 빼기
				// 날짜만 있을 때는 -> 시간 빈거랑 매칭하고 날짜 여분 빼기
				// 둘 다 비어있을 때도 안 해줘도 됨.
				if (i == timeList.getList().size() - 1 && j == dateList.getList().size() - 1) {
					continue;
				}

				logger.info("여1");

				int y = dateList.getElement(j).getYear();
				int m = dateList.getElement(j).getMonth();
				int dt = dateList.getElement(j).getDate();
				DayOfWeek day = dateList.getElement(j).getDay();
				boolean isFocusOnDay = dateList.getElement(j).isFocusOnDay();

				int h = timeList.getElement(i).getHour();
				int min = timeList.getElement(i).getMinute();

				// 월간 일수 차이에 대한 예외처리
				if ((m == 2 && dt > 28) || (m < 8 && m % 2 == 0 && dt > 30) || (m > 7 && m % 2 == 1 && dt > 30)) {

					break;
				}

				// 날짜 정보가 없으면 가장 근접한 미래날짜로 세팅.
				if (dateList.getList().size() == 1) {
					logger.info("여기2");

					// 현재 시각과 비교해서 이미 지난 시간일 경우 + 12;
					MyLocalDateTime tmpCal = new MyLocalDateTime();

					// 메소드의 객체가 now 캘린더가 아니면 true 입력
					tmpCal.setHour(h, true);
					if (min == -1) {
						tmpCal.setMinute(0);
					} else {
						tmpCal.setMinute(min);
					}

					// for 문 돌면서 비교할 기준 시 설정
					MyLocalDateTime comparedCal = new MyLocalDateTime();
					comparedCal.setTimePoint(now.getTimePoint());

					for (int k = 0; k < recomNum; k++) {
						DateTimeObject dtObj = new DateTimeObject();

						tmpCal.setCloseDateOfTime(comparedCal);
						comparedCal.setTimePoint(tmpCal.getTimePoint());

						// 현재 시스템 날짜
						dtObj.setAllDate(tmpCal);
						dtObj.setHour(tmpCal.getHour());
						dtObj.setMinute(tmpCal.getMinute());

						resultList.insertDtObj(dtObj);
					}
				}

				else { // 날짜 정보 있으면 (시간은 있든 말든 상관없음.)
					for (int k = 0; k < recomNum; k++) {
						DateTimeObject dtObj = new DateTimeObject();
						DateTimeObject secDtObj = new DateTimeObject();

						dtObj.setAllDate(dateList.getElement(j));
						dtObj.setFocusOnDay(isFocusOnDay);
						dtObj.setFocusToRepeat(dateList.getElement(j).getFocusToRepeat());

						// 무슨 정보가 있는지 담겨있음
						dtObj.setHasInfo(DateTimeEn.year.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.year.ordinal()));
						dtObj.setHasInfo(DateTimeEn.month.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.month.ordinal()));
						dtObj.setHasInfo(DateTimeEn.date.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.date.ordinal()));
						dtObj.setHasInfo(DateTimeEn.day.ordinal(),
								dateList.getElement(j).hasInfo(DateTimeEn.day.ordinal()));
						
						
						// 시간정보 없을 땐, 종일 로 나타내기
						if (timeList.getList().size() == 1) {
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
								dtObj.setMonth(now.getMonth());
							}
							if (dt == -1) {
								dtObj.setDate(now.getDate());
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
								MyLocalDateTime tmpCal2 = new MyLocalDateTime();
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

						resultList.insertDtObj(dtObj);

						// 시간정보와 날짜 모두 있을 땐, halfTime 일 경우, 오후 시간도 저장
						if (timeList.getList().size() > 1 && dtObj.getHour() <= 12) {
							secDtObj.setHour((dtObj.getHour() + 12) % 24);
							secDtObj.setMinute(dtObj.getMinute());
							secDtObj.setAllDate(dtObj);
							resultList.insertDtObj(secDtObj);
						}
					}
				}

			}
		}

		return resultList.toJsonString();
	}

	void initPatterns(List<String> datePatterns, List<String> daysPatterns,
					List<String> specialDatePatterns, List<String> timePatterns) {

		datePatterns.add("^(.*)(?<year>[0-9]{4})-(?<month>0?[1-9]|1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 2018-(0)3-9
		datePatterns.add("^(.*)(?<year>[0-9]{4})/(?<month>0?[1-9]|1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 2018/(0)3/9
		datePatterns.add("^(.*)(?<year>[0-9]{4})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 2018.(0)3.9
		datePatterns.add("^(.*)(?<year>[0-9]{4})년 (?<month>0?[1-9]|1[0-2])월 (?<date>[1-9]|[1-2][0-9]|3[0-1])일(|[^0-9].*)$"); // 2018년(0)3월9일

		datePatterns.add("^(.*)(?<year>[0-9]{4})-(?<month>0?[1-9]|1[0-2])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 2018-(0)3-09
		datePatterns.add("^(.*)(?<year>[0-9]{4})/(?<month>0?[1-9]|1[0-2])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 2018/(0)3/09
		datePatterns.add("^(.*)(?<year>[0-9]{4})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 2018.(0)3.09
		datePatterns.add("^(.*)(?<year>[0-9]{4})년 (?<month>0?[1-9]|1[0-2])월 (?<date>0[1-9]|[1-2][0-9]|3[0-1])일(.*)$"); // 2018년(0)3월09일

		datePatterns.add("^(.*)(?<year>[0-9]{2})-(?<month>0?[1-9]|1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 18-(0)3-9
		datePatterns.add("^(.*)(?<year>[0-9]{2})/(?<month>0?[1-9]|1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 18/(0)3/9
		datePatterns.add("^(.*)(?<year>[0-9]{2})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 18.(0)3.9
		datePatterns.add("^(.*)(?<year>[0-9]{2})년 (?<month>0?[1-9]|1[0-2])월 (?<date>[1-9]|[1-2][0-9]|3[0-1])일(|[^0-9].*)$"); // 18년(0)3월9일

		datePatterns.add("^(.*)(?<year>[0-9]{2})-(?<month>0?[1-9]|1[0-2])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 18-(0)3-09
		datePatterns.add("^(.*)(?<year>[0-9]{2})/(?<month>0?[1-9]|1[0-2])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 18/(0)3/09
		datePatterns.add("^(.*)(?<year>[0-9]{2})\\.(?<month>0?[1-9]|1[0-2])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 18.(0)3.09
		datePatterns.add("^(.*)(?<year>[0-9]{2})년 (?<month>0?[1-9]|1[0-2])월 (?<date>0[1-9]|[1-2][0-9]|3[0-1])일(.*)$"); // 18년(0)3월09일


		// 20180319

		datePatterns.add("^(.*)(?<month>1[0-2])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11-09
		datePatterns.add("^(|.*[^1])(?<month>[1-9])-(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3-09
		datePatterns.add("^(.*)(?<month>1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11-9
		datePatterns.add("^(|.*[^1])(?<month>[1-9])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3-9
		datePatterns.add("^(.*)(?<month>1[0-2])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11.09
		datePatterns.add("^(|.*[^1])(?<month>[1-9])\\.(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3.09
		datePatterns.add("^(.*)(?<month>1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11.9
		datePatterns.add("^(|.*[^1])(?<month>[1-9])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3.9
		datePatterns.add("^(.*)(?<month>1[0-2])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 11/09
		datePatterns.add("^(|.*[^1])(?<month>[1-9])/(?<date>0[1-9]|[1-2][0-9]|3[0-1])(.*)$"); // 3/09
		datePatterns.add("^(.*)(?<month>1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11/9
		datePatterns.add("^(|.*[^1])(?<month>[1-9])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3/9

		datePatterns.add("^(.*)(?<year>[1-2][0-9][0-9][0-9])년(.*)$"); // 2018년
		datePatterns.add("^(.*)(?<year>[0-9][0-9])년(.*)$"); // 94년, 18년
		// 일만 입력받기
		datePatterns.add("^(.*)(?<date>[1-2][0-9]|3[0-1])일(.*)$"); // 19일
		datePatterns.add("^(|.*[^1-3])(?<date>[1-9])일(.*)$"); // 1일
		// 월만 입력받기
		datePatterns.add("^(.*)(?<month>1[0-2])월(.*)$"); // 12월
		datePatterns.add("^(|.*[^1])(?<month>[1-9])월(.*)$"); // 1월

		// 요일 패턴
		daysPatterns.add("^(.*)(?<day>월|화|수|목|금|토|일)요일(.*)$"); // 월요일

		// 그 외 특이 패턴
		specialDatePatterns.add("^(.*)(?<today>오늘)(.*)$"); // 오늘
		specialDatePatterns.add("^(.*)(?<tomorrow>내일)(.*)$"); // 내일
		specialDatePatterns.add("^(.*)(?<dayAfterTomorrow>모레)(.*)$"); // 모레

		specialDatePatterns.add("^(.*)(?<thisWeek>이번주)(.*)$"); // 이번주
		specialDatePatterns.add("^(|.*[^다])(?<nextWeek>다음주)(.*)$"); // 다음주
		specialDatePatterns.add("^(.*)(?<weekAfterNext>다다음주)(.*)$"); // 다다음주

		// 시간 패턴
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3]):(?<minute>[0-5][0-9])(.*)$"); // 12:01 // 12:1은 안 됨
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9]):(?<minute>[0-5][0-9])(.*)$"); // 2:01 // 2:1은 안 됨
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시 (?<minute>[0-5][0-9])분(.*)$"); // 12시 30분
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시 (?<minute>[0-9])분(.*)$"); // 12시 3분
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시 (?<minute>[0-5][0-9])분(.*)$"); // 7시 30분
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시 (?<minute>[0-9])분(.*)$"); // 7시 3분
		// 분 정보 없는 시간
		timePatterns.add("^(.*)(?<hour>1[0-9]|2[0-3])시([^분]*)$"); // 12시
		timePatterns.add("^(|.*[^1-2])(?<hour>[1-9])시([^분]*)$"); // 7시
	}

	void matchingProcess(String inputEv, List<String> patterns, TokenType tokenType, DateTimeListDTO targetList) {
		// 요일 매칭
		Iterator<String> iter = patterns.iterator();
		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputEv);

			if (matcher.matches()) {
				logger.info("패턴 : " + pattern);
				logger.info("패턴 만족 : " + matcher.group(0));

				DateTimeObject dtObj = new DateTimeObject();

				// enum의 추상메소드로 바로 감.
				tokenType.setDtObjInfo(dtObj, matcher);

				targetList.insertDtObj(dtObj);
			}
		}
	}

	// 년, 월, 일을 각각 받게 되면 여기서 merge 할 것.
	void mergeItself(DateTimeListDTO targetList) {
		DateTimeListDTO tmpList = new DateTimeListDTO();
		tmpList.insertDtObj(new DateTimeObject());

		for (int i = 0; i < targetList.getList().size(); i++) {
			for (int j = 0; j < tmpList.getList().size(); j++) {
				// target 의 원소 중 존재하는 값들이 모두 list 에 없으면 추가
				boolean ableToPut = true;
				for (DateTimeEn d : DateTimeEn.values()) {
					if (targetList.getElement(i).hasInfo(d.ordinal()) && tmpList.getElement(j).hasInfo(d.ordinal())) {
						ableToPut = false;
						break;
					}
				}
				if (ableToPut) {
					// 합치는 프로세스 시작
					// y, m, dt 모두 정보가 없으면 list 에 빈 객체 추가
					if (!tmpList.getElement(j).hasInfo(DateTimeEn.year.ordinal())
							&& !tmpList.getElement(j).hasInfo(DateTimeEn.month.ordinal())
							&& !tmpList.getElement(j).hasInfo(DateTimeEn.date.ordinal())) {
						tmpList.insertDtObj(new DateTimeObject());
					}
					if (targetList.getElement(i).getYear() != -1) {
						tmpList.getElement(j).setYear(targetList.getElement(i).getYear());
						tmpList.getElement(j).setHasInfo(0, true);
					}
					if (targetList.getElement(i).getMonth() != -1) {
						tmpList.getElement(j).setMonth(targetList.getElement(i).getMonth());
						tmpList.getElement(j).setHasInfo(1, true);
					}
					if (targetList.getElement(i).getDate() != -1) {
						tmpList.getElement(j).setDate(targetList.getElement(i).getDate());
						tmpList.getElement(j).setHasInfo(2, true);
					}
					break;
				}
			}
		}

		targetList.clearList();
		for (int j = 0; j < tmpList.getList().size() - 1; j++) {
			targetList.insertDtObj(tmpList.getElement(j));
		}
		for (int j = 0; j < targetList.getList().size(); j++) {
			if (targetList.getElement(j).getYear() == -1) {
				targetList.getElement(j).setFocusToRepeat(DateTimeEn.year);
			}
			if (targetList.getElement(j).getMonth() == -1) {
				targetList.getElement(j).setFocusToRepeat(DateTimeEn.month);
			}
			if (targetList.getElement(j).getDate() == -1) {
				targetList.getElement(j).setFocusToRepeat(DateTimeEn.date);
			}
		}
		
		
		
		// 여기에서 중복 제거를 하던지, 아니면 우선순위 부여를 하던지, 아니면 날짜 비교해서 너무 터무니없이 먼 날짜면 지우는 방향으로!
		// 중복 제거
		// 있는 정보 중에는 모두 같은 거
		for(int j = 0; j < targetList.getList().size(); j++) {
			for(int i = j+1 ; i < targetList.getList().size() ; i++) {
				if(((targetList.getElement(i).hasInfo(DateTimeEn.year.ordinal())
						&& targetList.getElement(j).getYear() == targetList.getElement(i).getYear())
						|| !targetList.getElement(i).hasInfo(DateTimeEn.year.ordinal()))
					&& ((targetList.getElement(i).hasInfo(DateTimeEn.month.ordinal())
						&& targetList.getElement(j).getMonth() == targetList.getElement(i).getMonth())
						|| !targetList.getElement(i).hasInfo(DateTimeEn.month.ordinal()))
					&& ((targetList.getElement(i).hasInfo(DateTimeEn.date.ordinal())
						&& targetList.getElement(j).getDate() == targetList.getElement(i).getDate())
						|| !targetList.getElement(i).hasInfo(DateTimeEn.date.ordinal()))) {

					targetList.deleteList(i);
					i -= 1;
				}
			}
		}

		// 터무니 없는 날짜 제거
		
		
		// 우선순위 부여
		
		
		// 로그 찍기
		for (int j = 0; j < targetList.getList().size() ; j++) {
			logger.info(targetList.getElement(j).toString());
		}
	}
	
	void removeDuplicate(DateTimeListDTO targetList, TokenType tokenType) {
		
		switch(tokenType) {
		case dates:
			// 있는 정보 중에는 모두 같은 거
			for(int j = 0; j < targetList.getList().size(); j++) {
				for(int i = j+1 ; i < targetList.getList().size() ; i++) {
					if(((targetList.getElement(i).hasInfo(DateTimeEn.year.ordinal())
							&& targetList.getElement(j).getYear() == targetList.getElement(i).getYear())
							|| !targetList.getElement(i).hasInfo(DateTimeEn.year.ordinal()))
							&& ((targetList.getElement(i).hasInfo(DateTimeEn.month.ordinal())
									&& targetList.getElement(j).getMonth() == targetList.getElement(i).getMonth())
									|| !targetList.getElement(i).hasInfo(DateTimeEn.month.ordinal()))
							&& ((targetList.getElement(i).hasInfo(DateTimeEn.date.ordinal())
									&& targetList.getElement(j).getDate() == targetList.getElement(i).getDate())
									|| !targetList.getElement(i).hasInfo(DateTimeEn.date.ordinal()))) {
						
						targetList.deleteList(i);
						i -= 1;
					}
				}
			}
			break;
		case days:
			// 있는 정보 중에는 모두 같은 거
			for(int j = 0; j < targetList.getList().size(); j++) {
				for(int i = j+1 ; i < targetList.getList().size() ; i++) {
					if(
							(
									(targetList.getElement(i).hasInfo(DateTimeEn.day.ordinal())
											&& targetList.getElement(j).getDay() == targetList.getElement(i).getDay())
							|| !targetList.getElement(i).hasInfo(DateTimeEn.year.ordinal()))
							
							&& ((targetList.getElement(i).hasInfo(DateTimeEn.month.ordinal())
									&& targetList.getElement(j).getMonth() == targetList.getElement(i).getMonth())
									|| !targetList.getElement(i).hasInfo(DateTimeEn.month.ordinal()))
							
							&& (
									(targetList.getElement(i).hasInfo(DateTimeEn.date.ordinal())
									&& targetList.getElement(j).getDate() == targetList.getElement(i).getDate())
									
								|| !targetList.getElement(i).hasInfo(DateTimeEn.date.ordinal()))) {
						
						targetList.deleteList(i);
						i -= 1;
					}
				}
			}
			break;
		case times:
			break;
		case special:
			break;
		default:
			break;
		}
		
	}

	// specialDate은 여기서 merge.
	void merge(DateTimeListDTO targetList, DateTimeListDTO secList) {
		// 빈 객체 하나 넣어주기
		targetList.insertDtObj(new DateTimeObject());

		boolean out = false;
		for (int i = 0; i < targetList.getList().size(); i++) {
			for (int j = 0; j < secList.getList().size(); j++) {

				MyLocalDateTime cal = new MyLocalDateTime();
				cal.setTimePoint(LocalDateTime.now());
				if (secList.getElement(j).getSpecialDate().equals("오늘")) {
					targetList.getElement(i).setAllDate(cal);
					out = true;
				} else if (secList.getElement(j).getSpecialDate().equals("내일")) {
					cal.plusDate(1);
					targetList.getElement(i).setAllDate(cal);
					out = true;
				} else if (secList.getElement(j).getSpecialDate().equals("모레")) {
					cal.plusDate(2);
					targetList.getElement(i).setAllDate(cal);
					out = true;
				}
				if (out) {
					targetList.insertDtObj(new DateTimeObject());
					break;
				}
			}
			if (out) {
				break;
			}
		}

		if (!out) {
			for (int i = 0; i < targetList.getList().size(); i++) {
				if (targetList.getList().size() > 1 && i == targetList.getList().size() - 1) {
					continue;
				}
				for (int j = 0; j < secList.getList().size(); j++) {
					DateTimeObject dtObj = new DateTimeObject();

					// dtObj 초기화 : secList로 세팅
					dtObj.setAllDate(secList.getElement(j));
					if (!dtObj.getSpecialDate().equals("-1")) {
						for (specialDateTypeNeedsDay specialDT : specialDateTypeNeedsDay.values()) {
							if (!dtObj.getSpecialDate().equals(specialDT.getTitle())) {
								continue;
							}
							// 이번주 : 0, 다음주 : 1, 다다음주 : 2
							// 이번주라고 무조건 1번째주인건 아님.
							LocalDate td = LocalDate.now();

							// LocalDateTime 에는 Week of Month 가 없어서...
							Calendar calendar = new GregorianCalendar();
							Date trialTime = new Date();
							calendar.setTime(trialTime);
							int wom = calendar.get(Calendar.WEEK_OF_MONTH);

							// 오늘이 수요일인데 이번주 화요일 입력하면 지났지만 나와야함. 그러니까 무턱대고 1주를 더하면 안 됨.
							if (targetList.getElement(i).getDay() != null) {
								td = td.with(TemporalAdjusters.dayOfWeekInMonth(specialDT.ordinal() + wom,
										targetList.getElement(i).getDay()));
							}else { // 요일 정보가 없는데 이번주, 다음주 등의 정보가 있을 때
								td = td.with(TemporalAdjusters.dayOfWeekInMonth(specialDT.ordinal() + wom, td.getDayOfWeek()));
							}
							targetList.getElement(i).setDate(td.getDayOfMonth());
							targetList.getElement(i).setMonth(td.getMonthValue());
							targetList.getElement(i).setYear(td.getYear());
							break;
						}
					}
					if (targetList.getElement(i).getDay() == null) {
						targetList.insertDtObj(new DateTimeObject());
						out = true;
						break;
					}
				}
				if (out) {
					break;
				}
			}
		}
		targetList.deleteList(targetList.getList().size() - 1);
	}

}
