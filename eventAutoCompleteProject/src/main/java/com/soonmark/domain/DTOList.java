package com.soonmark.domain;

import java.util.List;

public class DTOList {
	List<DateTimeDTO> dtoList;

	public DTOList() {
	}

	public List<DateTimeDTO> getDtoList() {
		return dtoList;
	}

	public void setDtoList(List<DateTimeDTO> dtoList) {
		this.dtoList = dtoList;
	}
	
	@Override
	public String toString() {
		int recomNum = 2; // 추천할 개수를 2개로 한정
		String jsonStr = "[";

		if (dtoList.size() > recomNum) {
			for (int i = 0; i < recomNum; i++) {
				jsonStr += dtoList.get(i).toString();

				if (i == recomNum - 1) {
					break;
				} else {
					jsonStr += ",";
				}
			}
		} else {
			for (int i = 0; i < dtoList.size(); i++) {
				jsonStr += dtoList.get(i).toString();

				if (i == dtoList.size() - 1) {
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
