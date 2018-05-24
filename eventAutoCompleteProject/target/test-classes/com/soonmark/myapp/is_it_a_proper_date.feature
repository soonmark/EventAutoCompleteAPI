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
Feature: Are they proper recommendations?
  I want to see that they are proper recommendations

  Scenario: Getting a specific date with year, month, date notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  15년 4월 9일  |
		|  2018-03-19  |
		|  2018-3-19   |
		|   20-10-1    |
		|  1999/01/01  |
		|    11/3/19   |
		|    11/3/1    |
		|   2025.2.2   |
		|   00.12.10   |
		|   10.10.09   |
    Then I should see a proper recommendation
		|	  2015/04/09 (목)	|
		|	  2018/03/19 (월)	|
		|	  2018/03/19 (월)	|
		|	  2020/10/01 (목)	|
		|	  1999/01/01 (금)	|
		|	  2011/03/19 (토)	|
		|	  2011/03/01 (화)	|
		|	  2025/02/02 (일)	|
		|	  2000/12/10 (일)	|
		|	  2010/10/09 (토)	|

  Scenario: Getting no recommendation with year, month notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  벚꽃달인 18년 4월 계획짜기  |
		|  내 생일 94년 6월 중		   |
    Then I should see no recommendation

  Scenario: Getting recommendations with year, date notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  2000년 21일에 여행갔었음.  |
		|   2018년 4일에 여행갔었음.  |
    Then I should see proper recommendations
    |	  2000/01/21 (금)	|	  2000/02/21 (월)	|
    |	  2018/06/04 (월)	|	  2018/07/04 (수)	|
		
  Scenario: Getting a recommendation with month, date notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		| 		 4-9		  |
		|   1/1 신년행사  |
    Then I should see a proper recommendation
		|	  2019/04/09 (화)	|
		|	  2019/01/01 (화)	|
		
  Scenario: Getting no recommendation with year notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  				15년 			   |
		|  07년에 중학교 졸업		   |
    Then I should see no recommendation
		
  Scenario: Getting no recommendation with month notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  			2월에 졸업식		   |
		| 		 5월에 꽃구경		   |
		|  겨울 12월에는 빙어낚시   |
    Then I should see no recommendation
		
  Scenario: Getting recommendations with date notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  			1일		   |
		| 		   9일	 	   |
		|  17일 축구동호회   |
		|  29일날 가족 외식  |
    Then I should see proper recommendations
		|	  2018/06/01 (금)	|	  2018/07/01 (일)	|
		|	  2018/06/09 (토)	|	  2018/07/09 (월)	|
    |	  2018/05/17 (목)	|	  2018/06/17 (일)	|	
    |	  2018/05/29 (화)	|	  2018/06/29 (금)	|
		
  Scenario: Getting recommendations with last date notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  			가족모임 29일		   |
		| 		  친구보기 30일에	 	   |
		|       31일에 불꽃놀이      |
    Then I should see proper recommendations
		|	  2018/05/29 (화)	|	  2018/06/29 (금)	|
		|	  2018/05/30 (수)	|	  2018/06/30 (토)	|
    |	  2018/05/31 (목)	|	  2018/07/31 (화)	|

  Scenario: Getting recommendations with valid but out of range date notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  		2월 30일		   |
		|  		2월 31일		   |
    Then I should see a proper recommendation
		|	  2018/05/30 (수)	|
		|	  2018/05/31 (목)	|

  Scenario: Getting recommendations with valid but out of range date, time notified without pre-selected event informations
    Given I didn't select any date and time yet
    When I type
		|  		2월 30일	1시	   |
    Then I should see proper recommendations
		|	  2018/05/30 (수) 오전 1시 	|	  2018/05/30 (수) 오후 1시  	|
		
		########################################
		

  Scenario: Getting recommendations with past year, month date notified with pre-selected date and time
    Given I selected
    | 2018/05/30 오전 11:00 |
    When I type
		|  		15년 4월 9일		   |
    Then I should see a proper recommendation
		|	  2018/05/30 (수)	|
		|	  2018/05/31 (목)	|

