var tmpStr = "default";

var startDate = "startDate";
var endDate = "endDate";
var inputEvent = $('#inputEvent');

const INVALID_INPUT_CHARACTER = -2;
const NO_DATA = "";

var viewingObjId = 0;

$(function() {
	defaultSettings();
});

function defaultSettings() {
	inputEvent.focus();
	$('.list_eventDates').addClass('activeInputRange');

	setDefaultClickEvents();

	setKeyEvents();
}

function setDefaultClickEvents() {
	$(document).click(function(event) {
		// 입력창 부분 클릭시
		if ($(event.target).closest('.article').length) {
			$('.list_eventDates').addClass('activeInputRange');
			inputEvent.focus();
			$('.activeObj').removeClass('activeObj');
		} else {
			$('.list_eventDates').removeClass("activeInputRange");
		}
	});
}

// 키 이벤트 세팅
function setKeyEvents() {

	// 입력창에 입력이 될 때마다
	$(inputEvent).keyup(function() {
		var prev = tmpStr;
		tmpStr = inputEvent.val();

		// but 이전 입력값과 같으면 얼럿 안 나오게
		if (tmpStr != prev)
			checkInput();
	}); // end keyup

	$(document).keydown(function(ev) {
		// 백스페이스 누르면 생성되었던 객체 지워지게
		// backspace : 8 in ascii
		if (ev.keyCode == 8) {
			// 선택된 객체가 있으면
			if ($('.activeObj').length) {
				$('.activeObj').remove();
				inputEvent.focus();
			} else {
				if (inputEvent.val() == '') {
					for (var i = 0; i < viewingObjId; i++) {
						var id = "#" + i;
						if ($(id).length) {
							$(id).addClass('activeObj');
							inputEvent.blur();
							break;
						}
					}
				}
			}
		}

		if (ev.keyCode == 46) {
			// 선택된 객체가 있으면
			if ($('.activeObj').length) {
				$('.activeObj').remove();
				inputEvent.focus();
			}
		}

		// 위쪽 방향키, 왼쪽 방향키 누르면 객체 선택 왼쪽으로 이동.
		if (ev.keyCode == 37 || ev.keyCode == 38) {
			if ($('.activeObj').length) {
				var prevActivObjId = parseInt($('.activeObj').attr('id'));
				for (var i = prevActivObjId + 1; i < viewingObjId; i++) {
					var id = "#" + i;
					if ($(id).length) {
						$('.activeObj').removeClass('activeObj');
						$(id).addClass('activeObj');
						inputEvent.val('');
						inputEvent.blur();
						break;
					}
				}
			} else {
				if (inputEvent.get(0).selectionEnd == 0) {
					for (var i = 0; i < viewingObjId; i++) {
						var id = "#" + i;
						if ($(id).length) {
							$(id).addClass('activeObj');
							inputEvent.val('');
							inputEvent.blur();
							break;
						}
					}
				}
			}
		}

		// 아래쪽 방향키, 오른쪽 방향키 누르면 객체 선택 오른쪽으로 이동.
		if (ev.keyCode == 39 || ev.keyCode == 40) {
			if ($('.activeObj').length) {
				var isLast = true;
				var prevActivObjId = parseInt($('.activeObj').attr('id'));
				for (var i = prevActivObjId - 1; i >= 0; i--) {
					var id = "#" + i;
					if ($(id).length) {
						$('.activeObj').removeClass('activeObj');
						$(id).addClass('activeObj');
						isLast = false;
						inputEvent.val('');
						inputEvent.blur();
						break;
					}
				}

				if (isLast) {
					$('.activeObj').removeClass('activeObj');
					inputEvent.focus();
				}
			}
		}
	}); // end keypress

}

$
		.ajaxSetup({
			error : function(jqXHR, exception) {
				if (jqXHR.status === 0) {
					alert('Not connect.\n Verify Network.');
				} else if (jqXHR.status == 400) {
					alert('Server understood the request, but request content was invalid. [400]');
				} else if (jqXHR.status == 401) {
					alert('Unauthorized access. [401]');
				} else if (jqXHR.status == 403) {
					alert('Forbidden resource can not be accessed. [403]');
				} else if (jqXHR.status == 404) {
					alert('Requested page not found. [404]');
				} else if (jqXHR.status == 500) {
					alert('Internal server error. [500]');
				} else if (jqXHR.status == 503) {
					alert('Service unavailable. [503]');
				} else if (exception === 'parsererror') {
					alert('Requested JSON parse failed. [Failed]');
				} else if (exception === 'timeout') {
					alert('Time out error. [Timeout]');
				} else if (exception === 'abort') {
					alert('Ajax request aborted. [Aborted]');
				} else {
					alert('Uncaught Error.n' + jqXHR.responseText);
				}
			}
		});

function createJsonObj(strDate) {
	var oriStr = strDate;
	var idVal = "." + strDate;

	strDate = new Object();
	strDate.date = $(idVal).find('#date').text();
	strDate.time = $(idVal).find('#time').text();
	/* strDate.allDayEvent = $(idVal).find('#allDayEvent').text(); */
	strDate = JSON.stringify(strDate);
	console.log("?" + strDate);

	if (oriStr == "startDate") {
		startDate = strDate;
	}
	if (oriStr == "endDate") {
		endDate = strDate;
	}
}

function zeroFill(number, width) {
	width -= number.toString().length;
	if (width > 0) {
		return new Array(width + (/\./.test(number) ? 2 : 1)).join('0')
				+ number;
	}
	return number + "";
}
function addEventDate(ev) {
	console.log("text: "
			+ $(ev.target).clone().children().remove().end().text());
	var newEventDate = "<li class=\"list-float-left newly-added event-date\" id=\""
			+ viewingObjId++ + "\">";
	newEventDate += "일시 : "
			+ $(ev.target).clone().children().remove().end().text();
	newEventDate += "<span class=\"startDate\"></span>";
	newEventDate += "<span class=\"endDate\"></span>";
	newEventDate += "</li>";

	$('.list_eventDates').prepend(newEventDate);

	$(ev.target).find('.start-date').find(".info").each(function() {
		$('.startDate').append($(this).clone());
	});
	$(ev.target).find('.end-date').find(".info").each(function() {
		$('.endDate').append($(this).clone());
	});
}

function biggerThan(target, element) {
	var targetIsBigger = false;
	
	var targetStartDateInfoLeng = $(target).find('.start-date').find(".info").length ;
	var targetEndDateInfoLeng = $(target).find('.end-date').find(".info").length ;

	if (targetStartDateInfoLeng > $(element).find(".startDate").find(".info").length
			|| targetEndDateInfoLeng > $(element).find(".endDate").find(".info").length) {
		targetIsBigger = true;
	}

	return targetIsBigger;
}

// 리스트 중에서 하나 클릭했을 때 이벤트
$(document).on({
	click : function(ev) {
		/* $('.changedText').text("선택한 날짜 : " + $(this).text()); */

		// 일시 추가
		if ($('.newly-added, .event-date').length == 0) {
			addEventDate(ev);
		} else {
			if (biggerThan(ev.target, '.newly-added, .event-date')) {
				$('.newly-added, .event-date').remove();
				addEventDate(ev);
			}
		}

		if ($('.startDate').text() != "") {
			if (startDate == "startDate") {
				createJsonObj(startDate);
			}
		}
		if ($('.endDate').text() != "") {
			if (endDate == "endDate") {
				createJsonObj(endDate);
			}
		}

		inputEvent.val('');
		tmpStr = inputEvent.val();
		
		console.log("star : " +startDate);
		

		checkInput();

		// 포인터 포커스 일정 입력창에..
		inputEvent.focus();
	}
}, '.atcp-group-item');

// request data 세팅
function setRequestData(data) {
	data = {
		inputText : tmpStr
	};
	if (startDate != "startDate") {
		data = {
			inputText : tmpStr,
			startDate : startDate
		};
	}

	if (endDate != "endDate") {
		data = {
			inputText : tmpStr,
			startDate : startDate,
			endDate : endDate
		};
	}

	return data;
}

// ajax request
function checkInput() {
	var requestData = setRequestData(requestData);

	// 뷰 올리기...
	var settings = {
		url : 'autoCompletion',
		type : 'post',
		dataType : 'json',
		data : requestData,
		success : function(data) {
			var str = "";
			$(data)
					.each(
							function(idx, dataEach) {

								if (dataEach.startDate.date == INVALID_INPUT_CHARACTER) {
									alert("., /, -, : 외의 기호는 입력이 불가능합니다.");
								} else {
									var infoStr = "";
									str += "<li class=\"atcp-group-item\">";
									str += dataEach.displayName;

									infoStr += "<div class=\"start-date\">";
									if (dataEach.startDate.date != NO_DATA) {
										/* str += dataEach.startDate.date + " "; */
										infoStr += "<div class=\"info\" id=\"date\">"
												+ dataEach.startDate.date
												+ "</div>";
										var day = new Date(
												dataEach.startDate.date);
										var days = [ "일요일", "월요일", "화요일",
												"수요일", "목요일", "금요일", "토요일" ];
										/* str += days[day.getDay()] + " "; */
									}
									// if (dataEach.allDayEvent == true) {
									// /* str += "종일";*/
									// /*
									// * infoStr += "<div class=\"info\"
									// * id=\"allDayEvent\">" +
									// * dataEach.allDayEvent + "</div>";
									// */
									// } else {
									if (dataEach.startDate.time != NO_DATA) {
										/* str += dataEach.startDate.time; */
										infoStr += "<div class=\"info\" id=\"time\">"
												+ dataEach.startDate.time
												+ "</div>";
									}
									// }
									infoStr += "</div>";

									// endDate 추가
									infoStr += "<div class=\"end-date\">";
									if (dataEach.endDate != null) {
										if (dataEach.endDate.date != NO_DATA) {
											/* str += dataEach.endDate.date + " "; */
											infoStr += "<div class=\"info\" id=\"date\">"
													+ dataEach.endDate.date
													+ "</div>";
											/*
											 * var day = new Date(
											 * dataEach.endDate.date); var days = [
											 * "일요일", "월요일", "화요일", "수요일",
											 * "목요일", "금요일", "토요일" ]; str +=
											 * days[day.getDay()] + " ";
											 */
										}
										// if (dataEach.allDayEvent == true) {
										// /* str += "종일";*/
										// /*
										// * infoStr += "<div class=\"info\"
										// * id=\"allDayEvent\">" +
										// * dataEach.allDayEvent + "</div>";
										// */
										// } else {
										if (dataEach.endDate.time != NO_DATA) {
											/* str += dataEach.endDate.time; */
											infoStr += "<div class=\"info\" id=\"time\">"
													+ dataEach.endDate.time
													+ "</div>";
										}
										// }
										infoStr += "</div>";
									}
									str += infoStr + "</li>";
								}

							});

			/* str += "<div class=\"parsedText\">" + data.parsedText + "</div>"; */

			if (str == "") {
				$('.list-group').removeClass('autoEvent');
			} else {
				$('.list-group').addClass('autoEvent');
			}
			$(".list-group").html(str);
			/* $('.inputText').text("입력한 일정 : " + tmpStr); */
			console.log("성공");

			$(document).on({
				mouseenter : function() {
					$(this).addClass("active");
				},
				mouseleave : function() {
					$(this).removeClass("active");
				}
			}, '.atcp-group-item');
		}
	}

	console.log("request :");
	console.log(settings.data);

	$.ajax(settings);
};
