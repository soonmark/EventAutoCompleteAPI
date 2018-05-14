package com.soonmark.core;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.soonmark.domain.DateTimeEn;
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

	// 완성하기!!
	public EventListManager mergeWith(DateTimeListManager firstList,
			DateTimeListManager secondList) {
		return null;
	}

}

class Ascending implements Comparator<InvalidDateTimeObj> {

	@Override
	public int compare(InvalidDateTimeObj o1, InvalidDateTimeObj o2) {
		return o1.getPriority().compareTo(o2.getPriority());
	}
}

class AscendingEvents implements Comparator<InvalidEventObj> {
	
	@Override
	public int compare(InvalidEventObj o1, InvalidEventObj o2) {
		if(o1.getStartDate() != null && o2.getStartDate() != null) {
			return o1.getStartDate().getPriority().compareTo(o2.getStartDate().getPriority());
		}
		else {
			return o1.getEndDate().getPriority().compareTo(o2.getEndDate().getPriority());
		}
	}
}

class AscendingDateTimeEvents implements Comparator<InvalidEventObj> {
	
	@Override
	public int compare(InvalidEventObj o1, InvalidEventObj o2) {
		if(o1.getStartDate() != null && o2.getStartDate() != null) {
			if(!o1.getStartDate().hasNoTime() && !o2.getStartDate().hasNoTime()) {
				LocalDateTime o1Dt = LocalDateTime.of(o1.getStartDate().getLocalDate(), o1.getStartDate().getLocalTime());
				LocalDateTime o2Dt = LocalDateTime.of(o2.getStartDate().getLocalDate(), o2.getStartDate().getLocalTime());
				
				if(o1Dt.isEqual(o2Dt)) {
					if(o1.getEndDate() != null && o2.getEndDate() != null) {
						if(!o1.getEndDate().hasNoTime() && !o2.getEndDate().hasNoTime()) {
							o1Dt = LocalDateTime.of(o1.getEndDate().getLocalDate(), o1.getEndDate().getLocalTime());
							o2Dt = LocalDateTime.of(o2.getEndDate().getLocalDate(), o2.getEndDate().getLocalTime());
						}
						else {
							o1Dt = LocalDateTime.of(o1.getEndDate().getLocalDate(), LocalTime.of(0, 0));
							o2Dt = LocalDateTime.of(o2.getEndDate().getLocalDate(), LocalTime.of(0, 0));
						}
					}
				}
				return o1Dt.compareTo(o2Dt);
				
			}
			else {
				return o1.getStartDate().getLocalDate().compareTo(o2.getStartDate().getLocalDate());
			}
		}
		else {
			if(o1.getEndDate().getLocalTime() != null && o2.getEndDate().getLocalTime() != null) {
				LocalDateTime o1Dt = LocalDateTime.of(o1.getEndDate().getLocalDate(), o1.getEndDate().getLocalTime());
				LocalDateTime o2Dt = LocalDateTime.of(o2.getEndDate().getLocalDate(), o2.getEndDate().getLocalTime());
				
				return o1Dt.compareTo(o2Dt);
			}
			else {
				return o1.getEndDate().getLocalDate().compareTo(o2.getEndDate().getLocalDate());
			}
		}
	}
}
