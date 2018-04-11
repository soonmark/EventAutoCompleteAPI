package com.soonmark.service;

import org.springframework.stereotype.Service;
import com.soonmark.managers.RecommendationManager;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	private RecommendationManager recommendationManager;


	@Override
	public String getRecommendation(String inputText) throws Exception {
		
		recommendationManager = new RecommendationManager(inputText);
		
		return recommendationManager.getRecommendations();
	}
}
