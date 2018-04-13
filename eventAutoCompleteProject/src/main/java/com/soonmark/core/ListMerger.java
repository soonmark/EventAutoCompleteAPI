package com.soonmark.core;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.TokenType;

public class ListMerger {
	DateTimeListManager afterListMgr;

	ListMerger() {
	}

	void listMergeByTokenType(TokenType tokenType, DateTimeListManager targetListMgr, DateTimeListManager listMgr) {
		afterListMgr = targetListMgr;
		// 일단 merge
		DateTimeListManager tmpListMgr = simplyMergeBy(listMgr);
		
		// 병합된 리스트 element this에 옮기기
		if (tmpListMgr.getDtMgrList().size() > 0) {
			afterListMgr.clearList();
			for (int i = 0; i < tmpListMgr.getDtMgrList().size(); i++) {
				afterListMgr.insertDtObj(tmpListMgr.getElement(i));
			}
		}

		// 값 조정
		adjustDataByTokenType(tokenType);

	}
	

	private DateTimeListManager simplyMergeBy(DateTimeListManager list) {

		// this <- list 를 병합해 넣을 리스트
		DateTimeListManager tmpList = new DateTimeListManager();

		// list가 비었으면 그냥 나가기
		if (afterListMgr.isListEmpty(list.getDtMgrList()) == true) {
			return tmpList;
		}

		// 빈 값일 때도 for문 돌아야 하므로 빈 객체 삽입.
		if (afterListMgr.isListEmpty(afterListMgr.getDtMgrList()) == true) {
			afterListMgr.insertDtObj(new DateTimeLogicalObject());
		}


		for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
			for (int j = 0; j < list.getDtMgrList().size(); j++) {

				DateTimeLogicalObject dateTimeObject = new DateTimeLogicalObject();

				dateTimeObject = afterListMgr.getElement(i);
				// nonTarget 정보 있으면 담음.
				for (DateTimeEn dtEn : DateTimeEn.values()) {
					if (list.getElement(j).hasInfo(dtEn.ordinal()) == true) {
						if(dtEn == DateTimeEn.specialDate) {
							dateTimeObject.setSpecialDate(list.getElement(j).getSpecialDate());
						}
						else {
							dateTimeObject.setByDateTimeEn(dtEn, list.getElement(j).getByDateTimeEn(dtEn));
						}
						dateTimeObject.setHasInfo(dtEn.ordinal(), true);
					}
				}

				tmpList.insertDtObj(dateTimeObject);
			}
		}
		return tmpList;
	}
	
	//// 미완!!!!!!
	private void adjustDataByTokenType(TokenType tokenType) {
		// 아무 값이 들어간 상태에서 처리 로직 구현하자.
		
		// 날짜, 요일
		// 날짜+요일, 특수날짜
		if(tokenType == TokenType.days) {
			
			// 요일 정보가 없다면 나가기
			for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
				if(afterListMgr.getElement(i).getDay() == AppConstants.NO_DATA_FOR_DAY) {
					break;
				}
				
				boolean hasDateInfo = false;
				
				// 현재 입력받은 데이터 값만 집어넣기
				for(DateTimeEn dtEn : DateTimeEn.values()) {
					if(dtEn == DateTimeEn.day) {
						break;
					}
					if (afterListMgr.getElement(i).hasInfo(dtEn.ordinal())) {
						hasDateInfo = true;
					}
				}
				
				// 날짜 정보 있다면 요일 무시
				if(hasDateInfo == true) {
					afterListMgr.getElement(i).setFocusOnDay(false);
					
//					afterListMgr.getElement(i).setProperDay();
					afterListMgr.getElement(i).setHasInfo(DateTimeEn.day.ordinal(), true);
				}
				else {
					afterListMgr.getElement(i).setFocusOnDay(true);
				}
			}
		}
		else if(tokenType == TokenType.special){
			// 날짜 데이터 조정할 객체: 초기값 오늘
			DateTimeAdjuster dateTimeAdjuster = new DateTimeAdjuster();
		
			for (int i = 0; i < afterListMgr.getDtMgrList().size(); i++) {
				// 특수 날짜 없으면 나가기
				if(afterListMgr.getElement(i).getSpecialDate().equals(AppConstants.NO_DATA_FOR_SPECIALDATE)) {
					break;
				}
				else if (afterListMgr.getElement(i).getSpecialDate().equals("오늘")) {
					dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().plusDays(0));
				}
				else if (afterListMgr.getElement(i).getSpecialDate().equals("내일")) {
					dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().plusDays(1));
				}
				else if (afterListMgr.getElement(i).getSpecialDate().equals("모레")) {
					dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().plusDays(2));
				}
				
				DayOfWeek day = AppConstants.NO_DATA_FOR_DAY;
				int date = AppConstants.NO_DATA ;
				
				if(afterListMgr.getElement(i).hasInfo(DateTimeEn.day.ordinal())) {
					day = afterListMgr.getElement(i).getDay();
				}
				if(afterListMgr.getElement(i).hasInfo(DateTimeEn.date.ordinal())) {
					date = afterListMgr.getElement(i).getDate();
				}
				else if (afterListMgr.getElement(i).getSpecialDate().equals("이번주")) {
					// 무조건 이번주 일요일로 세팅
					dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));
					// 요일 정보 있으면 재세팅
					if(day != AppConstants.NO_DATA_FOR_DAY) {
						dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().with(TemporalAdjusters.nextOrSame(day)));
					}
					
					// 이번주 3일 이런 경우!
					if(date != AppConstants.NO_DATA) {
						//////////////
					}
				}
				else if (afterListMgr.getElement(i).getSpecialDate().equals("다음주")) {
					// 무조건 다음주 일요일로 세팅
					dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
					// 요일 정보 있으면 재세팅
					if(day != AppConstants.NO_DATA_FOR_DAY) {
						dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().with(TemporalAdjusters.nextOrSame(day)));
					}

					// 이번주 3일 이런 경우!
					if(date != AppConstants.NO_DATA) {
						//////////////
					}
				}
				else if (afterListMgr.getElement(i).getSpecialDate().equals("다다음주")) {
					// 무조건 다다음주 일요일로 세팅
					dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().plusWeeks(1));
					dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
					// 요일 정보 있으면 재세팅
					if(day != AppConstants.NO_DATA_FOR_DAY) {
						dateTimeAdjuster.setTimePoint(dateTimeAdjuster.getTimePoint().with(TemporalAdjusters.nextOrSame(day)));
					}

					// 이번주 3일 이런 경우!
					if(date != AppConstants.NO_DATA) {
						//////////////
					}
				}

				afterListMgr.getElement(i).setAllDate(dateTimeAdjuster);
			}
		}
		
	}

}
