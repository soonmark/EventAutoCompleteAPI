package com.soonmark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;


@Service
public interface RecommendationService {
	
	public List<DateTimeDTO> getRecommendations(String inputText, String startDate, String endDate) throws Exception ;

	public EventDTO autoCompleteEvent(String inputEvent, String startDate, String endDate) throws Exception ;
	
}
