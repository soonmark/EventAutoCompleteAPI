package com.soonmark.myapp;

import static org.junit.Assert.*;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.soonmark.core.DateTimeListManager;
import com.soonmark.core.ListElementDeduplicator;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
		locations = {"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
public class CoreModuleTester {

	@Inject
	private WebApplicationContext wac;
	
	private MockMvc mockMvc;
	
	@Autowired
	ListElementDeduplicator listElementDeduplicator;
	
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
//		logger.info("setup..");
	}
	
	@Test
	public void test_Gather_Partials_To_() {
		DateTimeListManager tmpList = new DateTimeListManager();
		listElementDeduplicator.gatherPartialsTo(tmpList);
	}

}
