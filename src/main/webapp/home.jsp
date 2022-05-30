<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="stylesheet" href="style.css">
	<title>Home</title>
</head>
<body>
	<nav class="navbar">
	  <div class="box">
	  	<div>
			<img src="images/email_icon.jpg" align="left" />
			<p>E-MAIL CLIENT
				<br><% out.println(request.getAttribute("email")); %>
			</p>
	  	</div>
	  	<div id="right"><a href="login.jsp">Logout</a></div>
	  </div>
	</nav>
	
	<div class="grid-container">
		<form class="btn-group" action="navigation" method="post">
			<input type="hidden" name="email" value="<%= request.getAttribute("email") %>">
			<input type="hidden" name="password" value="<%= request.getAttribute("password") %>">
			<input type="submit" name="newMail" value="New Mail">
			<input type="submit" name="inbox" value="Inbox">
			<input type="submit" name="sent" value="Sent">
		</form>
		
		<%= request.getAttribute("content")!=null ? request.getAttribute("content") : "" %>
		<% if(request.getAttribute("error")!=null) { %>
          <p class='error'>Error: <%= request.getAttribute("error")%></p>
        <% } %>
	</div>
</body>
</html>