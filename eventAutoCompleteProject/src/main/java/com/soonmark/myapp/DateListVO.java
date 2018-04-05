package com.soonmark.myapp;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DateListVO {
	List<DateVO> vos;

	public DateListVO() {
		vos = new ArrayList<DateVO>();
	}

	public List<DateVO> getVos() {
		return vos;
	}

	public void setVos(List<DateVO> vos) {
		this.vos = vos;
	}

	public void insertVOs(DateVO vo) {
		vos.add(vo);
	}

	public void deleteVOs(int index) {
		vos.remove(index);
	}

	public void clearVOs() {
		vos.clear();
	}

	public DateVO getElement(int index) {
		return vos.get(index);
	}

	public Iterator<DateVO> getIter() {
		return null;
	}

	public void setDayToElement(int index, DayOfWeek val) {
		vos.get(index).setDay(val);
	}
	
	public String toJsonString() {

		int recomNum = 2; // 추천할 개수를 10개로 한정
		String jsonStr = "[";

		if (vos.size() > recomNum) {
			for (int i = 0; i < recomNum; i++) {
				jsonStr += vos.get(i).toString();

				if (i == recomNum - 1) {
					break;
				} else {
					jsonStr += ",";
				}
			}
		} else {

			for (int i = 0; i < vos.size(); i++) {
				jsonStr += vos.get(i).toString();

				if (i == vos.size() - 1) {
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
