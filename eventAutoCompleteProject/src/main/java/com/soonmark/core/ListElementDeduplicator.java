package com.soonmark.core;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.Priority;
import com.soonmark.domain.TokenType;

public class ListElementDeduplicator {
	DateTimeListManager afterListMgr;
	TokenType listType;

	ListElementDeduplicator() {
	}

	public void mergeProcess(DateTimeListManager dtObjListMgr, TokenType listType) {
		afterListMgr = dtObjListMgr;
		this.listType = listType;

		// 각기 흩어진 토큰들을 더 큰 토큰으로 묶어내는 프로세스
		grouping();

		// 중복 제거
		elementDeduplicates();
		// 터무니 없는 날짜 제거
		removeIrrelevants();
		// 우선순위 부여
		givePriority();
	}

	private void grouping() {

		// 임시로 병합데이터 담고 있을 리스트
		DateTimeListManager tmpList = new DateTimeListManager();
		tmpList.insertDtObj(new InvalidDateTimeObj());

		// 흩어진 토큰을 리스트에 하나로 모아 넣음.
		gatherPartialsTo(tmpList);

		// 윤년있으면 처리
		setIfIsLeapYear(tmpList);

		// 빈 토큰 있으면 focus 두도록 하기
		setElementFocusedIfEmpty(tmpList);

		// 넣은 리스트를 타겟 리스트에 옮겨 넣음.
		moveDataToTargetList(tmpList);
	}

	private void setElementFocusedIfEmpty(DateTimeListManager tmpList) {
		for (int j = 0; j < tmpList.getDtMgrList().size(); j++) {
			for (DateTimeEn d : DateTimeEn.values()) {
				if (d.getTypeNum() != listType.getInteger()) {
					continue;
				}
				if (d == DateTimeEn.year) {
					continue;
				}
				if (tmpList.getElement(j).getByDateTimeEn(d) == AppConstants.NO_DATA) {
					tmpList.getElement(j).setFocusToRepeat(d);
				}
			}
		}
	}

	private void setIfIsLeapYear(DateTimeListManager tmpList) {
		for (int i = 0; i < tmpList.getDtMgrList().size(); i++) {
			if (tmpList.getElement(i).getMonth() == 2 && tmpList.getElement(i).getDate() == 29) {
				tmpList.getElement(i).setLeapYear(true);

				DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
				if (tmpList.getElement(i).getYear() != AppConstants.NO_DATA) {
					dateTimeAdjuster.setYear(tmpList.getElement(i).getYear());
				}

				// 해당 년도가 윤년이 아니라면 근접한 미래 년도로 세팅. -> 년도 무시
				tmpList.getElement(i).setYear(dateTimeAdjuster.getNextOrSameLeapYear());
			}
		}
	}

	private void gatherPartialsTo(DateTimeListManager tmpList) {
		// tmpList 인덱스
		int j = 0;
		for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
			// tmpList에 this 를 넣을 수 있는지 확인
			if (afterListMgr.ableToPut(tmpList.getElement(j), afterListMgr.getElement(i)) == true) {
				// 합치는 프로세스 시작
				// tmpList의 각 토큰 모두 정보가 없으면 tmpList에 빈 객체 추가.
				if (afterListMgr.isTargetMgrEmpty(tmpList.getElement(j)) == true) {
					tmpList.insertDtObj(new InvalidDateTimeObj());
				}
				// 기존에 있던 값들과 다른값을 저장할 때
				else if (afterListMgr.isDiffValueFromTargetMgr(tmpList.getElement(j), afterListMgr.getElement(i))) {
					tmpList.insertDtObj(new InvalidDateTimeObj());
					j++;
				}
				// else if(afterListMgr.containsList(tmpList.getElement(j),
				// afterListMgr.getElement(i))) {
				// tmpList.insertDtObj(new DateTimeLogicalObject());
				// j++;
				// }

				for (DateTimeEn d : DateTimeEn.values()) {
					if (d.getTypeNum() != listType.getInteger()) {
						continue;
					}
					if (afterListMgr.getElement(i).getByDateTimeEn(d) != AppConstants.NO_DATA) {
						int ampm = 0;
						if(d == DateTimeEn.am || d == DateTimeEn.pm) {
							ampm = afterListMgr.getElement(i).getByDateTimeEn(d);
							tmpList.getElement(j).setByDateTimeEn(DateTimeEn.values()[ampm], ampm);
							tmpList.getElement(j).setHasInfo(ampm, true);
						}
						else {
							tmpList.getElement(j).setByDateTimeEn(d, afterListMgr.getElement(i).getByDateTimeEn(d));
							tmpList.getElement(j).setHasInfo(d.ordinal(), true);
						}
					}
				}
			}
		}
	}

	private void moveDataToTargetList(DateTimeListManager tmpList) {
		// 임시 리스트 -> 타겟 리스트로 데이터 옮기기
		afterListMgr.clearList();
		// tmpList의 마지막 element 는 빈 객체라서 빼고 옮김.
		for (int j = 0; j < tmpList.getDtMgrList().size() - 1; j++) {
			afterListMgr.insertDtObj(tmpList.getElement(j));
		}
	}

	private void givePriority() {

		boolean allExists = true;
		// 있는 정보 중에
		for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
			if (afterListMgr.getElement(i).getByDateTimeEn(DateTimeEn.year) == AppConstants.NO_DATA) {
				afterListMgr.getElement(i).setPriority(Priority.yearIsMissing);
				allExists = false;
			}
			if (afterListMgr.getElement(i).getByDateTimeEn(DateTimeEn.month) == AppConstants.NO_DATA) {
				afterListMgr.getElement(i).setPriority(Priority.monthIsMissing);
				allExists = false;
			}
			if (afterListMgr.getElement(i).getByDateTimeEn(DateTimeEn.date) == AppConstants.NO_DATA) {
				afterListMgr.getElement(i).setPriority(Priority.dateIsMissing);
				allExists = false;
			}
			if (allExists) {
				afterListMgr.getElement(i).setPriority(Priority.everyTokenExists);
			}
		}
	}

	// 10/10/10 일 때 2010년 10월 10일 빼고는 다 지우기
	private void removeIrrelevants() {
		// 있는 정보 중에는 모두 같은 거
		// 리스트 중 최대 존재 정보 개수
		int minInfoNum = 0;
		int minInfoIdxNum = 0;

		for (int j = 0; j < afterListMgr.getDtMgrList().size(); j++) {
			int infoNum = 0;
			for (DateTimeEn d : DateTimeEn.values()) {
				if (afterListMgr.getElement(j).hasInfo(d.getInteger())) {
					infoNum++;
				}
			}
			if (minInfoNum > infoNum) {
				minInfoNum = infoNum;
				minInfoIdxNum = j;
			}
		}

		for (int j = 0; j < afterListMgr.getDtMgrList().size();) {
			if (j > minInfoIdxNum) {
				afterListMgr.deleteDtObj(j);
			} else {
				j++;
			}
		}
	}

	private void elementDeduplicates() {
		// 있는 정보 중에는 모두 같은 거
		for (int j = 0; j < afterListMgr.getDtMgrList().size(); j++) {
			for (int i = j + 1; i < afterListMgr.getDtMgrList().size(); i++) {
				boolean ableToDelete = true;
				for (DateTimeEn d : DateTimeEn.values()) {
					if (d.getTypeNum() != listType.getInteger()) {
						continue;
					}
					if (afterListMgr.getElement(i).hasInfo(d.ordinal()) && afterListMgr.getElement(j)
							.getByDateTimeEn(d) != afterListMgr.getElement(i).getByDateTimeEn(d)) {
						ableToDelete = false;
					}
				}
				if (ableToDelete) {
					afterListMgr.deleteDtObj(i);
					i -= 1;
				}
			}
		}
	}
}
