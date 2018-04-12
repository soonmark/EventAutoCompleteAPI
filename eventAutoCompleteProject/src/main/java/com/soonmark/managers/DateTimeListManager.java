package com.soonmark.managers;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.enums.DateTimeEn;
import com.soonmark.enums.TokenType;

public class DateTimeListManager {
	private List<DateTimeObjManager> dtObjList;
	private ListElementDeduplicator innerMerger;
	private ListMerger outerMerger;
	TokenType listType;

	public DateTimeListManager() {
		dtObjList = new ArrayList<DateTimeObjManager>();
		innerMerger = new ListElementDeduplicator();
		outerMerger = new ListMerger();
		this.listType = null;
	}

	public DateTimeListManager(TokenType listType) {
		dtObjList = new ArrayList<DateTimeObjManager>();
		innerMerger = new ListElementDeduplicator();
		outerMerger = new ListMerger();
		this.listType = listType;
	}
	
	public List<DateTimeDTO> getDtDTOList() {
		List<DateTimeDTO> newList = new ArrayList<DateTimeDTO>();
		
		Iterator<DateTimeObjManager> iter = dtObjList.iterator();
		while(iter.hasNext()) {
			newList.add(iter.next().getDateTimeDTO());
		}
		return newList;
	}

	public List<DateTimeObjManager> getDtMgrList() {
		return dtObjList;
	}

	public TokenType getListType() {
		return listType;
	}

	public void setListType(TokenType listType) {
		this.listType = listType;
	}

	public void insertDtObj(DateTimeObjManager dtObj) {
		dtObjList.add(dtObj);
	}

	public void deleteDtObj(int index) {
		dtObjList.remove(index);
	}

	public void clearList() {
		dtObjList.clear();
	}

	public DateTimeObjManager getElement(int index) {
		return dtObjList.get(index);
	}

//	public Iterator<DateTimeManager> getIter() {
//		return null;
//	}

	public void setDayToElement(int index, DayOfWeek val) {
		dtObjList.get(index).setDay(val);
	}

	void mergeItself() {
		innerMerger.mergeProcess(this, listType);
	}

	boolean isTargetMgrEmpty(DateTimeObjManager nonTarget) {
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
	
	boolean isListEmpty(List<DateTimeObjManager> list) {
		boolean isEmpty = false;
		if (list.size() == 0) {
			isEmpty = true;
		}
		return isEmpty;
	}

	boolean ableToPut(DateTimeObjManager target, DateTimeObjManager nonTarget) {
		// target 의 원소 중 존재하는 값들이 모두 list 에 없으면 추가
		boolean ableToPut = true;
		for (DateTimeEn d : DateTimeEn.values()) {
			if (target.hasInfo(d.ordinal()) && nonTarget.hasInfo(d.ordinal())) {
				ableToPut = false;
				break;
			}
		}

		return ableToPut;
	}

	// 리스트 간 병합
	void mergeBy(TokenType tokenType, DateTimeListManager list) {
		
		outerMerger.listMerge(this, list);
	}

}
