package com.soonmark.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soonmark.core.RecommendationManager;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	@Autowired private RecommendationManager recommendationManager;

	private Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
	
	@Override
	public EventDTO autoCompleteEvent(String inputEvent, String start, String end) throws Exception {
		
		DateTimeDTO startDate = recommendationManager.getObjectFromJson(start);
		DateTimeDTO endDate = recommendationManager.getObjectFromJson(end);
		
		List<DateTimeDTO> list = getRecommendations(inputEvent, startDate, endDate);
		
		EventDTO event = new EventDTO(inputEvent, startDate, endDate, list);
		
		logger.info("최종 response : " + event.toString());
		
		return event;
	}
	
	@Override
	public List<DateTimeDTO> getRecommendations(String inputText, DateTimeDTO startDate, DateTimeDTO endDate) throws Exception {
		
		return recommendationManager.getRecommendations(inputText, startDate, endDate);
	}
}
