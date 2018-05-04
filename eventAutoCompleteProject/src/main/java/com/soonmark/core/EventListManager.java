package com.soonmark.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.soonmark.domain.EventDTO;

public class EventListManager {
	private List<InvalidEventObj> evObjList;
	private boolean focusStart ;
	
	public EventListManager() {
		this.evObjList = new ArrayList<InvalidEventObj>();
		focusStart = true;
	}

	public EventListManager(List<InvalidEventObj> dtObjList) {
		this.evObjList = dtObjList;
		focusStart = true;
	}

	public List<EventDTO> getEventDTOList() {
		List<EventDTO> newList = new ArrayList<EventDTO>();
		
		Iterator<InvalidEventObj> iter = evObjList.iterator();
		while (iter.hasNext()) {
			newList.add(iter.next().toEventDTO());
		}
		return newList;
	}
	
	public boolean isFocusStart() {
		return focusStart;
	}

	public void setFocusStart(boolean focusStart) {
		this.focusStart = focusStart;
	}

	public void insertDtObj(InvalidEventObj dtObj) {
		evObjList.add(dtObj);
	}

	public InvalidEventObj getElement(int index) {
		return evObjList.get(index);
	}
	
	public List<InvalidEventObj> getEvMgrList() {
		return evObjList;
	}
	
	public void setEvObjList(List<InvalidEventObj> evObjList) {
		this.evObjList = evObjList;
	}

	public void sortByPriority() {
		AscendingEvents ascending = new AscendingEvents();
		Collections.sort(this.getEvMgrList(), ascending);
	}
	
	public void deleteDtObj(int index) {
		evObjList.remove(index);
	}
}
