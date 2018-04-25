package com.soonmark.domain;

public class EventDTO {
	private DateTimeDTO startDate;
	private DateTimeDTO endDate;
	private boolean allDayEvent;
	private String displayName;
	
	public EventDTO(DateTimeDTO startDate, DateTimeDTO endDate, boolean allDayEvent, String displayName) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.allDayEvent = allDayEvent;
		this.displayName = displayName;
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
	
	public boolean isAllDayEvent() {
		return allDayEvent;
	}
	
	public void setAllDayEvent(boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public String toString() {
		boolean dateExists = false;
		String jsonString = "";
		
		if(this.startDate != null) {	
			jsonString += "{\"startDate\":\"" + this.startDate.toString();
			dateExists = true;
		}
		if(this.endDate != null) {
			jsonString += "\", \"endDate\":\"" + this.endDate.toString();
			dateExists = true;
		}
		if(dateExists) {
			jsonString += "\", \"allDayEvent\":" + this.allDayEvent;
			jsonString += "\", \"displayName\":" + this.displayName;
			jsonString += "\"}";
		}
		
		return jsonString;
	}
}
