package com.soonmark.managers;

import com.soonmark.enums.DateTimeEn;

public class ListMerger {
	DateTimeListManager afterListMgr;

	ListMerger() {
	}

	void listMerge(DateTimeListManager targetListMgr, DateTimeListManager listMgr) {
		afterListMgr = targetListMgr;
		// 일단 merge
		DateTimeListManager tmpListMgr = simplyMergeBy(listMgr);
		// 값 조정
		adjustData();

		// 병합된 리스트 element this에 옮기기
		if (tmpListMgr.getDtMgrList().size() > 0) {
			afterListMgr.clearList();
			for (int i = 0; i < tmpListMgr.getDtMgrList().size(); i++) {
				afterListMgr.insertDtObj(tmpListMgr.getElement(i));
			}
		}
	}
	

	private DateTimeListManager simplyMergeBy(DateTimeListManager list) {

		// this <- list 를 병합해 넣을 리스트
		DateTimeListManager tmpList = new DateTimeListManager();

		// list가 비었으면 그냥 나가기
		if (afterListMgr.isListEmpty(list.getDtMgrList()) == true) {
			return tmpList;
		}

		// 빈 값일 때도 for문 돌아야 하므로 빈 객체 삽입.
		if (afterListMgr.isListEmpty(afterListMgr.getDtMgrList()) == false) {
			afterListMgr.insertDtObj(new DateTimeManager());
		}


		for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
			for (int j = 0; j < list.getDtMgrList().size(); j++) {

				DateTimeManager dateTimeObject = new DateTimeManager();

				dateTimeObject = afterListMgr.getElement(i);
				// nonTarget 정보 있으면 담음.
				for (DateTimeEn dtEn : DateTimeEn.values()) {
					if (list.getElement(j).hasInfo(dtEn.ordinal()) == true) {
						dateTimeObject.setByDateTimeEn(dtEn, list.getElement(j).getByDateTimeEn(dtEn));
					}
				}

				tmpList.insertDtObj(dateTimeObject);
			}
		}
		return tmpList;
	}
	
	//// 미완!!!!!!
	private void adjustData() {
		// 아무 값이 들어간 상태에서 처리 로직 구현하자.
		
		
		
		for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
			
		}
		
		//		for (int i = 0; i < dateList.getList().size(); i++) {
//					// 가까운 미래시 날짜 찾아 tmpCal에 세팅.
//					tmpCal.setCloseDateOfTheDay(dayList.getElement(j).getDay());
//					dtObj.setFocusOnDay(true);
//
//				} else { // 요일 정보와 날짜 정보가 있을 때는 요일 정보를 무시
//					if (dateList.getElement(i).hasInfo(DateTimeEn.year.ordinal())) {
//						tmpCal.setYear(dateList.getElement(i).getYear());
//					}
//					if (dateList.getElement(i).hasInfo(DateTimeEn.month.ordinal())) {
//						tmpCal.setMonth(dateList.getElement(i).getMonth());
//					}
//					if (dateList.getElement(i).hasInfo(DateTimeEn.date.ordinal())) {
//						tmpCal.setDate(dateList.getElement(i).getDate());
//					}
//					dtObj.setFocusOnDay(false);
//				}
//
//				dtObj.setAllDate(tmpCal);
//
//				dateList.getElement(i).setAllDate(dtObj);
//
//				dateList.getElement(i).setFocusOnDay(dtObj.isFocusOnDay());
//				dateList.getElement(i).setHasInfo(DateTimeEn.day.ordinal(), true);
//			}

	}

}
