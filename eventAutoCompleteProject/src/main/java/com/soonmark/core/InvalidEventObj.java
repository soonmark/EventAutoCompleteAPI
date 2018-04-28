package com.soonmark.core;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.StringDateTimeDTO;

public class InvalidEventObj {
	InvalidDateTimeObj startDate;
	InvalidDateTimeObj endDate;

	public InvalidEventObj() {
		this.startDate = null;
		this.endDate = null;
	}

	public InvalidEventObj(InvalidDateTimeObj startDate, InvalidDateTimeObj endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public InvalidDateTimeObj getStartDate() {
		return startDate;
	}

	public void setStartDate(InvalidDateTimeObj startDate) {
		this.startDate = startDate;
	}

	public InvalidDateTimeObj getEndDate() {
		return endDate;
	}

	public void setEndDate(InvalidDateTimeObj endDate) {
		this.endDate = endDate;
	}

	public EventDTO toEventDTO() {
		StringDateTimeDTO startDateTimeDTO = null;
		if (startDate != null) {
			startDateTimeDTO = startDate.tostrDtDTO();
		}
		StringDateTimeDTO endDateTimeDTO = null;
		if (endDate != null) {
			endDateTimeDTO = endDate.tostrDtDTO();
		}

		// isAllDayEvent 세팅하기
		boolean allDayEvent = isAllDayEvent();
		// displayName 세팅하기
		String displayName = createDisplayNameBy(startDateTimeDTO, endDateTimeDTO, allDayEvent);

		EventDTO eventDTO = new EventDTO(startDateTimeDTO, endDateTimeDTO, /* allDayEvent, */ displayName);

		return eventDTO;
	}

	private boolean isAllDayEvent() {
		DateTimeDTO start = null;
		if (startDate != null) {
			start = startDate.toDtDTO();
		}
		DateTimeDTO end = null;
		if (endDate != null) {
			end = endDate.toDtDTO();
		}

		if (startDate != null && end != null) {
			if (start.getDate().isEqual(end.getDate())) {
				if (start.getTime().getHour() == 0 && start.getTime().getMinute() == 0 && end.getTime().getHour() == 23
						&& end.getTime().getMinute() == 59) {
					return true;
				}
			}
		}

		return false;
	}

	private String createDisplayNameBy(StringDateTimeDTO startDateTime, StringDateTimeDTO endDateTime,
			boolean isAllDayEvent) {
		String displayName = "";
		if (startDateTime != null) {
			if (startDateTime.getDate() != null) {
				displayName += startDateTime.getDate() + " ";
			}
			if (startDateTime.getTime() != null && !startDateTime.getTime().equals("")) {
				displayName += startDateTime.getTime() + " ";
			}
		}
		displayName += "~";
		if (endDateTime != null) {
			if (endDateTime.getDate() != null) {
				displayName += " " + endDateTime.getDate();
			}
			if (endDateTime.getTime() != null && !endDateTime.getTime().equals("")) {
				displayName += " " + endDateTime.getTime();
			}
		}
		if (isAllDayEvent) {
			displayName += " 종일";
		}

		return displayName;
	}
}
