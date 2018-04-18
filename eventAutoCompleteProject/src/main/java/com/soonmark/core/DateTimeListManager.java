package com.soonmark.core;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.TokenType;

public class DateTimeListManager {
	private List<DateTimeLogicalObject> dtObjList;
	private ListElementDeduplicator listElementDeduplicator;
	private ListMerger listMerger;
	TokenType listType;

	public DateTimeListManager() {
		dtObjList = new ArrayList<DateTimeLogicalObject>();
		listElementDeduplicator = new ListElementDeduplicator();
		listMerger = new ListMerger();
		this.listType = null;
	}

	public DateTimeListManager(TokenType listType) {
		dtObjList = new ArrayList<DateTimeLogicalObject>();
		listElementDeduplicator = new ListElementDeduplicator();
		listMerger = new ListMerger();
		this.listType = listType;
	}
	
	public List<DateTimeDTO> getDtDTOList() {
		List<DateTimeDTO> newList = new ArrayList<DateTimeDTO>();
		
		Iterator<DateTimeLogicalObject> iter = dtObjList.iterator();
		while(iter.hasNext()) {
			newList.add(iter.next().toDTO());
		}
		return newList;
	}

	public List<DateTimeLogicalObject> getDtMgrList() {
		return dtObjList;
	}

	public TokenType getListType() {
		return listType;
	}

	public void setListType(TokenType listType) {
		this.listType = listType;
	}

	public void insertDtObj(DateTimeLogicalObject dtObj) {
		dtObjList.add(dtObj);
	}

	public void deleteDtObj(int index) {
		dtObjList.remove(index);
	}

	public void clearList() {
		dtObjList.clear();
	}

	public DateTimeLogicalObject getElement(int index) {
		return dtObjList.get(index);
	}

	public void setDayToElement(int index, DayOfWeek val) {
		dtObjList.get(index).setDay(val);
	}

	void deduplicateElements() {
		listElementDeduplicator.mergeProcess(this, listType);
	}

	boolean isTargetMgrEmpty(DateTimeLogicalObject nonTarget) {
		boolean isEmpty = true;
		for (DateTimeEn d : DateTimeEn.values()) {
			if (d.getTypeNum() != listType.getInteger()) {
				continue;
			}
			if (nonTarget.hasInfo(d.ordinal())) {
				isEmpty = false;
			}
		}
		return isEmpty;
	}
	
	boolean isListEmpty(List<DateTimeLogicalObject> list) {
		boolean isEmpty = false;
		if (list.size() == 0) {
			isEmpty = true;
		}
		return isEmpty;
	}

	boolean ableToPut(DateTimeLogicalObject target, DateTimeLogicalObject nonTarget) {
		// target 의 원소 중 존재하는 값들이 모두 list 에 없으면 추가
		boolean ableToPut = true;
		for (DateTimeEn d : DateTimeEn.values()) {
			if (target.hasInfo(d.ordinal()) && nonTarget.hasInfo(d.ordinal())
					&& target.getByDateTimeEn(d) == nonTarget.getByDateTimeEn(d)) {
				ableToPut = false;
				break;
			}
		}

		return ableToPut;
	}

	// 리스트 간 병합
	void mergeByList(TokenType tokenType, DateTimeListManager list) {
		
		listMerger.listMergeByTokenType(tokenType, this, list);
	}
	
	void addPmTime() {
		DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
		dateTimeAdjuster.addPmTime(this);
	}

	public void sortByPriority() {
		Ascending ascending = new Ascending();
        Collections.sort(this.getDtMgrList(), ascending);
	}

	public boolean isDiffValueFromTargetMgr(DateTimeLogicalObject target, DateTimeLogicalObject nonTarget) {
		boolean isDiffValueFromTargetMgr = false;
		for (DateTimeEn d : DateTimeEn.values()) {
			if (target.hasInfo(d.ordinal()) && nonTarget.hasInfo(d.ordinal())
					&& target.getByDateTimeEn(d) != nonTarget.getByDateTimeEn(d)) {
				isDiffValueFromTargetMgr = true;
				break;
			}
		}

		return isDiffValueFromTargetMgr;
	}

}

class Ascending implements Comparator<DateTimeLogicalObject> {
	 
	@Override
	public int compare(DateTimeLogicalObject o1, DateTimeLogicalObject o2) {
		return o1.getPriority().compareTo(o2.getPriority());
	}
}