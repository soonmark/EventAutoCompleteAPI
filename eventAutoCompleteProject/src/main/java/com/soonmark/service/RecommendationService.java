package com.soonmark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;

@Service
public interface RecommendationService {
	
	public List<EventDTO> getRecommendations(String inputText, DateTimeDTO startDate, DateTimeDTO endDate) throws Exception ;

	public List<EventDTO> autoCompleteEvent(String inputEvent, String startDate, String endDate) throws Exception ;
	
}
