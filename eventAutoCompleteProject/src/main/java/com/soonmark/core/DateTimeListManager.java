package com.soonmark.core;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.EventDTO;
import com.soonmark.domain.TokenType;

public class DateTimeListManager {
	private List<InvalidDateTimeObj> dtObjList;
	TokenType listType;

	public DateTimeListManager() {
		dtObjList = new ArrayList<InvalidDateTimeObj>();
		this.listType = null;
	}

	public DateTimeListManager(TokenType listType) {
		dtObjList = new ArrayList<InvalidDateTimeObj>();
		this.listType = listType;
	}
	
	public List<EventDTO> getEventDTOList() {
		List<EventDTO> newList = new ArrayList<EventDTO>();
		
		Iterator<InvalidDateTimeObj> iter = dtObjList.iterator();
		while (iter.hasNext()) {
			newList.add(iter.next().toEventDTO());
		}
		return newList;
	}

//		public List<DateTimeDTO> getDtDTOList() {
//		List<DateTimeDTO> newList = new ArrayList<DateTimeDTO>();
//
//		Iterator<DateTimeLogicalObject> iter = dtObjList.iterator();
//		while (iter.hasNext()) {
//			newList.add(iter.next().toDTO());
//		}
//		return newList;
//	}

	public List<InvalidDateTimeObj> getDtMgrList() {
		return dtObjList;
	}

	public TokenType getListType() {
		return listType;
	}

	public void setListType(TokenType listType) {
		this.listType = listType;
	}

	public void insertDtObj(InvalidDateTimeObj dtObj) {
		dtObjList.add(dtObj);
	}

	public void deleteDtObj(int index) {
		dtObjList.remove(index);
	}

	public void clearList() {
		dtObjList.clear();
	}

	public InvalidDateTimeObj getElement(int index) {
		return dtObjList.get(index);
	}

	public void setDayToElement(int index, DayOfWeek val) {
		dtObjList.get(index).setDay(val);
	}

	void deduplicateElements() {
		new ListElementDeduplicator().mergeProcess(this, listType);
	}

	boolean isTargetMgrEmpty(InvalidDateTimeObj nonTarget) {
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

	boolean isListEmpty(List<InvalidDateTimeObj> list) {
		boolean isEmpty = false;
		if (list.size() == 0) {
			isEmpty = true;
		}
		return isEmpty;
	}

	boolean ableToPut(InvalidDateTimeObj target, InvalidDateTimeObj nonTarget) {
		// target 의 원소 중 존재하는 값들이 모두 list 에 없으면 추가
		boolean ableToPut = true;
		for (DateTimeEn d : DateTimeEn.values()) {
			if (target.hasInfo(d.ordinal()) && nonTarget.hasInfo(d.ordinal())
					&& target.getByDateTimeEn(d) == nonTarget.getByDateTimeEn(d)) {
				ableToPut = false;
				break;
			}
		}

		if (ableToPut == false) {
			{

				boolean nonTargetOnly = true;
				for (DateTimeEn d : DateTimeEn.values()) {
					if (target.hasInfo(d.ordinal()) && !nonTarget.hasInfo(d.ordinal())) {
						nonTargetOnly = false;
					}
				}

				if (nonTargetOnly == true) {
					ableToPut = true;
				}
			}
			{
				boolean hasDiffValue = false;
				for (DateTimeEn d : DateTimeEn.values()) {
					if (target.hasInfo(d.ordinal()) && nonTarget.hasInfo(d.ordinal())
							&& target.getByDateTimeEn(d) != nonTarget.getByDateTimeEn(d)) {
						hasDiffValue = true;
					}
				}

				if (hasDiffValue == true) {
					ableToPut = true;
				}
			}
		}

		return ableToPut;
	}

	// 리스트 간 병합
	void mergeByList(TokenType tokenType, DateTimeListManager list) {

		new ListMerger().listMergeByTokenType(tokenType, this, list);
	}

	void adjustForAmPmTime() {
		DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
		dateTimeAdjuster.adjustForAmPmTime(this);
	}

	public void sortByPriority() {
		Ascending ascending = new Ascending();
		Collections.sort(this.getDtMgrList(), ascending);
	}

	public boolean isDiffValueFromTargetMgr(InvalidDateTimeObj target, InvalidDateTimeObj nonTarget) {
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

	public boolean containsList(InvalidDateTimeObj target, InvalidDateTimeObj nonTarget) {
		boolean isIncludingTarget = true;
		for (DateTimeEn d : DateTimeEn.values()) {
			if (target.hasInfo(d.ordinal()) && nonTarget.hasInfo(d.ordinal())
					&& target.getByDateTimeEn(d) != nonTarget.getByDateTimeEn(d)) {
				isIncludingTarget = false;
				break;
			}
		}
		if(isIncludingTarget == true) {
			for (DateTimeEn d : DateTimeEn.values()) {
				if (target.hasInfo(d.ordinal()) && !nonTarget.hasInfo(d.ordinal())) {
					isIncludingTarget = false;
					break;
				}
			}
		}

		return isIncludingTarget;
	}

}

class Ascending implements Comparator<InvalidDateTimeObj> {

	@Override
	public int compare(InvalidDateTimeObj o1, InvalidDateTimeObj o2) {
		return o1.getPriority().compareTo(o2.getPriority());
	}
}
