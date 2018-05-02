package com.soonmark.myapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.LocalDate;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.service.RecommendationService;

@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class WithStartDateTest {

	private TestContextManager testContextManager;

	public WithStartDateTest(String inputText, /*DateTimeDTO inputStartDate, */List<EventDTO> expectedList) {
		super();
		this.expectedList = expectedList;
		this.inputText = inputText;
		this.inputStartDate = new DateTimeDTO(LocalDate.of(2018, 4, 30), LocalTime.of(11, 0));
	}

	private List<EventDTO> expectedList;
	private DateTimeDTO inputStartDate;
	private String inputText;

	
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
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-30", "오전 11:00"), new StringDateTimeDTO("2015-04-09", ""), "2018-04-30 오전 11:00 ~")) });

		params.add(new Object[] {"1일",
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-04-30", "오전 11:00"), new StringDateTimeDTO("2018-05-01", ""), "2018-04-30 오전 11:00 ~ 2018-05-01"),
						new EventDTO(new StringDateTimeDTO("2018-04-30", "오전 11:00"), new StringDateTimeDTO("2018-06-01", ""), "2018-04-30 오전 11:00 ~ 2018-06-01")) });
		
		return params;
	}
	
	
	
	@Test
	public void test() throws Exception {
		//
		// When
		//
		List<EventDTO> outputList = recommendationService.getRecommendations(inputText, inputStartDate, null);

		//
		// Then
		//
		assertThat(outputList.size(), is(expectedList.size()));
		for (int i = 0; i < outputList.size(); i++) {
			assertEquals(outputList.get(i).toString(), expectedList.get(i).toString());
		}
	}

}
