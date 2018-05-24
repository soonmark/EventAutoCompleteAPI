package com.soonmark.myapp;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = {"src/test/java/com/soonmark/myapp/is_it_a_proper_date.feature"})
public class RunCucumberTest {
	
}