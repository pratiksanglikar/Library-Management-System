<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="edu.cmpe275.team13.search.BookSearch"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Library Management System: search book</title>
</head>
<body>
	<%
		BookSearch bs = new BookSearch();
		/*private String title;
	private String author_name;
	private String publisher_name;
	private Long isbn;
	private Date year_of_publication;
	private boolean book_status;
	private int created_by;
	private int updated_by*/
	%>
	<h1>Book:</h1>
	<form action="/books/searchbook/" method="get">
		<table>
			<tr>
				<td><label>ISBN</label></td>
				<td><input name="isbn" value=${bs.isbn}></td>
			</tr>
			<tr>
				<td><label>Author Name</label></td>
				<td><input name="author_name" value="${bs.author_name}"></td>
			</tr>
			<tr>
				<td><label>Title</label></td>
				<td><input name="title" value="${bs.title}"></td>
			</tr>
			<tr>
				<td><label>Publisher Name</label></td>
				<td><input name="publisher_name" value="${bs.publisher_name}"></td>
			</tr>
			<tr>
				<td><label>Year of Publication</label></td>
				<td><input type="text" name="year_of_publication" placeholder="yyyy-mm-dd"
					value="${bs.year_of_publication}"></td>
			</tr>
			<tr>
				<td><label>Book Status</label></td>
				<td><input name="book_status" value="false"></td>
			</tr>
			<%-- <tr>
				<td><label>Available Copies</label></td>
				<td><input name= "available_copies" value = "${bs.available_copies}"></td>
			</tr> --%>
			<!-- created by must be set by the librarian logged into the system -->
			<tr>
				<td><label>Created By</label></td>
				<td><input name="created_by" value=${bs.created_by}></td>
			</tr>
			<!--  updated by must be set by the librarian logged into the system -->
			<tr>
				<td><label>Updated By</label></td>
				<td><input name="updated_by" value=${bs.updated_by}></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="Search Book" /></td>
			</tr>
		</table>
	</form>
</body>
</html>