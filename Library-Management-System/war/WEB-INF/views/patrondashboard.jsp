<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="edu.cmpe275.team13.beans.*"%>
<%@ page import="java.util.*"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Dashboard</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
</head>
<%
	StringBuilder sb = new StringBuilder();
	Patron patron = (Patron) session.getAttribute("user");
	List<IssueBook> issue_books = (List<IssueBook>) request.getAttribute("issue_books");
	List<Book> books = (List<Book>) request.getAttribute("books");
	request.setAttribute("books", null);
	request.setAttribute("issue_books", null);
	if (issue_books.size() > 0) {
		sb.append("<table class=\"table table-bordered\"><tr><th> Book </th><th> Title </th><th> Issue Date </th> <th> Due Date </th> <th></th></tr>");
		for (IssueBook issuebook : issue_books) {
			for (Book book : books) {
				if (issuebook.getId().getIsbn().equals(book.getIsbn())) {
					sb.append("<tr><td><img src=\"" + book.getImage() + "\"></td>");
					sb.append("<td>" + book.getTitle() + "</td>");
					sb.append("<td>" + issuebook.getId().getIssue_date().toLocaleString() + "</td>");
					sb.append("<td>" + issuebook.getDue_date().toLocaleString() + "</td>");
					String input = "<button class=\"return\" class=\"btn btn-danger\" id=\"return_" + book.getIsbn() + "\"> Add to return list </button>";
					sb.append("<td>" + input + "</td></tr>");
				}
			}
		}
		sb.append("</table>");
	} else {
		sb.append("No books issued as of now!");
	}
%>
<body>
	<p style="float:right;">
	<a href="http://1-dot-cmpe-275-term-project-team-13.appspot.com/logout">Logout</a>
	</p>
	<h1>
		Welcome,
		<%=patron.getPatron_name()%>
		!
	</h1>
	<br>
	<h3>Summary of books:</h3>
	<%=sb.toString()%>
	
	<script type="text/javascript">
		$(".return").click(function(event) {
		var isbn = event.currentTarget.id.replace(
				"return_", "");
		var url = "http://1-dot-cmpe-275-term-project-team-13.appspot.com/books/addtocart/" + isbn;
		$("#return_" + isbn).attr("disabled","disabled");
		$.get(url);
	});
	</script>
	
	<form action="/transaction/return" method="GET">	<input class="btn btn-link" type="submit" value="Return"/></form>
	
	<a href="http://1-dot-cmpe-275-term-project-team-13.appspot.com/books/search/">Search</a>
	
</body>
</html>