package com.soonmark.myapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import com.soonmark.domain.EventDTO;
import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.service.RecommendationService;

@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class WithFullRequestTest {

	private TestContextManager testContextManager;

	public WithFullRequestTest(String input, List<EventDTO> expectedList) {
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

		LocalDate tmpDate = LocalDate.now();

		// 0
		// 년 월 일
		params.add(new Object[] {"15년 4월 9일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2015-04-09", ""), null, "2015-04-09 ~")) });
		
		params.add(new Object[] { "2018-03-19",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-03-19", ""), null, "2018-03-19 ~")) });

		params.add(new Object[] { "2018-3-19",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-03-19", ""), null, "2018-03-19 ~")) });

		params.add(new Object[] { "20-10-1",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2020-10-01", ""), new StringDateTimeDTO("2020-10-01", ""),"")) });
		
		params.add(new Object[] { "1999/01/01",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("1999-01-01", ""), new StringDateTimeDTO("1999-01-01", ""),"")) });
		
		params.add(new Object[] { "11/3/19",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2011-03-19", ""), new StringDateTimeDTO("2011-03-19", ""),"")) });
		
		params.add(new Object[] { "11/3/1",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2011-03-01", ""), new StringDateTimeDTO("2011-03-01", ""),"")) });
		
		params.add(new Object[] { "2025.2.2",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2025-02-02", ""), new StringDateTimeDTO("2025-02-02", ""),"")) });
		
		params.add(new Object[] { "00.12.10",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2000-12-10", ""), new StringDateTimeDTO("2000-12-10", ""),"")) });

		params.add(new Object[] { "10.10.09",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2010-10-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		// 년 월
		params.add(new Object[] { "벚꽃달인 18년 4월 계획짜기",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-26", ""), new StringDateTimeDTO("2018-04-26", ""),"")) });

		params.add(new Object[] { "내 생일 94년 6월 중",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		// 년 일
		params.add(new Object[] { "2000년 21일에 여행갔었음.",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "2018년 4일에 여행갔었음.",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		// 월 일
		params.add(new Object[] { "4-9",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "1/1 신년행사",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		// 년
		params.add(new Object[] { "15년",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "07년에 중학교 졸업",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		// 월
		params.add(new Object[] { "2월에 졸업식",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "4월에 벚꽃구경",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
				
		params.add(new Object[] { "겨울 12월에는 빙어낚시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		// 일
		params.add(new Object[] { "9일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "17일 축구동호회",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "19일날 가족 외식",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });


		// 일 - 4월 31일, 2월 30일 등 범위 이탈에 대한 처리
		params.add(new Object[] { "가족모임 29일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "친구보기 30일에",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "31일에 불꽃놀이",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		

		// 월 시
		params.add(new Object[] { "11월 1시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "4월 1시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "4월 13시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		// 월 일 시간
		params.add(new Object[] { "4월 3일에 친구 모임 1시 서현",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "3/5에 친구 모임 11시 20분 야탑",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "4.9 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		
		// 월 일 요일 시간
		params.add(new Object[] { "4월 9일 월요일 19시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

		params.add(new Object[] { "4.18 월요일 10:10",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "4/9 화요일 7:00",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		// 월 일 요일
		params.add(new Object[] { "4/16 화요일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "4-1 화요일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });
		
		params.add(new Object[] { "4-1 일요일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""),"")) });

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
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		// 요일
		params.add(new Object[] { "금요일에 약속",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		// 특수날짜(내일)
		params.add(new Object[] { "내일 저녁에 회의",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		
		// 특수날짜(오늘) 시 분
		params.add(new Object[] { "오늘 12시 30분에 음원차트 확인",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		
		// 특수날짜(이번주) 일
		params.add(new Object[] { "이번주 13일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		params.add(new Object[] { "이번주 16일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		params.add(new Object[] { "이번주 21일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		
		// 특수날짜(이번주) 시간
		params.add(new Object[] { "이번주 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		// 특수날짜(이번주) 날짜 시간
		params.add(new Object[] { "이번주 13일 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		// 특수날짜(이번주) 요일 시간
		params.add(new Object[] { "이번주 수요일 12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		// 특수날짜(이번주) 날짜 요일 시간
		params.add(new Object[] { "이번주 3일 수요일 1시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });

		// 특수날짜(이번주) 요일
		params.add(new Object[] { "이번주 영화보기 금요일 조조",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		
		// 특수날짜(다음주)
		params.add(new Object[] { "약속 하나 있다 다음주",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		
		// 특수날짜(이번주)
		params.add(new Object[] { "아무 약속도 없는 이번주",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		


		
		// 특수날짜(다다음주)
		params.add(new Object[] { "친구 놀러옴 다다음주에",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
				
		
		// 오전 오후  없는 시간
		params.add(new Object[] { "9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		params.add(	new Object[] { "12시 30분",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		
		// 50
		// 오전 오후 있는 시간
		params.add(new Object[] { "오전 9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		params.add(new Object[] { "am 9시", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		params.add(new Object[] { "AM 9시", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		params.add(new Object[] { "A.M. 9시",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		params.add(new Object[] { "19:01", 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		
		
		// 오전/오후
		params.add(new Object[] { "오전",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		params.add(new Object[] { "오후",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		params.add(new Object[] { "am",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-09", ""), new StringDateTimeDTO("2010-10-09", ""), "")) });
		

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
			assertThat(outputList.get(i).toString(), is(expectedList.get(i).toString()));
		}
	}

}
