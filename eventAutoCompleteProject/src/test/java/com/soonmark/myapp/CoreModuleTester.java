package com.soonmark.myapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.service.RecommendationService;

import cucumber.api.PendingException;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CoreModuleTester {
	
	final int NO_RECOM_NUM = 0;
	final int ONE_RECOM = 1;
	final int DEFAULT_RECOM_NUM = 2;
	
	RecommendationManager recommendationManager;
	List<String> inputText;
	DateTimeDTO selectedStartDate;
	DateTimeDTO selectedEndDate;
	
	@Before
	public void setup() {
		recommendationManager = new RecommendationManager();
	}
	
	@Given("^I didn't select any date and time yet$")
	public void i_didn_t_select_any_date_and_time_yet() throws Exception {
		selectedStartDate = null;
		selectedEndDate = null;
	}

	@When("^I type$")
	public void i_type_text(List<String> inputText) throws Exception {
		this.inputText = inputText;
	}

	@Then("^I should see a proper recommendation$")
	public void i_should_see_a_proper_recommendation(List<String> expectedDisplayName) throws Exception {
		assertEquals(inputText.size(), expectedDisplayName.size());
		
		for(int i = 0 ; i < inputText.size() ; i++) {
			for(int j = 0 ; j < ONE_RECOM ; j++) {
				assertEquals(ONE_RECOM, recommendationManager.getRecommendations(inputText.get(i), selectedStartDate, selectedEndDate).size());
				assertEquals(expectedDisplayName.get(i), recommendationManager.getRecommendations(inputText.get(i), selectedStartDate, selectedEndDate).get(j).getDisplayName());
			}
		}
	}
	
	@Then("^I should see no recommendation$")
	public void i_should_see_no_recommendation() throws Exception {
		for(int i = 0 ; i < inputText.size() ; i++) {
			assertEquals(NO_RECOM_NUM, recommendationManager.getRecommendations(inputText.get(i), selectedStartDate, selectedEndDate).size());
		}
	}
	
	@Then("^I should see proper recommendations$")
	public void i_should_see_proper_recommendations(List<List<String>> expectedDisplayNames) throws Exception {
		assertEquals(inputText.size(), expectedDisplayNames.size());
		
		for(int i = 0 ; i < inputText.size() ; i++) {
			for(int j = 0 ; j < DEFAULT_RECOM_NUM ; j++) {
				assertEquals(DEFAULT_RECOM_NUM, recommendationManager.getRecommendations(inputText.get(i), selectedStartDate, selectedEndDate).size());
				assertEquals(expectedDisplayNames.get(i).get(j), recommendationManager.getRecommendations(inputText.get(i), selectedStartDate, selectedEndDate).get(j).getDisplayName());
			}
		}
	}
}
