package com.soonmark.myapp;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.service.RecommendationService;

@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class WithFullRequestTest {

	private TestContextManager testContextManager;

	public WithFullRequestTest(String input, DateTimeDTO inputStartDate, String inputStartDateTime, DateTimeDTO inputEndDate, String inputEndDateTime, List<EventDTO> expectedList) {
		super();
		this.expectedList = expectedList;
		this.input = input;
		this.inputStartDate = inputStartDate;
		this.inputEndDate = inputEndDate;
	}

	private List<EventDTO> expectedList;
	private DateTimeDTO inputStartDate;
	private DateTimeDTO inputEndDate;
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
	@Parameterized.Parameters(name = "\"{0}\" with startDate ({2}), endDate ({4})")
	public static Collection<Object[]> testCases() {
		Collection<Object[]> params = new ArrayList<Object[]>();

		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		params.add(new Object[] {"15년 4월 9일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				new DateTimeDTO(LocalDate.of(2018, 5, 31), null), LocalDate.of(2018, 5, 30).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2015-04-09", ""), null, "2015/04/09 (목)")) });
		
		params.add(new Object[] {"1일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				new DateTimeDTO(LocalDate.of(2018, 5, 31), null), LocalDate.of(2018, 5, 31).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-01", ""), null, "2018/06/01 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-07-01", ""), null, "2018/07/01 (일)")) });
		
		params.add(new Object[] {"1시", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 01:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 1시" )) });
		
		params.add(new Object[] {"12시", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat),
				new DateTimeDTO(LocalDate.of(2018, 5, 31), null), LocalDate.of(2018, 5, 31).format(dateFormat),
				new ArrayList<Object>() });
		
		params.add(new Object[] {"12시~10시", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat),
				new DateTimeDTO(LocalDate.of(2018, 5, 31), null), LocalDate.of(2018, 5, 31).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 12:00"), new StringDateTimeDTO("2018-05-31", "오전 10:00"), "2018/05/30 (수) 오전 12시 ~ 2018/05/31 (목) 오전 10시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 12:00"), new StringDateTimeDTO("2018-05-31", "오후 10:00"), "2018/05/30 (수) 오전 12시 ~ 2018/05/31 (목) 오후 10시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 12:00"), new StringDateTimeDTO("2018-05-31", "오전 10:00"), "2018/05/30 (수) 오후 12시 ~ 2018/05/31 (목) 오전 10시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 12:00"), new StringDateTimeDTO("2018-05-31", "오후 10:00"), "2018/05/30 (수) 오후 12시 ~ 2018/05/31 (목) 오후 10시" )) });
		
		params.add(new Object[] {"1시", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				new DateTimeDTO(LocalDate.of(2018, 5, 31), null), LocalDate.of(2018, 5, 31).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오전 01:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오전 1시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오후 01:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오후 1시" )) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat),
				new DateTimeDTO(LocalDate.of(2018, 5, 31), null), LocalDate.of(2018, 5, 31).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 07:00"), new StringDateTimeDTO("2018-05-31", "오전 07:00"), "2018/05/30 (수) 오전 7시 ~ 2018/05/31 (목) 오전 7시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 08:00"), new StringDateTimeDTO("2018-05-31", "오전 08:00"), "2018/05/30 (수) 오전 8시 ~ 2018/05/31 (목) 오전 8시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 09:00"), new StringDateTimeDTO("2018-05-31", "오전 09:00"), "2018/05/30 (수) 오전 9시 ~ 2018/05/31 (목) 오전 9시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 10:00"), new StringDateTimeDTO("2018-05-31", "오전 10:00"), "2018/05/30 (수) 오전 10시 ~ 2018/05/31 (목) 오전 10시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오전 11:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오전 11시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 12:00"), new StringDateTimeDTO("2018-05-31", "오후 12:00"), "2018/05/30 (수) 오후 12시 ~ 2018/05/31 (목) 오후 12시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 01:00"), new StringDateTimeDTO("2018-05-31", "오후 01:00"), "2018/05/30 (수) 오후 1시 ~ 2018/05/31 (목) 오후 1시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 02:00"), new StringDateTimeDTO("2018-05-31", "오후 02:00"), "2018/05/30 (수) 오후 2시 ~ 2018/05/31 (목) 오후 2시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 03:00"), new StringDateTimeDTO("2018-05-31", "오후 03:00"), "2018/05/30 (수) 오후 3시 ~ 2018/05/31 (목) 오후 3시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 04:00"), new StringDateTimeDTO("2018-05-31", "오후 04:00"), "2018/05/30 (수) 오후 4시 ~ 2018/05/31 (목) 오후 4시" )) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 16), null), LocalDate.of(2018, 5, 16).format(dateFormat),
				new DateTimeDTO(LocalDate.of(2018, 5, 23), null), LocalDate.of(2018, 5, 23).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 04:00"), new StringDateTimeDTO("2018-05-23", "오후 04:00"), "2018/05/16 (수) 오후 4시 ~ 2018/05/23 (수) 오후 4시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:00"), new StringDateTimeDTO("2018-05-23", "오후 05:00"), "2018/05/16 (수) 오후 5시 ~ 2018/05/23 (수) 오후 5시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:00"), new StringDateTimeDTO("2018-05-23", "오후 06:00"), "2018/05/16 (수) 오후 6시 ~ 2018/05/23 (수) 오후 6시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 07:00"), new StringDateTimeDTO("2018-05-23", "오후 07:00"), "2018/05/16 (수) 오후 7시 ~ 2018/05/23 (수) 오후 7시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 08:00"), new StringDateTimeDTO("2018-05-23", "오후 08:00"), "2018/05/16 (수) 오후 8시 ~ 2018/05/23 (수) 오후 8시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 09:00"), new StringDateTimeDTO("2018-05-23", "오후 09:00"), "2018/05/16 (수) 오후 9시 ~ 2018/05/23 (수) 오후 9시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 10:00"), new StringDateTimeDTO("2018-05-23", "오후 10:00"), "2018/05/16 (수) 오후 10시 ~ 2018/05/23 (수) 오후 10시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 11:00"), new StringDateTimeDTO("2018-05-23", "오후 11:00"), "2018/05/16 (수) 오후 11시 ~ 2018/05/23 (수) 오후 11시" )) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 16), null), LocalDate.of(2018, 5, 16).format(dateFormat),
				new DateTimeDTO(LocalDate.of(2018, 5, 16), null), LocalDate.of(2018, 5, 16).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 04:00"), new StringDateTimeDTO("2018-05-16", "오후 05:00"), "2018/05/16 (수) 오후 4시 ~ 2018/05/16 (수) 오후 5시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:00"), new StringDateTimeDTO("2018-05-16", "오후 06:00"), "2018/05/16 (수) 오후 5시 ~ 2018/05/16 (수) 오후 6시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:00"), new StringDateTimeDTO("2018-05-16", "오후 07:00"), "2018/05/16 (수) 오후 6시 ~ 2018/05/16 (수) 오후 7시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 07:00"), new StringDateTimeDTO("2018-05-16", "오후 08:00"), "2018/05/16 (수) 오후 7시 ~ 2018/05/16 (수) 오후 8시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 08:00"), new StringDateTimeDTO("2018-05-16", "오후 09:00"), "2018/05/16 (수) 오후 8시 ~ 2018/05/16 (수) 오후 9시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 09:00"), new StringDateTimeDTO("2018-05-16", "오후 10:00"), "2018/05/16 (수) 오후 9시 ~ 2018/05/16 (수) 오후 10시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 10:00"), new StringDateTimeDTO("2018-05-16", "오후 11:00"), "2018/05/16 (수) 오후 10시 ~ 2018/05/16 (수) 오후 11시" )) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 26), LocalTime.of(13, 0)), LocalDateTime.of(2018, 5, 26, 13, 0).format(format),
				new DateTimeDTO(LocalDate.of(2018, 6, 3), LocalTime.of(4, 0), true), LocalDateTime.of(2018, 6, 3, 4, 0).format(format),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:00"), "2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오전 4시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:10"), "2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오전 4시 10분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:20"), "2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오전 4시 20분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:30"), "2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오전 4시 30분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:40"), "2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오전 4시 40분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-26", "오후 01:00"), new StringDateTimeDTO("2018-06-03", "오전 04:50"), "2018/05/26 (토) 오후 1시 ~ 2018/06/03 (일) 오전 4시 50분" )) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 16), LocalTime.of(13, 0)), LocalDateTime.of(2018, 5, 16, 13, 0).format(format),
				new DateTimeDTO(LocalDate.of(2018, 5, 16), LocalTime.of(18, 0), true), LocalDateTime.of(2018, 5, 16, 18, 0).format(format),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:00"), new StringDateTimeDTO("2018-05-16", "오후 06:00"), "2018/05/16 (수) 오후 1시 ~ 2018/05/16 (수) 오후 6시" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:00"), new StringDateTimeDTO("2018-05-16", "오후 06:10"), "2018/05/16 (수) 오후 1시 ~ 2018/05/16 (수) 오후 6시 10분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:00"), new StringDateTimeDTO("2018-05-16", "오후 06:20"), "2018/05/16 (수) 오후 1시 ~ 2018/05/16 (수) 오후 6시 20분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:00"), new StringDateTimeDTO("2018-05-16", "오후 06:30"), "2018/05/16 (수) 오후 1시 ~ 2018/05/16 (수) 오후 6시 30분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:00"), new StringDateTimeDTO("2018-05-16", "오후 06:40"), "2018/05/16 (수) 오후 1시 ~ 2018/05/16 (수) 오후 6시 40분" ),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 01:00"), new StringDateTimeDTO("2018-05-16", "오후 06:50"), "2018/05/16 (수) 오후 1시 ~ 2018/05/16 (수) 오후 6시 50분" )) });
		
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format),
				new DateTimeDTO(LocalDate.of(2018, 5, 31), null), LocalDate.of(2018, 5, 31).format(dateFormat),
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오전 07:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오전 08:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오전 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오전 09:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오전 10:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오전 10시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오전 11:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오전 11시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오후 12:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오후 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오후 01:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오후 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오후 02:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오후 2시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오후 03:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-31", "오후 04:-"), "2018/05/30 (수) 오전 11시 ~ 2018/05/31 (목) 오후 4시")) });
		
		return params;
	}
	
	@Test
	public void test() throws Exception {
		//
		// When
		//
		List<EventDTO> outputList = recommendationService.getRecommendations(input, inputStartDate, inputEndDate);

		//
		// Then
		//
		assertEquals(expectedList.size(), outputList.size());
		for (int i = 0; i < outputList.size(); i++) {
			assertEquals(expectedList.get(i).toString(), outputList.get(i).toString());
		}
	}

}
