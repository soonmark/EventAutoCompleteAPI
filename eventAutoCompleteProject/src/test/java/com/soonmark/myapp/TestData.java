package com.soonmark.myapp;

import com.soonmark.domain.DateTimeObject;

public class TestData {
	private String input;
	private DateTimeObject firstOutput;
	private DateTimeObject secondOutput;
	
	public TestData(String input, DateTimeObject firstOutput, DateTimeObject secondOutput) {
		this.input = input;
		this.firstOutput = firstOutput;
		this.secondOutput = secondOutput;
	}
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public DateTimeObject getFirstOutput() {
		return firstOutput;
	}
	public void setFirstOutput(DateTimeObject firstOutput) {
		this.firstOutput = firstOutput;
	}
	public DateTimeObject getSecondOutput() {
		return secondOutput;
	}
	public void setSecondOutput(DateTimeObject secondOutput) {
		this.secondOutput = secondOutput;
	}
}
