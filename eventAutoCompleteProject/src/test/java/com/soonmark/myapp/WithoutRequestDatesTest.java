package com.soonmark.myapp;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;

import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.service.RecommendationService;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

// Parameterized.class 를 쓰는 이유는 가독성을 높이기 위함이고,
// 불필요한 객체 생성을 막기 위함.
// ( before에서 일일이 추가하는 방식을 사용하면, Object[] 로 처리했던 input, List 를 클래스로 정의한 뒤 사용해야함. )
// -> 내가 잘 몰라서 그러는 거일수도..
// 파라미터마다 이름도 붙일 수 있어서 편리함.
@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class WithoutRequestDatesTest {
	private TestContextManager testContextManager;

	public WithoutRequestDatesTest(String input, List<EventDTO> expectedList) {
		super();
		this.expectedList = expectedList;
		this.input = input;
	}

	private List<EventDTO> expectedList;
	private String input;

	@Autowired
	RecommendationService recommendationService;

	@Before
	public void setup() throws Exception {
		this.testContextManager = new TestContextManager(getClass());
		this.testContextManager.prepareTestInstance(this);
	}

	//
	// Given
	//
	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> testCases() {
		Collection<Object[]> params = new ArrayList<Object[]>();

		// 년 월 일
		
		params.add(new Object[] {"15년 4월 9일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2015-04-09", ""), null, "2015/04/09 (목)")) });
		
		params.add(new Object[] { "2018-03-19",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-03-19", ""), null, "2018/03/19 (월)")) });

		params.add(new Object[] { "2018-3-19",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-03-19", ""), null, "2018/03/19 (월)")) });

		params.add(new Object[] { "20-10-1",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2020-10-01", ""), null, "2020/10/01 (목)")) });
		
		params.add(new Object[] { "1999/01/01",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("1999-01-01", ""), null, "1999/01/01 (금)")) });
		                                      
		params.add(new Object[] { "11/3/19",                                              
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2011-03-19", ""), null, "2011/03/19 (토)")) });
		                                                                                  
		params.add(new Object[] { "11/3/1",                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2011-03-01", ""), null, "2011/03/01 (화)")) });
		                                                                                  
		params.add(new Object[] { "2025.2.2",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2025-02-02", ""), null, "2025/02/02 (일)")) });
		                                                                                  
		params.add(new Object[] { "00.12.10",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2000-12-10", ""), null, "2000/12/10 (일)")) });
                                                                                          
		params.add(new Object[] { "10.10.09",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2010-10-09", ""), null, "2010/10/09 (토)")) });

		// 년 월
		params.add(new Object[] { "벚꽃달인 18년 4월 계획짜기", new ArrayList<Object>() });

		params.add(new Object[] { "내 생일 94년 6월 중", new ArrayList<Object>() });

		// 년 일
		params.add(new Object[] { "2000년 21일에 여행갔었음.",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2000-01-21", ""), null, "2000/01/21 (금)"),
						new EventDTO(new StringDateTimeDTO("2000-02-21", ""), null, "2000/02/21 (월)")) });
                                                                                          
		params.add(new Object[] { "2018년 4일에 여행갔었음.",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-04", ""), null, "2018/06/04 (월)"),
						new EventDTO(new StringDateTimeDTO("2018-07-04", ""), null, "2018/07/04 (수)")) });
                                                                                          
		// 월 일                                                                            
		params.add(new Object[] { "4-9",                                                  
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", ""), null, "2019/04/09 (화)")) });
		                
		params.add(new Object[] { "1/1 신년행사",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-01-01", ""), null, "2019/01/01 (화)")) });
                                                                                          
		// 년                                                                              
		params.add(new Object[] { "15년", new ArrayList<Object>() });
                                                                                          
		params.add(new Object[] { "07년에 중학교 졸업", new ArrayList<Object>() });
                                                                                          
		// 월                                                                              
		params.add(new Object[] { "2월에 졸업식", new ArrayList<Object>() });
		                                                                                  
		params.add(new Object[] { "5월에 꽃구경", new ArrayList<Object>() });

		params.add(new Object[] { "겨울 12월에는 빙어낚시", new ArrayList<Object>() });

		// 일                                                                              
		params.add(new Object[] {"1일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-01", ""), null, "2018/06/01 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-07-01", ""), null, "2018/07/01 (일)")) });
		
		params.add(new Object[] { "9일",                                                   
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-09", ""), null, "2018/06/09 (토)"),
						new EventDTO(new StringDateTimeDTO("2018-07-09", ""), null, "2018/07/09 (월)")) });
                                                                                          
		params.add(new Object[] { "17일 축구동호회",                                            
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018/05/17 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-06-17", ""), null, "2018/06/17 (일)")) });

		params.add(new Object[] { "19일날 가족 외식",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-19", ""), null, "2018/05/19 (토)"),
						new EventDTO(new StringDateTimeDTO("2018-06-19", ""), null, "2018/06/19 (화)")) });


		// 일 - 4월 31일, 2월 30일 등 범위 이탈에 대한 처리
		params.add(new Object[] { "가족모임 29일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-29", ""), null, "2018/05/29 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-06-29", ""), null, "2018/06/29 (금)")) });
        
		params.add(new Object[] { "친구보기 30일에",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", ""), null, "2018/05/30 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-06-30", ""), null, "2018/06/30 (토)")) });
		                                                                                                                     
		params.add(new Object[] { "31일에 불꽃놀이",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-31", ""), null, "2018/05/31 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-07-31", ""), null, "2018/07/31 (화)")) });
		
		// 2월 30일과 같이 일자가 부분 범위 이탈
		params.add(new Object[] { "2월 30일",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", ""), null, "2018/05/30 (수)")) });

		params.add(new Object[] { "2월 30일 1시",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 01:-"), null, "2018/05/30 (수) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 01:-"), null, "2018/05/30 (수) 오후 1시")) });

		// 5월 32일과 같이 일자가 완전 범위 이탈 - 스펙 논의
		params.add(new Object[] { "2월 32일", new ArrayList<Object>() });
		
		params.add(new Object[] { "5/32", new ArrayList<Object>() });
		                                                                                                                     
                                                                                                                             
		// 월 시                                                                                                               
		params.add(new Object[] { "11월 1시",                                                                                  
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-11-01", "오전 01:00"), null, "2018/11/01 (목) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-11-01", "오후 01:00"), null, "2018/11/01 (목) 오후 1시"),
						new EventDTO(new StringDateTimeDTO("2018-11-02", "오전 01:00"), null, "2018/11/02 (금) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-11-02", "오후 01:00"), null, "2018/11/02 (금) 오후 1시")) });
                                                                                                                             
		params.add(new Object[] { "5월 1시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 01:00"), null, "2018/05/17 (수) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 01:00"), null, "2018/05/17 (목) 오후 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 01:00"), null, "2018/05/18 (금) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오후 01:00"), null, "2018/05/18 (토) 오후 1시")) });
                                                                                                                             
		params.add(new Object[] { "5월 13시 30분",                                                                              
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 01:30"), null, "2018/05/17 (목) 오후 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오후 01:30"), null, "2018/05/18 (금) 오후 1시 30분")) });

		// 월 일 시간                                                                                                            
		params.add(new Object[] { "4월 3일에 친구 모임 1시 서현",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-03", "오전 01:-"), null, "2019/04/03 (수) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2019-04-03", "오후 01:-"), null, "2019/04/03 (수) 오후 1시")) });
                                                                                                                             
		params.add(new Object[] { "3/5에 친구 모임 11시 20분 야탑",                                    
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-03-05", "오전 11:20"), null, "2019/03/05 (화) 오전 11시 20분"),
						new EventDTO(new StringDateTimeDTO("2019-03-05", "오후 11:20"), null, "2019/03/05 (화) 오후 11시 20분")) });
		                                                                                                                     
		params.add(new Object[] { "4.9 12시 30분",                                                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", "오전 12:30"), null, "2019/04/09 (화) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2019-04-09", "오후 12:30"), null, "2019/04/09 (화) 오후 12시 30분")) });
		                                                                                                                     
		                                                                                                                     
		// 월 일 요일 시간                                                                                                         
		params.add(new Object[] { "4월 9일 월요일 19시",                                                                           
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", "오후 07:-"), null, "2019/04/09 (화) 오후 7시"))});
                                                                                                                             
		params.add(new Object[] { "4.18 월요일 10:10",                                                                          
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-18", "오전 10:10"), null, "2019/04/18 (목) 오전 10시 10분"),
						new EventDTO(new StringDateTimeDTO("2019-04-18", "오후 10:10"), null, "2019/04/18 (목) 오후 10시 10분")) });

		params.add(new Object[] { "4/9 화요일 7:00",                                                                            
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", "오전 07:00"), null, "2019/04/09 (화) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2019-04-09", "오후 07:00"), null, "2019/04/09 (화) 오후 7시")) });
		                                                                                                                     
		// 월 일 요일                                                                                                            
		params.add(new Object[] { "4/16 화요일",                                                                                
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-16", ""), null, "2019/04/16 (화)")) });
		                                                                                                                     
		params.add(new Object[] { "4-1 화요일",                                                                                 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-01", ""), null, "2019/04/01 (월)")) });
		                                                                                                                     
		params.add(new Object[] { "4-1 일요일",                                                                                 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-01", ""), null, "2019/04/01 (월)")) });

		// 다중 날짜
//		params.add(new Object[] { "25일에 4.3 추모 행사 참여",
//				Arrays.asList(new DateTimeDTO(tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getYear(),
//						tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getMonthValue(),
//						tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getDayOfMonth(),
//						DayOfWeek.MONDAY, -1, -1, true),
//						new DateTimeDTO(tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getYear(),
//								tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getMonthValue(),
//								tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getDayOfMonth(),
//								DayOfWeek.FRIDAY, -1, -1, true)) });

		
// 긴 텍스트 고려 x
//		// 다중 요일
//		params.add(new Object[] { "월요일날 금요일에 만나요 콘서트",
//				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-25", ""), null, "2018-05-25 (금)"),
//						new EventDTO(new StringDateTimeDTO("2018-05-28", ""), null, "2018-05-28 (월)")) });

		// 요일
		params.add(new Object[] { "금요일에 약속",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-18", ""), null, "2018/05/18 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", ""), null, "2018/05/25 (금)")) });

		// 특수날짜(내일)
		params.add(new Object[] { "내일 저녁에 회의",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018/05/17 (목)")) });
		
		// 특수날짜(오늘) 시 분
		params.add(new Object[] { "오늘 12시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 12:-"), null, "2018/05/16 (수) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 12:-"), null, "2018/05/16 (수) 오후 12시")) });

		params.add(new Object[] { "오늘 6시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:-"), null, "2018/05/16 (수) 오후 6시")) });

		params.add(new Object[] { "오늘 12시 30분에 음원차트 확인",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 12:30"), null, "2018/05/16 (수) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 12:30"), null, "2018/05/16 (수) 오후 12시 30분")) });
		
		params.add(new Object[] { "오늘 8시 30분에 음원차트 확인",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 08:30"), null, "2018/05/16 (수) 오후 8시 30분")) });
		
		// 특수날짜(이번주) 일
		params.add(new Object[] { "이번주 17일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018/05/17 (목)")) });

		params.add(new Object[] { "이번주 11일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", ""), null, "2018/05/16 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018/05/17 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", ""), null, "2018/05/18 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", ""), null, "2018/05/19 (토)")) });

		params.add(new Object[] { "다음주 16일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-20", ""), null, "2018/05/20 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", ""), null, "2018/05/21 (월)"),
						new EventDTO(new StringDateTimeDTO("2018-05-22", ""), null, "2018/05/22 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", ""), null, "2018/05/23 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", ""), null, "2018/05/24 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", ""), null, "2018/05/25 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", ""), null, "2018/05/26 (토)")) });
		
		// 특수날짜(이번주) 시간
		params.add(new Object[] { "이번주 12시 30분",
				Arrays.asList(/*new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 12:30"), null, "2018-05-23 (일) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 12:30"), null, "2018-05-23 (일) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-07", "오전 12:30"), null, "2018-05-07 (월) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-07", "오후 12:30"), null, "2018-05-07 (월) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-08", "오전 12:30"), null, "2018-05-08 (화) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-08", "오후 12:30"), null, "2018-05-08 (화) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 12:30"), null, "2018-05-09 (수) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 12:30"), null, "2018-05-16 (수) 오후 12:30"),*/
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 12:30"), null, "2018/05/17 (목) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 12:30"), null, "2018/05/17 (목) 오후 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 12:30"), null, "2018/05/18 (금) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오후 12:30"), null, "2018/05/18 (금) 오후 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 12:30"), null, "2018/05/19 (토) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오후 12:30"), null, "2018/05/19 (토) 오후 12시 30분")) });

		// 특수날짜(이번주) 날짜 시간
		params.add(new Object[] { "이번주 16일 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 12:30"), null, "2018/05/16 (수) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 12:30"), null, "2018/05/16 (수) 오후 12시 30분")) });
		
		// 특수날짜(이번주) 요일 시간
		params.add(new Object[] { "이번주 화요일 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-15", "오전 12:30"), null, "2018/05/15 (화) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-15", "오후 12:30"), null, "2018/05/15 (화) 오후 12시 30분")) });
		
		params.add(new Object[] { "이번주 목요일 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 12:30"), null, "2018/05/17 (목) 오전 12시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 12:30"), null, "2018/05/17 (목) 오후 12시 30분")) });

		params.add(new Object[] { "이번주 화요",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-15", ""), null, "2018/05/15 (화)")) });
		
		params.add(new Object[] { "다음주 화요",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-22", ""), null, "2018/05/22 (화)")) });
		
		params.add(new Object[] { "목요",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018/05/17 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", ""), null, "2018/05/24 (목)")) });

// 고려 x
//		// 특수날짜(이번주) 날짜 요일 시간
//		params.add(new Object[] { "이번주 3일 수요일 1시 30분",
//				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:30"), null, "2018-05-16 (수) 오후 01:30"),
//						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:30"), null, "2018-05-16 (수) 오후 01:30"))});

		// 특수날짜(이번주) 요일
		params.add(new Object[] { "이번주 영화보기 금요일 조조",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-18", ""), null, "2018/05/18 (금)")) });

// 고려 x
//		// 특수날짜(이번주) + 특수날짜(오늘)
//		params.add(new Object[] { "다음주 오늘 영화보기 조조",
//				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", ""), null, "2018-05-16 (수)")) });
		
		// 특수날짜(다음주)
		params.add(new Object[] { "약속 하나 있다 다음주",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-20", ""), null, "2018/05/20 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", ""), null, "2018/05/21 (월)"),
						new EventDTO(new StringDateTimeDTO("2018-05-22", ""), null, "2018/05/22 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", ""), null, "2018/05/23 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", ""), null, "2018/05/24 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", ""), null, "2018/05/25 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", ""), null, "2018/05/26 (토)")) });
		
		// 특수날짜(이번주)
		params.add(new Object[] { "아무 약속도 없는 이번주",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", ""), null, "2018/05/16 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018/05/17 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", ""), null, "2018/05/18 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", ""), null, "2018/05/19 (토)")) });
		

		
		
		// 특수날짜(다다음주)
		params.add(new Object[] { "친구 놀러옴 다다음주에",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-27", ""), null, "2018/05/27 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-05-28", ""), null, "2018/05/28 (월)"),
						new EventDTO(new StringDateTimeDTO("2018-05-29", ""), null, "2018/05/29 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", ""), null, "2018/05/30 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-31", ""), null, "2018/05/31 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-06-01", ""), null, "2018/06/01 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-06-02", ""), null, "2018/06/02 (토)")) });
				
		
		// 오전 오후  없는 시간
		params.add(new Object[] { "9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 09:-"), null, "2018/05/16 (수) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 09:-"), null, "2018/05/17 (목) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 09:-"), null, "2018/05/17 (목) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 09:-"), null, "2018/05/18 (금) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오후 09:-"), null, "2018/05/18 (금) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 09:-"), null, "2018/05/19 (토) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오후 09:-"), null, "2018/05/19 (토) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오전 09:-"), null, "2018/05/20 (일) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오후 09:-"), null, "2018/05/20 (일) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오전 09:-"), null, "2018/05/21 (월) 오전 9시")	) });
		
		params.add(	new Object[] { "1시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 01:30"), null, "2018/05/17 (목) 오전 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 01:30"), null, "2018/05/17 (목) 오후 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 01:30"), null, "2018/05/18 (금) 오전 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오후 01:30"), null, "2018/05/18 (금) 오후 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 01:30"), null, "2018/05/19 (토) 오전 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오후 01:30"), null, "2018/05/19 (토) 오후 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오전 01:30"), null, "2018/05/20 (일) 오전 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오후 01:30"), null, "2018/05/20 (일) 오후 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오전 01:30"), null, "2018/05/21 (월) 오전 1시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오후 01:30"), null, "2018/05/21 (월) 오후 1시 30분")) });
		
		// 오전 오후 있는 시간
		params.add(new Object[] { "오전 9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 09:-"), null, "2018/05/17 (목) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 09:-"), null, "2018/05/18 (금) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 09:-"), null, "2018/05/19 (토) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오전 09:-"), null, "2018/05/20 (일) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오전 09:-"), null, "2018/05/21 (월) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-22", "오전 09:-"), null, "2018/05/22 (화) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 09:-"), null, "2018/05/23 (수) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", "오전 09:-"), null, "2018/05/24 (목) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", "오전 09:-"), null, "2018/05/25 (금) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 09:-"), null, "2018/05/26 (토) 오전 9시")) });
		
		params.add(new Object[] { "am 12시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 12:-"), null, "2018/05/17 (목) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 12:-"), null, "2018/05/18 (금) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 12:-"), null, "2018/05/19 (토) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오전 12:-"), null, "2018/05/20 (일) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오전 12:-"), null, "2018/05/21 (월) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-22", "오전 12:-"), null, "2018/05/22 (화) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 12:-"), null, "2018/05/23 (수) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", "오전 12:-"), null, "2018/05/24 (목) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", "오전 12:-"), null, "2018/05/25 (금) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 12:-"), null, "2018/05/26 (토) 오전 12시")) });
		params.add(new Object[] { "AM 7시", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 07:-"), null, "2018/05/17 (목) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 07:-"), null, "2018/05/18 (금) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 07:-"), null, "2018/05/19 (토) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오전 07:-"), null, "2018/05/20 (일) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오전 07:-"), null, "2018/05/21 (월) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-22", "오전 07:-"), null, "2018/05/22 (화) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 07:-"), null, "2018/05/23 (수) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", "오전 07:-"), null, "2018/05/24 (목) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", "오전 07:-"), null, "2018/05/25 (금) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 07:-"), null, "2018/05/26 (토) 오전 7시")) });
		
		params.add(new Object[] { "A.M. 6시 1분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 06:01"), null, "2018/05/17 (목) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 06:01"), null, "2018/05/18 (금) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 06:01"), null, "2018/05/19 (토) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오전 06:01"), null, "2018/05/20 (일) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오전 06:01"), null, "2018/05/21 (월) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-22", "오전 06:01"), null, "2018/05/22 (화) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 06:01"), null, "2018/05/23 (수) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", "오전 06:01"), null, "2018/05/24 (목) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", "오전 06:01"), null, "2018/05/25 (금) 오전 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 06:01"), null, "2018/05/26 (토) 오전 6시 1분")) });
		
		params.add(new Object[] { "19:01", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 07:01"), null, "2018/05/16 (수) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 07:01"), null, "2018/05/17 (목) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오후 07:01"), null, "2018/05/18 (금) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오후 07:01"), null, "2018/05/19 (토) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오후 07:01"), null, "2018/05/20 (일) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오후 07:01"), null, "2018/05/21 (월) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-22", "오후 07:01"), null, "2018/05/22 (화) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 07:01"), null, "2018/05/23 (수) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-24", "오후 07:01"), null, "2018/05/24 (목) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-25", "오후 07:01"), null, "2018/05/25 (금) 오후 7시 1분")) });
		
		
		// 오전/오후
		params.add(new Object[] { "오늘 오전",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 12:00"), null, "2018/05/16 (수) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 01:00"), null, "2018/05/16 (수) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 02:00"), null, "2018/05/16 (수) 오전 2시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 03:00"), null, "2018/05/16 (수) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 04:00"), null, "2018/05/16 (수) 오전 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 05:00"), null, "2018/05/16 (수) 오전 5시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 06:00"), null, "2018/05/16 (수) 오전 6시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 07:00"), null, "2018/05/16 (수) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 08:00"), null, "2018/05/16 (수) 오전 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오전 09:00"), null, "2018/05/16 (수) 오전 9시")) });

		params.add(new Object[] { "오전",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 12:00"), null, "2018/05/17 (목) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 01:00"), null, "2018/05/17 (목) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 02:00"), null, "2018/05/17 (목) 오전 2시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 03:00"), null, "2018/05/17 (목) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 04:00"), null, "2018/05/17 (목) 오전 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 05:00"), null, "2018/05/17 (목) 오전 5시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 06:00"), null, "2018/05/17 (목) 오전 6시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 07:00"), null, "2018/05/17 (목) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 08:00"), null, "2018/05/17 (목) 오전 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 09:00"), null, "2018/05/17 (목) 오전 9시")) });
		
		params.add(new Object[] { "am",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 12:00"), null, "2018/05/17 (목) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 01:00"), null, "2018/05/17 (목) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 02:00"), null, "2018/05/17 (목) 오전 2시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 03:00"), null, "2018/05/17 (목) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 04:00"), null, "2018/05/17 (목) 오전 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 05:00"), null, "2018/05/17 (목) 오전 5시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 06:00"), null, "2018/05/17 (목) 오전 6시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 07:00"), null, "2018/05/17 (목) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 08:00"), null, "2018/05/17 (목) 오전 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 09:00"), null, "2018/05/17 (목) 오전 9시")) });
		
		params.add(new Object[] { "오후",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 04:00"), null, "2018/05/16 (수) 오후 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:00"), null, "2018/05/16 (수) 오후 5시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:00"), null, "2018/05/16 (수) 오후 6시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 07:00"), null, "2018/05/16 (수) 오후 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 08:00"), null, "2018/05/16 (수) 오후 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 09:00"), null, "2018/05/16 (수) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 10:00"), null, "2018/05/16 (수) 오후 10시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 11:00"), null, "2018/05/16 (수) 오후 11시")) });
		
		params.add(new Object[] { "3일 오후",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 12:00"), null, "2018/06/03 (일) 오후 12시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 01:00"), null, "2018/06/03 (일) 오후 1시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 02:00"), null, "2018/06/03 (일) 오후 2시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 03:00"), null, "2018/06/03 (일) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 04:00"), null, "2018/06/03 (일) 오후 4시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 05:00"), null, "2018/06/03 (일) 오후 5시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 06:00"), null, "2018/06/03 (일) 오후 6시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 07:00"), null, "2018/06/03 (일) 오후 7시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 08:00"), null, "2018/06/03 (일) 오후 8시"),
						new EventDTO(new StringDateTimeDTO("2018-06-03", "오후 09:00"), null, "2018/06/03 (일) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 12:00"), null, "2018/07/03 (화) 오후 12시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 01:00"), null, "2018/07/03 (화) 오후 1시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 02:00"), null, "2018/07/03 (화) 오후 2시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 03:00"), null, "2018/07/03 (화) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 04:00"), null, "2018/07/03 (화) 오후 4시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 05:00"), null, "2018/07/03 (화) 오후 5시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 06:00"), null, "2018/07/03 (화) 오후 6시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 07:00"), null, "2018/07/03 (화) 오후 7시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 08:00"), null, "2018/07/03 (화) 오후 8시"),
						new EventDTO(new StringDateTimeDTO("2018-07-03", "오후 09:00"), null, "2018/07/03 (화) 오후 9시")) });

		params.add(new Object[] { "2023/1",
				new ArrayList<Object>() });
		
		params.add(new Object[] { "2013/1",
				new ArrayList<Object>() });
		
		params.add(new Object[] { "6/3",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-03", ""), null, "2018/06/03 (일)"))});
		
		params.add(new Object[] { "6/32",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-03", ""), null, "2018/06/03 (일)"))});
		
		params.add(new Object[] { "6/5",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-05", ""), null, "2018/06/05 (화)"))});
		
		params.add(new Object[] { "6/52",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-05", ""), null, "2018/06/05 (화)"))});
		
		

// 텍스트 기니까 고려 x
//		params.add(new Object[] { "오늘 제주 4.3 추모 행사",
//				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-23", ""), null, "2018-05-23 (수)"))});

// 기니까 고려 x
//		params.add(new Object[] { "5-16 12시",
//				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 12:00"), null, "2018-05-23 (수) 오후 12:00"))});
		
		// 기간
		params.add(new Object[] { "오늘부터 내일까지",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", ""), new StringDateTimeDTO("2018-05-17", ""), "2018/05/16 (수) ~ 2018/05/17 (목)"))});
		
		params.add(new Object[] { "4일부터 1일까지",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-04", ""), new StringDateTimeDTO("2018-07-01", ""), "2018/06/04 (월) ~ 2018/07/01 (일)"))});
		
		params.add(new Object[] { "4일부터 1일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-04", ""), new StringDateTimeDTO("2018-07-01", ""), "2018/06/04 (월) ~ 2018/07/01 (일)"))});

		params.add(new Object[] { "4일~1일부터",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-04", ""), new StringDateTimeDTO("2018-07-01", ""), "2018/06/04 (월) ~ 2018/07/01 (일)"))});
		
		params.add(new Object[] { "4/2~5일부터",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-02", ""), new StringDateTimeDTO("2019-04-05", ""), "2019/04/02 (화) ~ 2019/04/05 (금)"))});
		
		params.add(new Object[] { "6/2~5",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-02", ""), new StringDateTimeDTO("2018-06-05", ""), "2018/06/02 (토) ~ 2018/06/05 (화)"))});

		params.add(new Object[] { "4일~8일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-04", ""), new StringDateTimeDTO("2018-06-08", ""), "2018/06/04 (월) ~ 2018/06/08 (금)"))});

		params.add(new Object[] { "3일~3일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-03", ""), null, "2018/06/03 (일) 종일"))});
		
		params.add(new Object[] { "26일 1시 ~3일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 01:00"), new StringDateTimeDTO("2018-06-03", ""),
							"2018/05/26 (토) 오전 1시 ~ 2018/06/03 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", ""),
								"2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일)"))});

		params.add(new Object[] { "26일 1시 ~3일 4시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:-"),
							"2018/05/26 (토) 오전 1시 ~ 2018/06/03 (일) 오전 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 01:00"), new StringDateTimeDTO("2018-06-03", "오후 04:-"),
								"2018/05/26 (토) 오전 1시 ~ 2018/06/03 (일) 오후 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:-"),
								"2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오전 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오후 04:-"),
								"2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오후 4시"))});
		
		params.add(new Object[] { "26일 1시 ~2월 31일 4시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 01:00"), new StringDateTimeDTO("2018-05-31", "오전 04:-"),
						"2018/05/26 (토) 오전 1시 ~ 2018/05/31 (목) 오전 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오전 01:00"), new StringDateTimeDTO("2018-05-31", "오후 04:-"),
								"2018/05/26 (토) 오전 1시 ~ 2018/05/31 (목) 오후 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-05-31", "오전 04:-"),
								"2018/05/26 (토) 오후 1시 ~ 2018/05/31 (목) 오전 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-05-31", "오후 04:-"),
								"2018/05/26 (토) 오후 1시 ~ 2018/05/31 (목) 오후 4시"))});
		
		params.add(new Object[] { "3일~2일까지",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-03", ""), new StringDateTimeDTO("2018-07-02", ""), "2018/06/03 (일) ~ 2018/07/02 (월)"))});
		
		params.add(new Object[] { "3시부터 3시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 03:00"), new StringDateTimeDTO("2018-05-17", "오전 03:00"), "2018/05/17 (목) 오전 3시 ~ 2018/05/17 (목) 오전 3시"))});
		
		params.add(new Object[] { "2월 30일 1시 ~",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 01:-"), null, "2018/05/30 (수) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 01:-"), null, "2018/05/30 (수) 오후 1시")) });

		// 기간 - 동안
		
		params.add(new Object[] { "30일부터 2시간동안",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", ""), null, "2018/05/30 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-06-30", ""), null, "2018/06/30 (토)")) });
		
		params.add(new Object[] { "2시간동안",                                                                               
				new ArrayList<Object>() });
		
		params.add(new Object[] { "2시간 동안",                                                                               
				new ArrayList<Object>() });
		
		params.add(new Object[] { "오늘부터 2일 동안",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", ""), new StringDateTimeDTO("2018-05-18", ""), "2018/05/16 (수) ~ 2018/05/18 (금)")) });
		
		params.add(new Object[] { "5시부터 10시간동안",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:00"), new StringDateTimeDTO("2018-05-17", "오전 03:00"), "2018/05/16 (수) 오후 5시 ~ 2018/05/17 (목) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 05:00"), new StringDateTimeDTO("2018-05-17", "오후 03:00"), "2018/05/17 (목) 오전 5시 ~ 2018/05/17 (목) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 05:00"), new StringDateTimeDTO("2018-05-18", "오전 03:00"), "2018/05/17 (목) 오후 5시 ~ 2018/05/18 (금) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오전 05:00"), new StringDateTimeDTO("2018-05-18", "오후 03:00"), "2018/05/18 (금) 오전 5시 ~ 2018/05/18 (금) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", "오후 05:00"), new StringDateTimeDTO("2018-05-19", "오전 03:00"), "2018/05/18 (금) 오후 5시 ~ 2018/05/19 (토) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오전 05:00"), new StringDateTimeDTO("2018-05-19", "오후 03:00"), "2018/05/19 (토) 오전 5시 ~ 2018/05/19 (토) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", "오후 05:00"), new StringDateTimeDTO("2018-05-20", "오전 03:00"), "2018/05/19 (토) 오후 5시 ~ 2018/05/20 (일) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오전 05:00"), new StringDateTimeDTO("2018-05-20", "오후 03:00"), "2018/05/20 (일) 오전 5시 ~ 2018/05/20 (일) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-20", "오후 05:00"), new StringDateTimeDTO("2018-05-21", "오전 03:00"), "2018/05/20 (일) 오후 5시 ~ 2018/05/21 (월) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-21", "오전 05:00"), new StringDateTimeDTO("2018-05-21", "오후 03:00"), "2018/05/21 (월) 오전 5시 ~ 2018/05/21 (월) 오후 3시")) });
		
		params.add(new Object[] { "5분",                                                                               
				new ArrayList<Object>() });
		
		// 숫자나열 패턴
		params.add(new Object[] { "12",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-12", ""), null, "2018/06/12 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 12:-"), null, "2018/05/17 (목) 오전 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 12:-"), null, "2018/05/17 (목) 오후 12시"))});
		
		params.add(new Object[] { "1213",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-12-13", ""), null, "2018/12/13 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오전 12:13"), null, "2018/05/17 (목) 오전 12시 13분"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", "오후 12:13"), null, "2018/05/17 (목) 오후 12시 13분"))});

		params.add(new Object[] { "180914",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-09-14", ""), null, "2018/09/14 (목)"))});

		params.add(new Object[] { "1813",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:13"), null, "2018/05/16 (수) 오후 6시 13분"))});
		
		params.add(new Object[] { "18131",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:13"), null, "2018/05/16 (수) 오후 6시 13분"))});
		
		params.add(new Object[] { "181314",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:13"), null, "2018/05/16 (수) 오후 6시 13분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:13"), null, "2018/05/16 (수) 오후 6시 13분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:13"), null, "2018/05/16 (수) 오후 6시 13분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:13"), null, "2018/05/16 (수) 오후 6시 13분"))});
		
		
		
		// 예외처리
		
		params.add(new Object[] { "3~2일까지", new ArrayList<Object>()});
		params.add(new Object[] { "6-2~4",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-02", ""), null, "2018/06/02 (토)"))});
		
		return params;
	}
	

	@Test
	public void test() throws Exception {
		//
		// When
		//
		List<EventDTO> outputList = recommendationService.getRecommendations(input, null, null);

		//
		// Then
		//
		assertEquals(expectedList.size(), outputList.size());
		for (int i = 0; i < outputList.size(); i++) {
			assertEquals(expectedList.get(i).toString(), outputList.get(i).toString());
		}
	}

}
