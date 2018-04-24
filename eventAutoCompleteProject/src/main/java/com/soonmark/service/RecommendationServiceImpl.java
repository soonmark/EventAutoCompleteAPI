package com.soonmark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soonmark.core.RecommendationManager;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	@Autowired private RecommendationManager recommendationManager;

	@Override
	public List<DateTimeDTO> getRecommendations(String inputText, String startDate, String endDate) throws Exception {
		
		return recommendationManager.getRecommendations(inputText, startDate, endDate);
	}

	@Override
	public EventDTO autoCompleteEvent(String inputEvent, String start, String end) throws Exception {
		
		List<DateTimeDTO> list = getRecommendations(inputEvent, start, end);
		DateTimeDTO startDate = recommendationManager.getObjectFromJson(start);
		DateTimeDTO endDate = recommendationManager.getObjectFromJson(end);
		
		EventDTO event = new EventDTO(inputEvent, startDate, endDate, list);
		
		return event;
	}
}
