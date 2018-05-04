package com.soonmark.myapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
import com.soonmark.domain.EventDTO;
import com.soonmark.service.RecommendationService;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

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
	@Parameterized.Parameters
	public static Collection<Object[]> testCases() {
		Collection<Object[]> params = new ArrayList<Object[]>();

//		LocalDateTime now = LocalDateTime.of(2018, 5, 4, 12, 2);
//		String nowDate = now.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//		String nowTime = now.toLocalTime().format(DateTimeFormatter.ofPattern("a hh:mm"));

		// 0
		// 년 월 일
		params.add(new Object[] {"15년 4월 9일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2015-04-09", ""), null, "2015-04-09 (목)")) });
		
		params.add(new Object[] { "2018-03-19",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-03-19", ""), null, "2018-03-19 (월)")) });

		params.add(new Object[] { "2018-3-19",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-03-19", ""), null, "2018-03-19 (월)")) });

		params.add(new Object[] { "20-10-1",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2020-10-01", ""), null, "2020-10-01 (목)")) });
		
		params.add(new Object[] { "1999/01/01",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("1999-01-01", ""), null, "1999-01-01 (금)")) });
		                                      
		// 5
		params.add(new Object[] { "11/3/19",                                              
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2011-03-19", ""), null, "2011-03-19 (토)")) });
		                                                                                  
		params.add(new Object[] { "11/3/1",                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2011-03-01", ""), null, "2011-03-01 (화)")) });
		                                                                                  
		params.add(new Object[] { "2025.2.2",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2025-02-02", ""), null, "2025-02-02 (일)")) });
		                                                                                  
		params.add(new Object[] { "00.12.10",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2000-12-10", ""), null, "2000-12-10 (일)")) });
                                                                                          
		params.add(new Object[] { "10.10.09",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2010-10-09", ""), null, "2010-10-09 (토)")) });

		// 10
		// 년 월
		params.add(new Object[] { "벚꽃달인 18년 4월 계획짜기",
//				null});
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-01", ""), null, "2018-04-01 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-04-02", ""), null, "2018-04-02 (월)")) });

		params.add(new Object[] { "내 생일 94년 6월 중",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("1994-06-01", ""), null, "1994-06-01 (수)"),
						new EventDTO(new StringDateTimeDTO("1994-06-02", ""), null, "1994-06-02 (목)")) });
//				null});

		// 년 일
		params.add(new Object[] { "2000년 21일에 여행갔었음.",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2000-01-21", ""), null, "2000-01-21 (금)"),
						new EventDTO(new StringDateTimeDTO("2000-02-21", ""), null, "2000-02-21 (월)")) });
                                                                                          
		params.add(new Object[] { "2018년 4일에 여행갔었음.",                                     
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-06-04", ""), null, "2018-06-04 (월)")) });
                                                                                          
		// 월 일                                                                            
		params.add(new Object[] { "4-9",                                                  
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", ""), null, "2019-04-09 (화)")) });
		                
		// 15
		params.add(new Object[] { "1/1 신년행사",                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-01-01", ""), null, "2019-01-01 (화)")) });
                                                                                          
		// 년                                                                              
		params.add(new Object[] { "15년",                                 
//				null});
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2015-01-01", ""), null, "2015-01-01 (목)"),
						new EventDTO(new StringDateTimeDTO("2015-01-02", ""), null, "2015-01-02 (금)")) });
                                                                                          
		params.add(new Object[] { "07년에 중학교 졸업",                                          
//				null});
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2007-01-01", ""), null, "2007-01-01 (월)"),
						new EventDTO(new StringDateTimeDTO("2007-01-02", ""), null, "2007-01-02 (화)")) });
                                                                                          
		// 월                                                                              
		params.add(new Object[] { "2월에 졸업식",                                              
//				null});
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-02-01", ""), null, "2019-02-01 (금)"),
						new EventDTO(new StringDateTimeDTO("2019-02-02", ""), null, "2019-02-02 (토)")) });
		                                                                                  
		params.add(new Object[] { "5월에 꽃구경",                                             
//				null});
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-05", ""), null, "2018-05-05 (토)")) });

		// 20
		params.add(new Object[] { "겨울 12월에는 빙어낚시",                                        
//				null});
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-12-01", ""), null, "2018-12-01 (토)"),
						new EventDTO(new StringDateTimeDTO("2018-12-02", ""), null, "2018-12-02 (일)")) });

		// 일                                                                              
		params.add(new Object[] { "9일",                                                   
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-09", ""), null, "2018-05-09 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-06-09", ""), null, "2018-06-09 (토)")) });
                                                                                          
		params.add(new Object[] { "17일 축구동호회",                                            
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018-05-17 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-06-17", ""), null, "2018-06-17 (일)")) });

		params.add(new Object[] { "19일날 가족 외식",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-19", ""), null, "2018-05-19 (토)"),
						new EventDTO(new StringDateTimeDTO("2018-06-19", ""), null, "2018-06-19 (화)")) });


		// 일 - 4월 31일, 2월 30일 등 범위 이탈에 대한 처리
		params.add(new Object[] { "가족모임 29일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-29", ""), null, "2018-05-29 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-06-29", ""), null, "2018-06-29 (금)")) });
        
		// 25
		params.add(new Object[] { "친구보기 30일에",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", ""), null, "2018-05-30 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-06-30", ""), null, "2018-06-30 (토)")) });
		                                                                                                                     
		params.add(new Object[] { "31일에 불꽃놀이",                                                                               
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-31", ""), null, "2018-05-31 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-07-31", ""), null, "2018-07-31 (화)")) });
		                                                                                                                     
                                                                                                                             
		// 월 시                                                                                                               
		params.add(new Object[] { "11월 1시",                                                                                  
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-11-01", "오전 01:00"), null, "2018-11-01 (목) 오전 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-11-01", "오후 01:00"), null, "2018-11-01 (목) 오후 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-11-02", "오전 01:00"), null, "2018-11-02 (금) 오전 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-11-02", "오후 01:00"), null, "2018-11-02 (금) 오후 01:00")) });
                                                                                                                             
		params.add(new Object[] { "5월 1시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", "오전 01:00"), null, "2018-05-05 (토) 오전 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-05", "오후 01:00"), null, "2018-05-05 (토) 오후 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-06", "오전 01:00"), null, "2018-05-06 (일) 오전 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-06", "오후 01:00"), null, "2018-05-06 (일) 오후 01:00")) });
                                                                                                                             
		params.add(new Object[] { "5월 13시 30분",                                                                              
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", "오후 01:30"), null, "2018-05-05 (토) 오후 01:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-06", "오후 01:30"), null, "2018-05-06 (일) 오후 01:30")) });

		// 30
		// 월 일 시간                                                                                                            
		params.add(new Object[] { "4월 3일에 친구 모임 1시 서현",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-03", "오전 01:00"), null, "2019-04-03 (수) 오전 01:00"),
						new EventDTO(new StringDateTimeDTO("2019-04-03", "오후 01:00"), null, "2019-04-03 (수) 오후 01:00")) });
                                                                                                                             
		params.add(new Object[] { "3/5에 친구 모임 11시 20분 야탑",                                    
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-03-05", "오전 11:20"), null, "2019-03-05 (화) 오전 11:20"),
						new EventDTO(new StringDateTimeDTO("2019-03-05", "오후 11:20"), null, "2019-03-05 (화) 오후 11:20")) });
		                                                                                                                     
		params.add(new Object[] { "4.9 12시 30분",                                                                             
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", "오전 12:30"), null, "2019-04-09 (화) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2019-04-09", "오후 12:30"), null, "2019-04-09 (화) 오후 12:30")) });
		                                                                                                                     
		                                                                                                                     
		// 월 일 요일 시간                                                                                                         
		params.add(new Object[] { "4월 9일 월요일 19시",                                                                           
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", "오후 07:00"), null, "2019-04-09 (화) 오후 07:00"))});
                                                                                                                             
		params.add(new Object[] { "4.18 월요일 10:10",                                                                          
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-18", "오전 10:10"), null, "2019-04-18 (목) 오전 10:10"),
						new EventDTO(new StringDateTimeDTO("2019-04-18", "오후 10:10"), null, "2019-04-18 (목) 오후 10:10")) });

		// 35
		params.add(new Object[] { "4/9 화요일 7:00",                                                                            
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-09", "오전 07:00"), null, "2019-04-09 (화) 오전 07:00"),
						new EventDTO(new StringDateTimeDTO("2019-04-09", "오후 07:00"), null, "2019-04-09 (화) 오후 07:00")) });
		                                                                                                                     
		// 월 일 요일                                                                                                            
		params.add(new Object[] { "4/16 화요일",                                                                                
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-16", ""), null, "2019-04-16 (화)")) });
		                                                                                                                     
		params.add(new Object[] { "4-1 화요일",                                                                                 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-01", ""), null, "2019-04-01 (월)")) });
		                                                                                                                     
		params.add(new Object[] { "4-1 일요일",                                                                                 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-01", ""), null, "2019-04-01 (월)")) });

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

		// 다중 요일
		params.add(new Object[] { "월요일날 금요일에 만나요 콘서트",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-07", ""), null, "2018-05-07 (월)")) });

		// 40
		// 요일
		params.add(new Object[] { "금요일에 약속",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-11", ""), null, "2018-05-11 (금)")) });

		// 특수날짜(내일)
		params.add(new Object[] { "내일 저녁에 회의",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", ""), null, "2018-05-05 (토)")) });
		
		// 특수날짜(오늘) 시 분
		params.add(new Object[] { "오늘 12시 30분에 음원차트 확인",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 12:30"), null, "2018-05-04 (금) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 12:30"), null, "2018-05-04 (금) 오후 12:30")) });
		
		// 특수날짜(이번주) 일
		params.add(new Object[] { "이번주 4일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)")) });

		params.add(new Object[] { "이번주 13일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-05", ""), null, "2018-05-05 (토)")) });

		// 45
		params.add(new Object[] { "다음주 16일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-06", ""), null, "2018-05-06 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-05-07", ""), null, "2018-05-07 (월)"),
						new EventDTO(new StringDateTimeDTO("2018-05-08", ""), null, "2018-05-08 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-05-09", ""), null, "2018-05-09 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-10", ""), null, "2018-05-10 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-11", ""), null, "2018-05-11 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-12", ""), null, "2018-05-12 (토)")) });
		
		// 특수날짜(이번주) 시간
		params.add(new Object[] { "이번주 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-06", "오전 12:30"), null, "2018-05-06 (일) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-06", "오후 12:30"), null, "2018-05-06 (일) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-07", "오전 12:30"), null, "2018-05-07 (월) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-07", "오후 12:30"), null, "2018-05-07 (월) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-08", "오전 12:30"), null, "2018-05-08 (화) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-08", "오후 12:30"), null, "2018-05-08 (화) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-09", "오전 12:30"), null, "2018-05-09 (수) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-09", "오후 12:30"), null, "2018-05-09 (수) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-10", "오전 12:30"), null, "2018-05-10 (목) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-10", "오후 12:30"), null, "2018-05-10 (목) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-11", "오전 12:30"), null, "2018-05-11 (금) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-11", "오후 12:30"), null, "2018-05-11 (금) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-12", "오전 12:30"), null, "2018-05-12 (토) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-12", "오후 12:30"), null, "2018-05-12 (토) 오후 12:30")) });

		// 특수날짜(이번주) 날짜 시간
		params.add(new Object[] { "이번주 4일 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 12:30"), null, "2018-05-04 (금) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 12:30"), null, "2018-05-04 (금) 오후 12:30")) });
		
		// 특수날짜(이번주) 요일 시간
		params.add(new Object[] { "이번주 수요일 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-02", "오전 12:30"), null, "2018-05-02 (수) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-02", "오후 12:30"), null, "2018-05-02 (수) 오후 12:30")) });
		
		// 특수날짜(이번주) 날짜 요일 시간
		params.add(new Object[] { "이번주 3일 수요일 1시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-03", "오전 01:30"), null, "2018-05-03 (목) 오전 01:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-03", "오후 01:30"), null, "2018-05-03 (목) 오후 01:30")) });

		// 50
		// 특수날짜(이번주) 요일
		params.add(new Object[] { "이번주 영화보기 금요일 조조",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)")) });
		
		// 특수날짜(다음주)
		params.add(new Object[] { "약속 하나 있다 다음주",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-06", ""), null, "2018-05-06 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-05-07", ""), null, "2018-05-07 (월)"),
						new EventDTO(new StringDateTimeDTO("2018-05-08", ""), null, "2018-05-08 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-05-09", ""), null, "2018-05-09 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-10", ""), null, "2018-05-10 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-11", ""), null, "2018-05-11 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-12", ""), null, "2018-05-12 (토)")) });
		
		// 특수날짜(이번주)
		params.add(new Object[] { "아무 약속도 없는 이번주",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", ""), null, "2018-05-04 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-05", ""), null, "2018-05-05 (토)")) });
		

		
		
		// 특수날짜(다다음주)
		params.add(new Object[] { "친구 놀러옴 다다음주에",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-13", ""), null, "2018-05-13 (일)"),
						new EventDTO(new StringDateTimeDTO("2018-05-14", ""), null, "2018-05-14 (월)"),
						new EventDTO(new StringDateTimeDTO("2018-05-15", ""), null, "2018-05-15 (화)"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", ""), null, "2018-05-16 (수)"),
						new EventDTO(new StringDateTimeDTO("2018-05-17", ""), null, "2018-05-17 (목)"),
						new EventDTO(new StringDateTimeDTO("2018-05-18", ""), null, "2018-05-18 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-19", ""), null, "2018-05-19 (토)")) });
				
		
		// 오전 오후  없는 시간
		params.add(new Object[] { "9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 09:00"), null, "2018-05-04 (금) 오후 09:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-05", "오전 09:00"), null, "2018-05-05 (토) 오전 09:00")) });
		
		// 55
		params.add(	new Object[] { "12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", "오전 12:30"), null, "2018-05-05 (토) 오전 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-05", "오후 12:30"), null, "2018-05-05 (토) 오후 12:30")) });
		
		// 오전 오후 있는 시간
		params.add(new Object[] { "오전 9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", "오전 09:00"), null, "2018-05-05 (토) 오전 09:00")) });
		params.add(new Object[] { "am 9시", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", "오전 09:00"), null, "2018-05-05 (토) 오전 09:00")) });
		params.add(new Object[] { "AM 9시", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", "오전 09:00"), null, "2018-05-05 (토) 오전 09:00")) });
		params.add(new Object[] { "A.M. 9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-05", "오전 09:00"), null, "2018-05-05 (토) 오전 09:00")) });
		// 60
		params.add(new Object[] { "19:01", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 07:01"), null, "2018-05-04 (금) 오후 07:01")) });
		
		
		// 오전/오후
		params.add(new Object[] { "오전",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 07:00"), null, "2018-05-04 (금) 오전 07:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 07:30"), null, "2018-05-04 (금) 오전 07:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 08:00"), null, "2018-05-04 (금) 오전 08:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 08:30"), null, "2018-05-04 (금) 오전 08:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 09:00"), null, "2018-05-04 (금) 오전 09:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 09:30"), null, "2018-05-04 (금) 오전 09:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 10:00"), null, "2018-05-04 (금) 오전 10:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 10:30"), null, "2018-05-04 (금) 오전 10:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 11:00"), null, "2018-05-04 (금) 오전 11:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 11:30"), null, "2018-05-04 (금) 오전 11:30")) });
		
		params.add(new Object[] { "오후",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 12:00"), null, "2018-05-04 (금) 오후 12:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 12:30"), null, "2018-05-04 (금) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 01:00"), null, "2018-05-04 (금) 오후 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 01:30"), null, "2018-05-04 (금) 오후 01:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 02:00"), null, "2018-05-04 (금) 오후 02:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 02:30"), null, "2018-05-04 (금) 오후 02:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 03:00"), null, "2018-05-04 (금) 오후 03:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 03:30"), null, "2018-05-04 (금) 오후 03:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 04:00"), null, "2018-05-04 (금) 오후 04:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오후 04:30"), null, "2018-05-04 (금) 오후 04:30")) });
		
		params.add(new Object[] { "am",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 07:00"), null, "2018-05-04 (금) 오전 07:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 07:30"), null, "2018-05-04 (금) 오전 07:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 08:00"), null, "2018-05-04 (금) 오전 08:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 08:30"), null, "2018-05-04 (금) 오전 08:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 09:00"), null, "2018-05-04 (금) 오전 09:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 09:30"), null, "2018-05-04 (금) 오전 09:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 10:00"), null, "2018-05-04 (금) 오전 10:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 10:30"), null, "2018-05-04 (금) 오전 10:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 11:00"), null, "2018-05-04 (금) 오전 11:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-04", "오전 11:30"), null, "2018-05-04 (금) 오전 11:30")) });
		
		params.add(new Object[] { "오늘 제주 4.3 추모 행사",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-04-03", ""), null, "2019-04-03 (수)"))});
		
		
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
		assertThat(outputList.size(), is(expectedList.size()));
		for (int i = 0; i < outputList.size(); i++) {
			assertEquals(outputList.get(i).toString(), (expectedList.get(i).toString()));
		}
	}

}
