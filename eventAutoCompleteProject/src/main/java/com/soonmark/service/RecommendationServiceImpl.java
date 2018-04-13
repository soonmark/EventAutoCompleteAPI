package com.soonmark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soonmark.core.RecommendationManager;
import com.soonmark.domain.DateTimeDTO;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	@Autowired private RecommendationManager recommendationManager;

	@Override
	public List<DateTimeDTO> getRecommendation(String inputText) throws Exception {
		
		return recommendationManager.getRecommendations(inputText);
	}
}
