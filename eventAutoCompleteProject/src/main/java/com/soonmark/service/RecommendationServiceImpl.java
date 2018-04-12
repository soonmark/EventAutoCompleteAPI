package com.soonmark.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.managers.RecommendationManager;

@Service
public class RecommendationServiceImpl implements RecommendationService {

	@Autowired private RecommendationManager recommendationManager;

	@Override
	public List<DateTimeDTO> getRecommendation(String inputText) throws Exception {
		
		return recommendationManager.getRecommendations(inputText);
	}
}
