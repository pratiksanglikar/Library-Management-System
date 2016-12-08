<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="edu.cmpe275.team13.beans.*"%>
<%@ page import="java.util.List"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<title>Search Results!</title>
</head>
<body>
<p style="float:right;">
	<a href="http://1-dot-cmpe-275-term-project-team-13.appspot.com/logout">Logout</a>
	</p>
	
	<%
		List<Book> books = (List<Book>) request.getAttribute("books");
		String name = null;
		boolean isLibrarian = false;
		if (session.getAttribute("type").equals("librarian")) {
			Librarian user = (Librarian) session.getAttribute("user");
			name = user.getLibrarian_name();
			isLibrarian = true;
		} else {
			Patron user = (Patron) session.getAttribute("user");
			name = user.getPatron_name();
		}
		Long id = null;
	%>

	<c:forEach items="${books}" var="book">
		<h2>"${book.title}"</h2>
		<img alt="Title" src="${book.image}">
		<h4>"${book.isbn}"</h4>
		<h4>"${book.author_name}"</h4>
		<h4>"${book.call_number}"</h4>
		<h4>"${book.publisher_name}"</h4>
		<h4>"${book.year_of_publication}"</h4>
		<h4>"${book.location_in_library}"</h4>
		<h4>"${book.book_status}"</h4>
		<h4>"${book.keywords}"</h4>
		<c:set var="bool_val"><%=isLibrarian%></c:set>
		<c:choose>
			<c:when test="${bool_val eq true}">
				<button class="update" id="${book.isbn}_update" name="update">
					Update</button>
				<button class="delete" id="${book.isbn}_delete" name="delete">
					Delete</button>
			</c:when>
			<c:otherwise>
				<button class="addtocart" id="${book.isbn}_addtocart"
					name="addtocart">Add to Cart</button>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<script type="text/javascript">
		$(".update").click(function(event) {
			var isbn = event.currentTarget.id.replace("_update", "");
			var path = window.location.href;
			var i = path.lastIndexOf("/");
			var loc = path.substring(0, i).concat("librarian/" + isbn);
			loc = loc.replace("searchbook", "");
			window.location.href = loc;
		});

		$(".delete")
				.click(
						function(event) {
							var isbn = event.currentTarget.id.replace(
									"_delete", "");
							var url = "http://1-dot-cmpe-275-term-project-team-13.appspot.com/books/"
									+ isbn;
							console.log(url);
							$.ajax({
								"url" : url,
								"type" : "DELETE",
								"success" : function() {
									//window.location.href = "http://1-dot-cmpe-275-term-project-team-13.appspot.com/books/search";
									window.location.reload(true);
								}
							});
						});
		
		$(".addtocart").click(function(event) {
			var isbn = event.currentTarget.id.replace(
					"_addtocart", "");
			var url = "http://1-dot-cmpe-275-term-project-team-13.appspot.com/books/addtocart/" + isbn;
			$("#" + isbn + "_addtocart").attr("disabled","disabled");
			$.get(url);
		});
		/* $("#checkout").click(function(event) {
			var url = "http://1-dot-cmpe-275-term-project-team-13.appspot.com/transaction/checkout";
			$.get(url);
		}); */
	</script>
	<% String vari = "";
	if(!isLibrarian) {
		vari += "<form action=\"/transaction/checkout\" method=\"GET\">	<input type=\"submit\" value=\"Checkout\"/></form>";
	}%>
	<%=vari %>
</body>
</html>