package com.soonmark.myapp;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
		TestDataList testDataList = new TestDataList();
		Iterator<TestData> iter = testDataList.getList().iterator();
		while(iter.hasNext()) {
			TestData data = iter.next();
			
			assertEquals(data.outputToString(),
						recommendationService.getRecommendation(data.getInput()));
		}
	}
	
}
