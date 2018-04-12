package com.soonmark.managers;

import com.soonmark.domain.AppConstants;
import com.soonmark.enums.DateTimeEn;
import com.soonmark.enums.TokenType;

public class ListElementDeduplicator {
	DateTimeListManager afterListMgr;
	TokenType listType;
	
	ListElementDeduplicator(){
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
		tmpList.insertDtObj(new DateTimeObjManager());
		
		// 흩어진 토큰을 리스트에 하나로 모아 넣음.
		gatherPartialsTo(tmpList);
		
		// 넣은 리스트를 타겟 리스트에 옮겨 넣음.
		moveDataToTargetList(tmpList);
	}
	
	private void gatherPartialsTo(DateTimeListManager tmpList) {
		for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
			// tmpList에 this 를 넣을 수 있는지 확인
			if (afterListMgr.ableToPut(tmpList.getElement(0), afterListMgr.getElement(i)) == true) {
				// 합치는 프로세스 시작
				// tmpList의 y, m, dt 모두 정보가 없으면 tmpList에 빈 객체 추가.
				if (afterListMgr.isTargetMgrEmpty(tmpList.getElement(0)) == true) {
					tmpList.insertDtObj(new DateTimeObjManager());
				}
				
				for (DateTimeEn d : DateTimeEn.values()) {
					if (d.getTypeNum() != listType.getInteger()) {
						continue;
					}
					if (afterListMgr.getElement(i).getByDateTimeEn(d) != AppConstants.NO_DATA) {
						tmpList.getElement(0).setByDateTimeEn(d, afterListMgr.getElement(i).getByDateTimeEn(d));
						tmpList.getElement(0).setHasInfo(d.ordinal(), true);
					}
				}
			}
		}
		
	}
	
	private void moveDataToTargetList(DateTimeListManager tmpList) {
		// 임시 리스트 -> 타겟 리스트로 데이터 옮기기
		afterListMgr.clearList();
		for (int j = 0; j < tmpList.getDtMgrList().size() - 1; j++) {
			afterListMgr.insertDtObj(tmpList.getElement(j));
		}
		for (int j = 0; j < afterListMgr.getDtMgrList().size(); j++) {
			for (DateTimeEn d : DateTimeEn.values()) {
				if (d.getTypeNum() != listType.getInteger()) {
					continue;
				}
				if (afterListMgr.getElement(j).getByDateTimeEn(d) == AppConstants.NO_DATA) {
					tmpList.getElement(j).setFocusToRepeat(d);
				}
			}
		}
		
	}
	
	private void givePriority() {

	}

	private void removeIrrelevants() {

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
					if (afterListMgr.getElement(i).hasInfo(d.ordinal())
							&& afterListMgr.getElement(j).getByDateTimeEn(d) != afterListMgr.getElement(i).getByDateTimeEn(d)) {
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
