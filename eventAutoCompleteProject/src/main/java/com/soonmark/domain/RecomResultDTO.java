package com.soonmark.domain;

import java.util.List;

public class RecomResultDTO {
	String parsedText;
	List<EventDTO> recommendations;

	public RecomResultDTO(String parsedText, List<EventDTO> recommendations) {
		super();
		this.parsedText = parsedText;
		this.recommendations = recommendations;
	}

	public String getParsedText() {
		return parsedText;
	}

	public void setParsedText(String parsedText) {
		this.parsedText = parsedText;
	}

	public List<EventDTO> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(List<EventDTO> recommendations) {
		this.recommendations = recommendations;
	}

	@Override
	public String toString() {
		String jsonString = "";

		jsonString += "{\"parsedText\":\"" + this.parsedText;
		jsonString += "\", \"recommendations\":" + this.recommendations.toString() + "\"}";

		return jsonString;
	}
}
