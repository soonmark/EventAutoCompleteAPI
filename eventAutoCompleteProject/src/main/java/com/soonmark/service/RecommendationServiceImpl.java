package com.soonmark.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soonmark.core.RecommendationManager;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.RecomResultDTO;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	@Autowired private RecommendationManager recommendationManager;

	private Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
	
	@Override
	public RecomResultDTO autoCompleteEvent(String inputEvent, String start, String end) throws Exception {
		
		DateTimeDTO startDate = recommendationManager.getObjectFromJson(start);
		DateTimeDTO endDate = recommendationManager.getObjectFromJson(end);
		
		List<EventDTO> list = getRecommendations(inputEvent, startDate, endDate);
		
		RecomResultDTO recomResult = new RecomResultDTO(inputEvent, list);
		
		logger.info("최종 response : " + recomResult.toString());
		
		return recomResult;
	}
	
	@Override
	public List<EventDTO> getRecommendations(String inputText, DateTimeDTO startDate, DateTimeDTO endDate) throws Exception {
		
		return recommendationManager.getRecommendations(inputText, startDate, endDate);
	}
}
