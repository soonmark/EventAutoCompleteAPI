package com.soonmark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soonmark.domain.DateTimeDTO;


@Service
public interface RecommendationService {
	
	public List<DateTimeDTO> getRecommendations(String inputText) throws Exception ;
	
}
