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
		// datePatterns.add("^(.*)([0-9]{4})-(0?[1-9]|1[0-2])-([0-9]{1,2})((.*))$"); //
		// 2018-3-19
		// datePatterns.add("^(.*)([0-9]{4})/(0?[1-9]|1[0-2])/([0-9]{1,2})((.*))$"); //
		// 2018/3/19
		// datePatterns.add("^(.*)([0-9]{4})\\.(0?[1-9]|1[0-2])\\.([0-9]{1,2})((.*))$");
		// // 2018.3.19
		// datePatterns.add("^(.*)([0-9]{4})년 (0?[1-9]|1[0-2])월 ([0-9]{1,2})일((.*))$");
		// // 2018년 3월 19일
		datePatterns.add("^(.*)(1[0-2])월 ([1-9]|[1-2][0-9]|3[0-1])일((.*))$"); // 11월 19일
		datePatterns.add("^(|.*[^1])([1-9])월 ([1-9]|[1-2][0-9]|3[0-1])일((.*))$"); // 3월 19일
		datePatterns.add("^(.*)(1[0-2])-([1-9]|[1-2][0-9]|3[0-1])((.*))$"); // 11-19
		datePatterns.add("^(|.*[^1])([1-9])-([1-9]|[1-2][0-9]|3[0-1])((.*))$"); // 3-19
		datePatterns.add("^(.*)(0[1-9]|1[0-2])\\.([1-9]|[1-2][0-9]|3[0-1])((.*))$"); // 11.19
		datePatterns.add("^(|.*[^1])([1-9])\\.([1-9]|[1-2][0-9]|3[0-1])((.*))$"); // 3.19
		datePatterns.add("^(.*)(0[1-9]|1[0-2])/([1-9]|[1-2][0-9]|3[0-1])((.*))$"); // 11/19
		datePatterns.add("^(|.*[^1])([1-9])/([1-9]|[1-2][0-9]|3[0-1])((.*))$"); // 3/19
		// 20180319

		// 날짜 중 '일'만 있는 패턴
		List<String> DateOnlyPatterns = new ArrayList<String>();
		DateOnlyPatterns.add("^(.*)([1-9]|[1-2][0-9]|3[0-1])일((.*))$"); // 19일

		// 요일 패턴
		List<String> DaysPatterns = new ArrayList<String>();
		DaysPatterns.add("^(.*)(월|화|수|목|금|토|일)요일((.*))$"); // 월요일

		// 그 외 특이 패턴
		List<String> specialDatePatterns = new ArrayList<String>();
		specialDatePatterns.add("^(.*)(내일)((.*))$"); // 내일
		specialDatePatterns.add("^(.*)(오늘)((.*))$"); // 오늘
		specialDatePatterns.add("^(.*)(모레)((.*))$"); // 모레

		specialDatePatterns.add("^(.*)(이번주)((.*))$"); // 이번주
		specialDatePatterns.add("^(.*)(다음주)((.*))$"); // 다음주
		specialDatePatterns.add("^(.*)(다다음주)((.*))$"); // 다다음주

		specialDatePatterns.add("^(.*)(매일)((.*))$"); // 매일

		// 시간 패턴
		List<String> timePatterns = new ArrayList<String>();
		timePatterns.add("^(.*)(1[0-9]|2[0-3]):([0-9]{1,2})((.*))$"); // 12:00
		timePatterns.add("^(|.*[^1])([1-9]):([0-9]{1,2})((.*))$"); // 2:00
		timePatterns.add("^(.*)(0[0-9]|1[0-9]|2[0-3])시 ([0-9]{1,2})분((.*))$"); // 12시 30분
		timePatterns.add("^(|.*[^1])([1-9])시 ([0-9]{1,2})분((.*))$"); // 7시 30분
		timePatterns.add("^(.*)(0[0-9]|1[0-9]|2[0-3])시([^분]*)((.*))$"); // 12시
		timePatterns.add("^(|.*[^1])([1-9])시([^분]*)((.*))$"); // 7시

		// 상관없는 정보 얼마나 지나쳐야하는지 담는 변수
		int passingVals = 2;

		// 날짜 매칭
		Iterator<String> iter = datePatterns.iterator();
		while (iter.hasNext()) {
			String pattern = iter.next();
			Pattern inputPattern = Pattern.compile(pattern);
			Matcher matcher = inputPattern.matcher(inputEvent);
			//
			// if (pattern == null) {
			// logger.info("List : ");
			//
			// } else {
			// logger.info("List : " + pattern);
			//
			// }

			if (matcher.matches()) {
				logger.info("패턴 만족 : " + matcher.group(0));
				logger.info("x " + matcher.group(1) + matcher.group(2));
				// List<String> words = new ArrayList<String>();
				DateVO vo = new DateVO();

				for (int i = 0; i < matcher.groupCount() - passingVals - 1; i++) {
					// 0번째 꺼는 전체 스트링
					// 1번째 꺼는 상관없는 정보
					//
					// if(matcher.group(i + passingVals) == null) {
					// passedCnt++;
					// continue;
					// }

					vo.set(i + 1, matcher.group(i + passingVals));
					// words.add(matcher.group(i + passingVals));

					// logger.info("List (" + i + ") : " + words.get(i));
				}

				dateVos.insertVOs(vo);

			}
		}

		// 시간 매칭
		Iterator<String> timeIter = timePatterns.iterator();
		while (timeIter.hasNext()) {
			String curTimePattern = timeIter.next();
			Pattern inputTimePattern = Pattern.compile(curTimePattern);
			Matcher timeMatcher = inputTimePattern.matcher(inputEvent);

			if (curTimePattern == null) {
				logger.info("시간 List : ");

			} else {
				logger.info("시간 List : " + curTimePattern);

			}

			// 매칭된 패턴이 있을 시
			if (timeMatcher.matches()) {
				logger.info("시간 패턴 만족 : " + timeMatcher.group(0));
				// List<String> timewWords = new ArrayList<String>();
				DateVO vo = new DateVO();

				// 상관없는 정보 얼마나 지나쳐야하는지 담는 변수
				for (int i = 0; i < timeMatcher.groupCount() - passingVals - 1; i++) {
					// 0번째 꺼는 전체 스트링
					// 1번째 꺼는 상관없는 정보

					if (timeMatcher.group(i + passingVals) == null) {
						continue;
					}
					// 시간은 vo에서 4,5 인덱스에 들어가야함.
					vo.set(i + 4, timeMatcher.group(i + passingVals));

					if (i == 1 && !timeMatcher.group(i + passingVals).contains("분")) {
						vo.set(i + 4, "00");
					}

					// timewWords.add(timeMatcher.group(i + passingVals));

					// logger.info("List (" + i + ") : " + timewWords.get(i));
				}

				timeVos.insertVOs(vo);
			}

		}

		// 날짜, 시간 두개의 값이 없을 때도 크로스시켜야함.
		timeVos.insertVOs(new DateVO());
		dateVos.insertVOs(new DateVO());

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
						|| (Integer.parseInt(m) % 2 == 0 && Integer.parseInt(dt) > 30)) {

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
	
}
