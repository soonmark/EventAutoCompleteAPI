package com.soonmark.core;

import com.soonmark.domain.AppConstants;
import com.soonmark.domain.DateTimeEn;

public class During {
	private String text;
	private DateTimeEn type;
	private int value;
	
	public During(String text) {
		super();
		this.text = text;
		this.value = AppConstants.NO_DATA;
		type = null;
	}

	public During(String text, DateTimeEn type, int value) {
		super();
		this.text = text;
		this.type = type;
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public DateTimeEn getType() {
		return type;
	}

	public void setType(DateTimeEn type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
