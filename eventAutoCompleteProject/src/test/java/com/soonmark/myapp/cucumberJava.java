package com.soonmark.myapp;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContextManager;

import com.soonmark.core.RecommendationManager;
import com.soonmark.domain.EventDTO;
import com.soonmark.service.RecommendationService;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class cucumberJava {
	List<EventDTO> outputList;
	
	RecommendationManager recommendationManager;
	
	@Given("^I didn't select any date and time yet$")
	public void i_didnt_select_any_date_and_time_yet() {
	}

	@When("^I type (.*)$")
	public void i_type(String text) {
		
		recommendationManager = new RecommendationManager();
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
