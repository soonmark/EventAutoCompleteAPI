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
		params.add(new Object[] { "4월 9일 월요일 19시",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false),
						new DateTimeDTO(2019, 4, 9, DayOfWeek.TUESDAY, 19, 0, false))});
		params.add(new Object[] { "4-9",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(2019, 4, 9, DayOfWeek.TUESDAY, -1, -1, true))});

		params.add(new Object[] { "이번주 영화보기 토요일 조조",
				Arrays.asList(new DateTimeDTO(2018, 4, 14, DayOfWeek.SATURDAY, -1, -1, true))});
		params.add(new Object[] { "2월에 졸업식",
				Arrays.asList(new DateTimeDTO(2018, 2, 1, DayOfWeek.THURSDAY, -1, -1, true),
						new DateTimeDTO(2018, 2, 2, DayOfWeek.FRIDAY, -1, -1, true))});

		
		
		
		params.add(new Object[] { "4/9 화요일 7:00",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 7, 0, false),
						new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false))});
		params.add(new Object[] { "4.9 12시 30분",
				Arrays.asList(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 12, 30, false),
						new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 0, 30, false))});
		params.add(new Object[] { "이번주 13일",
				Arrays.asList(new DateTimeDTO(2018, 4, 13, DayOfWeek.FRIDAY, -1, -1, true))});
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
