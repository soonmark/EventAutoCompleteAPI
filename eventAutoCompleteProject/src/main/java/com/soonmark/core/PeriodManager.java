package com.soonmark.core;

import com.soonmark.domain.AppConstants;

public class PeriodManager {

	private DateTimeListMgrSet fromDTListMgr;
	private DateTimeListMgrSet toDTListMgr;
	
	private String from;
	private String to;
	private During during;
	
	public PeriodManager(String from, String to) {
		this.from = from;
		this.to = to;
		this.fromDTListMgr = new DateTimeListMgrSet();
		this.toDTListMgr = new DateTimeListMgrSet();
	}

	public PeriodManager(String from, String to, String during) {
		this.from = from;
		this.to = to;
		this.during = new During(during);
		this.fromDTListMgr = new DateTimeListMgrSet();
		this.toDTListMgr = new DateTimeListMgrSet();
	}

	public DateTimeListMgrSet getStartDateListMgr() {
		return fromDTListMgr;
	}

	public void setStartDateListMgr(DateTimeListMgrSet startDateListMgr) {
		this.fromDTListMgr = startDateListMgr;
	}

	public DateTimeListMgrSet getEndDateListMgr() {
		return toDTListMgr;
	}

	public void setEndDateListMgr(DateTimeListMgrSet endDateListMgr) {
		this.toDTListMgr = endDateListMgr;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public During getDuring() {
		return during;
	}

	public boolean duringExists() {
		During d = getDuring();
		if(d != null) {
			if(d.getValue() != AppConstants.NO_DATA) {
				return true;
			}
		}
		return false;
	}
}
