package com.soonmark.myapp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

		int recomNum = 10; // 추천할 개수를 10개로 한정

		// 현재 시스템 날짜 // 여기서 수정하자.
		MyCalendar now = new MyCalendar();
		// now.plusHour(3);

		// 숫자만 저장, 날짜 전체 저장
		logger.info("시간값 : " + now);

		// 입력값 불러오기
		String inputEvent = httpServletRequest.getParameter("inputEventsss");

		logger.info("현재 시간값 : " + now);

		// 앞으로 추천할 날짜 리스트
		DateListVO dateVos = new DateListVO();

		// 앞으로 추천할 날짜 리스트
		DateListVO dayVos = new DateListVO();

		// 앞으로 추천할 시간 리스트
		DateListVO timeVos = new DateListVO();

		// 최종 리스트
		DateListVO vos = new DateListVO();

		// 한글, 숫자, 영어, 공백만 입력 가능
		if (!(Pattern.matches("^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\\s\\:\\-\\.\\/]*$", inputEvent))) {

			logger.error("Error : 입력 허용 패턴이 아님");

			DateVO vo = new DateVO();
			// -2는 잘못된 기호나 문자 입력 시 에러 코드
			vo.setYear("-2");
			dateVos.insertVOs(vo);

			logger.info("JSON 값  : " + dateVos.toJsonString());
			return dateVos.toJsonString();
		}

		logger.info("입력받은 일정 : " + inputEvent);

		// 2018-03-19 | 2018/03/19 | 2018.03.19 형태인지 검사

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


		// 날짜 매칭
		Iterator<String> iter = datePatterns.iterator();
		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputEvent);

			if (matcher.matches()) {
				logger.info("날짜 패턴 : " + pattern);
				logger.info("패턴 만족 : " + matcher.group(0));

				DateVO vo = new DateVO();

				// month와 date 에 해당하는 group 만 따로 읽어 저장
				try {
					vo.setMonth(matcher.group("month"));
				} catch (IllegalArgumentException e) {
					vo.setMonth("-1");
				}
				try {
					vo.setDate(matcher.group("date"));
				} catch (IllegalArgumentException e) {
					vo.setDate("-1");
				}
				dateVos.insertVOs(vo);
			}
		}
		
		// 요일 매칭
		Iterator<String> dayIter = daysPatterns.iterator();
		while (dayIter.hasNext()) {
			String pattern = dayIter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputEvent);

			if (matcher.matches()) {
				logger.info("날짜 패턴 : " + pattern);
				logger.info("패턴 만족 : " + matcher.group(0));

				DateVO vo = new DateVO();

				// month와 date 에 해당하는 group 만 따로 읽어 저장
				try {
					vo.setDay(matcher.group("day"));
				} catch (IllegalArgumentException e) {
					vo.setDay("-1");
				}
				dayVos.insertVOs(vo);
			}
		}

		// 시간 매칭
		Iterator<String> timeIter = timePatterns.iterator();
		while (timeIter.hasNext()) {
			String pattern = timeIter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputEvent);

			// 매칭된 패턴이 있을 시
			if (matcher.matches()) {
				logger.info("시간 패턴 : " + pattern);
				logger.info("시간 패턴 만족 : " + matcher.group(0));
				// List<String> timewWords = new ArrayList<String>();
				DateVO vo = new DateVO();

				// hour와 minute에 해당하는 group 만 따로 읽어 저장
				vo.setHour(matcher.group("hour"));
				try {
					// 시간 중에 group 명이 minute 이 없는 경우 0으로 세팅
					vo.setMinute(matcher.group("minute"));

				} catch (IllegalArgumentException e) {
					vo.setMinute("0");
				}

				timeVos.insertVOs(vo);
			}

		}

		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야 하므로 빈 객체 삽입.
		timeVos.insertVOs(new DateVO());
		dateVos.insertVOs(new DateVO());
		// 요일도 날짜와 크로스시켜야하므로 빈 객체 삽입.
		dayVos.insertVOs(new DateVO());
		
		// 우선, 요일과 날짜 크로스
		// 날짜가 있고 요일이 없는 경우나 - clear
		// 요일이 있는데 날짜가 없는 경우
		// 요일과 날짜가 있지만 서로 안 맞는 경우
		// 요일과 날짜가 있고 둘이 맞는 경우
		// 위 4가지 경우에 대해 코딩.
		for(int i = 0 ; i < dateVos.getVos().size() ; i++) {
			for(int j = 0 ; j < dayVos.getVos().size() ; j++) {
				// 요일 정보 없으면 그냥 나가기
				// 요일 정보가 있을 때는 요일 빈 객체 스킵
				// 날짜 정보가 있을 때는 날짜 빈 객체 스킵
				// 날짜 없고 요일있는건 처리해야하니까 if문 처리 안 함.
				if(dayVos.getVos().size() == 1
					|| (dateVos.getVos().size() > 1 && i == dateVos.getVos().size() - 1)
					|| (dayVos.getVos().size() > 1 && j == dayVos.getVos().size() - 1)) {
					continue;
				}
				
				DateVO vo = new DateVO();
				
				// 요일 세팅
				vo.setDay(dayVos.getElement(j).getDay());
				// 날짜 세팅
				vo.setYear(dateVos.getElement(i).getYear());
				vo.setMonth(dateVos.getElement(i).getMonth());
				vo.setDate(dateVos.getElement(i).getDate());
				
				logger.info("날/요일 변경 전 : " + dateVos.getElement(i).getDay());
				dateVos.getElement(i).setDay(vo.getDay());
				
				logger.info("날/요일 변경 후 : " + dateVos.getElement(i).getDay());
			}
		}
		
		
		
		
		
		
		

		for (int i = 0; i < timeVos.getVos().size(); i++) {
			for (int j = 0; j < dateVos.getVos().size(); j++) {

				// 둘다 정보가 들어왔으면 빈값 매칭 안 해줘도 됨.
				// 시간만 있을 때는 -> 날짜 빈거랑 매칭하고 시간 여분 빼기
				// 날짜만 있을 때는 -> 시간 빈거랑 매칭하고 날짜 여분 빼기
				// 둘 다 비어있을 때도 안 해줘도 됨.
				if ((timeVos.getVos().size() > 1 && dateVos.getVos().size() > 1
						&& (i == timeVos.getVos().size() - 1 || j == dateVos.getVos().size() - 1))
						|| ((timeVos.getVos().size() > 1 && dateVos.getVos().size() == 1)
								&& (i == timeVos.getVos().size() - 1))
						|| ((timeVos.getVos().size() == 1 && dateVos.getVos().size() > 1)
								&& (j == dateVos.getVos().size() - 1))
						|| (timeVos.getVos().size() == 1 && dateVos.getVos().size() == 1)) {
					continue;
				}

				logger.info("여1");

				String y = dateVos.getElement(j).getYear();
				String m = dateVos.getElement(j).getMonth();
				String dt = dateVos.getElement(j).getDate();
				String day = dateVos.getElement(j).getDay();

				String h = timeVos.getElement(i).getHour();
				String min = timeVos.getElement(i).getMinute();

				// 월간 일수 차이에 대한 예외처리
				if ((Integer.parseInt(m) == 2 && Integer.parseInt(dt) > 28)
						|| (Integer.parseInt(m) < 8 && Integer.parseInt(m) % 2 == 0 && Integer.parseInt(dt) > 30)
						|| (Integer.parseInt(m) > 7 && Integer.parseInt(m) % 2 == 1 && Integer.parseInt(dt) > 30)) {

					break;
				}

				// 날짜 정보가 없으면 현재 시스템 날짜를 넣어서 '오늘' 일정으로 만들기
				if (dateVos.getVos().size() == 1) {
					logger.info("여2");

					// 현재 시각과 비교해서 이미 지난 시간일 경우 + 12;
					MyCalendar tmpCal = new MyCalendar();

					// 메소드의 객체가 now 캘린더가 아니면 true 입력
					tmpCal.setHour(Integer.parseInt(h), true);
					if (min == "-1") {
						tmpCal.setMinute(0);
					} else {
						tmpCal.setMinute(Integer.parseInt(min));
					}

					// for 문 돌면서 비교할 기준 시 설정
					MyCalendar comparedCal = new MyCalendar();
					comparedCal.setTimePoint(now.getTimePoint());

					for (int k = 0; k < recomNum; k++) {
						DateVO vo = new DateVO();
						tmpCal.setCloseDate(comparedCal);
						comparedCal.setTimePoint(tmpCal.getTimePoint());

						vo.setHour(tmpCal.getHour());

						// 현재 시스템 날짜
						vo.setYear(tmpCal.getYear());
						vo.setMonth(tmpCal.getMonth());
						vo.setDate(tmpCal.getDate());
						vo.setDay(tmpCal.getDay());
						vo.setMinute(tmpCal.getMinute());

						vos.insertVOs(vo);
					}
				}

				else { // 날짜 정보 있으면 (시간은 있든 말든 상관없음.)
					for (int k = 0; k < recomNum; k++) {
						DateVO vo = new DateVO();
						logger.info("여3");
						vo.setYear(y);
						vo.setMonth(m);
						vo.setDate(dt);
						vo.setDay(day);

						if (y == "-1") {
							vo.setYear(now.getYear());
						}
						if (m == "-1") {
							vo.setMonth(now.getMonth());
						}
						if (dt == "-1") {
							vo.setDate(now.getDate());
						}
						if (day == "-1") {

							// 날짜에 맞는 요일 구하는 로직
							MyCalendar tmpCal = new MyCalendar();
							tmpCal.setYear(Integer.parseInt(vo.getYear()));
							tmpCal.setMonth(Integer.parseInt(vo.getMonth()));
							tmpCal.setDate(Integer.parseInt(vo.getDate()));

							vo.setDay(tmpCal.getDay());
						}

						// 시간정보 없을 땐, 종일 로 나타내기
						if (timeVos.getVos().size() == 1) {
							vo.setHour("종일");

						} else {
							vo.setHour(h);
						}
						if (k == 0) {
							vo.setYear("매년");
							vo.setDay("-1");
						} else {
							vo.setYear((Integer.parseInt(vo.getYear()) + k - 1) + "");

							// 날짜에 맞는 요일 구하는 로직
							MyCalendar tmpCal = new MyCalendar();
							tmpCal.setYear(Integer.parseInt(vo.getYear()));
							tmpCal.setMonth(Integer.parseInt(vo.getMonth()));
							tmpCal.setDate(Integer.parseInt(vo.getDate()));

							vo.setDay(tmpCal.getDay());
						}
						vo.setMinute(min);

						vos.insertVOs(vo);
					}
				}

			}
		}

		return vos.toJsonString();
	}

	void initPatterns(List<String> datePatterns, List<String> daysPatterns,
			List<String> specialDatePatterns, List<String> timePatterns) {
		
		// datePatterns.add("^(.*)([0-9]{4})-(0?[1-9]|1[0-2])-([0-9]{1,2})((.*))$"); //
		// 2018-3-19
		// datePatterns.add("^(.*)([0-9]{4})/(0?[1-9]|1[0-2])/([0-9]{1,2})((.*))$"); //
		// 2018/3/19
		// datePatterns.add("^(.*)([0-9]{4})\\.(0?[1-9]|1[0-2])\\.([0-9]{1,2})((.*))$");
		// // 2018.3.19
		// datePatterns.add("^(.*)([0-9]{4})년 (0?[1-9]|1[0-2])월 ([0-9]{1,2})일((.*))$");
		// // 2018년 3월 19일
		datePatterns.add("^(.*)(?<month>1[0-2])월 (?<date>[1-9]|[1-2][0-9]|3[0-1])일(.*)$"); // 11월 19일
		datePatterns.add("^(|.*[^1])(?<month>[1-9])월 (?<date>[1-9]|[1-2][0-9]|3[0-1])일(.*)$"); // 3월 19일
		datePatterns.add("^(.*)(?<month>1[0-2])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11-19
		datePatterns.add("^(|.*[^1])(?<month>[1-9])-(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3-19
		datePatterns.add("^(.*)(?<month>1[0-2])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11.19
		datePatterns.add("^(|.*[^1])(?<month>[1-9])\\.(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3.19
		datePatterns.add("^(.*)(?<month>1[0-2])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 11/19
		datePatterns.add("^(|.*[^1])(?<month>[1-9])/(?<date>[1-9]|[1-2][0-9]|3[0-1])(|[^0-9].*)$"); // 3/19
		// 일만 입력받기
		datePatterns.add("^([^월]*)(?<date>[1-2][0-9]|3[0-1])일(.*)$"); // 19일
		datePatterns.add("^(|[^월]*[^1-3])(?<date>[1-9])일(.*)$"); // 1일
		// 일만 입력받기
		datePatterns.add("^(.*)(?<month>1[0-2])월([^일]*)$"); // 12월
		datePatterns.add("^(|.*[^1])(?<month>[1-9])월([^일]*)$"); // 1월
		// 20180319

		// 요일 패턴
		daysPatterns.add("^(.*)(?<day>월|화|수|목|금|토|일)요일(.*)$"); // 월요일

		// 그 외 특이 패턴
		specialDatePatterns.add("^(.*)(내일)(.*)$"); // 내일
		specialDatePatterns.add("^(.*)(오늘)(.*)$"); // 오늘
		specialDatePatterns.add("^(.*)(모레)(.*)$"); // 모레

		specialDatePatterns.add("^(.*)(이번주)(.*)$"); // 이번주
		specialDatePatterns.add("^(.*)(다음주)(.*)$"); // 다음주
		specialDatePatterns.add("^(.*)(다다음주)(.*)$"); // 다다음주

		specialDatePatterns.add("^(.*)(매일)(.*)$"); // 매일

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

}
