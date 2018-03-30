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
<link type="text/css"
	href="${pageContext.request.contextPath}/resources/vendor/bootstrap/css/bootstrap.min.css"
	rel="stylesheet">

<!-- Custom CSS -->
<link type="text/css"
	href="${pageContext.request.contextPath}/resources/vendor/bootstrap/css/style.css"
	rel="stylesheet">

<!-- Custom styles for this template -->
<style>
body {
	padding-top: 54px;
}

@media ( min-width : 992px) {
	body {
		padding-top: 56px;
	}
}
</style>


<!-- Jquery -->
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/lib/jquery-3.3.1.min.js"></script>

</head>
<body>

	<!-- Navigation -->
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
		<div class="container">
			<a class="navbar-brand" href="#">Schedules</a>
			<button class="navbar-toggler" type="button" data-toggle="collapse"
				data-target="#navbarResponsive" aria-controls="navbarResponsive"
				aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="navbarResponsive">
				<ul class="navbar-nav ml-auto">
					<li class="nav-item active"><a class="nav-link" href="#">Home
							<span class="sr-only">(current)</span>
					</a></li>
					<li class="nav-item"><a class="nav-link" href="#">About</a></li>
					<li class="nav-item"><a class="nav-link" href="#">Services</a>
					</li>
					<li class="nav-item"><a class="nav-link" href="#">Contact</a>
					</li>
				</ul>
			</div>
		</div>
	</nav>



	<!-- Page Content -->
	<div class="container">
		<div class="row">
			<div class="col-lg-12 text-center">
<!-- 				<h1 class="mt-5">문장에서 일정을 자동 추출합니다.</h1> -->
				<p class="lead">일정 관리</p>
<!-- 				<ul class="list-unstyled">
					<li>Bootstrap 4.0.0</li>
					<li>jQuery 3.3.0</li>
				</ul> -->
			</div>
		</div>
	</div>



	<!-- 	<form action="recommendations" method="post"> -->

	<form>
		<div class="formEvent">
			일정 <input type="text" id="inputEvent" name="inputEvent"
				placeholder="일정을 입력하세요.">
		</div>
	</form>

	<p class="result">입력한 일정 : ${message}</p>
	<p></p>


	<!-- jquery -->
	<script type="text/javascript">
		$(function() {
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

							if (dataEach.year == "-2") {
								alert("한글, 숫자, 영어 외의 기호는 입력이 불가능합니다.");
							} else {
								str += "<div class='recListElement'>";
								if (dataEach.year != "-1") {
									str += dataEach.year ;
								}
								if (dataEach.month != "-1") {
									str += "/" + dataEach.month + "/";
								}
								if (dataEach.date != "-1") {
									str += dataEach.date + " ";
								}
								if (dataEach.day != "-1") {
									str += dataEach.day + "요일 ";
								}
								if (dataEach.hour != "-1") {
									str += dataEach.hour;
								}
								if (dataEach.minute != "-1") {
									str += ":" + dataEach.minute;
								}
								str += "</div>";
							}
						});

						$(".recommendations").html(str);
						$('.result').text("입력한 일정 : " + tmpStr);
						console.log("성공");
					}
				}

				$.ajax(settings);
			}
			;
		});
	</script>


	<div class="recWrapper">
		<div class="recTitle">날짜/시간 자동 완성</div>
		<div class="recommendations"></div>
	</div>




	<!-- Bootstrap core JavaScript -->

	<script src="<c:url value="/resources/vendor/jquery/jquery.min.js" />"></script>
	<script
		src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.bundle.min.js" />"></script>

</body>
</html>
