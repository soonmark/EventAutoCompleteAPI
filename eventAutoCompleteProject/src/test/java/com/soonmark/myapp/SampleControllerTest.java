package com.soonmark.myapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.service.RecommendationService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
		locations = {"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
public class SampleControllerTest {
	
	private static final Logger logger = LoggerFactory.getLogger(SampleControllerTest.class);

	@Inject
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		logger.info("setup..");
	}
	
//	@Test
//	public void test() throws Exception {
//		mockMvc.perform(MockMvcRequestBuilders.get("/"));
//	}

	@Autowired RecommendationService recommendationService;
	
	@Test
	public void testGetRecommendation() throws Exception {
		//
		// Given
		//
		String input = "4월 9일 월요일 19시";
		List<DateTimeDTO> expectedList = new ArrayList<DateTimeDTO>();
		expectedList.add(new DateTimeDTO(2018, 4, 9, DayOfWeek.MONDAY, 19, 0, false));
		expectedList.add(new DateTimeDTO(2019, 4, 9, DayOfWeek.TUESDAY, 19, 0, false));
		
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
