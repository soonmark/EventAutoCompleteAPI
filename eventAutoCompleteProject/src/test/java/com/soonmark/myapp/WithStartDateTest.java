package com.soonmark.myapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
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

	public WithStartDateTest(String inputText, DateTimeDTO inputStartDate, String inputDateTime, List<EventDTO> expectedList) {
		super();
		this.expectedList = expectedList;
		this.inputText = inputText;
		this.inputStartDate = inputStartDate;
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
	@Parameterized.Parameters(name = "{0} with startDate ({2})")
	public static Collection<Object[]> testCases() {
		Collection<Object[]> params = new ArrayList<Object[]>();

//		LocalDate tmpDate = LocalDate.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// 0
		// 년 월 일
		params.add(new Object[] {"15년 4월 9일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2015-04-09", ""), "2015-04-09 (목)")) });

		params.add(new Object[] {"1일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-06-01", ""), "2018-05-30 (수) 오전 11:00 ~ 2018-06-01 (금)")) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 12:00"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 12:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 12:30"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 01:00"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 01:30"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 01:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 02:00"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 02:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 02:30"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 02:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 03:00"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 03:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 03:30"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 03:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 04:00"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 04:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 04:30"), "2018-05-30 (수) 오전 11:00 ~ 2018-05-30 (수) 오후 04:30")) });
		
		// 오늘이면 지금 시간보다 미래로 추천
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 23), null), LocalDate.of(2018, 5, 23).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 11:00"), null, "2018-05-23 (수) 오전 11:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오전 11:30"), null, "2018-05-23 (수) 오전 11:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 12:00"), null, "2018-05-23 (수) 오후 12:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 12:30"), null, "2018-05-23 (수) 오후 12:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 01:00"), null, "2018-05-23 (수) 오후 01:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 01:30"), null, "2018-05-23 (수) 오후 01:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 02:00"), null, "2018-05-23 (수) 오후 02:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 02:30"), null, "2018-05-23 (수) 오후 02:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 03:00"), null, "2018-05-23 (수) 오후 03:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-23", "오후 03:30"), null, "2018-05-23 (수) 오후 03:30")) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 07:00"), null, "2018-05-30 (수) 오전 07:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 07:30"), null, "2018-05-30 (수) 오전 07:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 08:00"), null, "2018-05-30 (수) 오전 08:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 08:30"), null, "2018-05-30 (수) 오전 08:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 09:00"), null, "2018-05-30 (수) 오전 09:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 09:30"), null, "2018-05-30 (수) 오전 09:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 10:00"), null, "2018-05-30 (수) 오전 10:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 10:30"), null, "2018-05-30 (수) 오전 10:30"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), null, "2018-05-30 (수) 오전 11:00"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:30"), null, "2018-05-30 (수) 오전 11:30")) });
		
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
