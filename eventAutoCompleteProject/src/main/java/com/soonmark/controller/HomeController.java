package com.soonmark.controller;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.RecomResultDTO;
import com.soonmark.service.RecommendationService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private Logger logger = LoggerFactory.getLogger(HomeController.class);

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		logger.info("Welcome home! The client locale is {}.", locale);

		return "home";
	}

	@Autowired private RecommendationService recommendationService;

	// Ajax request 받아서 json 형태로 response.
	// 시간, 날짜 추천 리스트 보냄
	@RequestMapping(value = "autoCompletion", method = RequestMethod.POST, produces = "application/json; charset=utf8")
	@ResponseBody
	public RecomResultDTO inputProcess(HttpServletRequest httpServletRequest) throws Exception {

		// 입력값 불러오기
		String inputEvent = httpServletRequest.getParameter("inputText");
		String startDate = httpServletRequest.getParameter("startDate");
		String endDate = httpServletRequest.getParameter("endDate");
		
		if(startDate != null) {
			logger.info(startDate.toString());
		}
		else {
			logger.info("No startDate");
		}
		if(endDate != null) {
			logger.info(endDate.toString());
		}
		else {
			logger.info("No endDate");
		}
		
		// 서비스에 넘김
		return recommendationService.autoCompleteEvent(inputEvent, startDate, endDate);
	}
}
