<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.cmpe275.team13.beans.Book" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Book</title>
</head>
<body>
	<% Book book = (Book) request.getAttribute("book"); %>
	<form action="/books/${book.isbn}" method="post">
		<table>
			<tr>
				<td><label>ISBN</label></td>
				<td><input name= "isbn" value="${book.isbn}" readonly></td>
			</tr>
			<tr>
				<td><label>Author Name</label></td>
				<td><input name= "author_name" value="${book.author_name}"></td>
			</tr>
			<tr>
				<td><label>Title</label></td>
				<td><input name= "title" value="${book.title}"></td>
			</tr>
			<tr>
				<td><label>Call Number</label></td>
				<td><input name= "call_number" value="${book.call_number}"></td>
			</tr>
			<tr>
				<td><label>Publisher Name</label></td>
				<td><input name= "publisher_name" value="${book.publisher_name}"></td>
			</tr>
			<tr>
				<td><label>Year of Publication</label></td>
				<td><input type="date" name= "year_of_publication" value="${book.year_of_publication}"></td>
			</tr>
			<tr>
				<td><label>Location in Library</label></td>
				<td><input name= "location_in_library" value="${book.location_in_library}"></td>
			</tr>
			<tr>
				<td><label>Number of Copies</label></td>
				<td><input name= "number_of_copies" value="${book.number_of_copies}"></td>
			</tr>
			<tr>
				<td><label>Book Status</label></td>
				<td><input name= "book_status" value="${book.book_status}"></td>
			</tr>
			<tr>
				<td><label>Image</label></td>
				<td><input type= "url" name= "image" value="${book.image}"></td>
			</tr>
			<tr>
				<td><label>Available Copies</label></td>
				<td><input name= "available_copies" value="${book.available_copies}" readonly></td>
			</tr>
			<!--  created by must be set by the librarian logged into the system -->
			<tr>
				<td><label>Created By</label></td>
				<td><input name = "created_by" value="${book.created_by}" readonly></td>
			</tr>
			<!--  updated by must be set by the librarian logged into the system -->
			<tr>
				<td><label>Updated By</label></td>
				<td><input name= "updated_by" value="${book.updated_by}" readonly></td>
			</tr>
			<tr>
				<td><label>Keywords</label></td>
				<td><input name="keywords" value="${book.keywords}" placeholder="Enter ; seperated keywords"></td>
			</tr>
			<tr>
				<td colspan="2"><input type= "submit" value ="Update Book"/></td>
			</tr>
		</table>
	</form>
	<form action="/books/${book.isbn}" method="post">
		<input type="hidden" name="_method" value="delete"/>
		<input type="submit" value="Delete Book"/>
	</form>
</body>
</html>