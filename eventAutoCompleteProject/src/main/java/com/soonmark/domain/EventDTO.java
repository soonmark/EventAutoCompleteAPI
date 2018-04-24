package com.soonmark.domain;

import java.util.List;

public class EventDTO {
	String parsedText;
	DateTimeDTO startDate;
	DateTimeDTO endDate;
	List<DateTimeDTO> recommendations;

	public EventDTO(String parsedText, DateTimeDTO startDate, DateTimeDTO endDate, List<DateTimeDTO> recommendations) {
		this.parsedText = parsedText;
		this.startDate = startDate;
		this.endDate = endDate;
		this.recommendations = recommendations;
	}

	public String getParsedText() {
		return parsedText;
	}

	public void setParsedText(String parsedText) {
		this.parsedText = parsedText;
	}

	public DateTimeDTO getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTimeDTO startDate) {
		this.startDate = startDate;
	}

	public DateTimeDTO getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTimeDTO endDate) {
		this.endDate = endDate;
	}

	public List<DateTimeDTO> getRecommendations() {
		return recommendations;
	}

	public void setRecommendations(List<DateTimeDTO> recommendations) {
		this.recommendations = recommendations;
	}
	
/*	@Override
	public String toString() {
		String jsonString = "";
		jsonString = "{\"parsedText\":\"" + this.parsedText
				+ "\", \"startDate\":\"" + this.startDate.toString()
				+ "\", \"endDate\":\"" + this.endDate.toString()
				+ "\", \"recommendations\":\"" + this.recommendations.toString() + "\"}";
		return jsonString;
	}*/
}
