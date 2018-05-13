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
public class WithStartDateTest {

	private TestContextManager testContextManager;

	public WithStartDateTest(String inputText, DateTimeDTO inputStartDate, String inputDateTime, boolean noMin, List<EventDTO> expectedList) {
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
	@Parameterized.Parameters(name = "\"{0}\" with startDate ({2}) withoutMin ({3})")
	public static Collection<Object[]> testCases() {
		Collection<Object[]> params = new ArrayList<Object[]>();

		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd a hh:mm");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		params.add(new Object[] {"15년 4월 9일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0), true), LocalDateTime.of(2018, 5, 30, 11, 0).format(format), true,
				new ArrayList<Object>() });

		params.add(new Object[] {"15년 4월 9일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0), false), LocalDateTime.of(2018, 5, 30, 11, 0).format(format), false,
				new ArrayList<Object>() });

		params.add(new Object[] {"1일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0), false), LocalDateTime.of(2018, 5, 30, 11, 0).format(format), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-06-01", ""), "2018/05/30 (수) 오전 11시 ~ 2018/06/01 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-07-01", ""), "2018/05/30 (수) 오전 11시 ~ 2018/07/01 (일)")) });
		
		params.add(new Object[] {"1일", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0), true), LocalDateTime.of(2018, 5, 30, 11, 0).format(format), true,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-06-01", ""), "2018/05/30 (수) 오전 11시 ~ 2018/06/01 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-07-01", ""), "2018/05/30 (수) 오전 11시 ~ 2018/07/01 (일)")) });
		
		params.add(new Object[] {"1일", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", ""), new StringDateTimeDTO("2018-06-01", ""), "2018/05/30 (수) ~ 2018/06/01 (금)"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", ""), new StringDateTimeDTO("2018-07-01", ""), "2018/05/30 (수) ~ 2018/07/01 (일)")) });
		
		params.add(new Object[] {"1:00", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat), false, 
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 01:00"), null, "2018/05/30 (수) 오전 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 01:00"), null, "2018/05/30 (수) 오후 1시")) });
		
		params.add(new Object[] {"3시", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(14, 0), false), LocalDateTime.of(2018, 5, 30, 14, 0).format(format), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 02:00"), new StringDateTimeDTO("2018-05-30", "오후 03:-"), "2018/05/30 (수) 오후 2시 ~ 2018/05/30 (수) 오후 3시")) });

		params.add(new Object[] {"3시", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(14, 0), true), LocalDateTime.of(2018, 5, 30, 14, 0).format(format), true,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 02:00"), new StringDateTimeDTO("2018-05-30", "오후 03:-"), "2018/05/30 (수) 오후 2시 ~ 2018/05/30 (수) 오후 3시")) });
		
		params.add(new Object[] {"15시 30분", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 03:30"), null, "2018/05/30 (수) 오후 3시 30분")) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 1)), LocalDateTime.of(2018, 5, 30, 11, 1).format(format), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 12:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 12시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 01:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 1시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 02:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 2시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 03:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 3시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 04:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 4시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 05:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 5시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 06:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 6시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 07:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 7시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 08:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 8시 1분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:01"), new StringDateTimeDTO("2018-05-30", "오후 09:01"), "2018/05/30 (수) 오전 11시 1분 ~ 2018/05/30 (수) 오후 9시 1분")) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0), true), LocalDateTime.of(2018, 5, 30, 11, 0).format(format), true,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), null, "2018/05/30 (수) 오전 11시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:10"), null, "2018/05/30 (수) 오전 11시 10분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:20"), null, "2018/05/30 (수) 오전 11시 20분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:30"), null, "2018/05/30 (수) 오전 11시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:40"), null, "2018/05/30 (수) 오전 11시 40분"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:50"), null, "2018/05/30 (수) 오전 11시 50분")) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0), false), LocalDateTime.of(2018, 5, 30, 11, 0).format(format), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 12:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 01:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 02:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 2시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 03:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 04:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 05:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 5시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 06:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 6시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 07:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 08:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:00"), new StringDateTimeDTO("2018-05-30", "오후 09:00"), "2018/05/30 (수) 오전 11시 ~ 2018/05/30 (수) 오후 9시")) });
		
		// 오늘이면 지금 시간보다 미래로 추천
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 16), LocalTime.of(18, 0), true), LocalDateTime.of(2018, 5, 16, 18, 0).format(format), true,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:00"), null, "2018/05/16 (수) 오후 6시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:10"), null, "2018/05/16 (수) 오후 6시 10분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:20"), null, "2018/05/16 (수) 오후 6시 20분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:30"), null, "2018/05/16 (수) 오후 6시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:40"), null, "2018/05/16 (수) 오후 6시 40분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:50"), null, "2018/05/16 (수) 오후 6시 50분")) });

		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 16), null), LocalDate.of(2018, 5, 16).format(dateFormat), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 04:-"), null, "2018/05/16 (수) 오후 4시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:-"), null, "2018/05/16 (수) 오후 5시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 06:-"), null, "2018/05/16 (수) 오후 6시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 07:-"), null, "2018/05/16 (수) 오후 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 08:-"), null, "2018/05/16 (수) 오후 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 09:-"), null, "2018/05/16 (수) 오후 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 10:-"), null, "2018/05/16 (수) 오후 10시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 11:-"), null, "2018/05/16 (수) 오후 11시")) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 16), LocalTime.of(17, 0), true), LocalDateTime.of(2018, 5, 16, 17, 0).format(format), true,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:00"), null, "2018/05/16 (수) 오후 5시"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:10"), null, "2018/05/16 (수) 오후 5시 10분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:20"), null, "2018/05/16 (수) 오후 5시 20분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:30"), null, "2018/05/16 (수) 오후 5시 30분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:40"), null, "2018/05/16 (수) 오후 5시 40분"),
						new EventDTO(new StringDateTimeDTO("2018-05-16", "오후 05:50"), null, "2018/05/16 (수) 오후 5시 50분")) });
		
		params.add(new Object[] {"", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 07:-"), null, "2018/05/30 (수) 오전 7시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 08:-"), null, "2018/05/30 (수) 오전 8시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 09:-"), null, "2018/05/30 (수) 오전 9시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 10:-"), null, "2018/05/30 (수) 오전 10시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 11:-"), null, "2018/05/30 (수) 오전 11시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 12:-"), null, "2018/05/30 (수) 오후 12시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 01:-"), null, "2018/05/30 (수) 오후 1시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 02:-"), null, "2018/05/30 (수) 오후 2시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 03:-"), null, "2018/05/30 (수) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 04:-"), null, "2018/05/30 (수) 오후 4시")) });

		params.add(new Object[] {"1시~3시", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 01:00"), new StringDateTimeDTO("2018-05-30", "오전 03:-"), "2018/05/30 (수) 오전 1시 ~ 2018/05/30 (수) 오전 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 01:00"), new StringDateTimeDTO("2018-05-30", "오후 03:-"), "2018/05/30 (수) 오전 1시 ~ 2018/05/30 (수) 오후 3시"),
						new EventDTO(new StringDateTimeDTO("2018-05-30", "오후 01:00"), new StringDateTimeDTO("2018-05-30", "오후 03:-"), "2018/05/30 (수) 오후 1시 ~ 2018/05/30 (수) 오후 3시")) });
		
		params.add(new Object[] {"1시~3시", new DateTimeDTO(LocalDate.of(2018, 5, 30), LocalTime.of(11, 0)), LocalDateTime.of(2018, 5, 30, 11, 0).format(format), false,
				new ArrayList<Object>() });
		
		params.add(new Object[] {"3시~1시", new DateTimeDTO(LocalDate.of(2018, 5, 30), null), LocalDate.of(2018, 5, 30).format(dateFormat), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-05-30", "오전 03:00"), new StringDateTimeDTO("2018-05-30", "오후 01:-"), "2018/05/30 (수) 오전 3시 ~ 2018/05/30 (수) 오후 1시")) });
		
		params.add(new Object[] {"3/2", new DateTimeDTO(LocalDate.of(2019, 5, 30), null), LocalDate.of(2019, 5, 30).format(dateFormat), false,
				Arrays.asList(new EventDTO(new StringDateTimeDTO("2019-05-30", ""), new StringDateTimeDTO("2020-03-02", ""), "2019/05/30 (목) ~ 2020/03/02 (월)")) });
		
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
		assertEquals( expectedList.size(), outputList.size());
		for (int i = 0; i < outputList.size(); i++) {
			assertEquals(expectedList.get(i).toString(), outputList.get(i).toString());
		}
	}

}
