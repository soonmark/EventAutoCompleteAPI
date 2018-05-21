#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
Feature: Is it a proper date ?
  I want to it is a proper date

  Scenario Outline: getting recommendations
    Given I didn't select any date and time yet
    When I type <text>
    Then I should see a <recommendations> of recommendations with size <size>
    
    Examples:
    |    text    | recommendations | size |
		| 15년 4월 9일 | 2015/04/09 (목) |   1  |
		| 2018-03-19 | 2018/03/19 (월)  |   1  |
		| 2018-3-19  | 2018/03/19 (월)  |   1  |
		| 20-10-1    | 2020/10/01 (목)  |   1  |
