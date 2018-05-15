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
	<nav class="navbar navbar-inverse navbar-fixed-top works-color">
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
		<h4>일정 등록</h4>
			<span class="event_tit">일정</span>
			<div class="event_domain">
				<div class="article">
					<ul class="list_eventDates">
						<li class="list-float-left input"><textarea id="inputEvent"
								autocomplete="off" name="inputEvent" rows="1" tabindex="1"></textarea>
						</li>
					</ul>
					<div class="atcp"><!-- hideAtcp -->
						<ul class="list-group">
						</ul>
					</div>
				</div>
			</div>
<!-- 		<button type="button"
			class="btn btn-success wider-width btn-lg disabled button">추가</button> -->
	</div>
	<div class="container"></div>


	<p></p>


	<!-- Bootstrap core JavaScript -->
	<%-- 
	<script
		src="<c:url value="/resources/vendor/bootstrap/js/bootstrap.bundle.min.js" />"></script>
 --%>


	<!-- js -->
	<script type="text/javascript"
		src="${pageContext.request.contextPath}/resources/vendor/bootstrap/js/script.js"></script>
</body>
</html>
