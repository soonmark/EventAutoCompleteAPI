package com.soonmark.myapp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.soonmark.core.RecommendationManager;
import com.soonmark.domain.EventDTO;

import cucumber.api.CucumberOptions;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.junit.Cucumber;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class cucumberJava {
	List<EventDTO> outputList;
	
	@Autowired
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Autowired
	RecommendationManager recommendationManager;
	
	@Before
	public void setup() {
//		logger.info("setup..");
	}
	
	@Given("^I didn't select any date and time yet$")
	public void i_didnt_select_any_date_and_time_yet() {
//		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@When("^I type (.*)$")
	public void i_type(String text) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		try {
			outputList = new ArrayList<EventDTO>();
			this.outputList = recommendationManager.getRecommendations(text, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Then("^I should see a (.*) of recommendations with size (.*)$")
	public void i_should_see(String recommendations, int size) {
		for(int i = 0 ; i < size ; i++) {
			assertEquals(recommendations, outputList.get(i).getDisplayName());
		}
	}
	
//	params.add(new Object[] {"15년 4월 9일",
//			Arrays.asList(new EventDTO(new StringDateTimeDTO("2015-04-09", ""), null, "2015/04/09 (목)")) });
	
//	params.add(new Object[] {"1일",
//			Arrays.asList(new EventDTO(new StringDateTimeDTO("2018-06-01", ""), null, "2018/06/01 (금)"),
//					new EventDTO(new StringDateTimeDTO("2018-07-01", ""), null, "2018/07/01 (일)")) });
}
