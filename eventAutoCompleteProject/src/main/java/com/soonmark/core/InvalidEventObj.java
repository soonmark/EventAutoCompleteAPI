package com.soonmark.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.Locale;

import com.soonmark.domain.AppConstants;
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
	
	public InvalidDateTimeObj getStartOrEnd(boolean isStartDate) {
		if(isStartDate) {
			return startDate;
		}
		else {
			return endDate;
		}
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
				if (start.getTime() != null && end.getTime() != null) {
					if (start.getTime().getHour() == 0 && start.getTime().getMinute() == 0
							&& end.getTime().getHour() == 23 && end.getTime().getMinute() == 59) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	private String changeStringDateFormat(String origin) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate localDate = LocalDate.parse(origin, formatter);
		
		return localDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
	}

	private String changeStringTimeFormat(String origin) {
		LocalTime localTime;
		try {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm");
		localTime = LocalTime.parse(origin, formatter);
		}catch(DateTimeParseException e) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:-");
			localTime = LocalTime.parse(origin, formatter);
		}

		if(localTime.getMinute() == 0) {
			return localTime.format(DateTimeFormatter.ofPattern("a h시"));
		}
		return localTime.format(DateTimeFormatter.ofPattern("a h시 m분"));
	}

	private String createDisplayNameBy(StringDateTimeDTO startDateTime, StringDateTimeDTO endDateTime,
			boolean isAllDayEvent) {
		String displayName = "";
		if (startDateTime != null) {
			if (startDateTime.getDate() != null) {
				
				displayName += changeStringDateFormat(startDateTime.getDate()) + " ("
						+ startDateTime.toDateTimeDTO().getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA) + ")";
			}
			if (startDateTime.getTime() != null && !startDateTime.getTime().equals("")) {
				displayName += " " + changeStringTimeFormat(startDateTime.getTime());
			}
		}
		if (endDateTime != null) {
			displayName += " ~ ";
			if (endDateTime.getDate() != null) {
				displayName += changeStringDateFormat(endDateTime.getDate())
						+ " (" + endDateTime.toDateTimeDTO().getDate().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREA) + ")";
			}
			if (endDateTime.getTime() != null && !endDateTime.getTime().equals("")) {
				displayName += " " + changeStringTimeFormat(endDateTime.getTime());
			}
		}
		if (isAllDayEvent) {
			displayName += " 종일";
		}

		return displayName;
	}
}
