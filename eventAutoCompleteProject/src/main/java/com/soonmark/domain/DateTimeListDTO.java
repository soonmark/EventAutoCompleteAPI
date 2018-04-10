package com.soonmark.domain;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateTimeListDTO {
	private Logger logger = LoggerFactory.getLogger(DateTimeListDTO.class);
	private List<DateTimeObject> dtObjList;

	public DateTimeListDTO() {
		dtObjList = new ArrayList<DateTimeObject>();
	}

	public List<DateTimeObject> getList() {
		return dtObjList;
	}

	public void setVos(List<DateTimeObject> vos) {
		this.dtObjList = vos;
	}

	public void insertDtObj(DateTimeObject vo) {
		dtObjList.add(vo);
	}

	public void deleteList(int index) {
		dtObjList.remove(index);
	}

	public void clearList() {
		dtObjList.clear();
	}

	public DateTimeObject getElement(int index) {
		return dtObjList.get(index);
	}

	public Iterator<DateTimeObject> getIter() {
		return null;
	}

	public void setDayToElement(int index, DayOfWeek val) {
		dtObjList.get(index).setDay(val);
	}

	// 년, 월, 일을 각각 받게 되면 여기서 merge 할 것.
	void mergeItself() {
		DateTimeListDTO tmpList = new DateTimeListDTO();
		tmpList.insertDtObj(new DateTimeObject());

		for (int i = 0; i < dtObjList.size(); i++) {
			for (int j = 0; j < tmpList.getList().size(); j++) {
				// target 의 원소 중 존재하는 값들이 모두 list 에 없으면 추가
				boolean ableToPut = true;
				for (DateTimeEn d : DateTimeEn.values()) {
					if (this.getElement(i).hasInfo(d.ordinal()) && tmpList.getElement(j).hasInfo(d.ordinal())) {
						ableToPut = false;
						break;
					}
				}
				if (ableToPut) {
					// 합치는 프로세스 시작
					// y, m, dt 모두 정보가 없으면 list 에 빈 객체 추가
					if (!tmpList.getElement(j).hasInfo(DateTimeEn.year.ordinal())
							&& !tmpList.getElement(j).hasInfo(DateTimeEn.month.ordinal())
							&& !tmpList.getElement(j).hasInfo(DateTimeEn.date.ordinal())) {
						tmpList.insertDtObj(new DateTimeObject());
					}
					if (this.getElement(i).getYear() != -1) {
						tmpList.getElement(j).setYear(this.getElement(i).getYear());
						tmpList.getElement(j).setHasInfo(0, true);
					}
					if (this.getElement(i).getMonth() != -1) {
						tmpList.getElement(j).setMonth(this.getElement(i).getMonth());
						tmpList.getElement(j).setHasInfo(1, true);
					}
					if (this.getElement(i).getDate() != -1) {
						tmpList.getElement(j).setDate(this.getElement(i).getDate());
						tmpList.getElement(j).setHasInfo(2, true);
					}
					break;
				}
			}
		}

		this.clearList();
		for (int j = 0; j < tmpList.getList().size() - 1; j++) {
			this.insertDtObj(tmpList.getElement(j));
		}
		for (int j = 0; j < this.getList().size(); j++) {
			if (this.getElement(j).getYear() == -1) {
				this.getElement(j).setFocusToRepeat(DateTimeEn.year);
			}
			if (this.getElement(j).getMonth() == -1) {
				this.getElement(j).setFocusToRepeat(DateTimeEn.month);
			}
			if (this.getElement(j).getDate() == -1) {
				this.getElement(j).setFocusToRepeat(DateTimeEn.date);
			}
		}

		// 여기에서 중복 제거를 하던지, 아니면 우선순위 부여를 하던지, 아니면 날짜 비교해서 너무 터무니없이 먼 날짜면 지우는 방향으로!
		// 중복 제거
		// 있는 정보 중에는 모두 같은 거
		for (int j = 0; j < dtObjList.size(); j++) {
			for (int i = j + 1; i < dtObjList.size(); i++) {
				if (((this.getElement(i).hasInfo(DateTimeEn.year.ordinal())
						&& this.getElement(j).getYear() == this.getElement(i).getYear())
						|| !this.getElement(i).hasInfo(DateTimeEn.year.ordinal()))
						&& ((this.getElement(i).hasInfo(DateTimeEn.month.ordinal())
								&& this.getElement(j).getMonth() == this.getElement(i).getMonth())
								|| !this.getElement(i).hasInfo(DateTimeEn.month.ordinal()))
						&& ((this.getElement(i).hasInfo(DateTimeEn.date.ordinal())
								&& this.getElement(j).getDate() == this.getElement(i).getDate())
								|| !this.getElement(i).hasInfo(DateTimeEn.date.ordinal()))) {

					this.deleteList(i);
					i -= 1;
				}
			}
		}

		// 터무니 없는 날짜 제거

		// 우선순위 부여

		// 로그 찍기
		for (int j = 0; j < dtObjList.size(); j++) {
			logger.info(this.getElement(j).toString());
		}
	}
	
	void mergeBy(TokenType tokenType) {
		
	}

	public String toJsonString() {

		int recomNum = 2; // 추천할 개수를 10개로 한정
		String jsonStr = "[";

		if (dtObjList.size() > recomNum) {
			for (int i = 0; i < recomNum; i++) {
				jsonStr += dtObjList.get(i).toString();

				if (i == recomNum - 1) {
					break;
				} else {
					jsonStr += ",";
				}
			}
		} else {

			for (int i = 0; i < dtObjList.size(); i++) {
				jsonStr += dtObjList.get(i).toString();

				if (i == dtObjList.size() - 1) {
					break;
				} else {
					jsonStr += ",";
				}
			}
		}
		jsonStr += "]";

		return jsonStr;
	}
}
