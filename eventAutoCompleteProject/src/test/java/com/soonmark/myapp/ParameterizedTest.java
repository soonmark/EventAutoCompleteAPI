package com.soonmark.myapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.service.RecommendationService;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

@RunWith(Parameterized.class)
@ContextConfiguration(
		locations = {"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
public class ParameterizedTest {
	private TestContextManager testContextManager;
	public ParameterizedTest(String input, List<DateTimeDTO> expectedList) {
		super();
		this.expectedList = expectedList;
		this.input = input;
	}

	private List<DateTimeDTO> expectedList;
	private String input;
	
	@Autowired RecommendationService recommendationService;
	
	@Before
	public void setup() throws Exception{
		this.testContextManager = new TestContextManager(getClass());
		this.testContextManager.prepareTestInstance(this);
	}

	//
	// Given
	//
	@Parameterized.Parameters
	public static Collection<Object[]> testCases() {
		Collection<Object[]> params = new ArrayList<Object[]>();
		params.add(new Object[] { "15년 4월 9일",
				Arrays.asList(new DateTimeDTO(2015, 4, 9, DayOfWeek.THURSDAY, -1, -1, true))});
		params.add(new Object[] { "4-9",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2019, 4, 9, DayOfWeek.TUESDAY, -1, -1, true))});
		params.add(new Object[] { "9일",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2018, 5, 9, DayOfWeek.WEDNESDAY, -1, -1, true))});
		params.add(new Object[] { "15년",
				Arrays.asList(new DateTimeDTO(2015, 1, 1, DayOfWeek.THURSDAY, -1, -1, true),
						new DateTimeDTO(2015, 1, 2, DayOfWeek.FRIDAY, -1, -1, true))});
		params.add(new Object[] { "2월에 졸업식",
				Arrays.asList(new DateTimeDTO(2018, 2, 1, DayOfWeek.THURSDAY, -1, -1, true),
						new DateTimeDTO(2018, 2, 2, DayOfWeek.FRIDAY, -1, -1, true))});
		params.add(new Object[] { "2018-03-19",
				Arrays.asList(new DateTimeDTO(2018, 3, 19, DayOfWeek.MONDAY, -1, -1, true))});
		params.add(new Object[] { "11/3/19",
				Arrays.asList(new DateTimeDTO(2011, 3, 19, DayOfWeek.SATURDAY, -1, -1, true),
						new DateTimeDTO(2018, 11, 3, DayOfWeek.SATURDAY, -1, -1, true))});
		params.add(new Object[] { "19일날 가족 외식",
				Arrays.asList(new DateTimeDTO(2018, 4, 19, DayOfWeek.THURSDAY, -1, -1, true),
						new DateTimeDTO(2018, 5, 19, DayOfWeek.SATURDAY, -1, -1, true))});
		params.add(new Object[] { "1/1 신년행사",
				Arrays.asList(new DateTimeDTO(2018, 1, 1, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2019, 1, 1, DayOfWeek.TUESDAY, -1, -1, true))});
		params.add(new Object[] { "4월에 벚꽃구경",
				Arrays.asList(new DateTimeDTO(2018, 4, 16, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2018, 4, 17, DayOfWeek.TUESDAY, -1, -1, true))});
		params.add(new Object[] { "겨울 12월에는 빙어낚시",
				Arrays.asList(new DateTimeDTO(2018, 12, 1, DayOfWeek.SATURDAY, -1, -1, true),
						new DateTimeDTO(2018, 12, 2, DayOfWeek.SUNDAY, -1, -1, true))});
		params.add(new Object[] { "07년에 중학교 졸업",
				Arrays.asList(new DateTimeDTO(2007, 1, 1, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2007, 1, 2, DayOfWeek.TUESDAY, -1, -1, true))});
		params.add(new Object[] { "내 생일 94년 6월 중",
				Arrays.asList(new DateTimeDTO(1994, 6, 1, DayOfWeek.WEDNESDAY, -1, -1, true),
						new DateTimeDTO(1994, 6, 2, DayOfWeek.THURSDAY, -1, -1, true))});
		params.add(new Object[] { "2000년 21일에 여행갔었음.",
				Arrays.asList(new DateTimeDTO(2000, 1, 21, DayOfWeek.FRIDAY, -1, -1, true),
						new DateTimeDTO(2000, 2, 21, DayOfWeek.MONDAY, -1, -1, true))});
		

		params.add(new Object[] { "12시 30분",
				Arrays.asList(new DateTimeDTO(2018, 4, 17, DayOfWeek.TUESDAY, 12, 30, false),
						new DateTimeDTO(2018, 4, 17, DayOfWeek.TUESDAY, 0, 30, false))});
		
		
		params.add(new Object[] { "11월 1시",
				Arrays.asList(new DateTimeDTO(2018, 11, 1, DayOfWeek.THURSDAY, 1, 0, false),
						new DateTimeDTO(2018, 11, 1, DayOfWeek.THURSDAY, 13, 0, false))});
		params.add(new Object[] { "4월 3일에 친구 모임 1시 서현",
				Arrays.asList(new DateTimeDTO(2018, 4, 3, DayOfWeek.TUESDAY, 1, 0, false),
						new DateTimeDTO(2018, 4, 3, DayOfWeek.TUESDAY, 13, 0, false))});
		params.add(new Object[] { "4월 9일 월요일 19시",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false),
						new DateTimeDTO(2019, 4, 9, DayOfWeek.TUESDAY, 19, 0, false))});
		params.add(new Object[] { "4/9 화요일 7:00",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 7, 0, false),
						new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false))});
		params.add(new Object[] { "4.9 12시 30분",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 12, 30, false),
						new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 0, 30, false))});
		
		
		params.add(new Object[] { "내일 저녁에 회의",
				Arrays.asList(new DateTimeDTO(2018, 4, 17, DayOfWeek.TUESDAY, -1, -1, true))});
		params.add(new Object[] { "오늘 12시 30분에 음원차트 확인",
				Arrays.asList(new DateTimeDTO(2018, 4, 16, DayOfWeek.MONDAY, 12, 30, false),
						new DateTimeDTO(2018, 4, 16, DayOfWeek.MONDAY, 0, 30, false))});
		params.add(new Object[] { "이번주 13일",
				Arrays.asList(new DateTimeDTO(2018, 4, 13, DayOfWeek.FRIDAY, -1, -1, true),
						new DateTimeDTO(2018, 4, 15, DayOfWeek.SUNDAY, -1, -1, true))});
		params.add(new Object[] { "이번주 영화보기 토요일 조조",
				Arrays.asList(new DateTimeDTO(2018, 4, 21, DayOfWeek.SATURDAY, -1, -1, true))});
		params.add(new Object[] { "약속 하나 있다 다음주",
				Arrays.asList(new DateTimeDTO(2018, 4, 22, DayOfWeek.SUNDAY, -1, -1, true))});
		params.add(new Object[] { "아무 약속도 없는 이번주",
				Arrays.asList(new DateTimeDTO(2018, 4, 15, DayOfWeek.SUNDAY, -1, -1, true))});
		params.add(new Object[] { "친구 놀러옴 다다음주에",
				Arrays.asList(new DateTimeDTO(2018, 4, 29, DayOfWeek.SUNDAY, -1, -1, true))});
		
		
		// 스펙 논의 필요
		params.add(new Object[] { "4/9 화요일",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2019, 4, 9, DayOfWeek.TUESDAY, -1, -1, true))});
		params.add(new Object[] { "월요일날 금요일에 만나요 콘서트",
				Arrays.asList(new DateTimeDTO(2018, 4, 16, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2018, 4, 20, DayOfWeek.FRIDAY, -1, -1, true))});
		return params;
	}
	
	@Test
	public void test() throws Exception {
		//
		// When
		//
		List<DateTimeDTO> outputList = recommendationService.getRecommendation(input);
		
		//
		// Then
		//
		assertThat(outputList.size(), is(expectedList.size()));
		for(int i = 0 ; i < outputList.size() ; i++) {
			assertThat(outputList.get(i).toString(), is(expectedList.get(i).toString()));
		}
	}

}
