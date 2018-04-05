<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<meta name="description" content="">
<meta name="author" content="">

<title>>일정 입력 페이지</title>

<!-- Bootstrap core CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<%-- <link type="text/css"
	href="${pageContext.request.contextPath}/resources/vendor/bootstrap/css/bootstrap.min.css"
	rel="stylesheet"> --%>

<!-- Custom CSS -->
<link type="text/css"
	href="${pageContext.request.contextPath}/resources/vendor/bootstrap/css/style.css"
	rel="stylesheet">


<!-- Jquery -->
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/lib/jquery-3.3.1.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<!-- Custom styles for this template -->
<!-- <style>
body {
	padding-top: 54px;
}

@media ( min-width : 992px) {
	body {
		padding-top: 56px;
	}
}
</style> -->


</head>
<body>

	<!-- Navigation -->
	<nav class="navbar navbar-inverse navbar-fixed-top">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse"
					data-target="#myNavbar">
					<span class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">Schedules</a>
			</div>
			<div class="collapse navbar-collapse" id="myNavbar">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Home</a></li>
				</ul>
			</div>
		</div>
	</nav>




	<!-- Page Content -->
	<div class="jumbotron text-center topConts">
		<h2>일정</h2>
		<form>
			<div class="formEvent form-group">
				<!-- <label for="inputEvent">일정</label> -->
				<input type="text" id="inputEvent" name="inputEvent"
					class="form-control input-lg" placeholder="일정을 입력하세요.">
			</div>
		</form>
		<div class="panel panel-default myPanelMargin">
			<div class="panel-heading font-bording display-right">
				<span class="glyphicon glyphicon-chevron-right"></span> <span
					class="inputText">입력한 일정 : ${message}</span>
			</div>
			<div class="panel-body font-bording display-right">
				<span class="glyphicon glyphicon-chevron-right"></span> <span
					class="changedText">선택한 날짜 : </span>
			</div>
		</div>
		
		<button type="button" class="btn btn-success wider-width btn-lg disabled">추가</button>
	</div>
	<div class="container"></div>




	<p></p>


	<!-- jquery -->
	<script type="text/javascript">
		$(function() {
			$.ajaxSetup({
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

			var inputEvent = $('#inputEvent');

			var tmpStr = "default";

			// 입력창에 입력이 될 때마다
			$(inputEvent).keyup(function() {
				var prev = tmpStr;
				tmpStr = inputEvent.val();

				// but 이전 입력값과 같으면 얼럿 안 나오게 
				if (tmpStr != prev)
					checkInput();
			}); // end keyup

			function checkInput() {
				console.log("tmpStr 내용 : " + tmpStr);

				// 뷰 올리기...
				var settings = {
					url : 'refresh',
					type : 'post',
					dataType : 'json',
					data : {
						"inputEventsss" : tmpStr
					},
					success : function(data) {
						var str = "";
						$(data).each(function(idx, dataEach) {
							console.log("data " + idx + ": " + dataEach.year);

							if (dataEach.year == "-002") {
								alert("., /, -, : 외의 기호는 입력이 불가능합니다.");
							} else {
								str += "<div class=\"list-group-item\">";
								if (dataEach.year != "-1") {
									str += dataEach.year;
								}
								if (dataEach.month != "-1") {
									str += "/" + dataEach.month + "/";
								}
								if (dataEach.date != "-1") {
									str += dataEach.date + " ";
								}
								if (dataEach.day != "-1") {
									str += dataEach.day + " ";
								}
								if(dataEach.isAllDayEvent == "true") {
									str += "종일";
								}else{
									if (dataEach.hour != "-1") {
										str += dataEach.hour;
									}
									if (dataEach.minute != "-1") {
										str += ":" + dataEach.minute;
									}
								}
								str += "</div>";
							}
						});

						$(".list-group").html(str);
						$('.inputText').text("입력한 일정 : " + tmpStr);
						console.log("성공");
						
						$(document).on({
							click:function(){ 
								$('.changedText').text("선택한 날짜 : " + $(this).text());
							},
							mouseenter: function () {
								$(this).addClass("active");
						    },
						    mouseleave: function () {
						    	$(this).removeClass("active");
						    }
						}, '.list-group-item');
						

					}
				}

				$.ajax(settings);
			};
			
		});
	</script>


	<div class="panel panel-default myPanelMargin">
		<div class="panel-heading font-bording">날짜/시간 자동 완성</div>
		<div class="list-group panel-body"></div>
	</div>




	<!-- Bootstrap core JavaScript -->
	<%-- 
	<script
		src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.bundle.min.js" />"></script>
 --%>
</body>
</html>
