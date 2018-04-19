package com.soonmark.myapp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;

import com.soonmark.domain.DateTimeDTO;
import com.soonmark.domain.DateTimeEn;
import com.soonmark.domain.SpecialDateType;
import com.soonmark.service.RecommendationService;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

@RunWith(Parameterized.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/**/*.xml" })
public class ParameterizedTest {
	private TestContextManager testContextManager;

	public ParameterizedTest(String input, List<DateTimeDTO> expectedList) {
		super();
		this.expectedList = expectedList;
		this.input = input;
	}

	private List<DateTimeDTO> expectedList;
	private String input;

	@Autowired
	RecommendationService recommendationService;

	@Before
	public void setup() throws Exception {
		this.testContextManager = new TestContextManager(getClass());
		this.testContextManager.prepareTestInstance(this);
	}
	
	static List<DateTimeDTO> setByMonth(int month){
		LocalDate first = LocalDate.now();
		LocalDate second = LocalDate.now();
		if (LocalDate.now().getMonthValue() != month) {
			first = first.withMonth(month).withDayOfMonth(1);
			second = first.plusDays(1);
		} else {
			second = first.plusDays(1).withMonth(month);
		}
		
		return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
									first.getDayOfWeek(), -1, -1, true),
							new DateTimeDTO(second.getYear(), second.getMonthValue(), second.getDayOfMonth(),
									second.getDayOfWeek(), -1, -1, true));
	}
	
	static List<DateTimeDTO> setByMonthSize(int date){
		LocalDate first = LocalDate.now();
		LocalDate second = LocalDate.now();
		// *일이 들어왔는데 이번달에 *일이 없을 때
		if (LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth() < date) {
			first = first.plusMonths(1).withDayOfMonth(date);
		}
		else {
			first = first.withDayOfMonth(date);
		}
		if(first.getDayOfMonth() == first.plusMonths(1).getDayOfMonth()) {
			second = first.plusMonths(1);
		}
		else {
			second = first.plusMonths(2);
		}
		
		return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
				first.getDayOfWeek(), -1, -1, true),
				new DateTimeDTO(second.getYear(), second.getMonthValue(), second.getDayOfMonth(),
						second.getDayOfWeek(), -1, -1, true));
	}
	
	static List<DateTimeDTO> setByHalfTime(int h, int m){
		LocalDateTime first = LocalDateTime.now();
		LocalDateTime second = LocalDateTime.now();
		
		first = first.withHour(h).withMinute(m);
		
		if(LocalDateTime.now().isAfter(first)) {
			if(LocalDateTime.now().isAfter(first.plusHours(12))) {
				first = first.plusHours(24);
			}
			else {
				first = first.plusHours(12);
			}
		}
		second = first.plusHours(12);

		
		return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
				first.getDayOfWeek(), first.getHour(), first.getMinute(), false),
				new DateTimeDTO(second.getYear(), second.getMonthValue(), second.getDayOfMonth(),
						second.getDayOfWeek(), second.getHour(), second.getMinute(), false));
	}
	
	static List<DateTimeDTO> setBy24Time(DateTimeEn ampm, int h, int m){
		LocalDateTime first = LocalDateTime.now();
		LocalDateTime second = LocalDateTime.now();
		
		first = first.withHour(h).withMinute(m);
		
		// 오전 19시는 오후 19시로 처리하게 함.
		// 오후 11시는 23시로 처리
		if(ampm == DateTimeEn.pm){
			if(h < 12) {
				first = first.plusHours(12);
			}
		}
		if(LocalDateTime.now().isAfter(first)) {
			first = first.plusDays(1);
		}
		second = first.plusDays(1);
		
		return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
				first.getDayOfWeek(), first.getHour(), first.getMinute(), false),
				new DateTimeDTO(second.getYear(), second.getMonthValue(), second.getDayOfMonth(),
						second.getDayOfWeek(), second.getHour(), second.getMinute(), false));
	}
	
//	static List<DateTimeDTO> setByDateDay(int m, int dt, DayOfWeek day){
//		LocalDateTime first = LocalDateTime.now();
//		LocalDateTime second = LocalDateTime.now();
//		
//		first = first.withMonth(m).withDayOfMonth(dt);
//		for(int i = 1 ; i < 15 ; i++) {
//			second = first.plusYears(i);
//			if(second.getDayOfWeek() == day) {
//				break;
//			}
//		}
//		
//		return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
//				first.getDayOfWeek(), -1, -1, true),
//				new DateTimeDTO(second.getYear(), second.getMonthValue(), second.getDayOfMonth(),
//						second.getDayOfWeek(), -1, -1, true));
//	}

	static List<DateTimeDTO> setByMonthTime(int month, int h, int min){
		LocalDate first = LocalDate.now();
		LocalDate second = LocalDate.now();
		if (LocalDate.now().getMonthValue() != month) {
			first = first.withMonth(month).withDayOfMonth(1);
		}
		
		int hPlus12 = h;
		if(h <= 12) {
			hPlus12 = (h+12)%24;
			second = first;
		}
		else {
			second = first.plusDays(1);
		}
		
		return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
									first.getDayOfWeek(), h, min, false),
							new DateTimeDTO(second.getYear(), second.getMonthValue(), second.getDayOfMonth(),
									second.getDayOfWeek(), hPlus12, min, false));
	}
	
	static List<DateTimeDTO> setDateByWeek(int dt, SpecialDateType week){
		LocalDateTime first = LocalDateTime.now();
		LocalDateTime second = LocalDateTime.now();
		
		int i = 0 ;
		if(week == SpecialDateType.thisWeek) {
			if(dt < first.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).getDayOfMonth()
				|| dt > first.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).getDayOfMonth()) {
				first = first.withDayOfMonth(dt);
				second = second.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
				i = 2;
			}
			else {
				first = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
				i = 1;
			}
		}
		else if(week == SpecialDateType.nextWeek) {
			if(dt < first.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).getDayOfMonth()
					|| dt > first.with(TemporalAdjusters.next(DayOfWeek.SUNDAY))
						.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).getDayOfMonth()) {
					first = first.withDayOfMonth(dt);
					second = second.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
					i = 2;
				}
				else {
					first = first.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
					i = 1;
				}
		}
		else if(week == SpecialDateType.weekAfterNext) {
			if(dt < first.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1).getDayOfMonth()
					|| dt > first.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1)
					.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)).getDayOfMonth()) {
				first = first.withDayOfMonth(dt);
				second = second.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1);
				i = 2;
			}
			else {
				first = first.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1);
				i = 1;
			}
		}
		
		if(i == 1) {
			return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
					first.getDayOfWeek(), -1, -1, true));
		}
		else if (i == 2){
			return Arrays.asList(new DateTimeDTO(first.getYear(), first.getMonthValue(), first.getDayOfMonth(),
					first.getDayOfWeek(), -1, -1, true),
					new DateTimeDTO(second.getYear(), second.getMonthValue(), second.getDayOfMonth(),
							second.getDayOfWeek(), -1, -1, true));
		}
		return null;
	}

	//
	// Given
	//
	@Parameterized.Parameters
	public static Collection<Object[]> testCases() {
		Collection<Object[]> params = new ArrayList<Object[]>();

		LocalDate tmpDate = LocalDate.now();

		// 0
		// 년 월 일
		params.add(new Object[] {"15년 4월 9일",
				Arrays.asList(new DateTimeDTO(2015, 4, 9, LocalDate.of(2015, 4, 9).getDayOfWeek(), -1, -1, true)) });
		
		params.add(new Object[] { "2018-03-19",
				Arrays.asList(new DateTimeDTO(2018, 3, 19, LocalDate.of(2018, 3, 19).getDayOfWeek(), -1, -1, true)) });

		params.add(new Object[] { "2018-3-19",
				Arrays.asList(new DateTimeDTO(2018, 3, 19, LocalDate.of(2018, 3, 19).getDayOfWeek(), -1, -1, true)) });

		params.add(new Object[] { "20-10-1",
				Arrays.asList(new DateTimeDTO(2020, 10, 1, LocalDate.of(2020, 10, 1).getDayOfWeek(), -1, -1, true)) });
		
		params.add(new Object[] { "1999/01/01",
				Arrays.asList(new DateTimeDTO(1999, 1, 1, LocalDate.of(1999, 1, 1).getDayOfWeek(), -1, -1, true))});
		
		params.add(new Object[] { "11/3/19",
				Arrays.asList(new DateTimeDTO(2011, 3, 19, LocalDate.of(2011, 3, 19).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(2018, 11, 3, LocalDate.of(2018, 11, 3).getDayOfWeek(), -1, -1, true)) });
		
		params.add(new Object[] { "11/3/1",
				Arrays.asList(new DateTimeDTO(2011, 3, 1, LocalDate.of(2011, 3, 1).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(2018, 11, 3, LocalDate.of(2018, 11, 3).getDayOfWeek(), -1, -1, true)) });
		
		params.add(new Object[] { "2025.2.2",
				Arrays.asList(new DateTimeDTO(2025, 2, 2, LocalDate.of(2025, 2, 2).getDayOfWeek(), -1, -1, true))});
		
		params.add(new Object[] { "00.12.10",
				Arrays.asList(new DateTimeDTO(2000, 12, 10, LocalDate.of(2000, 12, 10).getDayOfWeek(), -1, -1, true))});

		params.add(new Object[] { "10.10.09",
				Arrays.asList(new DateTimeDTO(2010, 10, 9, LocalDate.of(2010, 10, 9).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(2018, 10, 10, LocalDate.of(2018, 10, 10).getDayOfWeek(), -1, -1, true)) });

		// 10
		// 년 월
		params.add(new Object[] { "내 생일 94년 6월 중",
				Arrays.asList(new DateTimeDTO(1994, 6, 1, LocalDate.of(1994, 6, 1).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(1994, 6, 2, LocalDate.of(1994, 6, 2).getDayOfWeek(), -1, -1, true)) });

		// 년 일
		params.add(new Object[] { "2000년 21일에 여행갔었음.",
				Arrays.asList(new DateTimeDTO(2000, 1, 21, LocalDate.of(2000, 1, 21).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(2000, 2, 21, LocalDate.of(2000, 2, 21).getDayOfWeek(), -1, -1, true)) });

		// 월 일
		params.add(new Object[] { "4-9",
				Arrays.asList(
						new DateTimeDTO(tmpDate.getYear(), 4, 9, tmpDate.withMonth(4).withDayOfMonth(9).getDayOfWeek(),
								-1, -1, true),
						new DateTimeDTO(tmpDate.plusYears(1).getYear(), 4, 9,
								tmpDate.plusYears(1).withMonth(4).withDayOfMonth(9).getDayOfWeek(), -1, -1, true)) });
		
		params.add(new Object[] { "1/1 신년행사",
				Arrays.asList(
						new DateTimeDTO(tmpDate.withMonth(1).withDayOfMonth(1).getYear(), 1, 1, tmpDate.withMonth(1).withDayOfMonth(1).getDayOfWeek(),
								-1, -1, true),
						new DateTimeDTO(tmpDate.withMonth(1).withDayOfMonth(1).plusYears(1).getYear(), 1, 1,
								tmpDate.withMonth(1).withDayOfMonth(1).plusYears(1).getDayOfWeek(), -1, -1, true)) });

		// 년
		params.add(new Object[] { "15년",
				Arrays.asList(new DateTimeDTO(2015, 1, 1, LocalDate.of(2015, 1, 1).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(2015, 1, 2, LocalDate.of(2015, 1, 2).getDayOfWeek(), -1, -1, true)) });

		params.add(
				new Object[] { "07년에 중학교 졸업", Arrays.asList(new DateTimeDTO(2007, 1, 1, LocalDate.of(2007, 1, 1).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(2007, 1, 2, LocalDate.of(2007, 1, 2).getDayOfWeek(), -1, -1, true)) });

		// 월
		params.add(new Object[] { "2월에 졸업식", setByMonth(2)});
		
		
		params.add(new Object[] { "4월에 벚꽃구경", setByMonth(4)});
		
		params.add(new Object[] { "겨울 12월에는 빙어낚시", setByMonth(12)});

		// 일
		params.add(new Object[] { "9일",
				Arrays.asList(
						new DateTimeDTO(tmpDate.withDayOfMonth(9).getYear(), tmpDate.withDayOfMonth(9).getMonthValue(), 9,
								tmpDate.withDayOfMonth(9).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(tmpDate.plusMonths(1).withDayOfMonth(9).getYear(),
								tmpDate.plusMonths(1).withDayOfMonth(9).getMonthValue(), 9,
								tmpDate.plusMonths(1).withDayOfMonth(9).getDayOfWeek(), -1, -1, true)) });

		params.add(new Object[] { "17일 축구동호회",
				Arrays.asList(
						new DateTimeDTO(tmpDate.withDayOfMonth(17).getYear(), tmpDate.withDayOfMonth(17).getMonthValue(), 17,
								tmpDate.withDayOfMonth(17).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(tmpDate.plusMonths(1).withDayOfMonth(17).getYear(),
								tmpDate.plusMonths(1).withDayOfMonth(17).getMonthValue(), 17,
								tmpDate.plusMonths(1).withDayOfMonth(17).getDayOfWeek(), -1, -1, true)) });
		
		// 20
		params.add(new Object[] { "19일날 가족 외식", Arrays.asList(
				new DateTimeDTO(tmpDate.withDayOfMonth(19).getYear(), tmpDate.withDayOfMonth(19).getMonthValue(), 19,
						tmpDate.withDayOfMonth(19).getDayOfWeek(), -1, -1, true),
				new DateTimeDTO(tmpDate.withDayOfMonth(19).plusMonths(1).getYear(), tmpDate.withDayOfMonth(19).plusMonths(1).getMonthValue(), 19,
						tmpDate.withDayOfMonth(19).plusMonths(1).getDayOfWeek(), -1, -1, true)) });


		// 일 - 4월 31일, 2월 30일 등 범위 이탈에 대한 처리
		params.add(new Object[] { "가족모임 29일", setByMonthSize(29)});

		params.add(new Object[] { "친구보기 30일에", setByMonthSize(30)});
		
		params.add(new Object[] { "31일에 불꽃놀이", setByMonthSize(31)});
		

		// 월 시
		params.add(new Object[] { "11월 1시", setByMonthTime(11, 1, 0)});

		params.add(new Object[] { "4월 1시", setByMonthTime(4, 1, 0)});

		params.add(new Object[] { "4월 13시 30분", setByMonthTime(4, 13, 30)});
		
		// 월 일 시
		params.add(new Object[] { "4월 3일에 친구 모임 1시 서현",
				Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(3).getYear(), 4, 3,
						tmpDate.withMonth(4).withDayOfMonth(3).getDayOfWeek(), 1, 0, false),
						new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(3).getYear(), 4, 3,
								tmpDate.withMonth(4).withDayOfMonth(3).getDayOfWeek(), 13, 0, false)) });

		params.add(new Object[] { "3/5에 친구 모임 11시 20분 야탑",
				Arrays.asList(new DateTimeDTO(tmpDate.withMonth(3).withDayOfMonth(5).getYear(), 3, 5,
						tmpDate.withMonth(3).withDayOfMonth(5).getDayOfWeek(), 11, 20, false),
						new DateTimeDTO(tmpDate.withMonth(3).withDayOfMonth(5).getYear(), 3, 5,
								tmpDate.withMonth(3).withDayOfMonth(5).getDayOfWeek(), 23, 20, false)) });
		
		// 30
		// 월 일 요일 시
		params.add(new Object[] { "4월 9일 월요일 19시",
				Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(9).getYear(), 4, 9,
						tmpDate.withMonth(4).withDayOfMonth(9).getDayOfWeek(), 19, 0, false)) });

		params.add(new Object[] { "4.18 월요일 10:10",
				Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(18).getYear(), 4, 18,
						tmpDate.withMonth(4).withDayOfMonth(18).getDayOfWeek(), 10, 10, false),
						new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(18).getYear(), 4, 18,
								tmpDate.withMonth(4).withDayOfMonth(18).getDayOfWeek(), 22, 10, false)) });
		
		// 월 일 요일
//		params.add(new Object[] { "4/16 화요일", setByDateDay(4, 16, DayOfWeek.TUESDAY)});
		params.add(new Object[] { "4/16 화요일",
				Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(16).getYear(), 4, 16,
						tmpDate.withMonth(4).withDayOfMonth(16).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(tmpDate.plusYears(1).withMonth(4).withDayOfMonth(16).getYear(), 4, 16,
								tmpDate.plusYears(1).withMonth(4).withDayOfMonth(16).getDayOfWeek(), -1, -1, true)) });
		
//		params.add(new Object[] { "4-1 화요일", setByDateDay(4, 1, DayOfWeek.TUESDAY)});
		params.add(new Object[] { "4-1 화요일",
				Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(1).getYear(), 4, 1,
						tmpDate.withMonth(4).withDayOfMonth(1).getDayOfWeek(), -1, -1, true),
						new DateTimeDTO(tmpDate.plusYears(1).withMonth(4).withDayOfMonth(1).getYear(), 4, 1,
								tmpDate.plusYears(1).withMonth(4).withDayOfMonth(1).getDayOfWeek(), -1, -1, true)) });
		
		params.add(new Object[] { "4-1 일요일", Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(1).getYear(), 4, 1,
				tmpDate.withMonth(4).withDayOfMonth(1).getDayOfWeek(), -1, -1, true))});

		// 다중 날짜
//		params.add(new Object[] { "25일에 4.3 추모 행사 참여",
//				Arrays.asList(new DateTimeDTO(tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getYear(),
//						tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getMonthValue(),
//						tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getDayOfMonth(),
//						DayOfWeek.MONDAY, -1, -1, true),
//						new DateTimeDTO(tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getYear(),
//								tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getMonthValue(),
//								tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getDayOfMonth(),
//								DayOfWeek.FRIDAY, -1, -1, true)) });

		// 다중 요일
		params.add(new Object[] { "월요일날 금요일에 만나요 콘서트",
				Arrays.asList(new DateTimeDTO(tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getYear(),
						tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getMonthValue(),
						tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)).getDayOfMonth(),
						DayOfWeek.MONDAY, -1, -1, true),
						new DateTimeDTO(tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getYear(),
								tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getMonthValue(),
								tmpDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getDayOfMonth(),
								DayOfWeek.FRIDAY, -1, -1, true)) });

		// 요일
		params.add(new Object[] { "금요일에 약속",
				Arrays.asList(new DateTimeDTO(tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getYear(),
								tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getMonthValue(),
								tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getDayOfMonth(),
								DayOfWeek.FRIDAY, -1, -1, true),
						new DateTimeDTO(tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getYear(),
								tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getMonthValue(),
								tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)).getDayOfMonth(),
								DayOfWeek.FRIDAY, -1, -1, true)) });
		
		// 월 일 요일 시 분
		params.add(
				new Object[] { "4/9 화요일 7:00", Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(9).getYear(), 4, 9,
						tmpDate.withMonth(4).withDayOfMonth(9).getDayOfWeek(), 7, 0, false),
						new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(9).getYear(), 4, 9,
								tmpDate.withMonth(4).withDayOfMonth(9).getDayOfWeek(), 19, 0, false)) });
		
		// 월 일 시 분
		params.add(new Object[] { "4.9 12시 30분",
				Arrays.asList(new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(9).getYear(), 4, 9,
						tmpDate.withMonth(4).withDayOfMonth(9).getDayOfWeek(), 12, 30, false),
						new DateTimeDTO(tmpDate.withMonth(4).withDayOfMonth(9).getYear(), 4, 9,
								tmpDate.withMonth(4).withDayOfMonth(9).getDayOfWeek(), 0, 30, false)) });

		// 특수날짜(내일)
		params.add(new Object[] { "내일 저녁에 회의",
				Arrays.asList(new DateTimeDTO(tmpDate.plusDays(1).getYear(), tmpDate.plusDays(1).getMonthValue(), tmpDate.plusDays(1).getDayOfMonth(),
						tmpDate.plusDays(1).getDayOfWeek(), -1, -1, true)) });
		
		// 40
		// 특수날짜(오늘) 시 분
		params.add(new Object[] { "오늘 12시 30분에 음원차트 확인",
				Arrays.asList(new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(),
						tmpDate.getDayOfWeek(), 0, 30, false),
						new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(),
								tmpDate.getDayOfWeek(), 12, 30, false)) });
		
		// 특수날짜(이번주) 일
		params.add(new Object[] { "이번주 13일", setDateByWeek(13, SpecialDateType.thisWeek)});

		params.add(new Object[] { "이번주 16일", setDateByWeek(16, SpecialDateType.thisWeek)});

		params.add(new Object[] { "이번주 21일", setDateByWeek(21, SpecialDateType.thisWeek)});
		
		// 특수날짜(이번주) 요일
		params.add(new Object[] { "이번주 영화보기 토요일 조조",
				Arrays.asList(new DateTimeDTO(
						tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).getYear(),
						tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).getMonthValue(),
						tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).getDayOfMonth(),
						DayOfWeek.SATURDAY, -1, -1, true)) });
		
		// 특수날짜(다음주)
		params.add(new Object[] { "약속 하나 있다 다음주",
				Arrays.asList(new DateTimeDTO(
						tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).getYear(),
						tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).getMonthValue(),
						tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).getDayOfMonth(),
						DayOfWeek.SUNDAY, -1, -1, true)) });
		
		// 특수날짜(이번주)
		params.add(new Object[] { "아무 약속도 없는 이번주",
				Arrays.asList(new DateTimeDTO(
						tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).getYear(),
						tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).getMonthValue(),
						tmpDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)).getDayOfMonth(),
						DayOfWeek.SUNDAY, -1, -1, true)) });
		
		// 특수날짜(다다음주)
		params.add(new Object[] { "친구 놀러옴 다다음주에",
				Arrays.asList(new DateTimeDTO(
						tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1).getYear(),
						tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1).getMonthValue(),
						tmpDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusWeeks(1).getDayOfMonth(),
						DayOfWeek.SUNDAY, -1, -1, true)) });
		
		
		// 오전 오후  없는 시간
		params.add(new Object[] { "9시", setByHalfTime(9, 0) });
		params.add(	new Object[] { "12시 30분", setByHalfTime(12, 30)});
		
		// 50
		// 오전 오후 있는 시간
		params.add(new Object[] { "오전 9시", setBy24Time(DateTimeEn.am, 9, 0) });
		params.add(new Object[] { "am 9시", setBy24Time(DateTimeEn.am, 9, 0) });
		params.add(new Object[] { "AM 9시", setBy24Time(DateTimeEn.am, 9, 0) });
		params.add(new Object[] { "A.M. 9시", setBy24Time(DateTimeEn.am, 9, 0) });
		params.add(new Object[] { "19:01", setBy24Time(DateTimeEn.pm, 19, 1) });
		
		
		// 오전/오후
		params.add(new Object[] { "오전",
				Arrays.asList(new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpDate.getDayOfWeek(), 9, 0, true),
						new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpDate.getDayOfWeek(), 10, 0, true)) });
		params.add(new Object[] { "오후",
				Arrays.asList(new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpDate.getDayOfWeek(), 12, 0, true),
						new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpDate.getDayOfWeek(), 13, 0, true)) });
		params.add(new Object[] { "am",
				Arrays.asList(new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpDate.getDayOfWeek(), 9, 0, true),
						new DateTimeDTO(tmpDate.getYear(), tmpDate.getMonthValue(), tmpDate.getDayOfMonth(), tmpDate.getDayOfWeek(), 10, 0, true)) });
		

		return params;
	}

	@Test
	public void test() throws Exception {
		//
		// When
		//
		List<DateTimeDTO> outputList = recommendationService.getRecommendations(input);

		//
		// Then
		//
		assertThat(outputList.size(), is(expectedList.size()));
		for (int i = 0; i < outputList.size(); i++) {
			assertThat(outputList.get(i).toString(), is(expectedList.get(i).toString()));
		}
	}

}
