package com.soonmark.myapp;

import com.soonmark.managers.DateTimeManager;

public class TestData {
	private String input;
	private DateTimeManager firstOutput;
	private DateTimeManager secondOutput;
	
	public TestData(String input, DateTimeManager firstOutput, DateTimeManager secondOutput) {
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
	public DateTimeManager getFirstOutput() {
		return firstOutput;
	}
	public void setFirstOutput(DateTimeManager firstOutput) {
		this.firstOutput = firstOutput;
	}
	public DateTimeManager getSecondOutput() {
		return secondOutput;
	}
	public void setSecondOutput(DateTimeManager secondOutput) {
		this.secondOutput = secondOutput;
	}
	
	public String outputToString() {
		String first = "";
		String second = "";
		String json = "[" ;
		try {
			first = firstOutput.toString();
			json += first;
			second = secondOutput.toString();
			json +=  "," + second + "]";
			
		}catch(NullPointerException e) {
			json += "]";
			return json;
		}
		
		return json;
	}
}
