package com.soonmark.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soonmark.core.RecommendationManager;
import com.soonmark.domain.StringDateTimeDTO;
import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.RecomResultDTO;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	@Autowired private RecommendationManager recommendationManager;

	private Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
	
	@Override
	public List<EventDTO> autoCompleteEvent(String inputEvent, String start, String end) throws Exception {
		
		StringDateTimeDTO startStrDate = recommendationManager.getObjectFromJson(start);
		StringDateTimeDTO endStrDate = recommendationManager.getObjectFromJson(end);
		 
		DateTimeDTO startDate = null;
		DateTimeDTO endDate = null;
		
		if(startStrDate != null) {
			startDate = startStrDate.toDateTimeDTO();
		}
		if(endStrDate != null) {
			endDate = endStrDate.toDateTimeDTO();
		}
		
		List<EventDTO> list = getRecommendations(inputEvent, startDate, endDate);
		
//		RecomResultDTO recomResultDTO = new RecomResultDTO(inputEvent, list);
		
		logger.info("최종 response : " + list.toString());
		
		return list;
	}
	
	@Override
	public List<EventDTO> getRecommendations(String inputText, DateTimeDTO startDate, DateTimeDTO endDate) throws Exception {

		return recommendationManager.getRecommendations(inputText, startDate, endDate);
	}
}
