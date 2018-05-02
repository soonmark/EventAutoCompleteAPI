package com.soonmark.core;

public class PeriodManager {

	private DateTimeListMgrSet startDateListMgr;
	private DateTimeListMgrSet endDateListMgr;
	
	private String from;
	private String to;
	private String during;
	
	public PeriodManager(String from, String to) {
		this.from = from;
		this.to = to;
		this.startDateListMgr = new DateTimeListMgrSet();
		this.endDateListMgr = new DateTimeListMgrSet();
	}
	
	public PeriodManager(String during) {
		this.during = during;
		this.startDateListMgr = new DateTimeListMgrSet();
		this.endDateListMgr = new DateTimeListMgrSet();
	}

	public DateTimeListMgrSet getStartDateListMgr() {
		return startDateListMgr;
	}

	public void setStartDateListMgr(DateTimeListMgrSet startDateListMgr) {
		this.startDateListMgr = startDateListMgr;
	}

	public DateTimeListMgrSet getEndDateListMgr() {
		return endDateListMgr;
	}

	public void setEndDateListMgr(DateTimeListMgrSet endDateListMgr) {
		this.endDateListMgr = endDateListMgr;
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
}
