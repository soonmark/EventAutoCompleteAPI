package com.soonmark.service;

import org.springframework.stereotype.Service;


@Service
public interface RecommendationService {
	
	public String getRecommendation(String inputText) throws Exception ;
	
	public boolean blockInvalidCharacters(String inputText);
	
	
}
