package com.soonmark.myapp;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.soonmark.service.RecommendationService;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = {"src/test/java/com/soonmark/myapp/is_it_a_proper_date.feature"})
@WebAppConfiguration
public class RunCucumberTest {
	
}
